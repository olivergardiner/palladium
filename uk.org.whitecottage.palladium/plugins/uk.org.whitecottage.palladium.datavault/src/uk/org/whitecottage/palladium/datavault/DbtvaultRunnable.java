package uk.org.whitecottage.palladium.datavault;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
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
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;

import freemarker.template.Template;

public class DbtvaultRunnable extends DataVaultRunnable {
	protected enum TemplateKey {
		TPL_RAW, TPL_STAGE, TPL_HUB, TPL_SATELLITE, TPL_LINK, TPL_TYPES, TPL_REF_RAW, TPL_REF_STAGE, TPL_REF_HUB, TPL_REF_SATELLITE, TPL_REF_CV, TPL_REF_ENUM
	}
	
	protected enum OutputFolderKey {
		DIR_TEMPLATE, DIR_RAW, DIR_STAGE, DIR_HUB, DIR_SATELLITE, DIR_LINK, DIR_REF_RAW, DIR_REF_STAGE, DIR_REF_HUB, DIR_REF_SATELLITE, DIR_DATA
	}

	protected Map<TemplateKey, String> templates;
	protected Map<OutputFolderKey, String> folderPaths;
	protected Map<OutputFolderKey, File> folders;
	protected static final String SQL_EXTENSION = ".sql";
	
	protected List<Map<String, Object>> types;
	
	public DbtvaultRunnable(IStatusLineManager statusLineManager, DataVaultDialog dialog, Model model) {
		super(statusLineManager, dialog, model);

		templates = new EnumMap<>(TemplateKey.class);

		templates.put(TemplateKey.TPL_RAW, "raw.ftl");
		templates.put(TemplateKey.TPL_STAGE, "stage.ftl");
		templates.put(TemplateKey.TPL_HUB, "hub.ftl");
		templates.put(TemplateKey.TPL_SATELLITE, "satellite.ftl");
		templates.put(TemplateKey.TPL_LINK, "link.ftl");
		templates.put(TemplateKey.TPL_TYPES, "types.ftl");
		templates.put(TemplateKey.TPL_REF_RAW, "ref_raw.ftl");
		templates.put(TemplateKey.TPL_REF_STAGE, "ref_stage.ftl");
		templates.put(TemplateKey.TPL_REF_HUB, "ref_hub.ftl");
		templates.put(TemplateKey.TPL_REF_SATELLITE, "ref_satellite.ftl");
		templates.put(TemplateKey.TPL_REF_CV, "ref_cv.ftl");
		templates.put(TemplateKey.TPL_REF_ENUM, "ref_enum.ftl");
		
		folderPaths = new EnumMap<>(OutputFolderKey.class);
		folders = new EnumMap<>(OutputFolderKey.class);
		
		folderPaths.put(OutputFolderKey.DIR_TEMPLATE, "/templates");
		folderPaths.put(OutputFolderKey.DIR_RAW, "/models/raw");
		folderPaths.put(OutputFolderKey.DIR_STAGE, "/models/stage");
		folderPaths.put(OutputFolderKey.DIR_HUB, "/models/vault/hubs");
		folderPaths.put(OutputFolderKey.DIR_SATELLITE, "/models/vault/satellites");
		folderPaths.put(OutputFolderKey.DIR_LINK, "/models/vault/links");
		folderPaths.put(OutputFolderKey.DIR_REF_RAW, "/models/reference_data/raw");
		folderPaths.put(OutputFolderKey.DIR_REF_STAGE, "/models/reference_data/stage");
		folderPaths.put(OutputFolderKey.DIR_REF_HUB, "/models/reference_data/vault/hubs");
		folderPaths.put(OutputFolderKey.DIR_REF_SATELLITE, "/models/reference_data/vault/satellites");
		folderPaths.put(OutputFolderKey.DIR_DATA, "/data");

		types = new ArrayList<>();
	}
	
	@Override
	protected void export(String output) {
		SubMonitor task;

		if (!configureOutputFolders(output)) {
			// Show an error message
			return;
		}
		
		task = monitor.split(40);

		processPackage(model, task);

		if (monitor.isCanceled()) {
			return;
		}

		task = monitor.split(20);

		processAssociations(task);

		if (monitor.isCanceled()) {
			return;
		}

		task = monitor.split(30);

		processReferenceData(task);

		if (monitor.isCanceled()) {
			return;
		}

		task = monitor.split(9);

		processEnumerations(task);
		
		Map<String, Object> typeData = new HashMap<>();
		typeData.put("types", types);
		applyTemplate(OutputFolderKey.DIR_DATA, TemplateKey.TPL_TYPES, typeData, "types.ndjson");
		
		monitor.done();
	}

