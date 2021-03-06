/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.erlide.ui.editors.erl.hyperlink;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * ErlWordFinder
 * 
 */
public class ErlWordFinder {

	/**
	 * 
	 */
	public ErlWordFinder() {
		// NO-OP
	}

	/**
	 * Copied from <code>org.eclipse.jdt.internal.ui.text.JavaWordFinder</code>
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	public static IRegion findWord(final IDocument document, final int offset) {

		int start = -2;
		int end = -1;

		try {
			int pos = offset;
			char c;

			while (pos >= 0) {
				c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
				--pos;
			}
			start = pos;

			pos = offset;
			final int length = document.getLength();

			while (pos < length) {
				c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
				++pos;
			}
			end = pos;

		} catch (final BadLocationException x) {
		}

		if (start >= -1 && end > -1) {
			if (start == offset && end == offset) {
				return new Region(offset, 0);
			} else if (start == offset) {
				return new Region(start, end - start);
			} else {
				return new Region(start + 1, end - start - 1);
			}
		}

		return null;
	}

}
