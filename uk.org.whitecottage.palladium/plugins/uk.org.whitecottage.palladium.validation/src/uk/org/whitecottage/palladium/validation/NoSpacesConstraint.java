package uk.org.whitecottage.palladium.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.NamedElement;

public class NoSpacesConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		if (!(target instanceof NamedElement)) {
			return ctx.createSuccessStatus();
		}
		
		if (target instanceof NamedElement && !(target instanceof InstanceSpecification)) {
			String name = ((NamedElement) target).getName();
			if (name != null && name.contains(" ")) {
				return ctx.createFailureStatus(((NamedElement) target).getName());
			}
		}
		
		return ctx.createSuccessStatus();
	}
}
