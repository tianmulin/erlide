/*
 * Code borrowed from PyDev
 */
/*
 * @author Fabio Zadrozny
 * Created: June 2005
 * License: Common Public License v1.0
 */

package org.erlide.ui.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;

import org.erlide.core.util.FastStringBuffer;
import org.erlide.core.util.Tuple;

public class StringUtils {

	public static final Object EMPTY = "";

	/**
	 * Formats a string, replacing %s with the arguments passed.
	 * 
	 * @param str
	 *            string to be formatted
	 * @param args
	 *            arguments passed
	 * @return a string with the %s replaced by the arguments passed
	 */
	public static String format(String str, Object... args) {
		FastStringBuffer buffer = new FastStringBuffer(str.length()
				+ (16 * args.length));
		int j = 0;

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '%' && i + 1 < str.length()) {
				char nextC = str.charAt(i + 1);
				if (nextC == 's') {
					buffer.append(args[j].toString());
					j++;
					i++;
				} else if (nextC == '%') {
					buffer.append('%');
					j++;
					i++;
				}
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * Counts the number of %s in the string
	 * 
	 * @param str
	 *            the string to be analyzide
	 * @return the number of %s in the string
	 */
	public static int countPercS(String str) {
		int j = 0;

		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (c == '%' && i + 1 < len) {
				char nextC = str.charAt(i + 1);
				if (nextC == 's') {
					j++;
					i++;
				}
			}
		}
		return j;
	}

	/**
	 * Removes whitespaces at the beggining of the string.
	 */
	public static String rightTrim(String input) {
		int len = input.length();
		int st = 0;
		int off = 0;
		char[] val = input.toCharArray();

		while ((st < len) && (val[off + len - 1] <= ' ')) {
			len--;
		}
		return input.substring(0, len);
	}

	/**
	 * Removes whitespaces at the end of the string.
	 */
	public static String leftTrim(String input) {
		int len = input.length();
		int off = 0;
		char[] val = input.toCharArray();

		while ((off < len) && (val[off] <= ' ')) {
			off++;
		}
		return input.substring(off, len);
	}

	/**
	 * Given a string remove all from the rightmost '.' onwards.
	 * 
	 * E.g.: bbb.t would return bbb
	 * 
	 * If it has no '.', returns the original string unchanged.
	 */
	public static String stripExtension(String input) {
		return stripFromRigthCharOnwards(input, '.');
	}

	public static int rFind(String input, char ch) {
		int len = input.length();
		int st = 0;
		int off = 0;
		char[] val = input.toCharArray();

		while ((st < len) && (val[off + len - 1] != ch)) {
			len--;
		}
		len--;
		return len;
	}

	private static String stripFromRigthCharOnwards(String input, char ch) {
		int len = rFind(input, ch);
		if (len == -1) {
			return input;
		}
		return input.substring(0, len);
	}

	public static String stripFromLastSlash(String input) {
		return stripFromRigthCharOnwards(input, '/');
	}

	/**
	 * Removes the occurrences of the passed char in the beggining of the
	 * string.
	 */
	public static String rightTrim(String input, char charToTrim) {
		int len = input.length();
		int st = 0;
		int off = 0;
		char[] val = input.toCharArray();

		while ((st < len) && (val[off + len - 1] == charToTrim)) {
			len--;
		}
		return input.substring(0, len);
	}

	/**
	 * Removes the occurrences of the passed char in the start and end of the
	 * string.
	 */
	public static String leftAndRightTrim(String input, char charToTrim) {
		return rightTrim(leftTrim(input, charToTrim), charToTrim);
	}

	/**
	 * Removes the occurrences of the passed char in the end of the string.
	 */
	public static String leftTrim(String input, char charToTrim) {
		int len = input.length();
		int off = 0;
		char[] val = input.toCharArray();

		while ((off < len) && (val[off] == charToTrim)) {
			off++;
		}
		return input.substring(off, len);
	}

	/**
	 * Changes all backward slashes (\) for forward slashes (/)
	 * 
	 * @return the replaced string
	 */
	public static String replaceAllSlashes(String string) {
		int len = string.length();
		char c = 0;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);

