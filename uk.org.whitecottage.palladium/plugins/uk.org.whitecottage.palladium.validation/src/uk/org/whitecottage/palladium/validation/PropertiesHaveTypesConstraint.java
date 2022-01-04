package uk.org.whitecottage.palladium.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

public class PropertiesHaveTypesConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		if (!(target instanceof Classifier)) {
			return ctx.createSuccessStatus();
		}
		
		if (target instanceof Classifier) {
			StringBuilder invalidProperties = new StringBuilder();
			for (Property property: ((Classifier) target).getAttributes()) {
				Type type = property.getType();
				if (type == null) {
					if (invalidProperties.length() > 0) {
						invalidProperties.append(", ");
					}
					
					invalidProperties.append(property.getName());
				}

				if (invalidProperties.length() > 0) {
					return ctx.createFailureStatus(invalidProperties.toString(), ((Classifier) target).getName());
				}
			}
		}
		
		return ctx.createSuccessStatus();
	}

}
