package uk.org.whitecottage.palladium.helper;

import java.util.List;

import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;

/*
 * This class manages state for the Is Abstract command
 * 
 * It implements ISelectionListener so that the state of the command always reflects the abstract
 * status of the selected element (if applicable)
 */
public class IsAbstractState extends State implements ISelectionListener {

	private ISelectionService selectionService = null;

	private Element selectedElement = null;

	public IsAbstractState() {
		super();

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window != null) {
			selectionService = window.getSelectionService();
			if (selectionService != null) {
				selectionService.addSelectionListener(this);
				update(selectionService.getSelection());
			}
		}
	}

	@Override
	public void dispose() {
		if (selectionService != null) {
			selectionService.removeSelectionListener(this);
		}

		super.dispose();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		update(selection);
	}

	@SuppressWarnings("unchecked")
	private void update(ISelection selection) {
		// Default state is not canonical
		boolean state = false;

		selectedElement = null;
		
		List<Object> selectedElements = null;;

		if (selection instanceof IStructuredSelection) {
			selectedElements = ((IStructuredSelection) selection).toList();
		} else if (selection instanceof TreeSelection) {
			selectedElements = ((TreeSelection) selection).toList();
		}
		
		if (selectedElements != null && selectedElements.size() == 1) {
			Object selectedDiagramElement = selectedElements.get(0);
			
			if (selectedDiagramElement instanceof IAdaptable) {
				selectedElement = ((IAdaptable) selectedDiagramElement).getAdapter(Element.class);
			}
			
			if (selectedElement == null) {
				selectedElement = Platform.getAdapterManager().getAdapter(selectedDiagramElement, Element.class);
			}
			
			if (selectedElement instanceof Classifier) {
				state = ((Classifier) selectedElement).isAbstract();
			}
		}

		// Fires notification if changed from previous state
		setValue(state);
	}
	
	@Override
	public Object getValue() {
		if (selectedElement == null || !(selectedElement.eClass() instanceof Classifier)) {
			return false;
		}
		
		return ((Classifier) selectedElement).isAbstract();
	}
}
