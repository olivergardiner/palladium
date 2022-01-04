package uk.org.whitecottage.palladium.datavault;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getIdentifiers;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getProperties;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSubPackages;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSubTypes;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.isComplex;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.isControlledVocabulary;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.isEntity;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.isReferenceData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;

import freemarker.template.Template;

public class SnowflakeRunnable extends DataVaultRunnable {

	protected Map<String, Object> datavaultData;

	public static final String KEY_PREFERENCES = "preferences";
	public static final String KEY_HUBS = "hubs";
	public static final String KEY_LINKS = "links";
	public static final String KEY_SATELLITES = "satellites";
	public static final String KEY_TYPES = "types";
	public static final String KEY_CONTROLLED_VOCABULARIES = "controlledVocabularies";
	public static final String KEY_REFERENCE_ENTITIES = "referenceEntities";
	public static final String KEY_ENUMERATIONS = "enumerations";

	public SnowflakeRunnable(IStatusLineManager statusLineManager, DataVaultDialog dialog, Model model) {
		super(statusLineManager, dialog, model);

		datavaultData = new HashMap<>();
		datavaultData.put(KEY_HUBS, new ArrayList<Map<String, Object>>());
		datavaultData.put(KEY_LINKS, new ArrayList<Map<String, Object>>());
		datavaultData.put(KEY_SATELLITES, new ArrayList<Map<String, Object>>());
		datavaultData.put(KEY_TYPES, new ArrayList<Map<String, Object>>());
		datavaultData.put(KEY_CONTROLLED_VOCABULARIES, new ArrayList<Map<String, Object>>());
		datavaultData.put(KEY_REFERENCE_ENTITIES, new ArrayList<Map<String, Object>>());
		datavaultData.put(KEY_ENUMERATIONS, new ArrayList<Map<String, Object>>());
		
	}

	public void export(String outputFile) {
		SubMonitor task;
		
		File output = new File(outputFile);
		Path templateFile = output.getParentFile().toPath().resolve("ddl.ftl");
		
		copyPluginResource("resources/datavault/snowflake/ddl.ftl", new File(templateFile.toString()));
		
		configureFreemarker(output.getParentFile());
		
		datavaultData.put(KEY_PREFERENCES, preferences.getPreferences());
		
		task = monitor.split(5);
		
		try (FileWriter writer = new FileWriter(new File(outputFile))) {
			
			if (monitor.isCanceled()) {
				return;
			}
			
			task = monitor.split(55);
			
			processPackage(model, task);
			
			task = monitor.split(20);
			
			processAssociations(task);

			task = monitor.split(10);
			
			processReferenceData(task);
			
			task = monitor.split(10);
			
			processEnumerations(task);

			Template ddlTemplate = cfg.getTemplate("ddl.ftl");
			applyTemplate(ddlTemplate, datavaultData, output);
			
			Files.delete(templateFile);
		} catch (IOException e) {
			Activator.logError("Error writing output files", e);
		} catch (NullPointerException e) {
			Activator.logError("Error writing output files", e);
		}
						
		monitor.done();
	}

	protected void processPackage(Package parent, SubMonitor task) throws IOException {
		List<Package> subPackages = getSubPackages(parent, ignoreStereotypes);
		task.setWorkRemaining(subPackages.size() + 1);

		for (Object o: EcoreUtil.getObjectsByType(parent.getPackagedElements(), UMLPackage.Literals.CLASS)) {
			Class c = (Class) o;
			if (isEntity(c)) {
				entities.add(c);
				
				getList(KEY_HUBS).add(createHub(c));
				
				List<Map<String, Object>> satellites = getList(KEY_SATELLITES);
				List<Map<String, Object>> types = getList(KEY_TYPES);
						
				satellites.add(createSatellite(c, true, createName(c.getQualifiedName())));

				types.add(createType(c));

				for (Classifier sc: getSubTypes(c, false)) {
					entities.add(sc);
					
					if (!getProperties(sc).isEmpty()) {
						satellites.add(createSatellite(sc, true, createName(c.getQualifiedName())));
					}
					
					types.add(createType((Class)sc));
				}
				
				for (Interface i: c.allRealizedInterfaces()) {
					entities.add(i);
					
					if (!getProperties(i).isEmpty()) {
						satellites.add(createSatellite(i, true, createName(c.getQualifiedName())));
					}
				}
			}
		}
		
		task.worked(1);
		
		for (Package pkg: subPackages) {
			processPackage(pkg, task.split(1));
		}
	}
	
	private void processAssociations(SubMonitor task) throws IOException {
		task.setWorkRemaining(entities.size());
		
		List<Association> processed = new ArrayList<>();
		
		for (Classifier c: entities) {
			List<Association> associations = c.getAssociations();
			
			for (Association a: associations) {
				if (!processed.contains(a)) {
					processed.add(a);
					
					createLinks(a);
				}
			}
			
			task.worked(1);
		}
	}

