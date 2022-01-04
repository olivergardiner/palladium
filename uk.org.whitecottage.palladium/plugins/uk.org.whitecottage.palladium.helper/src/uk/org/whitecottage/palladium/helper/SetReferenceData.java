 
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.uml.tools.commands.ApplyStereotypeCommand;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;

import uk.org.whitecottage.palladium.util.handler.HandlerUtilPalladium;

public class SetReferenceData {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) {
		List<Object> selectedDiagramElements = HandlerUtilPalladium.lookupSelectedElements();
		
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		ServicesRegistry serviceRegistry = editor.getAdapter(ServicesRegistry.class);
		
		try {
			TransactionalEditingDomain editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
			CompoundCommand command = new CompoundCommand();

			for (Object selectedDiagramElement: selectedDiagramElements) {
				Element ele = null;
				if (selectedDiagramElement instanceof IAdaptable) {
					ele = ((IAdaptable) selectedDiagramElement).getAdapter(Element.class);
				}
				
				if (ele == null) {
					ele = Platform.getAdapterManager().getAdapter(selectedDiagramElement, Element.class);
				}
				
				if (ele instanceof Class) {
					Profile profile = ele.getModel().getAppliedProfile("ldml");
					if (profile == null) {
						profile = ele.getModel().getAppliedProfile("pldm.profile");
					}
					
					Class c = (Class) ele;
					
					if (profile != null) {
						Stereotype referenceData = profile.getOwnedStereotype("ReferenceData");
						
						if (referenceData != null) {
							EList<Stereotype> appliedStereotypes = c.getAppliedStereotypes();						
			
							if (!appliedStereotypes.contains(referenceData)) {
								// Add the Stereotype as it is not currently applied
								command.append(new ApplyStereotypeCommand(c, referenceData, editingDomain));
							}
						}
					}
				}
			}
			
			editingDomain.getCommandStack().execute(command);
		} catch (ServiceException e) {
			MessageDialog.openInformation(s, "<<ReferenceData>>", e.toString());
		}
	}	
}