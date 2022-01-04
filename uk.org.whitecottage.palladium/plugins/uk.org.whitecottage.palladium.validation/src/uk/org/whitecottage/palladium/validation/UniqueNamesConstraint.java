package uk.org.whitecottage.palladium.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.UMLPackage;

public class UniqueNamesConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		if (!(target instanceof NamedElement)) {
			return ctx.createSuccessStatus();
		}
		
		String name = ((NamedElement) target).getName();
		if (name != null) {
			EObject namespace = target.eContainer();
		
			boolean found = false;
			for (Object o: EcoreUtil.getObjectsByType(namespace.eContents(), UMLPackage.Literals.NAMED_ELEMENT)) {
				if (name.equals(((NamedElement) o).getName())) {
					if (found) {
						return ctx.createFailureStatus(((NamedElement) target).getName());
					}
					
					found = true;
				}
			}
		}
		
		return ctx.createSuccessStatus();
	}

}