	private void processPackage(Package parent, SubMonitor task) {
		List<Package> subPackages = getSubPackages(parent, ignoreStereotypes);
		task.setWorkRemaining(subPackages.size() + 1);

		for (Object o : EcoreUtil.getObjectsByType(parent.getPackagedElements(), UMLPackage.Literals.CLASS)) {
			Class c = (Class) o;
			if (isEntity(c)) {
				Map<String, Object> entity = createEntity(c);

				String name = "HUB_" + entity.get("name") + SQL_EXTENSION;
				applyTemplate(OutputFolderKey.DIR_HUB, TemplateKey.TPL_HUB, entity, name);
				
				name = "SAT_" + entity.get("name") + SQL_EXTENSION;
				applyTemplate(OutputFolderKey.DIR_SATELLITE, TemplateKey.TPL_SATELLITE, entity, name);
			}
		}

		task.worked(1);

		for (Package pkg : subPackages) {
			processPackage(pkg, task.split(1));
		}
	}

	private void processAssociations(SubMonitor task) {
		task.setWorkRemaining(entities.size());
		
		List<Association> processed = new ArrayList<>();
		
		for (Classifier c: entities) {
			List<Association> associations = c.getAssociations();
			
			for (Association a: associations) {
				if (!processed.contains(a) && !a.isStereotypeApplied(conceptualStereotype)) {
					processed.add(a);
					
					createLinks(a);
				}
			}
			
			task.worked(1);
		}
	}

	private void processReferenceData(SubMonitor task) {
		List<Class> processed = new ArrayList<>();
		List<Map<String, Object>> controlledVocabularies = new ArrayList<>();
		
		while (!referenceEntities.isEmpty()) {
			// Pop the next ReferenceEntity
			Class c = referenceEntities.get(0);
			referenceEntities.remove(0);
			
			// Check if we've already processed it
			if (!processed.contains(c)) {
				processed.add(c);
				
				if (preferences.isPreference(Preferences.USE_CV_TABLE) && isControlledVocabulary(c)) {
					controlledVocabularies.add(createControlledVocabulary(c));
				} else {
					Map<String, Object> referenceEntity = createReferenceEntity(c);
					
					String name = "HUB_" + referenceEntity.get("name") + SQL_EXTENSION;
					applyTemplate(OutputFolderKey.DIR_REF_HUB, TemplateKey.TPL_REF_HUB, referenceEntity, name);

					name = "SAT_" + referenceEntity.get("name") + SQL_EXTENSION;
					applyTemplate(OutputFolderKey.DIR_REF_SATELLITE, TemplateKey.TPL_REF_SATELLITE, referenceEntity, name);
				}
			}
		}
		
		if (preferences.isPreference(Preferences.USE_CV_TABLE)) {
			Map<String, Object> cvData = new HashMap<>();
			cvData.put("controlledVocabularies", controlledVocabularies);
			applyTemplate(OutputFolderKey.DIR_DATA, TemplateKey.TPL_REF_CV, cvData, "controlledVocabularies.ndjson");
		}
		
		task.done();
	}

	private void processEnumerations(SubMonitor task) {
		List<Enumeration> processed = new ArrayList<>();
		List<Map<String, Object>> enumerationData = new ArrayList<>();

		while (!enumerations.isEmpty()) {
			Enumeration e = enumerations.get(0);
			enumerations.remove(0);
			
			// Check if we've already processed it
			if (!processed.contains(e)) {
				processed.add(e);
				
				if (preferences.isPreference(Preferences.USE_ENUM_TABLE)) {
					enumerationData.add(createEnumeration(e));
				} else {
					// Should this even be an option?
				}
			}
		}
		
		if (preferences.isPreference(Preferences.USE_ENUM_TABLE)) {
			Map<String, Object> cvData = new HashMap<>();
			cvData.put("enumerations", enumerationData);
			applyTemplate(OutputFolderKey.DIR_DATA, TemplateKey.TPL_REF_ENUM, cvData, "enumerations.ndjson");
		}
		
		task.done();
	}