	protected void processReferenceData(SubMonitor task) throws IOException {
		List<Class> processed = new ArrayList<>();
		
		while (!referenceEntities.isEmpty()) {
			Class c = referenceEntities.get(0);
			referenceEntities.remove(0);
			processed.add(c);
			
			List<Map<String, Object>> controlledVocabularies = getList(KEY_CONTROLLED_VOCABULARIES);
			
			if (preferences.isPreference(Preferences.USE_CV_TABLE) && isControlledVocabulary(c)) {
				controlledVocabularies.add(createControlledVocabulary(c));
			} else {
				createReferenceEntities(c);
			}
		}
		
		task.done();
	}

	private void processEnumerations(SubMonitor task) throws IOException {
		task.setWorkRemaining(enumerations.size());
		
		for (Enumeration e: enumerations) {
			getList(KEY_ENUMERATIONS).add(createEnumeration(e));
			
			task.worked(1);
		}
	}

	private Map<String, Object> createHub(Class c) {
		Map<String, Object> hub = new HashMap<>();
		hub.put("name", createName(c.getQualifiedName()));
		
		List<String> identifiers = new ArrayList<>();
		
		for (Property property: getIdentifiers(c)) {
			identifiers.add(property.getName());
		}
		
		hub.put("identifiers", identifiers);
	
		return hub;
	}

	private Map<String, Object> createSatellite(Classifier c, boolean isForHub, String satelliteOf) {
		Map<String, Object> satellite = new HashMap<>();
		
		satellite.put("name", createName(c.getQualifiedName()));
		satellite.put("isForHub", Boolean.valueOf(isForHub));
		satellite.put("satelliteOf", satelliteOf);
		
		List<Map<String, Object>> properties = new ArrayList<>();
		
		for (Property property: getProperties(c)) {
			Map<String, Object> propertyMap = new HashMap<>();
			
			propertyMap.put("name", property.getName());
			propertyMap.put("column", columnType(property));
			propertyMap.put("isMandatory", Boolean.valueOf(property.lowerBound() > 0));
			
			properties.add(propertyMap);
		}
		
		satellite.put("properties", properties);
		
		return satellite;
	}

	private Map<String, Object> createType(Class c) {
		Map<String, Object> type = new HashMap<>();
		
		type.put("name", c.getQualifiedName());
		type.put("key", computeHash(c.getQualifiedName()));
		type.put("description", "");
	
		Class parent = getParent(c, false);
		if (parent == null) {
			type.put("parent", "NULL");
		} else {
			type.put("parent", computeHash(parent.getQualifiedName()));
		}
	
		Class root = getParent(c, true);
		if (root == null) {
			type.put("root", "NULL");
		} else {
			type.put("root", computeHash(root.getQualifiedName()));
		}
		
		return type;
	}

	private Map<String, Object> createLink(Association a, Class from, Class to) {
		Map<String, Object> link = new HashMap<>();
		
		List<Property> ends;
		if (a.eClass() == UMLPackage.Literals.ASSOCIATION_CLASS) {
			ends = a.getMemberEnds();
		} else {
			ends = a.getOwnedEnds();
		}
		
		String column1 = ends.get(0).getType().getName();
		String column2 = ends.get(1).getType().getName();
	
		String qualifiedName = null;
		boolean isAssociationClass = false;
	
		if (a.eClass() == UMLPackage.Literals.ASSOCIATION_CLASS) {
			isAssociationClass = true;
			
			qualifiedName = a.getQualifiedName();
		} else if (a.eClass() == UMLPackage.Literals.ASSOCIATION) {
			qualifiedName = a.getPackage().getQualifiedName() + "::" + column1 + "_" + column2;
		}
	
		link.put("name", createName(qualifiedName));
		
		if (column1.equals(column2)) {
			link.put("column1Name", column1 + "_1");
			link.put("column2Name", column2 + "_2");
		} else {
			link.put("column1Name", column1);
			link.put("column2Name", column2);
		}
	
		link.put("hub1Name", createName(getAncestor(from).getQualifiedName()));
		link.put("hub2Name", createName(getAncestor(to).getQualifiedName()));
	
		link.put("isAssociationClass", Boolean.valueOf(isAssociationClass));
		
		if (isAssociationClass && !getProperties(a).isEmpty()) {
			getList("satellites").add(createSatellite(a, false, createName(qualifiedName)));
		}
	
		return link;
	}

	protected void createLinks(Association a) {
		List<Property> ends;
		List<Class> classesFrom = new ArrayList<>();
		List<Class> classesTo = new ArrayList<>();
	
		if (a.eClass() == UMLPackage.Literals.ASSOCIATION_CLASS) {
			ends = a.getMemberEnds();
		} else {
			ends = a.getOwnedEnds();
		}
		
		if (ends.size() == 2) {
			// We have an Association with two ends so we are safe to process it
			classesFrom = getClassesFromType(ends.get(0).getType());
			classesTo = getClassesFromType(ends.get(1).getType());
		}
		
		List<Map<String, Object>> links = getList("links");
		
		for (Class from: classesFrom) {
			if (entities.contains(from)) {
				for (Class to: classesTo) {
					if (entities.contains(to)) {
						links.add(createLink(a, from, to));
					}
				}
			}
		}
	}

