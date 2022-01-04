 
package uk.org.whitecottage.palladium.helper;

import java.util.List;

import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;

import uk.org.whitecottage.palladium.util.handler.ElementHandler;

public class CardinalityHandler extends ElementHandler {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s, @Named("uk.org.whitecottage.palladium.commandparameter.cardinality") String cardinality) {
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

			if (ele instanceof Association) {
				Association association = (Association) ele;
				EList<Property> ends = association.getOwnedEnds();

				if (ends.size() == 2) {
					IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
					ServicesRegistry serviceRegistry = editor.getAdapter(ServicesRegistry.class);
					
					ValueSpecification lowerSource = null;
					ValueSpecification upperSource = null;
					ValueSpecification lowerTarget = null;
					ValueSpecification upperTarget = null;
					
					switch (cardinality) {
					case "ONE_TO_MANY":
						lowerSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerSource).setValue(1);
						upperSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) upperSource).setValue(1);
						lowerTarget = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerTarget).setValue(0);
						upperTarget = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperTarget).setValue(LiteralUnlimitedNatural.UNLIMITED);
						break;
					case "OPTIONAL_ONE_TO_MANY":
						lowerSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerSource).setValue(0);
						upperSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) upperSource).setValue(1);
						lowerTarget = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerTarget).setValue(0);
						upperTarget = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperTarget).setValue(LiteralUnlimitedNatural.UNLIMITED);
						break;
					case "OPTIONAL_ONE_TO_MANY_MANDATORY":
						lowerSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerSource).setValue(0);
						upperSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) upperSource).setValue(1);
						lowerTarget = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerTarget).setValue(1);
						upperTarget = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperTarget).setValue(LiteralUnlimitedNatural.UNLIMITED);
						break;
					case "ONE_TO_MANY_MANDATORY":
						lowerSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerSource).setValue(1);
						upperSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) upperSource).setValue(1);
						lowerTarget = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerTarget).setValue(1);
						upperTarget = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperTarget).setValue(LiteralUnlimitedNatural.UNLIMITED);
						break;
					case "MANY_TO_MANY":
						lowerSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerSource).setValue(0);
						upperSource = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperSource).setValue(LiteralUnlimitedNatural.UNLIMITED);
						lowerTarget = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerTarget).setValue(0);
						upperTarget = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperTarget).setValue(LiteralUnlimitedNatural.UNLIMITED);
						break;
					case "MANDATORY_MANY_TO_MANY":
						lowerSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerSource).setValue(1);
						upperSource = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperSource).setValue(LiteralUnlimitedNatural.UNLIMITED);
						lowerTarget = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerTarget).setValue(0);
						upperTarget = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperTarget).setValue(LiteralUnlimitedNatural.UNLIMITED);
						break;
					case "MANDATORY_MANY_TO_MANY_MANDATORY":
						lowerSource = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerSource).setValue(1);
						upperSource = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperSource).setValue(LiteralUnlimitedNatural.UNLIMITED);
						lowerTarget = UMLFactory.eINSTANCE.createLiteralInteger();
						((LiteralInteger) lowerTarget).setValue(1);
						upperTarget = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
						((LiteralUnlimitedNatural) upperTarget).setValue(LiteralUnlimitedNatural.UNLIMITED);
						break;
					case "SWAP":
						lowerSource = ends.get(0).getLowerValue();					
						upperSource = ends.get(0).getUpperValue();
						lowerTarget = ends.get(1).getLowerValue();
						upperTarget = ends.get(1).getUpperValue();
						break;
					default:
						break;
					}
					
					if (lowerSource != null) {
						try {
							TransactionalEditingDomain editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
							CompoundCommand command = new CompoundCommand();
							command.append(SetCommand.create(editingDomain, ends.get(1), UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER_VALUE, lowerSource));
							command.append(SetCommand.create(editingDomain, ends.get(1), UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER_VALUE, upperSource));
							command.append(SetCommand.create(editingDomain, ends.get(0), UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER_VALUE, lowerTarget));
							command.append(SetCommand.create(editingDomain, ends.get(0), UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER_VALUE, upperTarget));
							editingDomain.getCommandStack().execute(command);
						} catch (ServiceException e) {
							MessageDialog.openInformation(s, "Cardinality", e.toString());
						}
					}
				}
			}
		}
	}
}