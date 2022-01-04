package uk.org.whitecottage.palladium.util.papyrus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;

import uk.org.whitecottage.palladium.util.Activator;
import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public class ModelUtil {
	private ModelUtil() {
		throw new IllegalStateException("Utility class");
	}
	
	public static List<Package> getSubPackages(Package pkg) {
		return getSubPackages(pkg, new ArrayList<Stereotype>());
	}
	
	public static List<Package> getSubPackages(Package pkg, Stereotype ignoreStereotype) {
		List<Package> packageList = new ArrayList<>();
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			if (ignoreStereotype == null || !((Package) o).isStereotypeApplied(ignoreStereotype)) {
				packageList.add((Package) o);
			}
		}
		
		for (PackageImport packageImport: pkg.getPackageImports()) {
			/* 
			 * NB: When traversing PackageImports, we assume that we:
			 * a) only want top-level imports
			 * b) don't want the imported model to be represented as a node in the Package hierarchy
			 * 
			 * We achieve both of these by ensuring that, if the Package is a Model, we substitute the contained Packages for the Model
			 */
			Package importedPackage = packageImport.getImportedPackage();
			if (importedPackage instanceof Model) {
				for (Object o: EcoreUtil.getObjectsByType(importedPackage.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
					if (ignoreStereotype == null || !((Package) o).isStereotypeApplied(ignoreStereotype)) {
						packageList.add((Package) o);
					}
				}
			} else {
				packageList.add(importedPackage);
			}
		}
		
		return packageList;
	}
	
	public static List<Package> getSubPackages(Package pkg, List<Stereotype> ignoreStereotypes) {
		List<Package> packageList = new ArrayList<>();
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			addPackage(packageList, (Package) o, ignoreStereotypes);
		}
		
		for (PackageImport packageImport: pkg.getPackageImports()) {
			/* 
			 * NB: When traversing PackageImports, we assume that we:
			 * a) only want top-level imports
			 * b) don't want the imported model to be represented as a node in the Package hierarchy
			 * 
			 * We achieve both of these by ensuring that, if the Package is a Model, we substitute the contained Packages for the Model
			 */
			Package importedPackage = packageImport.getImportedPackage();
			if (importedPackage instanceof Model) {
				for (Object o: EcoreUtil.getObjectsByType(importedPackage.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
					addPackage(packageList, (Package) o, ignoreStereotypes);
				}
			} else {
				packageList.add(importedPackage);
			}
		}
		
		return packageList;
	}

	public static List<Package> getPlainSubPackages(Package pkg) {
		List<Package> packageList = new ArrayList<>();
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			if (((Package) o).getAppliedStereotypes().isEmpty()) {
				packageList.add((Package) o);
			}
		}
		
		for (PackageImport packageImport: pkg.getPackageImports()) {
			/* 
			 * NB: When traversing PackageImports, we assume that we:
			 * a) only want top-level imports
			 * b) don't want the imported model to be represented as a node in the Package hierarchy
			 * 
			 * We achieve both of these by ensuring that, if the Package is a Model, we substitute the contained Packages for the Model
			 */
			Package importedPackage = packageImport.getImportedPackage();
			if (importedPackage instanceof Model) {
				for (Object o: EcoreUtil.getObjectsByType(importedPackage.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
					if (((Package) o).getAppliedStereotypes().isEmpty()) {
						packageList.add((Package) o);
					}
				}
			} else {
				packageList.add(importedPackage);
			}
		}
		
		return packageList;
	}

	protected static void addPackage(List<Package> packageList, Package pkg, List<Stereotype> ignoreStereotypes) {
		boolean ignore = false;
		if (ignoreStereotypes != null) {
			for (Stereotype s: ignoreStereotypes) {
				if (pkg.isStereotypeApplied(s)) {
					ignore = true;
				}
			}
		}
		
		if (!ignore) {
			packageList.add(pkg);
		}
	}
	
	public static Collection<Classifier> filterClasses(Stereotype stereotype, Collection<Classifier> classes) {
		Collection<Classifier> result = new ArrayList<>();
		for (Classifier c: classes) {
			if (stereotype == null || c.isStereotypeApplied(stereotype)) {
				result.add(c);
			}
		}
		
		return result;
	}

	public static String cardinality(MultiplicityElement element) {
		String result = "";
		if (element.upperBound() == -1) {
			if (element.lowerBound() == 0) {
				result = "0..*";
			} else {
				result += element.lowerBound() + "..*";
			}
		} else {
			if (element.lowerBound() == element.upperBound()) {
				result += element.lowerBound();
			} else {
				result += element.lowerBound() + ".." + element.upperBound();
			}
		}
		
		return result;
	}
	
	public static EList<Class> getAllClasses(Stereotype stereotype, Package pkg) {
		EList<Class> result = getPackageClasses(stereotype, pkg);
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			result.addAll(getAllClasses(stereotype, (Package) o));
		}
		
		return result;
	}

	public static EList<Class> getPackageClasses(Stereotype stereotype, Package pkg) {
		EList<Class> result = new BasicEList<>();
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.CLASS)) {
			Class entity = (Class) o;
			if (stereotype == null) {
				if (entity.getAppliedStereotypes().isEmpty()) {
					result.add(entity);
				}
			} else {
				if (entity.isStereotypeApplied(stereotype)) {
					result.add(entity);
				}
			}
		}
				
		return result;
	}

	public static List<Class> getPackageClasses(Package pkg) {
		List<Class> classList = new ArrayList<>();
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.CLASS)) {
			classList.add((Class) o);
		}
		
		return classList;
	}

	public static boolean isModel(Package pkg) {
		return pkg instanceof Model;
	}
	
	public static String getQualifiedName(NamedElement element) {
		if (element == null) {
			return "";
		}
		
		String qualifiedName = element.getQualifiedName();
		
		if (qualifiedName != null && qualifiedName.contains("::")) {
			return qualifiedName.substring(qualifiedName.indexOf("::") + 2);
		}
		
		return "";
	}
	
	public static String getQualifiedName(NamedElement element, Element relativeTo) {
		if (element.getNearestPackage() == relativeTo.getNearestPackage()) {
			return element.getName();
		}
		
		String qualifiedName = element.getQualifiedName();
		return qualifiedName.substring(qualifiedName.indexOf("::") + 2);
	}
	
	public static List<Package> findPackages(Package pkg, Stereotype s) {
		List<Package> packages = new ArrayList<>();
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			Package subPackage = (Package) o;
			if (subPackage.isStereotypeApplied(s)) {
				packages.add(subPackage);
			} else {
				packages.addAll(findPackages(subPackage, s));
			}
		}
		
		return packages;
	}

	public static String camelCaseToSpaces(String camelCase) {
		String c = camelCase.substring(0, 1);
		String c1 = camelCase.substring(1, 2);
		
		boolean isUpper = c.matches("[A-Z]");
		
		boolean sequence = (isUpper && c1.matches("[A-Z]"));
		
		int checkLength = camelCase.length() - 1;
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < camelCase.length(); i++) {
			c = camelCase.substring(i, i + 1);
			if (sequence) {
				if (!isUpper) {
					c = c.toUpperCase();
					if (i < checkLength) {
						c1 = camelCase.substring(i + 1, i + 2);
						if (c1.matches("[A-Z]")) {
							sequence = false;
							isUpper = true;
						}
					}
				} else {
					if (i < checkLength && i > 0) {
						c1 = camelCase.substring(i + 1, i + 2);
						if (c1.matches("[a-z]")) {
							builder.append(" ");
							sequence = false;
						}
					}
				}
				
				builder.append(c);
			} else {
				if (c.matches("[A-Z]") && i > 0) {
					builder.append(" ");
					if (i < checkLength) {
						c1 = camelCase.substring(i + 1, i + 2);
						sequence = c1.matches("[A-Z]");
					}
				}
				
				builder.append(c);
			}
		}
		
		return builder.toString();
	}
	
	// TODO: Consider refactoring code to use the more general methods for Classifier and deprecate
	public static List<Class> getSuperClasses(Class c) {
		List<Class> superClasses = new ArrayList<>();
		
		for (Class sc: c.getSuperClasses()) {
			superClasses.add(sc);
			
			superClasses.addAll(getSuperClasses(sc));
		}
		
		return superClasses;
	}

	// TODO: Consider refactoring code to use the more general methods for Classifier and deprecate
	public static List<Class> getSubClasses(Class c) {
		List<Class> subClasses = new ArrayList<>();
		
		Model model = c.getModel();
		getSubClasses(c, model, subClasses);
				
		return subClasses;
	}

	// TODO: Consider refactoring code to use the more general methods for Classifier and deprecate
	protected static void getSubClasses(Class c, Package pkg, List<Class> subClasses) {
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.CLASS)) {
			Class sc = (Class) o;
			if (getSuperClasses(sc).contains(c)) {
				subClasses.add(sc);
			}
		}
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			getSubClasses(c, (Package) o, subClasses);
		}
	}
	
	public static List<Classifier> getSuperClassifiers(Classifier c) {
		List<Classifier> superClassifiers = new ArrayList<>();
		
		for (Generalization g: c.getGeneralizations()) {
			Classifier sc = g.getGeneral();
			superClassifiers.add(sc);
			
			superClassifiers.addAll(getSuperClassifiers(sc));
		}
		
		return superClassifiers;
	}

	public static List<Classifier> getSubClassifiers(Classifier c) {
		List<Classifier> subClassifiers = new ArrayList<>();
		
		Model model = c.getModel();
		getSubClassifiers(c, model, subClassifiers);
				
		return subClassifiers;
	}

	protected static void getSubClassifiers(Classifier c, Package pkg, List<Classifier> subClassifiers) {
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.CLASSIFIER)) {
			Classifier sc = (Classifier) o;
			if (getSuperClassifiers(sc).contains(c)) {
				subClassifiers.add(sc);
			}
		}
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			getSubClassifiers(c, (Package) o, subClassifiers);
		}
	}
	
	public static boolean isEntity(Classifier c) {
		EList<Generalization> generalizations = c.getGeneralizations();
		EList<Stereotype> stereotypes = c.getAppliedStereotypes();
		List<Property> identifiers = getIdentifiers(c);
		
		return c instanceof Class && 
				generalizations.isEmpty() &&
				stereotypes.isEmpty() &&
				!identifiers.isEmpty();	
	}

	public static boolean isReferenceData(Property p) {
		return isReferenceData(p.getType());	
	}

	public static boolean isReferenceData(Type t) {
		if (t.eClass() != UMLPackage.Literals.CLASS) {
			return false;
		}
		
		Profile profile = ProfileUtil.getProfile(t.getModel());
		if (profile == null) {
			Activator.logInfo("No profile for type:" + t.getQualifiedName());
		}

		Stereotype referenceData = profile.getOwnedStereotype("ReferenceData");

		return t.eClass() == UMLPackage.Literals.CLASS && t.isStereotypeApplied(referenceData);	
	}

	public static boolean isControlledVocabulary(Class c) {
		Profile profile = ProfileUtil.getProfile(c.getModel());
		
		Stereotype referenceLabel = profile.getOwnedStereotype("ReferenceLabel");
		Stereotype referenceCode = profile.getOwnedStereotype("ReferenceCode");
		
		// If there are *any* sub-classes then we will need a full table and cannot use the CV table
		if (!getSubClasses(c).isEmpty()) {
			return false;
		}
		
		List<Property> properties = new ArrayList<>();
		properties.addAll(getAllAttributes(c));
		
		// Check that there is a name property designated as the <<ReferenceLabel>>
		Property name = findAttribute(properties, "name");
		if (name == null || !name.isStereotypeApplied(referenceLabel)) {
			return false;
		}
		properties.remove(name);
		
		// Check that there is a value property designated as the <<ReferenceCode>>
		Property value = findAttribute(properties, "value");
		if (value == null) {
			value = findAttribute(properties, "code");
		}
		if (value == null || !value.isStereotypeApplied(referenceCode)) {
			return false;
		}
		properties.remove(value);
		
		// Check that there is a description property
		Property description = findAttribute(properties, "description");
		if (description == null) {
			return false;
		}
		properties.remove(description);
		
		// Check that there are no other properties
		return properties.isEmpty();	
	}
	
	public static Collection<Property> getAllAttributes(Class c) {
		List<Property> properties = new ArrayList<>();
		
		properties.addAll(c.getAttributes());
		
		for (InterfaceRealization r: c.getInterfaceRealizations()) {
			Interface i = r.getContract();
			properties.addAll(i.getAttributes());
			
			for (Classifier si: getSuperClassifiers(i)) {
				properties.addAll(si.getAttributes());
			}
			
			for (Classifier si: getSubClassifiers(i)) {
				properties.addAll(si.getAttributes());
			}
		}
		
		return properties;
	}

	protected static Property findAttribute(List<Property> properties, String name) {
		for (Property p: properties) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		
		return null;
	}

	public static boolean isComplex(Type t) {
		
		Profile profile = ProfileUtil.getProfile(t.getModel());
		
		Stereotype complex = profile.getOwnedStereotype("Complex");

		return t.eClass() == UMLPackage.Literals.DATA_TYPE && t.isStereotypeApplied(complex);	
	}

	public static List<Property> getIdentifiers(Classifier c) {
		List<Property> identifiers = new ArrayList<>();
		
		Profile profile = ProfileUtil.getProfile(c.getModel());
		
		Stereotype identifier = profile.getOwnedStereotype("Identifier");
		Stereotype containment = profile.getOwnedStereotype("Containment");
		
		for (Property p: c.getAttributes()) {
			if (p.isStereotypeApplied(identifier)) {
				identifiers.add(p);
			}
		}
		
		// Now need to scan Associations for a <<Containment>> relationship
		for (Association a: c.getAssociations()) {
			if (a.isStereotypeApplied(containment)) {
				// If we have a <<Containment>> relationship, we first need to find the far end
				EList<Property> ends = a.getMemberEnds();
				Property to;
				if (ends.get(0).getType().equals(c)) {
					to = ends.get(1);
				} else {
					to = ends.get(0);
				}
				
				// If the far end has a multiplicity of one, then we need to include its identifiers
				if (to.upperBound() == 1 && to instanceof Classifier) {
					// Could potentially check for the absence of a declared identifier and create an "xxxNumber" identifier automatically
					
					identifiers.addAll(getIdentifiers((Classifier) to.getType()));
				}
			}
		}
		
		return identifiers;
	}
	
	public static List<Property> getProperties(Classifier c) {
		List<Property> properties = new ArrayList<>();
		
		Profile profile = ProfileUtil.getProfile(c.getModel());
		
		Stereotype identifier = profile.getOwnedStereotype("Identifier");
		
		for (Property p: c.getAttributes()) {
			if (!p.isStereotypeApplied(identifier)) {
				properties.add(p);
			}
		}
		
		return properties;
	}

	public static List<Classifier> getSuperTypes(Classifier c, boolean direct) {
		List<Classifier> superClasses = new ArrayList<>();
		
		for (Classifier sc: c.getGenerals()) {
			superClasses.add(sc);
			
			if (!direct) {
				superClasses.addAll(getSuperTypes(sc, direct));
			}
		}
		
		return superClasses;
	}

	public static List<Classifier> getSubTypes(Classifier c, boolean direct) {
		List<Classifier> subClasses = new ArrayList<>();
		
		Model model = c.getModel();
		getSubTypes(c, model, subClasses, direct);
				
		return subClasses;
	}

	protected static void getSubTypes(Classifier c, Package pkg, List<Classifier> subClasses, boolean direct) {
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.CLASSIFIER)) {
			Classifier sc = (Classifier) o;
			if (getSuperTypes(sc, direct).contains(c)) {
				subClasses.add(sc);
			}
		}
		
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
			getSubTypes(c, (Package) o, subClasses, direct);
		}
	}
	
	public static Classifier getRoot(Classifier c) {
		EList<Classifier> generals = c.getGenerals();
		if (generals.isEmpty()) {
			return c;
		} else {
			return getRoot(generals.get(0));
		}		
	}
	
	public static NamedElement findElement(Element element, String qualifiedName) {
		Package start = null;
		
		if (qualifiedName.startsWith("/")) {
			start = element.getModel();
			qualifiedName = qualifiedName.substring(1);
		} else {
			start = element.getNearestPackage();
		}
		
		List<String> name = Arrays.asList(qualifiedName.split("/"));
		
		return findElement(start, name, 0);
	}
	
	protected static NamedElement findElement(Package pkg, List<String> name, int index) {
		if ((name.size() - index) > 1) {
			if (name.get(index).equals("..")) {
				Element parent = pkg.getOwner();
				if (parent instanceof Package) {
					return findElement((Package) parent, name, index + 1);
				}
			} else if (name.get(index).equals(".")) {
				return findElement(pkg, name, index + 1);
			} else {
				for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.PACKAGE)) {
					if (((Package) o).getName().equals(name.get(index))) {
						return findElement((Package) o, name, index + 1);
					}
				}
			}
		} else if ((name.size() - index) == 1) {
			for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.NAMED_ELEMENT)) {
				if (((NamedElement) o).getName().equals(name.get(index))) {
					return (NamedElement) o;
				}
			}
		}
		
		return null;
	}
	
	public static Property getOneEnd(Association a) {
		if (a instanceof AssociationClass) {
			return null;
		}
		
		for (Property p: a.getOwnedEnds()) {
			if (!p.isMultivalued()) {
				return p;
			}
		}
		
		return null;
	}
	
	public static Property getOtherEnd(Association a, Property end) {
		List<Property> ends;
		
		if (a instanceof AssociationClass) {
			ends = a.getMemberEnds();
		} else {
			ends = a.getOwnedEnds();
		}
		
		for (Property p: ends) {
			if (p != end) {
				return p;
			}
		}
		
		return null;
	}
}
