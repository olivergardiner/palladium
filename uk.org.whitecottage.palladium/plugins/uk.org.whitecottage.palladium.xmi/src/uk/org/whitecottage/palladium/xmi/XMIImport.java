package uk.org.whitecottage.palladium.xmi;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;
import org.w3c.dom.Document;

public abstract class XMIImport {
	protected Document document;
	protected Package pkg;
	
	protected Map<String, Class> classMap;
	
	public XMIImport() {
		classMap = new HashMap<>();
	}
	
	public abstract void importXMI(File file);

	public Command getCommand(Package targetPackage) {
		return AddCommand.create(getDomain(), targetPackage, UMLPackage.eINSTANCE.getPackage_NestedPackage(), pkg);
	}
	
	protected static TransactionalEditingDomain getDomain() {
		TransactionalEditingDomain editingDomain = null;
		
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		ServicesRegistry serviceRegistry = editor.getAdapter(ServicesRegistry.class);
		
		try {
			editingDomain = ServiceUtils.getInstance().getTransactionalEditingDomain(serviceRegistry);
		} catch (ServiceException e) {
			Activator.logError(e.getMessage(), e);
		}

		return editingDomain;
	}
}
