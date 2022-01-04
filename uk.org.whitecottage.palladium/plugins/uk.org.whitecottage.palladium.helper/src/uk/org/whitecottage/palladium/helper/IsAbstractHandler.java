package uk.org.whitecottage.palladium.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.gmfdiag.common.utils.ServiceUtilsForEditPart;
import org.eclipse.papyrus.infra.ui.util.ServiceUtilsForHandlers;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.UMLPackage;

public class IsAbstractHandler extends AbstractHandler implements ISelectionListener {

	private ISelectionService selectionService = null;

	private Element selectedElement = null;

	private List<?> selection = Collections.EMPTY_LIST;

	public IsAbstractHandler() {
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
				state = true;
			}
		}

		setBaseEnabled(state);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			selection = lookupSelectedElements(event);
			
			if (selection.size() == 1) {
				Object selectedDiagramElement = selection.get(0);
				
				NamedElement ele = null;
				if (selectedDiagramElement instanceof IAdaptable) {
					ele = ((IAdaptable) selectedDiagramElement).getAdapter(NamedElement.class);
				}
				
				if (ele == null) {
					ele = Platform.getAdapterManager().getAdapter(selectedDiagramElement, NamedElement.class);
				}

				if (ele instanceof Classifier) {
					Classifier c = (Classifier) ele;
					
					TransactionalEditingDomain editingDomain = getEditingDomain(event);
					Command command = SetCommand.create(editingDomain, c, UMLPackage.Literals.CLASSIFIER__IS_ABSTRACT, !c.isAbstract());
					editingDomain.getCommandStack().execute(command);
				}
				
			}
		} finally {
			// clear the selection
			this.selection = Collections.EMPTY_LIST;
		}

		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		if (evaluationContext instanceof IEvaluationContext) {
			Object selection = ((IEvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (selection instanceof Collection<?>) {
				this.selection = (selection instanceof List<?>) ? (List<?>) selection : new java.util.ArrayList<Object>((Collection<?>) selection);
			} else if (selection instanceof IStructuredSelection) {
				this.selection = ((IStructuredSelection) selection).toList();
			} else if (selection instanceof TreeSelection) {
				this.selection = ((TreeSelection) selection).toList();
			}
			
			if (this.selection.size() == 1) {
				Object selectedDiagramElement = this.selection.get(0);
				
				NamedElement ele = null;
				if (selectedDiagramElement instanceof IAdaptable) {
					ele = ((IAdaptable) selectedDiagramElement).getAdapter(NamedElement.class);
				}
				
				if (ele == null) {
					ele = Platform.getAdapterManager().getAdapter(selectedDiagramElement, NamedElement.class);
				}

				if (ele instanceof Classifier) {
					setBaseEnabled(true);
				}
			}
			
			this.selection = Collections.EMPTY_LIST;
		}
		super.setEnabled(evaluationContext);
	}
	
	public boolean isVisible() {
		return isEnabled();
	}

	protected TransactionalEditingDomain getEditingDomain(ExecutionEvent event) {
		try {
			return ServiceUtilsForHandlers.getInstance().getTransactionalEditingDomain(event);
		} catch (ServiceException ex) {
			return null;
		}
	}

	protected TransactionalEditingDomain getEditingDomain() {
		TransactionalEditingDomain editingDomain = null;
		for (IGraphicalEditPart editPart : getSelectedElements()) {
			try {
				editingDomain = ServiceUtilsForEditPart.getInstance().getTransactionalEditingDomain(editPart);
				if (editingDomain != null) {
					break;
				}
			} catch (ServiceException ex) {
				// Keep searching
			}
		}

		// TODO: From active editor?

		return editingDomain;
	}

	protected List<IGraphicalEditPart> getSelectedElements() {
		List<IGraphicalEditPart> result = new LinkedList<IGraphicalEditPart>();
		for (Object element : getSelection()) {
			if (element instanceof IGraphicalEditPart) {
				result.add((IGraphicalEditPart) element);
			}
		}

		return result;
	}

	protected List<?> getSelection() {
		return selection;
	}

	@SuppressWarnings("unchecked")
	protected List<Object> lookupSelectedElements(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
	
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			return structuredSelection.toList();
		} else if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			return treeSelection.toList();
		}
	
		return null;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		update(selection);
	}
}
