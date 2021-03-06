package org.erlide.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.erlide.jinterface.backend.util.PreferencesUtils;
import org.erlide.ui.util.FolderSelectionDialog;
import org.erlide.ui.util.StatusInfo;
import org.erlide.ui.util.TypedElementSelectionValidator;
import org.erlide.ui.util.TypedViewerFilter;

public class ProjectPathEditor extends PathEditor {
	private final IProject project;
	private String fOutputLocation;
	private final String dirChooserLabelText;

	public ProjectPathEditor(final String name, final String labelText,
			final String dirChooserLabelText, final Composite parent,
			IProject project) {
		super(name, labelText, dirChooserLabelText, parent);
		this.dirChooserLabelText = dirChooserLabelText;
		this.project = project;
	}

	@Override
	protected String getNewInputObject() {
		IContainer container = chooseLocation();
		if (container != null) {
			return container.getProjectRelativePath().toString();
		}
		return null;
	}

	private IContainer chooseLocation() {
		IWorkspaceRoot root = project.getWorkspace().getRoot();
		final Class<?>[] acceptedClasses = new Class[] { IProject.class,
				IFolder.class };
		IProject[] allProjects = root.getProjects();
		List<IProject> rejectedElements = new ArrayList<IProject>(
				allProjects.length);
		for (int i = 0; i < allProjects.length; i++) {
			if (!allProjects[i].equals(project)) {
				rejectedElements.add(allProjects[i]);
			}
		}
		ViewerFilter filter = new TypedViewerFilter(acceptedClasses,
				rejectedElements.toArray());

		ILabelProvider lp = new WorkbenchLabelProvider();
		ITreeContentProvider cp = new WorkbenchContentProvider();

		IResource initSelection = null;
		if (fOutputLocation != null) {
			initSelection = root.findMember(fOutputLocation);
		}

		FolderSelectionDialog dialog = new FolderSelectionDialog(getShell(),
				lp, cp);
		dialog.setTitle(dirChooserLabelText);

		ISelectionStatusValidator validator = new ISelectionStatusValidator() {
			ISelectionStatusValidator validator = new TypedElementSelectionValidator(
					acceptedClasses, false);

			public IStatus validate(Object[] selection) {
				IStatus typedStatus = validator.validate(selection);
				if (!typedStatus.isOK()) {
					return typedStatus;
				}
				if (selection[0] instanceof IFolder) {
					IFolder folder = (IFolder) selection[0];
					// try {
					// IStatus result = ClasspathModifier
					// .checkSetOutputLocationPrecondition(
					// fEntryToEdit, folder.getFullPath(),
					// fAllowInvalidClasspath, fCPJavaProject);
					// if (result.getSeverity() == IStatus.ERROR) {
					// return result;
					// }
					// } catch (CoreException e) {
					// JavaPlugin.log(e);
					// }
					return new StatusInfo();
				} else {
					return new StatusInfo(IStatus.ERROR, "");
				}
			}
		};
		dialog.setValidator(validator);
		dialog.setMessage(getLabelText());
		dialog.addFilter(filter);
		dialog.setInput(root);
		dialog.setInitialSelection(initSelection);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

		if (dialog.open() == Window.OK) {
			return (IContainer) dialog.getFirstResult();
		}
		return null;
	}

	@Override
	protected String createList(String[] items) {
		return PreferencesUtils.packArray(items);
	}

	@Override
	protected String[] parseString(String stringList) {
		return PreferencesUtils.unpackArray(stringList);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);
		org.eclipse.swt.widgets.List list = getListControl(parent);
		GridData gd = (GridData) list.getLayoutData();
		gd.heightHint = 100;
	}
}
