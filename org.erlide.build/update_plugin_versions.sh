#! /bin/sh

BASE=$1
COMMIT=$2

# $COMMIT = co   = comit changes
#           dry  = dry-run, just test versions
#                = modify plugin/feature versions

CRT=$(git branch | grep '*' | cut -d ' ' -f 2)
PROJECTS=$(git log --name-only $BASE..$CRT --oneline | cut -d ' ' -f 1 | grep org.erlide | cut -f 1 -d '/' | sort | uniq)

function inc_version {
  local VER=$1
  local WHICH=$2	

  local MAJ=$(echo $VER | cut -d '.' -f 1)
  local MIN=$(echo $VER | cut -d '.' -f 2)
  local MICRO=$(echo $VER | cut -d '.' -f 3)
  local QUAL=$(echo $VER | cut -d '.' -f 4)
  
  local MAJ2=$MAJ
  local MIN2=$MIN
  local MICRO2=$MICRO
  case "$WHICH" in
    major)
	  MAJ2=$(($MAJ + 1))
	  MIN2=0
	  MICRO2=0
	  ;;
	minor)
	  MAJ2=$MAJ
	  MIN2=$(($MIN + 1))
	  MICRO2=0
	  ;;
	micro)
	  MAJ2=$MAJ
	  MIN2=$MIN
	  MICRO2=$(($MICRO + 1))
	  ;;
	*)
	  ;;
  esac
  
  local NEW=""
  if [ -z $QUAL ] 
  then
	NEW="$MAJ2.$MIN2.$MICRO2"
  else
	NEW="$MAJ2.$MIN2.$MICRO2.$QUAL"
  fi
  echo "      : $VER -> $NEW" >&2 
  echo "$NEW"
}

function which_changed {
  OLD=$1
  NEW=$2

  local MAJ1=$(echo $OLD | cut -d '.' -f 1)
  local MIN1=$(echo $OLD | cut -d '.' -f 2)
  local MICRO1=$(echo $OLD | cut -d '.' -f 3)

  local MAJ2=$(echo $NEW | cut -d '.' -f 1)
  local MIN2=$(echo $NEW | cut -d '.' -f 2)
  local MICRO2=$(echo $NEW | cut -d '.' -f 3)
  
  if [ "$MAJ1" != "$MAJ2" ]
  then
	echo major
  elif [ "$MIN1" != "$MIN2" ] 
  then
    echo minor
  elif [ "$MICRO1" != "$MICRO2" ] 
  then
	echo micro
  else
	echo none
  fi
}

function select_change {
  if [ "$1" = "major" -o "$2" = "major" ]
  then  
    echo major
  elif [ "$1" = "minor" -o "$2" = "minor" ]
  then  
    echo minor
  elif [ "$1" = "micro" -o "$2" = "micro" ]
  then  
    echo micro
  else
    echo "none"
  fi
}

CHANGED="none"
for PRJ in $PROJECTS
do
  echo $PRJ
  if [ -a $PRJ/META-INF/MANIFEST.MF ] 
  then 
    OLDFILE=$(git show $BASE:$PRJ/META-INF/MANIFEST.MF 2> /dev/null)
	ERR=$?
	if [ $ERR -eq 0 ] 
	then
      OLD=$(git show $BASE:$PRJ/META-INF/MANIFEST.MF | grep Bundle-Version: | cut -d ' ' -f 2)
      NEW=$(cat $PRJ/META-INF/MANIFEST.MF | grep Bundle-Version: | cut -d ' ' -f 2)
	  echo "    $OLD:$NEW"
	  if [ $OLD = $NEW ]
	  then
		  CH="micro"
                  echo "$PRJ::"
		  NEW=$(inc_version $NEW $CH)
		  
		  if [ "$COMMIT" != "dry" ]
		  then
			sed "s/Bundle-Version: $OLD/Bundle-Version: $NEW/" < $PRJ/META-INF/MANIFEST.MF > $PRJ/META-INF/MANIFEST.MF1
			mv $PRJ/META-INF/MANIFEST.MF1 $PRJ/META-INF/MANIFEST.MF
		  fi
	  else
	    CH=$(which_changed $OLD $NEW)
	  fi
	  echo "    changed: $CH"
	  CHANGED=$(select_change $CHANGED $CH)
	fi
  fi
done

echo "final changed: $CHANGED..."
if [ "$CHANGED" != "none" ]
then
  OLD=$(git show $BASE:org.erlide/feature.xml | grep "  version=" | head -n 1 | cut -d '"' -f 2)
  NEW=$(cat org.erlide/feature.xml | grep "  version=" | head -n 1 | cut -d '"' -f 2)
  CH=$(which_changed $OLD $NEW)
  CHG=$(select_change $CH $CHANGED)
  if [ "$CHG" != "$CH" ]
  then
	  VER=$(inc_version $NEW $CHG)
	  echo "-> $VER"
	  
	  if [ "$COMMIT" != "dry" ]
	  then
		sed "s/  version=\"$OLD\"/  version=\"$VER\"/" < org.erlide/feature.xml > org.erlide/feature.xml1
		mv org.erlide/feature.xml1 org.erlide/feature.xml

		NEW_=$(echo $NEW | sed 's/.qualifier//')
		VER_=$(echo $VER | sed 's/.qualifier//')
		mv CHANGES CHANGES.old		
		echo "List of user visible changes between $NEW_ and $VER_ ($(date +%Y%m%d))" > CHANGES
		echo "" >> CHANGES
		git log v$NEW_..$CRT --oneline >> CHANGES
		echo "" >> CHANGES
		cat CHANGES.old >> CHANGES
		rm CHANGES.old

	fi
  fi
fi

if [ "$COMMIT" = "co" ] 
then
  git commit -a -m "prepared $VER_"
fi
