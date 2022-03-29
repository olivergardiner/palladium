package uk.org.whitecottage.palladium.owl2;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;

import uk.org.whitecottage.palladium.exception.NoProfileException;
import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public class Owl2 {
    private Owl2Dialog dialog;
    private Shell s;
	protected SubMonitor monitor;

	protected Model model;
	protected Profile profile;
	protected Stereotype ignoreStereotype;
	protected Stereotype exampleStereotype;
	protected Stereotype tutorialStereotype;
	protected Stereotype documentationStereotype;
	protected List<Stereotype> ignoreStereotypes;
	protected List<Stereotype> minimalIgnoreStereotypes;

	OntModel ontology;
	
	protected Owl2(Model model, Owl2Dialog dialog, IProgressMonitor monitor, Shell s) throws InvocationTargetException {
		this.model = model;
		this.dialog = dialog;
		this.monitor = SubMonitor.convert(monitor, 100);
		this.s = s;
		
		profile = ProfileUtil.getProfile(model);
		if (profile == null) {

			Activator.logInfo("Profile error");
			throw new InvocationTargetException(new NoProfileException());
		}

		ignoreStereotype = profile.getOwnedStereotype("Ignore");
		exampleStereotype = profile.getOwnedStereotype("Example");
		tutorialStereotype = profile.getOwnedStereotype("Tutorial");
		documentationStereotype = profile.getOwnedStereotype("Documentation");
		
		ignoreStereotypes = new ArrayList<>();
		ignoreStereotypes.add(ignoreStereotype);
		ignoreStereotypes.add(exampleStereotype);
		ignoreStereotypes.add(tutorialStereotype);
		ignoreStereotypes.add(documentationStereotype);

		minimalIgnoreStereotypes = new ArrayList<>();
		minimalIgnoreStereotypes.add(ignoreStereotype);
		minimalIgnoreStereotypes.add(exampleStereotype);
	}

    public void createOwl2() throws FileNotFoundException, InvocationTargetException {
		SubMonitor task;
		
		task = monitor.split(5);
		ontology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		task.done();
		
		if (monitor.isCanceled()) {
			return;
		}
		
		s.getDisplay().readAndDispatch();
		
		task = monitor.split(90);
		buildPackage(model, task);
		
		if (monitor.isCanceled()) {
			return;
		}		
     	
		task = monitor.split(5);
	   	File output = new File(dialog.getOutputFile());
	   	Activator.logInfo("Format: " + dialog.getFormat());
    	ontology.write(new FileOutputStream(output), dialog.getFormat(), "http://www.whitecottage.org.uk/ULDM/");
		task.done();
    }
    
    protected void buildPackage(Package pkg, SubMonitor task) {
    	List<Package> childPackages = getSubPackages(pkg, ignoreStereotypes);
    	task.setWorkRemaining(1 + childPackages.size());
    	
    	// Process the package here
		Collection<Classifier> classes = getObjectsByType(pkg, UMLPackage.Literals.CLASS);

		for (Classifier c: classes) {
			if (!c.isStereotypeApplied(profile.getOwnedStereotype("ReferenceEntity"))) {
				createOwl2Class((Class) c);
			}
		}
		
    	task.worked(1);
		
		s.getDisplay().readAndDispatch();

		for (Package childPkg: childPackages) {
			buildPackage(childPkg, task.split(1));
			
			if (monitor.isCanceled()) {
				return;
			}
		}
    }
    
    protected OntClass createOwl2Class(Class c) {
    	OntClass ontClass = ontology.getOntClass(c.getQualifiedName());
    	if (ontClass != null) {
    		return ontClass;
    	}
    	
    	ontClass = ontology.createClass(c.getQualifiedName());
    	
    	for (Association a: c.getAssociations()) {
    		Property oneEnd = getOneEnd(a);
    		if (oneEnd != null && oneEnd.getType() == c) {
    			Property otherEnd = getOtherEnd(a, oneEnd);
    			if (otherEnd != null) {
    			Type targetType = otherEnd.getType();
	    			if (targetType instanceof Class) {
		    			OntClass target = createOwl2Class((Class) targetType);
		    			String qualifiedName = a.getNearestPackage().getQualifiedName() + "::has" + targetType.getName();
		        		ObjectProperty op = ontology.createObjectProperty(qualifiedName);
		        		op.addDomain(ontClass);
		        		op.addRange(target);
	    			}
    			}
    		}
    	}
    	    	
    	EList<Generalization> generalizations = c.getGeneralizations();
		if (!generalizations.isEmpty()) {
			ontClass.setSuperClass(createOwl2Class((Class) generalizations.get(0).getGeneral()));
		}
		
		return ontClass;
    }
}
