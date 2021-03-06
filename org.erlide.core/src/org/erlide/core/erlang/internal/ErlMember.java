package org.erlide.core.erlang.internal;

import org.erlide.core.erlang.IErlElement;
import org.erlide.core.erlang.IErlMember;
import org.erlide.core.erlang.ISourceRange;

/**
 * 
 * @author Vlad Dumitrescu
 */
public abstract class ErlMember extends SourceRefElement implements IErlMember {
	int fNameRangeStart, fNameRangeEnd;

	protected ErlMember(final IErlElement parent, final String name) {
		super(parent, name);
	}

	// private OtpErlangObject fTree;

	// public void setParseTree(OtpErlangObject tree) {
	// fTree = tree;
	// }

	// public OtpErlangObject getParseTree() {
	// return fTree;
	// }

	public boolean isVisibleInOutline() {
		return true;
	}

	public void setNameRangeStartEnd(final int start, final int end) {
		fNameRangeStart = start;
		fNameRangeEnd = end;
	}

	public ISourceRange getNameRange() {
		if (fNameRangeStart == 0 && fNameRangeEnd == 0) {
			return new SourceRange(getSourceRangeStart(), getSourceRangeEnd()
					- getSourceRangeStart());
		}
		return new SourceRange(fNameRangeStart, fNameRangeEnd - fNameRangeStart);
	}

}