	private Map<String, Object> createEnumeration(Enumeration e) {
		Map<String, Object> enumeration = new HashMap<>();
		
		enumeration.put("name", e.getQualifiedName());
		enumeration.put("key", computeHash(e.getQualifiedName()));
		enumeration.put("description", "");
		
		List<Map<String, Object>> values = new ArrayList<>();

		for (EnumerationLiteral literal: e.getOwnedLiterals()) {
			Map<String, Object> value = new HashMap<>();
			
			value.put("label", literal.getName());
			value.put("value", computeHash(e.getQualifiedName() + literal.getName()));
			value.put("description", "");
			
			values.add(value);
		}
		
		enumeration.put("values", values);
		
		return enumeration;
	}

	private Map<String, Object> createControlledVocabulary(Class c) {
		Map<String, Object> cv = new HashMap<>();

		cv.put("name", c.getQualifiedName());
		cv.put("key", computeHash(c.getQualifiedName()));
		cv.put("description", "");

		return cv;
	}

	private Map<String, Object> createReferenceEntity(Class c, Class root) {
		Map<String, Object> referenceEntity = new HashMap<>();

		referenceEntity.put("name", createName(c.getQualifiedName()));
		referenceEntity.put("isChild", Boolean.valueOf(root != null));
		if (root != null ) {
			referenceEntity.put("root", createName(root.getQualifiedName()));
		}
		
		List<Map<String, Object>> properties = new ArrayList<>();
		
		List<Property> allProperties = getProperties(c);
		for (InterfaceRealization r: c.getInterfaceRealizations()) {
			allProperties.addAll(r.getContract().allAttributes());
		}

		for (Property property: allProperties) {
			Map<String, Object> propertyMap = new HashMap<>();
			
			propertyMap.put("name", property.getName());
			propertyMap.put("column", columnType(property));
			propertyMap.put("isMandatory", Boolean.valueOf(property.lowerBound() > 0));
			
			properties.add(propertyMap);
		}
		
		referenceEntity.put("properties", properties);

		return referenceEntity;
	}

	private void createReferenceEntities(Class c) {
		List<Map<String, Object>> referenceList = getList(KEY_REFERENCE_ENTITIES);
		
		referenceList.add(createReferenceEntity(c, null));

		for (Classifier sc: getSubTypes(c,  false)) {
			referenceList.add(createReferenceEntity((Class) sc, c));
		}
	}

	protected Map<String, Object> columnType(Property property) {
		Map<String, Object> column = new HashMap<>();
		String columnType = "";
		Type type = property.getType();
		
		if (isReferenceData(type)) {
			addReferenceData(property);
			scanForReferenceData((Class) type, true);
		} else if (type.eClass() == UMLPackage.Literals.ENUMERATION) {
			addEnumeration((Enumeration) type);
		} else if (type.eClass() == UMLPackage.Literals.DATA_TYPE) {
			scanForReferenceData((DataType) type, true);
		}

		column.put("isReferenceData", Boolean.valueOf(false));
		
		if (property.upperBound() > 1 || property.upperBound() < 0) {
			columnType = "ARRAY"; // Array elements are of VARIANT type
		} else if (isReferenceData(type)) {
			columnType = "HASHKEY"; // Need to agree reference data identifier strategy
			column.put("isReferenceData", Boolean.valueOf(true));
			if (preferences.isPreference(Preferences.USE_CV_TABLE) && isControlledVocabulary((Class) type)) {
				column.put("reference", "ControlledVocabularyValues");
				column.put("referenceKey", "valueId");
			} else {
				column.put("reference", createName(getAncestor((Class) type).getQualifiedName()));
				column.put("referenceKey", "hashKey");
			}
		} else if (type.eClass() == UMLPackage.Literals.PRIMITIVE_TYPE) {
			columnType = type.getName();
		} else if (type.eClass() == UMLPackage.Literals.ENUMERATION) {
			columnType = "HASHKEY";
			column.put("isReferenceData", Boolean.valueOf(true));
			column.put("reference", "EnumerationValues");
			column.put("referenceKey", "valueId");
		} else if (type.eClass() == UMLPackage.Literals.DATA_TYPE) {
			if (isComplex(type)) {
				columnType = "JSON"; // JSON
			} else {
				columnType = "OBJECT"; // Objects contain key value pairs where the values are of VARIANT type
			}
		}
		
		column.put("columnType", columnType);
		
		return column;
	}

	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> getList(String key) {
		return (List<Map<String, Object>>) datavaultData.get(key);
	}
}
