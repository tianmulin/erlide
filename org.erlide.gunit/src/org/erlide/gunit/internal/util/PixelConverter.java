/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.erlide.gunit.internal.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

/*
 * class copied from jdt ui
 */
public class PixelConverter {

	private final FontMetrics fFontMetrics;

	public PixelConverter(final Control control) {
		final GC gc = new GC(control);
		gc.setFont(control.getFont());
		this.fFontMetrics = gc.getFontMetrics();
		gc.dispose();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertHeightInCharsToPixels(int)
	 */
	public int convertHeightInCharsToPixels(final int chars) {
		return Dialog.convertHeightInCharsToPixels(this.fFontMetrics, chars);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertHorizontalDLUsToPixels(int)
	 */
	public int convertHorizontalDLUsToPixels(final int dlus) {
		return Dialog.convertHorizontalDLUsToPixels(this.fFontMetrics, dlus);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertVerticalDLUsToPixels(int)
	 */
	public int convertVerticalDLUsToPixels(final int dlus) {
		return Dialog.convertVerticalDLUsToPixels(this.fFontMetrics, dlus);
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertWidthInCharsToPixels(int)
	 */
	public int convertWidthInCharsToPixels(final int chars) {
		return Dialog.convertWidthInCharsToPixels(this.fFontMetrics, chars);
	}

}