	private Map<String, Object> createEntity(Class c) {
		Map<String, Object> entity = new HashMap<>();
		entity.put("name", createName(c.getQualifiedName()));

		List<String> identifiers = new ArrayList<>();

		for (Property property : getIdentifiers(c)) {
			identifiers.add(property.getName());
		}

		entity.put("identifiers", identifiers);

		List<Map<String, Object>> properties = new ArrayList<>();
		List<Interface> processedInterfaces = new ArrayList<>();

		entity.put("properties", properties);
		
		addProperties(c, true, properties);
		addInterfaceProperties(c, properties, processedInterfaces);

		createType(c);
		entities.add(c);
								
		for (Classifier sc : getSubTypes(c, false)) {
			addProperties(sc, false, properties);

			createType((Class) sc);
			entities.add(sc);
			
			addInterfaceProperties((Class) sc, properties, processedInterfaces);
		}		
		
		return entity;
	}

	private void addInterfaceProperties(Class c, List<Map<String, Object>> properties, List<Interface> processed) {
		for (Interface i : getRealizedInterfaces(c)) {
			if (!processed.contains(i)) {
				processed.add(i);
				addProperties(i, false, properties);
	
				entities.add(i);
			}
		}
	}

	private void addProperties(Classifier c, boolean isHubEntity, List<Map<String, Object>> properties) {
		for (Property property : getProperties(c)) {
			Map<String, Object> propertyMap = new HashMap<>();
			
			String name = (isHubEntity) ? property.getName() : c.getName() + "_" + property.getName();

			propertyMap.put("name", name.toUpperCase());
			propertyMap.put("column", columnType(property));
			propertyMap.put("isMandatory", Boolean.valueOf(property.lowerBound() > 0));

			properties.add(propertyMap);
		}
	}

	private Map<String, Object> createType(Class c) {
		Map<String, Object> type = new HashMap<>();

		type.put("name", c.getQualifiedName());
		type.put("key", computeHash(c.getQualifiedName()));
		type.put("description", "");

		Class parent = getParent(c, false);
		if (parent != null) {
			type.put("parent", computeHash(parent.getQualifiedName()));
		}

		Class root = getParent(c, true);
		if (root != null) {
			type.put("root", computeHash(root.getQualifiedName()));
		}
		
		types.add(type);

		return type;
	}

