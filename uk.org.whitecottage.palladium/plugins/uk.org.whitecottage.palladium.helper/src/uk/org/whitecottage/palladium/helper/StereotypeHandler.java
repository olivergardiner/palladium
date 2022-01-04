 
package uk.org.whitecottage.palladium.helper;

import java.util.List;

import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.uml.properties.profile.ui.dialogs.ChooseSetStereotypeDialog;
import org.eclipse.papyrus.uml.tools.commands.ApplyStereotypeCommand;
import org.eclipse.papyrus.uml.tools.commands.UnapplyStereotypeCommand;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Stereotype;

import uk.org.whitecottage.palladium.util.handler.HandlerUtilPalladium;


public class StereotypeHandler {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) {
		List<Object> selectedDiagramElements = HandlerUtilPalladium.lookupSelectedElements();
		
		if (selectedDiagramElements.size() == 1) {
			Object selectedDiagramElement = selectedDiagramElements.get(0);
			
			Element ele = null;
			if (selectedDiagramElement instanceof IAdaptable) {
				ele = ((IAdaptable) selectedDiagramElement).getAdapter(Element.class);
			}
			
			if (ele == null) {
				ele = Platform.getAdapterManager().getAdapter(selectedDiagramElement, Element.class);
			}
			
			ChooseSetStereotypeDialog dialog = new ChooseSetStereotypeDialog(s, ele);
			if (dialog.open() == Dialog.OK) {
				IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				ServicesRegistry serviceRegistry = editor.getAdapter(ServicesRegistry.class);

				@SuppressWarnings("unchecked")
				List<Stereotype> stereotypes = dialog.getSelectedElements();

				EList<Stereotype> appliedStereotypes = ele.getAppliedStereotypes();

				TransactionalEditingDomain editingDomain;
				try {
					editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
					CompoundCommand command = new CompoundCommand();
	
					for (Stereotype stereotype : stereotypes) {
						if (!appliedStereotypes.contains(stereotype)) {
							// Add the Stereotype as it is not currently applied
							command.append(new ApplyStereotypeCommand(ele, stereotype, editingDomain));
						}
					}
	
					for (Stereotype stereotype : appliedStereotypes) {
						if (!stereotypes.contains(stereotype)) {
							// Remove the Stereotype as it should no longer be applied
							command.append(new UnapplyStereotypeCommand(ele, stereotype, editingDomain));
						}
					}
					
					editingDomain.getCommandStack().execute(command);
				} catch (ServiceException e) {
					MessageDialog.openInformation(s, "Stereotype", e.toString());
				}
			}
		}
	}
}