			if (c == '\\') { // only do some processing if there is a
				// backward slash
				char[] ds = string.toCharArray();
				ds[i] = '/';
				for (int j = i; j < len; j++) {
					if (ds[j] == '\\') {
						ds[j] = '/';
					}
				}
				return new String(ds);
			}

		}
		return string;
	}

	/**
	 * Splits the given string in a list where each element is a line.
	 * 
	 * @param string
	 *            string to be splitted.
	 * @return list of strings where each string is a line.
	 * 
	 * @note the new line characters are also added to the returned string.
	 */
	public static List<String> splitInLines(String string) {
		ArrayList<String> ret = new ArrayList<String>();
		int len = string.length();

		char c;
		FastStringBuffer buf = new FastStringBuffer();

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);

			buf.append(c);

			if (c == '\r') {
				if (i < len - 1 && string.charAt(i + 1) == '\n') {
					i++;
					buf.append('\n');
				}
				ret.add(buf.toString());
				buf.clear();
			}
			if (c == '\n') {
				ret.add(buf.toString());
				buf.clear();

			}
		}
		if (buf.length() != 0) {
			ret.add(buf.toString());
		}
		return ret;

	}

	public static boolean isSingleWord(String string) {
		for (char c : string.toCharArray()) {
			if (!Character.isJavaIdentifierStart(c)) {
				return false;
			}
		}
		return true;
	}

	public static String replaceAll(String string, String replace, String with) {
		FastStringBuffer ret = new FastStringBuffer();
		int len = string.length();
		int replaceLen = replace.length();

		for (int i = 0; i < len; i++) {
			if (i + replaceLen > len) {
				ret.append(string.charAt(i));
				continue;
			}
			String s = string.substring(i, i + replaceLen);
			if (s.equals(replace)) {
				ret.append(with);
				i = i + replaceLen - 1;
			} else {
				ret.append(s.charAt(0));
			}
		}

		return ret.toString();
	}

	public static String removeWhitespaceColumnsToLeft(String hoverInfo) {
		FastStringBuffer buf = new FastStringBuffer();
		int firstCharPosition = Integer.MAX_VALUE;

		List<String> splitted = splitInLines(hoverInfo);
		for (String line : splitted) {
			if (line.trim().length() > 0) {
				int found = ErlideSelection.getFirstCharPosition(line);
				firstCharPosition = Math.min(found, firstCharPosition);
			}
		}

		if (firstCharPosition != Integer.MAX_VALUE) {
			for (String line : splitted) {
				if (line.length() > firstCharPosition) {
					buf.append(line.substring(firstCharPosition));
				}
			}
			return buf.toString();
		} else {
			return hoverInfo;// return initial
		}
	}

	/**
	 * Given some html, extracts its text.
	 */
	public static String extractTextFromHTML(String html) {
		try {
			EditorKit kit = new HTMLEditorKit();
			Document doc = kit.createDefaultDocument();

			// The Document class does not yet handle charset's properly.
			doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);

			// Create a reader on the HTML content.
			Reader rd = new StringReader(html);

			// Parse the HTML.
			kit.read(rd, doc, 0);

			// The HTML text is now stored in the document
			return doc.getText(0, doc.getLength());
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * Splits the passed string based on the toSplit string.
	 */
	public static List<String> split(final String string, final String toSplit) {
		if (toSplit.length() == 1) {
			return split(string, toSplit.charAt(0));
		}
		ArrayList<String> ret = new ArrayList<String>();
		if (toSplit.length() == 0) {
			ret.add(string);
			return ret;
		}

		int len = string.length();

		int last = 0;

		char c = 0;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			if (c == toSplit.charAt(0) && matches(string, toSplit, i)) {
				if (last != i) {
					ret.add(string.substring(last, i));
				}
				last = i + toSplit.length();
				i += toSplit.length() - 1;
			}
		}

		if (last < len) {
			ret.add(string.substring(last, len));
		}

		return ret;
	}

	private static boolean matches(String string, String toSplit, int i) {
		if (string.length() - i >= toSplit.length()) {
			for (int j = 0; j < toSplit.length(); j++) {
				if (string.charAt(i + j) != toSplit.charAt(j)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Splits some string given some char
	 */
	public static List<String> split(String string, char toSplit) {
		ArrayList<String> ret = new ArrayList<String>();
		int len = string.length();

		int last = 0;

		char c = 0;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			if (c == toSplit) {
				if (last != i) {
					ret.add(string.substring(last, i));
				}
				while (c == toSplit && i < len - 1) {
					i++;
					c = string.charAt(i);
				}
				last = i;
			}
		}
		if (c != toSplit) {
			if (last == 0 && len > 0) {
				ret.add(string); // it is equal to the original (no dots)

			} else if (last < len) {
				ret.add(string.substring(last, len));

			}
		}
		return ret;
	}

	/**
	 * Splits some string given many chars
	 */
	public static List<String> split(String string, char... toSplit) {
		ArrayList<String> ret = new ArrayList<String>();
		int len = string.length();

		int last = 0;

		char c = 0;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);

			if (contains(c, toSplit)) {
				if (last != i) {
					ret.add(string.substring(last, i));
				}
				while (contains(c, toSplit) && i < len - 1) {
					i++;
					c = string.charAt(i);
				}
				last = i;
			}
		}
		if (!contains(c, toSplit)) {
			if (last == 0 && len > 0) {
				ret.add(string); // it is equal to the original (no dots)

			} else if (last < len) {
				ret.add(string.substring(last, len));

			}
		}
		return ret;
	}

	public static List<String> splitAndRemoveEmptyTrimmed(String string, char c) {
		List<String> split = split(string, c);
		for (int i = split.size() - 1; i >= 0; i--) {
			if (split.get(i).trim().length() == 0) {
				split.remove(i);
			}
		}
		return split;
	}

	private static boolean contains(char c, char[] toSplit) {
		for (char ch : toSplit) {
			if (c == ch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Splits some string given some char in 2 parts. If the separator is not
	 * found, everything is put in the 1st part.
	 */
	public static Tuple<String, String> splitOnFirst(String fullRep,
			char toSplit) {
		int i = fullRep.indexOf(toSplit);
		if (i != -1) {
			return new Tuple<String, String>(fullRep.substring(0, i), fullRep
					.substring(i + 1));
		} else {
			return new Tuple<String, String>(fullRep, "");
		}

	}

	/**
	 * Splits the string as would string.split("\\."), but without yielding
	 * empty strings
	 */
	public static List<String> dotSplit(String string) {
		return splitAndRemoveEmptyTrimmed(string, '.');
	}

	/**
	 * Same as Python join: Go through all the paths in the string and join them
	 * with the passed delimiter.
	 */
	public static String join(String delimiter, String[] splitted) {
		FastStringBuffer buf = new FastStringBuffer(splitted.length * 100);
		for (String string : splitted) {
			if (buf.length() > 0) {
				buf.append(delimiter);
			}
			buf.append(string);
		}
		return buf.toString();
	}

	/**
	 * Same as Python join: Go through all the paths in the string and join them
	 * with the passed delimiter.
	 */
	public static String join(String delimiter, List<String> splitted) {
		FastStringBuffer buf = new FastStringBuffer(splitted.size() * 100);
		for (String string : splitted) {
			if (buf.length() > 0) {
				buf.append(delimiter);
			}
			buf.append(string);
		}
		return buf.toString();
	}

	/**
	 * Adds a char to an array of chars and returns the new array.
	 * 
	 * @param c
	 *            The chars to where the new char should be appended
	 * @param toAdd
	 *            the char to be added
	 * @return a new array with the passed char appended.
	 */
	public static char[] addChar(char[] c, char toAdd) {
		char[] c1 = new char[c.length + 1];

		System.arraycopy(c, 0, c1, 0, c.length);
		c1[c.length] = toAdd;
		return c1;

	}

	public static String replaceNewLines(String message, String string) {
		message = message.replaceAll("\r\n", string);
		message = message.replaceAll("\r", string);
		message = message.replaceAll("\n", string);

		return message;
	}

	public static String removeNewLineChars(String message) {
		return message.replaceAll("\r", "").replaceAll("\n", "");
	}

}
