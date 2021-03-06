/*******************************************************************************
 * Copyright (c) 2009 Vlad Dumitrescu and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available
 * at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vlad Dumitrescu
 *******************************************************************************/
package org.erlide.ui.console;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.swt.widgets.Display;
import org.erlide.jinterface.backend.BackendShell;
import org.erlide.jinterface.backend.BackendShellListener;
import org.erlide.jinterface.backend.console.IoRequest.IoRequestKind;

public final class ErlConsoleDocument extends Document implements
		BackendShellListener {

	private static String[] LEGAL_CONTENT_TYPES = null;

	private final BackendShell shell;

	public ErlConsoleDocument(final BackendShell shell) {
		super();

		if (LEGAL_CONTENT_TYPES == null) {
			IoRequestKind[] values = IoRequestKind.values();
			LEGAL_CONTENT_TYPES = new String[values.length];
			for (int i = 0; i < LEGAL_CONTENT_TYPES.length; i++) {
				LEGAL_CONTENT_TYPES[i] = values[i].name();
			}
		}

		Assert.isNotNull(shell);
		this.shell = shell;
		shell.addListener(this);
		changed(shell);

		final IDocumentPartitioner partitioner = new FastPartitioner(
				createScanner(), LEGAL_CONTENT_TYPES);
		partitioner.connect(this);
		setDocumentPartitioner(partitioner);
	}

	private IPartitionTokenScanner createScanner() {
		return new IoRequestScanner(shell);
	}

	public void changed(BackendShell aShell) {
		if (aShell != shell) {
			return;
		}
		final String text = shell.getText();
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				try {
					replace(0, getLength(), text);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public BackendShell getShell() {
		return shell;
	}
}
