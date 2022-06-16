package uk.org.whitecottage.palladium.util.emf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.emf.gmf.command.GMFtoEMFCommandWrapper;
import org.eclipse.papyrus.infra.services.edit.service.ElementEditServiceUtils;
import org.eclipse.papyrus.infra.services.edit.service.IElementEditService;
import org.eclipse.papyrus.uml.tools.commands.ApplyStereotypeCommand;
import org.eclipse.papyrus.uml.tools.commands.UnapplyStereotypeCommand;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;

import uk.org.whitecottage.palladium.util.Activator;
import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public class CommandUtil {
	
	private CommandUtil() {
		throw new IllegalStateException("Utility class");
	}

	@SuppressWarnings("unchecked")
	public static Command buildDeleteCommand(Collection<EObject> selectedElements) {

		ICommand gmfCommand = null;

		Map<Object, Object> parameters = new HashMap<>();

		for (EObject selectedEObject : selectedElements) {

			if (selectedEObject == null) {
				continue;
			}

			IElementEditService provider = ElementEditServiceUtils.getCommandProvider(selectedEObject);
			if (provider == null) {
				continue;
			}

			DestroyElementRequest request = new DestroyElementRequest(selectedEObject, false);
			request.getParameters().putAll(parameters);

			ICommand deleteCommand = provider.getEditCommand(request);

			gmfCommand = CompositeCommand.compose(gmfCommand, deleteCommand);

			parameters.clear();
			parameters.putAll(request.getParameters());
		}

		if (gmfCommand == null) {
			return UnexecutableCommand.INSTANCE;
		}

		return GMFtoEMFCommandWrapper.wrap(gmfCommand.reduce());
	}

	public static Command buildDeleteCommand(Element element) {
		IElementEditService provider = ElementEditServiceUtils.getCommandProvider(element);
		if (provider == null) {
			return null;
		}

		DestroyElementRequest request = new DestroyElementRequest(element, false);
		ICommand deleteCommand = provider.getEditCommand(request);

		return GMFtoEMFCommandWrapper.wrap(deleteCommand.reduce());
	}

	public static TransactionalEditingDomain getDomain() {
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
	
	public static Command buildUpdateCommentCommand(Comment comment, String commentBody) {
		return SetCommand.create(getDomain(), comment, UMLPackage.eINSTANCE.getComment_Body(), commentBody);
	}

	public static Command buildCreateCommentCommand(Element element, Comment comment) {
		return AddCommand.create(getDomain(), element, UMLPackage.eINSTANCE.getElement_OwnedComment(), comment);
	}
	
	public static Command buildMoveCommentCommand(Element element, Comment comment, int index) {
		return MoveCommand.create(getDomain(), element, UMLPackage.eINSTANCE.getElement_OwnedComment(), comment, index);
	}
	
	public static Command buildDocumentationCommand(Element element, Comment comment, boolean isDocumentation) {
		Profile profile = ProfileUtil.getProfile(element.getModel());
		Stereotype documentationStereotype = profile.getOwnedStereotype("Documentation");
		
		if (isDocumentation) {		
			return new ApplyStereotypeCommand(comment, documentationStereotype, getDomain());
		} else {
			return new UnapplyStereotypeCommand(comment, documentationStereotype, getDomain());
		}
	}
}
