 
package uk.org.whitecottage.palladium.helper;

import java.util.List;

import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;

import uk.org.whitecottage.palladium.util.handler.HandlerUtilPalladium;

public class CommentsHandler {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) {
		List<Object> selectedDiagramElements = HandlerUtilPalladium.lookupSelectedElements();
		
		if (selectedDiagramElements.size() == 1) {
			Object selectedDiagramElement = selectedDiagramElements.get(0);
			
			Element ele = null;
			Comment selected = null;
			if (selectedDiagramElement instanceof IAdaptable) {
				ele = ((IAdaptable) selectedDiagramElement).getAdapter(Element.class);
			}
			
			if (ele == null) {
				ele = Platform.getAdapterManager().getAdapter(selectedDiagramElement, Element.class);
			}
			
			if (ele instanceof Comment) {
				selected = (Comment) ele;
				ele = selected.getOwner();
			}
			
			CommentsDialog dialog = new CommentsDialog(s, ele, selected);
			if (dialog.open() == Dialog.OK) {
				IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				ServicesRegistry serviceRegistry = editor.getAdapter(ServicesRegistry.class);
				
				TransactionalEditingDomain editingDomain;
				try {
					editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
					CompoundCommand command = dialog.getAddDeleteCommand();
					editingDomain.getCommandStack().execute(command);

					command = dialog.getOrderCommand();
					editingDomain.getCommandStack().execute(command);
				} catch (ServiceException e) {
					Activator.logError(e.getMessage(), e);
				}
			}
		}
	}
		
}