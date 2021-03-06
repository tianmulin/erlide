/*******************************************************************************
 * Copyright (c) 2009 * and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available
 * at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     *
 *******************************************************************************/
package org.erlide.ui.information;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.erlide.core.erlang.ErlModelException;
import org.erlide.core.erlang.ErlangCore;
import org.erlide.core.erlang.IErlElement;
import org.erlide.jinterface.util.ErlLogger;
import org.erlide.ui.ErlideUIPlugin;
import org.erlide.ui.ErlideUIPluginImages;
import org.erlide.ui.actions.OpenAction;
import org.erlide.ui.editors.erl.SimpleSelectionProvider;
import org.erlide.ui.editors.util.EditorUtility;
import org.erlide.ui.util.eclipse.BrowserInformationControl;
import org.erlide.ui.util.eclipse.BrowserInformationControlInput;
import org.erlide.ui.util.eclipse.BrowserInput;
import org.erlide.ui.views.EdocView;

import erlang.OpenResult;

public final class PresenterControlCreator extends
		AbstractReusableInformationControlCreator {

	/**
	 * Action to go back to the previous input in the hover control.
	 */
	private static final class BackAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public BackAction(final BrowserInformationControl infoControl) {
			fInfoControl = infoControl;
			setText("Previous");
			final ISharedImages images = PlatformUI.getWorkbench()
					.getSharedImages();
			setImageDescriptor(images
					.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
			setDisabledImageDescriptor(images
					.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));

			update();
		}

		@Override
		public void run() {
			final BrowserInformationControlInput previous = (BrowserInformationControlInput) fInfoControl
					.getInput().getPrevious();
			if (previous != null) {
				fInfoControl.setInput(previous);
			}
		}

		public void update() {
			final BrowserInformationControlInput current = fInfoControl
					.getInput();

			if (current != null && current.getPrevious() != null) {
				final BrowserInput previous = current.getPrevious();
				setToolTipText(String.format("Go back to %s", previous
						.getInputName()));
				setEnabled(true);
			} else {
				setToolTipText("");
				setEnabled(false);
			}
		}
	}

	/**
	 * Action to go forward to the next input in the hover control.
	 */
	private static final class ForwardAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public ForwardAction(final BrowserInformationControl infoControl) {
			fInfoControl = infoControl;
			setText("Next");
			final ISharedImages images = PlatformUI.getWorkbench()
					.getSharedImages();
			setImageDescriptor(images
					.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
			setDisabledImageDescriptor(images
					.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));

			update();
		}

		@Override
		public void run() {
			final BrowserInformationControlInput next = (BrowserInformationControlInput) fInfoControl
					.getInput().getNext();
			if (next != null) {
				fInfoControl.setInput(next);
			}
		}

		public void update() {
			final BrowserInformationControlInput current = fInfoControl
					.getInput();

			if (current != null && current.getNext() != null) {
				setToolTipText(String.format("Go to next %s", current.getNext()
						.getInputName()));
				setEnabled(true);
			} else {
				setToolTipText("");
				setEnabled(false);
			}
		}
	}

	/**
	 * Action that shows the current hover contents in the Javadoc view.
	 */
	private static final class ShowInEdocViewAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public ShowInEdocViewAction(final BrowserInformationControl infoControl) {
			fInfoControl = infoControl;
			setText("Show in eDoc view");
			setImageDescriptor(ErlideUIPluginImages.DESC_OBJS_EDOCTAG);
		}

		/*
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			final BrowserInformationControlInput infoInput = fInfoControl
					.getInput();
			fInfoControl.notifyDelayedInputChange(null);
			fInfoControl.dispose();
			try {
				final EdocView view = (EdocView) ErlideUIPlugin.getActivePage()
						.showView(EdocView.ID);
				// TODO view.setInput(infoInput);
				view.setText(infoInput.getHtml());
			} catch (final PartInitException e) {
				ErlLogger.error(e);
			}
		}
	}

	/**
	 * Action that opens the current hover input element.
	 * 
	 * @since 3.4
	 */
	private static final class OpenDeclarationAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public OpenDeclarationAction(final BrowserInformationControl infoControl) {
			fInfoControl = infoControl;
			setText("Open declaration");
			ErlideUIPluginImages.setLocalImageDescriptors(this,
					"goto_input.gif");
		}

		/*
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			final BrowserInformationControlInput infoInput = fInfoControl
					.getInput();
			fInfoControl.notifyDelayedInputChange(null);
			fInfoControl.dispose();
			// TODO: add hover location to editor navigation history?
			try {
				final Object element = infoInput.getInputElement();
				if (element instanceof IErlElement) {
					EditorUtility.openElementInEditor(element, true);
				} else if (element instanceof OpenResult) {
					final OpenResult or = (OpenResult) element;
					try {
						OpenAction.openOpenResult(null, null, ErlangCore
								.getBackendManager().getIdeBackend(), -1, null,
								or);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			} catch (final PartInitException e) {
				e.printStackTrace();
			} catch (final ErlModelException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected IInformationControl doCreateInformationControl(final Shell parent) {
		if (BrowserInformationControl.isAvailable(parent)) {
			final ToolBarManager tbm = new ToolBarManager(SWT.FLAT);

			final String font = JFaceResources.DIALOG_FONT;
			final BrowserInformationControl iControl = new BrowserInformationControl(
					parent, font, tbm);

			final PresenterControlCreator.BackAction backAction = new PresenterControlCreator.BackAction(
					iControl);
			backAction.setEnabled(false);
			tbm.add(backAction);
			final PresenterControlCreator.ForwardAction forwardAction = new PresenterControlCreator.ForwardAction(
					iControl);
			tbm.add(forwardAction);
			forwardAction.setEnabled(false);

			final PresenterControlCreator.ShowInEdocViewAction showInEdocViewAction = new PresenterControlCreator.ShowInEdocViewAction(
					iControl);
			tbm.add(showInEdocViewAction);
			final PresenterControlCreator.OpenDeclarationAction openDeclarationAction = new PresenterControlCreator.OpenDeclarationAction(
					iControl);
			tbm.add(openDeclarationAction);

			final SimpleSelectionProvider selectionProvider = new SimpleSelectionProvider();
			// OpenExternalBrowserAction openExternalJavadocAction = new
			// OpenExternalBrowserAction(
			// parent.getDisplay(), selectionProvider);
			// selectionProvider
			// .addSelectionChangedListener(openExternalJavadocAction);
			// selectionProvider.setSelection(new
			// StructuredSelection());
			// tbm.add(openExternalJavadocAction);

			final IInputChangedListener inputChangeListener = new IInputChangedListener() {
				public void inputChanged(final Object newInput) {
					backAction.update();
					forwardAction.update();
					if (newInput == null) {
						selectionProvider
								.setSelection(new StructuredSelection());
					} else if (newInput instanceof BrowserInformationControlInput) {
						final BrowserInformationControlInput input = (BrowserInformationControlInput) newInput;
						final Object inputElement = input.getInputElement();
						selectionProvider.setSelection(new StructuredSelection(
								inputElement));
						final boolean hasInputElement = inputElement != null;
						showInEdocViewAction.setEnabled(hasInputElement);
						openDeclarationAction.setEnabled(hasInputElement);
					}
				}
			};
			iControl.addInputChangeListener(inputChangeListener);

			tbm.update(true);

			return iControl;
		} else {
			return new DefaultInformationControl(parent, EditorsUI
					.getTooltipAffordanceString(), new ErlInformationPresenter(
					true));
		}
	}
}