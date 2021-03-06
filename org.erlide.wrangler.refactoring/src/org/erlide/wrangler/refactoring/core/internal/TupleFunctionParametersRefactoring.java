package org.erlide.wrangler.refactoring.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.erlide.wrangler.refactoring.backend.IRefactoringRpcMessage;
import org.erlide.wrangler.refactoring.backend.WranglerBackendManager;
import org.erlide.wrangler.refactoring.core.SimpleOneStepWranglerRefactoring;
import org.erlide.wrangler.refactoring.selection.IErlMemberSelection;
import org.erlide.wrangler.refactoring.selection.IErlSelection;
import org.erlide.wrangler.refactoring.selection.IErlSelection.SelectionKind;
import org.erlide.wrangler.refactoring.util.GlobalParameters;
import org.erlide.wrangler.refactoring.util.WranglerUtils;

public class TupleFunctionParametersRefactoring extends
		SimpleOneStepWranglerRefactoring {
	protected int numberOfTuplingParameters = -1;

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		IErlSelection sel = GlobalParameters.getWranglerSelection();
		if (sel instanceof IErlMemberSelection) {
			SelectionKind kind = sel.getKind();
			if (kind == SelectionKind.FUNCTION_CLAUSE
					|| kind == SelectionKind.FUNCTION) {
				IErlMemberSelection s = (IErlMemberSelection) sel;
				numberOfTuplingParameters = calculateParametersNumber(WranglerUtils
						.getTextFromEditor(s.getSelectionRange(), s
								.getDocument()));
				if (numberOfTuplingParameters > 0)
					return new RefactoringStatus();
			}
		}
		return RefactoringStatus
				.createFatalErrorStatus("Please select function parameters!");
	}

	private int calculateParametersNumber(String textFromEditor) {
		int noC = 0;
		int depth = 0;
		for (int i = 0; i < textFromEditor.length(); ++i) {
			char c = textFromEditor.charAt(i);
			switch (c) {
			case '{':
			case '(':
			case '[':
				depth++;
				break;
			case ')':
			case '}':
			case ']':
				depth--;
				break;
			case ',':
				if (depth == 0)
					noC++;
				break;
			}
		}
		if (depth == 0)
			return noC + 1;
		else
			return -1;
	}

	@Override
	public String getName() {
		return "Tuple functon parameters";
	}

	@Override
	public IRefactoringRpcMessage run(IErlSelection selection) {
		IErlMemberSelection sel = (IErlMemberSelection) selection;
		return WranglerBackendManager.getRefactoringBackend().call(
				"tuple_funpar_eclipse", "siisxi", sel.getFilePath(),
				sel.getSelectionRange().getStartLine(),
				sel.getSelectionRange().getStartCol(),
				String.valueOf(numberOfTuplingParameters), sel.getSearchPath(),
				GlobalParameters.getTabWidth());
	}

}
