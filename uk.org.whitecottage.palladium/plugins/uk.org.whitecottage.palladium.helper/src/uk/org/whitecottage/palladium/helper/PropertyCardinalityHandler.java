 
package uk.org.whitecottage.palladium.helper;

import java.util.List;

import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;

import uk.org.whitecottage.palladium.util.handler.ElementHandler;

public class PropertyCardinalityHandler extends ElementHandler {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s, @Named("uk.org.whitecottage.palladium.commandparameter.propertycardinality") String cardinality) {
		List<Object> selectedDiagramElements = lookupSelectedElements();
		
		if (selectedDiagramElements.size() == 1) {
			Object selectedDiagramElement = selectedDiagramElements.get(0);
			
			NamedElement ele = null;
			if (selectedDiagramElement instanceof IAdaptable) {
				ele = ((IAdaptable) selectedDiagramElement).getAdapter(NamedElement.class);
			}
			
			if (ele == null) {
				ele = Platform.getAdapterManager().getAdapter(selectedDiagramElement, NamedElement.class);
			}

			if (ele instanceof Property) {
				IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				ServicesRegistry serviceRegistry = editor.getAdapter(ServicesRegistry.class);

				ValueSpecification lower = null;
				ValueSpecification upper = null;
				
				switch (cardinality) {
				case "MANDATORY":
					lower = UMLFactory.eINSTANCE.createLiteralInteger();
					((LiteralInteger) lower).setValue(1);
					upper = UMLFactory.eINSTANCE.createLiteralInteger();
					((LiteralInteger) upper).setValue(1);
					break;
				case "OPTIONAL":
					lower = UMLFactory.eINSTANCE.createLiteralInteger();
					((LiteralInteger) lower).setValue(0);
					upper = UMLFactory.eINSTANCE.createLiteralInteger();
					((LiteralInteger) upper).setValue(1);
					break;
				case "MANY":
					lower = UMLFactory.eINSTANCE.createLiteralInteger();
					((LiteralInteger) lower).setValue(0);
					upper = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
					((LiteralUnlimitedNatural) upper).setValue(LiteralUnlimitedNatural.UNLIMITED);
					break;
				case "MANY_MANDATORY":
					lower = UMLFactory.eINSTANCE.createLiteralInteger();
					((LiteralInteger) lower).setValue(1);
					upper = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
					((LiteralUnlimitedNatural) upper).setValue(LiteralUnlimitedNatural.UNLIMITED);
					break;
				default:
					break;
				}
				
				if (lower != null && upper != null) {
					try {
						TransactionalEditingDomain editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
						CompoundCommand command = new CompoundCommand();
						command.append(SetCommand.create(editingDomain, ele, UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER_VALUE, lower));
						command.append(SetCommand.create(editingDomain, ele, UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER_VALUE, upper));
						editingDomain.getCommandStack().execute(command);
					} catch (ServiceException e) {
						MessageDialog.openInformation(s, "Property Cardinality", e.toString());
					}
				}
			}
		}
	}		
}