	private Map<String, Object> createLink(Association a, Class from, Class to) {
		Map<String, Object> link = new HashMap<>();

		/*
		 List<Property> ends;
		if (a.eClass() == UMLPackage.Literals.ASSOCIATION_CLASS) {
			ends = a.getMemberEnds();
		} else {
			ends = a.getOwnedEnds();
		}

		String column1 = ends.get(0).getType().getName();
		String column2 = ends.get(1).getType().getName();
		*/
		String column1 = from.getName();
		String column2 = to.getName();

		String qualifiedName = null;
		boolean isAssociationClass = false;

		if (a.eClass() == UMLPackage.Literals.ASSOCIATION_CLASS) {
			isAssociationClass = true;

			qualifiedName = a.getQualifiedName();
		} else if (a.eClass() == UMLPackage.Literals.ASSOCIATION) {
			qualifiedName = a.getPackage().getQualifiedName() + "::" + from.getName() + "_" + to.getName();
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

		/*if (isAssociationClass && !getProperties(a).isEmpty()) {
		    getList("satellites").add(createSatellite(a, false,
		    createName(qualifiedName)));
	 	}*/

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
			classesFrom = getTargetClassesFromType(ends.get(0).getType());
			classesTo = getTargetClassesFromType(ends.get(1).getType());
		}

		for (Class from : classesFrom) {
			if (entities.contains(from)) {
				for (Class to : classesTo) {
					if (entities.contains(to)) {
						Map<String, Object> link = createLink(a, from, to);
						String name = "LNK_" + link.get("name") + SQL_EXTENSION;
						applyTemplate(OutputFolderKey.DIR_LINK, TemplateKey.TPL_LINK, link, name);
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
		
		List<EnumerationLiteral> literals = e.getOwnedLiterals();

		for (EnumerationLiteral literal : literals) {
			Map<String, Object> value = new HashMap<>();

			value.put("label", literal.getName());
			value.put("value", Integer.valueOf(literals.indexOf(literal)));
			value.put("description", "");

			values.add(value);
		}

		enumeration.put("literals", values);

		return enumeration;
	}

	private Map<String, Object> createControlledVocabulary(Class c) {
		Map<String, Object> cv = new HashMap<>();

		cv.put("name", c.getQualifiedName());
		cv.put("key", computeHash(c.getQualifiedName()));
		cv.put("description", "");

		return cv;
	}

	private Map<String, Object> createReferenceEntity(Class c) {
		Map<String, Object> referenceEntity = new HashMap<>();

		referenceEntity.put("name", createName(c.getQualifiedName()));

		List<Map<String, Object>> properties = new ArrayList<>();
		List<Interface> processedInterfaces1 = new ArrayList<>();
		List<Interface> processedInterfaces2 = new ArrayList<>();
		
		addProperties(c, true, properties);
		addAssociatedReferenceEntities(c, true, properties);
		addInterfaceProperties(c, properties, processedInterfaces1);
		addInterfaceAssociatedReferenceEntities(c, properties, processedInterfaces2);
		
		for (Classifier sc : getSubTypes(c, false)) {
			addProperties(sc, false, properties);
			addAssociatedReferenceEntities(sc, false, properties);
			addInterfaceProperties((Class) sc, properties, processedInterfaces1);
			addInterfaceAssociatedReferenceEntities((Class) sc, properties, processedInterfaces2);
		}		

		referenceEntity.put("properties", properties);

		return referenceEntity;
	}

	private void addInterfaceAssociatedReferenceEntities(Class c, List<Map<String, Object>> properties, List<Interface> processed) {
		for (Interface i : getRealizedInterfaces(c)) {
			if (!processed.contains(i)) {
				processed.add(i);
				addAssociatedReferenceEntities(i, false, properties);
			}
		}
	}

	private void addAssociatedReferenceEntities(Classifier c, boolean isHubEntity, List<Map<String, Object>> properties) {
		for (Association a: c.getAssociations()) {
			if (!a.isStereotypeApplied(conceptualStereotype)) {
				Property oneEnd = getOneEnd(a);
				if (oneEnd == null) {
					// Need to create a Link table for a many to many
				} else {				
					String propertyName = "";
					
					Property manyEnd = getOtherEnd(a, oneEnd);
					
					Type oneType = oneEnd.getType();
					Type manyType = manyEnd.getType();
					
					if (oneType == c) {
						propertyName = "parent" + c.getName();
					} else {
						propertyName = oneType.getName().substring(0, 1).toLowerCase() + oneType.getName().substring(1);
					}
					
					if (manyType == c ) {
						Map<String, Object> propertyMap = new HashMap<>();
						
						String name = (isHubEntity) ? propertyName : c.getName() + "_" + propertyName;
						propertyMap.put("name", name.toUpperCase());
						propertyMap.put("column", columnType(oneEnd));
						propertyMap.put("isMandatory", Boolean.valueOf(oneEnd.lowerBound() > 0));
		
						properties.add(propertyMap);
					}
				}
			}
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
			columnType = "VARIANT"; // Array elements are of VARIANT type
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
			columnType = "VARIANT";
		}

		column.put("columnType", columnType);

		return column;
	}
	
	protected boolean configureOutputFolders(String outputDir) {		
		Boolean dirsCreated = true;
		for (OutputFolderKey key : folderPaths.keySet()) {
			String path = folderPaths.get(key);
			File folder = new File(outputDir + path);
			dirsCreated &= folder.mkdirs();
			folders.put(key, folder);
		}
		
		File templateDir = folders.get(OutputFolderKey.DIR_TEMPLATE);
		
		for (TemplateKey key : templates.keySet()) {
			String template = templates.get(key);
			copyPluginResource("resources/datavault/dbtvault/" + template,
					templateDir.toPath().resolve(template).toFile());
		}

		configureFreemarker(templateDir);
		
		return dirsCreated;
	}
	
	protected void applyTemplate(OutputFolderKey folderKey, TemplateKey templateKey, Map<String, Object> data, String name) {
		try {
			File output = new File(folders.get(folderKey).getPath() + "/" + name);
			Template template = cfg.getTemplate(templates.get(templateKey));
			applyTemplate(template, data, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
