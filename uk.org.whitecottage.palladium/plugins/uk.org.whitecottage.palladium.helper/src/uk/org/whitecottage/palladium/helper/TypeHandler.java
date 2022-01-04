 
package uk.org.whitecottage.palladium.helper;

import java.util.List;

import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.widgets.editors.TreeSelectorDialog;
import org.eclipse.papyrus.uml.tools.providers.UMLContentProvider;
import org.eclipse.papyrus.uml.tools.providers.UMLLabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;

import uk.org.whitecottage.palladium.util.handler.HandlerUtilPalladium;

public class TypeHandler {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) {
		List<Object> selectedDiagramElements = HandlerUtilPalladium.lookupSelectedElements();
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		ServicesRegistry serviceRegistry = editor.getAdapter(ServicesRegistry.class);
		
		Element ele = getElement(selectedDiagramElements.get(0));
		Model model = ele.getModel();
		
		CustomUMLContentProvider provider = new CustomUMLContentProvider(model, UMLPackage.eINSTANCE.getPackage_PackagedElement());
		TreeSelectorDialog dialog = new TreeSelectorDialog(s);
		dialog.setContentProvider(provider);
		dialog.setLabelProvider(new UMLLabelProvider());

		if (dialog.open() == Window.OK) {
			CompoundCommand command = new CompoundCommand();

			try {
				TransactionalEditingDomain editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
				
				for (Object selectedDiagramElement: selectedDiagramElements) {
					ele = getElement(selectedDiagramElement);
					
					EObject type = (EObject) (dialog.getResult()[0]);
					command.append(SetCommand.create(editingDomain, ele, UMLPackage.Literals.TYPED_ELEMENT__TYPE, type));
				}

				editingDomain.getCommandStack().execute(command);
			} catch (ServiceException e) {
				MessageDialog.openInformation(s, "Set Type: ", e.toString());
			}
		}
	}
	
	protected Element getElement(Object selectedDiagramElement) {
		Element ele = null;
		if (selectedDiagramElement instanceof IAdaptable) {
			ele = ((IAdaptable) selectedDiagramElement).getAdapter(Element.class);
		}
		
		if (ele == null) {
			ele = Platform.getAdapterManager().getAdapter(selectedDiagramElement, Element.class);
		}
		
		return ele;
	}

	public class CustomUMLContentProvider extends UMLContentProvider {

		public CustomUMLContentProvider() {
			super();
		}

		public CustomUMLContentProvider(EObject source, EStructuralFeature feature, Stereotype stereotype, ResourceSet root) {
			super(source, feature, stereotype, root);
		}

		public CustomUMLContentProvider(EObject source, EStructuralFeature feature, Stereotype stereotype) {
			super(source, feature, stereotype);
		}

		public CustomUMLContentProvider(EObject source, EStructuralFeature feature) {
			super(source, feature);
		}

		/**
		 * Check whether a child belongs to the given parent, i.e. is owned by it.
		 *
		 * @param parent
		 *            a parent
		 * @param child
		 *            a child
		 * @return true, if owned
		 */
		public boolean isOwned(Object parent, EObject child) {
			child = child.eContainer();
			while (child != null) {
				if (child == parent) {
					return true;
				}
				child = child.eContainer();
			}
			return false;
		}
	}
}