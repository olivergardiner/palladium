 
package uk.org.whitecottage.palladium.util.handler;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


public abstract class ElementHandler {
	@SuppressWarnings("unchecked")
	protected List<Object> lookupSelectedElements() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
	
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			return structuredSelection.toList();
		} else if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			return treeSelection.toList();
		}
	
		return null;
	}
}