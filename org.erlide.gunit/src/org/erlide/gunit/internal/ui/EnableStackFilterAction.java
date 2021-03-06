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
package org.erlide.gunit.internal.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * Action to enable/disable stack trace filtering.
 */
public class EnableStackFilterAction extends Action {

	private final FailureTrace fView;

	public EnableStackFilterAction(final FailureTrace view) {
		super(GUnitMessages.EnableStackFilterAction_action_label);
		setDescription(GUnitMessages.EnableStackFilterAction_action_description);
		setToolTipText(GUnitMessages.EnableStackFilterAction_action_tooltip);

		setDisabledImageDescriptor(GUnitPlugin
				.getImageDescriptor("dlcl16/cfilter.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(GUnitPlugin
				.getImageDescriptor("elcl16/cfilter.gif")); //$NON-NLS-1$
		setImageDescriptor(GUnitPlugin.getImageDescriptor("elcl16/cfilter.gif")); //$NON-NLS-1$
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IGUnitHelpContextIds.ENABLEFILTER_ACTION);

		this.fView = view;
		setChecked(GUnitPreferencePage.getFilterStack());
	}

	/*
	 * @see Action#actionPerformed
	 */
	@Override
	public void run() {
		GUnitPreferencePage.setFilterStack(isChecked());
		this.fView.refresh();
	}
}
