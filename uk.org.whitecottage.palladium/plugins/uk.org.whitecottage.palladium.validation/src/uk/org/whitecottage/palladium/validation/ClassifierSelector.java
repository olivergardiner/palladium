package uk.org.whitecottage.palladium.validation;

import org.eclipse.emf.validation.model.IClientSelector;
import org.eclipse.uml2.uml.Classifier;

public class ClassifierSelector implements IClientSelector {

	@Override
	public boolean selects(Object object) {
		return object instanceof Classifier;
	}
}
