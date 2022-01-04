package uk.org.whitecottage.palladium.catalogue;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.cardinality;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getQualifiedName;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSubPackages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.image.ImageFileFormat;
import org.eclipse.gmf.runtime.diagram.ui.render.util.CopyToImageUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import uk.org.whitecottage.palladium.util.papyrus.ModelUtil;

public class CatalogueJavadoc extends Catalogue {
	protected Configuration cfg;

	public CatalogueJavadoc(Model model, List<Diagram> diagrams, IProgressMonitor monitor, IEditorPart editor, Shell s) {
		super(model, diagrams, monitor, editor, s);

	}

	public void buildCatalogue() {
		File output = new File(outputFolder);
		if (!output.exists()) {
			try {
				Files.createDirectory(output.toPath());
			} catch (IOException e) {
				Activator.logError("Could not create output folder", e);
			}
		}
		
		if (isDefaultTemplate) {
			copyTemplates("platform:/plugin/uk.org.whitecottage.palladium/resources/templates/html/");
		} else {
			templateDir = new File(template);
		}

		configureFreemarker();
		
		String packagePath = "";
		String entityName = "";

		try {
			Map<String, Object> packageListData = buildPackageListData(model);
	
			Map<String, Object> allentityData = buildAllentityData(model);
			
			Map<String, Object> modelOverviewData = buildModelOverviewData(model);
			
			List<Map<String, Object>> packageData = buildPackageData(model);
			
			Activator.logInfo("Building classes");
			List<Map<String, Object>> entityData = buildEntityData(model, null, UMLPackage.Literals.CLASS);
			
			Activator.logInfo("Building interfaces");
			List<Map<String, Object>> interfaceData = buildEntityData(model, null, UMLPackage.Literals.INTERFACE);
			
			Activator.logInfo("Building data types");
			List<Map<String, Object>> dataTypeData = buildEntityData(model, null, UMLPackage.Literals.DATA_TYPE);
			
			Activator.logInfo("Building reference data");
			List<Map<String, Object>> referenceEntityData = buildEntityData(model, profile.getOwnedStereotype("ReferenceData"), UMLPackage.Literals.CLASS);
	
			Activator.logInfo("Building enumerations");
			List<Map<String, Object>> enumerationData = buildEntityData(model, null, UMLPackage.Literals.ENUMERATION);

			saveDiagrams();
			
			Template packageListTmpl = cfg.getTemplate("overview-frame.ftl");
			processTemplate(packageListTmpl, packageListData, outputFolder + "/overview-frame.html");

			
			Template packageTmpl = cfg.getTemplate("package-frame.ftl");
			processTemplate(packageTmpl, allentityData, outputFolder + "/allentities-frame.html");
			
			for (Map<String, Object> pkg: packageData) {
				packagePath = (String) pkg.get("packagePath");
				packagePath = "/" + packagePath;
				File packageDir = new File(outputFolder + packagePath);
				createFolder(packageDir);
				
				processTemplate(packageTmpl, pkg, outputFolder + packagePath + "/package-frame.html");
			}

			Template packageOverviewTmpl = cfg.getTemplate("package-overview.ftl");
			processTemplate(packageOverviewTmpl, modelOverviewData, outputFolder + "/package-overview.html");
			
			for (Map<String, Object> pkg: packageData) {
				packagePath = (String) pkg.get("packagePath");		
				packagePath = "/" + packagePath;
				processTemplate(packageOverviewTmpl, pkg, outputFolder + packagePath + "/package-overview.html");
			}

			Template entityTmpl = cfg.getTemplate("entity.ftl");

			for (Map<String, Object> entity: entityData) {
				packagePath = (String) entity.get("packagePath");
				packagePath = "/" + packagePath;
				entityName = (String) entity.get("entityName");
				processTemplate(entityTmpl, entity, outputFolder + packagePath + "/" + entityName + ".html");
			}

			for (Map<String, Object> entity: interfaceData) {
				packagePath = (String) entity.get("packagePath");
				packagePath = "/" + packagePath;
				entityName = (String) entity.get("entityName");
				processTemplate(entityTmpl, entity, outputFolder + packagePath + "/" + entityName + ".html");
			}

			for (Map<String, Object> entity: dataTypeData) {
				packagePath = (String) entity.get("packagePath");
				packagePath = "/" + packagePath;
				entityName = (String) entity.get("entityName");
				processTemplate(entityTmpl, entity, outputFolder + packagePath + "/" + entityName + ".html");
			}

			for (Map<String, Object> entity: referenceEntityData) {
				packagePath = (String) entity.get("packagePath");
				packagePath = "/" + packagePath;
				entityName = (String) entity.get("entityName");
				processTemplate(entityTmpl, entity, outputFolder + packagePath + "/" + entityName + ".html");
			}

			for (Map<String, Object> entity: enumerationData) {
				packagePath = (String) entity.get("packagePath");
				packagePath = "/" + packagePath;
				entityName = (String) entity.get("entityName");
				processTemplate(entityTmpl, entity, outputFolder + packagePath + "/" + entityName + ".html");
			}

			copyTemplateFiles(templateDir, output);
		} catch (IOException e) {
			Activator.logError("Error writing output files", e);
		} catch (Error e) {
			// Caught because otherwise they get swallowed by Eclipse!
			Activator.logError("Error thrown parsing model", e);
		}
	}
	
	protected void processTemplate(Template template, Map<String, Object> map, String path) {
		try (FileWriter writer = new FileWriter(new File(path))) {
			template.process(map, writer);
		} catch (IOException | TemplateException e) {
			Activator.logError("Error processing " + path, e);
		}
	}
	
	protected void createFolder(File folder) {
		if (!folder.exists()) {
			File parent = folder.toPath().getParent().toFile();
			createFolder(parent);
			try {
				Files.createDirectory(folder.toPath());
			} catch (IOException e) {
				Activator.logError("Could not create folder", e);
			}
		}
	}
	
	protected void copyTemplateFiles(File source, File target) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(source.toPath())) {
		    for (Path file: stream) {
		    	if (file.toFile().isDirectory()) {
		    		File newDir = new File(target.getAbsolutePath().concat(File.separator).concat(file.getFileName().toString()));
					Files.createDirectory(newDir.toPath());
					
					copyTemplateFiles(file.toFile(), newDir);
		    	} else {
		    		if (!file.getFileName().endsWith(".ftl")) {
			    		File newFile = new File(target.getAbsolutePath().concat(File.separator).concat(file.getFileName().toString()));
			    		Files.copy(file, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		    		}
		    	}
		    }
		} catch (IOException e) {
			Activator.logError("Problem copying template files", e);
		}
	}

	private void saveDiagrams() throws IOException {
		for (Diagram diagram: diagrams) {
			Package pkg = null;
			if (diagram.isSetElement() && UMLPackage.Literals.PACKAGE.isSuperTypeOf(diagram.getElement().eClass())) {
				pkg = (Package) diagram.getElement();

				String qualifiedPackageName = ModelUtil.getQualifiedName(pkg);
				String packagePath = "/" + qualifiedPackageName.replace("::", "/");
				
				CopyToImageUtil renderer = new CopyToImageUtil();
				try {
					String fileName = sanitiseFileName(diagram.getName() + ".svg");
					IPath folder = new org.eclipse.core.runtime.Path(outputFolder + packagePath);
					IPath destination = folder.append(fileName);

					if (!folder.toFile().exists()) {
						createFolder(folder.toFile());
					}

					renderer.copyToImage(diagram, destination, ImageFileFormat.SVG, new NullProgressMonitor(), PreferencesHint.USE_DEFAULTS);
				} catch (CoreException e) {
					Activator.logError("Problem rendering image", e);
				}
			}
		}
	}
	
	private String sanitiseFileName(String fileName) {
		//fileName = fileName.toLowerCase();
		fileName = fileName.replace(' ', '-');

		return fileName;
	}
	
	protected String getRootPath(String packagePath) {
		if (packagePath.isEmpty()) {
			return "";
		}
		
		String rootPath = "";
		int depth = packagePath.split("/").length + 1;
		
		while (--depth > 0) {
			rootPath += "../";
		}
		
		return rootPath;
	}

	protected void configureFreemarker() {		
		cfg = new Configuration(Configuration.VERSION_2_3_22); // This is latest version in Orbit

		try {
			// Specify the data source where the template files come from
			cfg.setDirectoryForTemplateLoading(templateDir);	
			
			// Set your preferred charset template files are stored in
			cfg.setDefaultEncoding("UTF-8");

			// Sets how errors will appear.
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

			// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
			cfg.setLogTemplateExceptions(false);

			// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
			//cfg.setWrapUncheckedExceptions(true);
			
		} catch (Exception e) {
			String stackTrace = "";
			StackTraceElement[] trace = e.getStackTrace();
			for (int i = 0; i < trace.length; i++) {
				stackTrace.concat(trace[i].toString() + "\n");
			}
			e.printStackTrace();
		}
	}

	protected Map<String, Object> buildPackageListData(Package pkg) {
		Map<String, Object> packageListData = new HashMap<>();
		
		packageListData.put("packages", buildPackageList(pkg));
		
		return packageListData;
	}
	
	protected List<Map<String, Object>> buildPackageList(Package pkg) {
		List<Map<String, Object>> packageList = new ArrayList<>();

		Activator.logInfo("Scanning Package: " + pkg.getQualifiedName());
		
		Collection<Class> classes = EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.CLASS);
		Collection<DataType> dataTypes = EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.DATA_TYPE);
		Collection<Interface> interfaces = EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.INTERFACE);

		String qualifiedPackageName = ModelUtil.getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");
		
		if (!(classes.isEmpty() && dataTypes.isEmpty() && interfaces.isEmpty())) {
			Map<String, Object> packageEntry = new HashMap<>();
			packageEntry.put("qualifiedPackageName", qualifiedPackageName);
			packageEntry.put("packagePath", packagePath);
			packageList.add(packageEntry);
		}
		
		for (Package o: getSubPackages(pkg, ignoreStereotype)) {
			packageList.addAll(buildPackageList(o));
		}

		return packageList;
	}
	
	protected Map<String, Object> buildAllentityData(Package pkg) {
		Map<String, Object> packageListData = new HashMap<>();
		
		packageListData.put("rootPath", ".");
		
		packageListData.put("entities", buildAllEntityList(pkg, null, UMLPackage.Literals.CLASS));
		
		packageListData.put("interfaces", buildAllEntityList(pkg, null, UMLPackage.Literals.INTERFACE));
		
		packageListData.put("dataTypes", buildAllEntityList(pkg, null, UMLPackage.Literals.DATA_TYPE));
		
		packageListData.put("referenceEntities", buildAllEntityList(pkg, profile.getOwnedStereotype("ReferenceData"), UMLPackage.Literals.CLASS));
				
		packageListData.put("enumerations", buildAllEntityList(pkg, null, UMLPackage.Literals.ENUMERATION));
		
		return packageListData;
	}
	
	protected List<Map<String, Object>> buildPackageData(Package pkg) {
		List<Map<String, Object>> packageData = new ArrayList<>();
		Map<String, Object> packageListData = new HashMap<>();
		
		String qualifiedPackageName = getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");

		String rootPath = getRootPath(packagePath);
		
		packageListData.put("qualifiedPackageName", qualifiedPackageName);		
		packageListData.put("packagePath", packagePath);
		packageListData.put("rootPath", rootPath);
		
		packageListData.put("entities", buildEntityList(pkg, null, UMLPackage.Literals.CLASS));
		
		packageListData.put("interfaces", buildEntityList(pkg, null, UMLPackage.Literals.INTERFACE));
		
		packageListData.put("dataTypes", buildEntityList(pkg, null, UMLPackage.Literals.DATA_TYPE));
		
		packageListData.put("referenceEntities", buildEntityList(pkg, profile.getOwnedStereotype("ReferenceData"), UMLPackage.Literals.CLASS));
		
		packageListData.put("enumerations", buildEntityList(pkg, null, UMLPackage.Literals.ENUMERATION));
		
		packageListData.put("comments", parseComments(pkg));

		for (Package o: getSubPackages(pkg, ignoreStereotype)) {
			packageData.addAll(buildPackageData(o));
		}
		
		packageData.add(packageListData);
				
		return packageData;
	}
	
	private Map<String, Object> buildModelOverviewData(Model model) {
		Map<String, Object> modelOverviewData = new HashMap<>();
		
		String qualifiedPackageName = model.getName();
		String packagePath = "";
		String rootPath="";
		
		modelOverviewData.put("qualifiedPackageName", qualifiedPackageName);		
		modelOverviewData.put("packagePath", packagePath);
		modelOverviewData.put("rootPath", rootPath);
		
		modelOverviewData.put("comments", parseComments(model));

		return modelOverviewData;
	}

	protected List<Map<String, Object>> buildAllEntityList(Package pkg, Stereotype s, EClass eClass) {
		List<Map<String, Object>> entityList = new ArrayList<>();

		entityList.addAll(buildEntityList(pkg, s, eClass));
		
		for (Package o: getSubPackages(pkg, ignoreStereotype)) {
			entityList.addAll(buildAllEntityList(o, s, eClass));
		}

		return entityList;
	}
	
	protected List<Map<String, Object>> buildEntityList(Package pkg, Stereotype s, EClass eClass) {
		List<Map<String, Object>> entityList = new ArrayList<>();

		Collection<Classifier> types = filterClasses(s, EcoreUtil.getObjectsByType(pkg.getPackagedElements(), eClass));

		String qualifiedPackageName = getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");
		
		for (Classifier c: types) {
			if (c.eClass() == eClass) {
				if ((s == null && c.getAppliedStereotypes().isEmpty()) || (s != null && c.isStereotypeApplied(s))) {
					Map<String, Object> entity = new HashMap<>();
					entity.put("entityName", c.getName());
					entity.put("packagePath", packagePath);
					if (c instanceof Classifier) {
						entity.put("isAbstract", Boolean.valueOf(((Classifier) c).isAbstract()));
					} else {
						entity.put("isAbstract", Boolean.valueOf(false));
					}
					entityList.add(entity);
				}
			}
		}

		return entityList;
	}
		
	protected List<Map<String, Object>> buildEntityData(Package pkg, Stereotype s, EClass eClass) {
		List<Map<String, Object>> entityData = new ArrayList<>();
		Collection<Classifier> classes = filterClasses(s, EcoreUtil.getObjectsByType(pkg.getPackagedElements(), eClass));

		String qualifiedPackageName = getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");

		String rootPath = getRootPath(packagePath);
		
		File packageDir = new File(outputFolder + "/" + packagePath);
		
		if (!packageDir.exists()) {
			packageDir.mkdir();
		}
		
		if (classes.size() > 0) {
			// Build the package summary page
			//renderComments(docx, pkg.getOwnedComments());
		}
		
		for (Object o: classes) {
			if ((s == null && ((Classifier) o).getAppliedStereotypes().isEmpty()) ||
					(s != null && ((Classifier) o).isStereotypeApplied(s))) {
				Map<String, Object> entity = new HashMap<>();
				entity.put("qualifiedPackageName", qualifiedPackageName);
				entity.put("packagePath", packagePath);
				entity.put("rootPath", rootPath);
				//entity.put("stereotype", s.getQualifiedName());
				//entity.put("class", s.getName());
				buildEntity(entity, (Classifier) o);
				entityData.add(entity);
			}
		}
		
		for (Package o: getSubPackages(pkg, ignoreStereotype)) {
			entityData.addAll(buildEntityData(o, s, eClass));
		}
		
		return entityData;
	}
	
	protected void buildEntity(Map<String, Object> entity, Classifier c) {
		entity.put("entityName", c.getName());
		
		Activator.logInfo("Building entity: " + c.getName());
		
		String qualifiedEntityName = getQualifiedName(c);
		String qualifiedPackageName = getQualifiedName(c.getNearestPackage());
		String packagePath = qualifiedPackageName.replace("::", "/");

		String rootPath = getRootPath(packagePath);
		entity.put("rootPath", rootPath);

		Activator.logInfo("Building hierarchy");
		List<Classifier> hierarchy = new ArrayList<>();
		if (c instanceof Classifier) {
			buildHierarchy(hierarchy, (Classifier) c);
		}
		
		Activator.logInfo("Building entity references");
		List<Map<String, String>> entityHierarchy = new ArrayList<>();
		for (Classifier cls: hierarchy) {
			entityHierarchy.add(createEntityReference(cls));
		}

		String parent = "";
		if (!hierarchy.isEmpty()) {
			parent = hierarchy.get(hierarchy.size() - 1).getName();
		}
		entity.put("parent", parent);
		
		Activator.logInfo("Adding basic details");
		Map<String, String> entityReference = new HashMap<>();
		entityReference.put("qualifiedEntityName", qualifiedEntityName);
		entityReference.put("entityPath", "");
		entityHierarchy.add(entityReference);

		entity.put("entityHierarchy", entityHierarchy);
		
		entity.put("comments", parseComments(c));
		
		entity.put("interfaces", buildInterfaceListData(c));
		
		entity.put("isAbstract", Boolean.valueOf(c.isAbstract()));
		
		entity.put("attributes", buildAttributeListData(c));
		
		entity.put("enumerationValues", buildEnumerationData(c));
		
		entity.put("associations", buildAssociationListData(c));
		
		entity.put("inheritedAttributes", buildInheritedAttributesListData(hierarchy));
		
		entity.put("implementedAttributes", buildImplementedAttributesListData(c));
		
		entity.put("inheritedAssociations", buildInheritedAssociationsListData(hierarchy));
	}
	
	protected List<Map<String, Object>> buildInheritedAssociationsListData(List<Classifier> hierarchy) {
		List<Map<String, Object>> inheritedAssociationsListData = new ArrayList<>();
		
		for (Classifier c: hierarchy) {
			Map<String, Object> inheritedAssociations = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(c);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			inheritedAssociations.put("entityName", qualifiedEntityName);
			inheritedAssociations.put("entityPath", entityPath);
			
			List<String> associations = new ArrayList<String>();
			for (Association association: c.getAssociations()) {
				EList<Property> ends = association.getMemberEnds();
				Property from;
				Property to;
				if (ends.get(0).getType().equals(c)) {
					from = ends.get(0);
					to = ends.get(1);
				} else {
					from = ends.get(1);
					to = ends.get(0);
				}
				
				String a = from.getName() + " " + to.getType().getName() + " " + cardinality(from) + " to " + cardinality(to);
				
				associations.add(a);
			}

			inheritedAssociations.put("associations", associations);
			
			inheritedAssociationsListData.add(inheritedAssociations);
		}

		return inheritedAssociationsListData;
	}
	
	protected List<Map<String, Object>> buildInheritedAttributesListData(List<Classifier> hierarchy) {
		List<Map<String, Object>> inheritedAttributeListData = new ArrayList<>();
		
		for (Classifier c: hierarchy) {
			Map<String, Object> inheritedAttributes = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(c);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			inheritedAttributes.put("entityName", qualifiedEntityName);
			inheritedAttributes.put("entityPath", entityPath);
			
			List<String> attributes = new ArrayList<String>();
			for (Property p: c.getAttributes()) {
				attributes.add(p.getName());
			}
			
			inheritedAttributes.put("attributes", attributes);
			
			inheritedAttributeListData.add(inheritedAttributes);
		}

		return inheritedAttributeListData;
	}
	
	protected List<Map<String, Object>> buildImplementedAttributesListData(Classifier c) {
		List<Map<String, Object>> implementedAttributeListData = new ArrayList<>();

		List<Classifier> hierarchy = new ArrayList<>();
		for (Interface i: c.allRealizedInterfaces()) {
			hierarchy.add(i);
			buildHierarchy(hierarchy, i);
		}
		
		for (Classifier i: hierarchy) {
			Map<String, Object> implementedAttributes = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(i);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			implementedAttributes.put("entityName", qualifiedEntityName);
			implementedAttributes.put("entityPath", entityPath);
			
			List<String> attributes = new ArrayList<>();
			for (Property p: i.getAttributes()) {
				attributes.add(p.getName());
			}
			
			implementedAttributes.put("attributes", attributes);
			
			implementedAttributeListData.add(implementedAttributes);
		}

		return implementedAttributeListData;
	}
	
	protected List<Map<String, Object>> buildAssociationListData(Classifier c) {
		List<Map<String, Object>> associationListData = new ArrayList<>();
		
		for (Association association: c.getAssociations()) {
			Map<String, Object> associationData = new HashMap<>();
			
			EList<Property> ends = association.getMemberEnds();
			Property from;
			Property to;
			if (ends.get(0).getType().equals(c)) {
				from = ends.get(0);
				to = ends.get(1);
			} else {
				from = ends.get(1);
				to = ends.get(0);
			}
			
			associationData.put("name", from.getName());
			Type toType = to.getType();
			associationData.put("type", to.getType().getName());
			String typePath = getQualifiedName(toType).replace("::", "/");
			associationData.put("typePath", typePath);
			String cardinality = cardinality(from) + " to " + cardinality(to);
			associationData.put("cardinality", cardinality);

			List<String> comments = new ArrayList<String>();
			for (Comment comment: association.getOwnedComments()) {
				comments.add(comment.getBody());
			}
			associationData.put("comments", comments);
			
			associationListData.add(associationData);
		}
		
		return associationListData;
	}
	
	protected List<Map<String, Object>> buildAttributeListData(Classifier c) {
		List<Map<String, Object>> attributeListData = new ArrayList<>();
		
		for (Property attribute: c.getAttributes()) {
			Map<String, Object> attributeData = new HashMap<>();
			attributeData.put("name", attribute.getName());
			String stereotypes = "";
			for (Stereotype s: attribute.getAppliedStereotypes()) {
				stereotypes += "&lt;&lt;" + s.getName() + "&gt;&gt;";
			}
			attributeData.put("stereotypes", stereotypes);
			String type = "NoType";
			String typePath = "";
			Type attributeType = attribute.getType();
			if (attributeType != null) {
				if (attributeType.getName() != null) {
					type = attributeType.getName();
					
					if (attributeType.isStereotypeApplied(profile.getOwnedStereotype("ReferenceData"))) {
						typePath = getQualifiedName(attributeType).replace("::", "/");
					}
				}
			}
			
			attributeData.put("type", type);
			attributeData.put("typePath", typePath);
			attributeData.put("cardinality", cardinality(attribute));
			List<String> comments = new ArrayList<>();
			for (Comment comment: attribute.getOwnedComments()) {
				comments.add(comment.getBody());
			}
			attributeData.put("comments", comments);
			
			attributeListData.add(attributeData);
		}
		
		return attributeListData;
	}

	protected List<Map<String, Object>> buildEnumerationData(Classifier c) {
		List<Map<String, Object>> enumerationData = new ArrayList<>();
		
		if (c.eClass() != UMLPackage.Literals.ENUMERATION) {
			return enumerationData;
		}
		
		Enumeration e = (Enumeration) c;
		for (EnumerationLiteral enumeration: e.getOwnedLiterals()) {
			Map<String, Object> enumerationLiteralData = new HashMap<>();
			enumerationLiteralData.put("name", enumeration.getName());
			String stereotypes = "";
			for (Stereotype s: enumeration.getAppliedStereotypes()) {
				stereotypes += "&lt;&lt;" + s.getName() + "&gt;&gt;";
			}
			enumerationLiteralData.put("stereotypes", stereotypes);

			List<String> comments = new ArrayList<>();
			for (Comment comment: enumeration.getOwnedComments()) {
				comments.add(comment.getBody());
			}
			enumerationLiteralData.put("comments", comments);
			
			enumerationData.add(enumerationLiteralData);
		}
		
		return enumerationData;
	}

	protected List<Map<String, String>> buildInterfaceListData(Classifier c) {
		List<Map<String, String>> interfaceListData = new ArrayList<>();
		
		for (Interface implementedInterface: c.allRealizedInterfaces()) {
			String qualifiedInterfaceName = getQualifiedName(implementedInterface);
			String interfacePath = qualifiedInterfaceName.replace("::", "/");

			Map<String, String> interfaceReference = new HashMap<>();
			interfaceReference.put("qualifiedInterfaceName", qualifiedInterfaceName);
			interfaceReference.put("interfacePath", interfacePath);
			
			interfaceListData.add(interfaceReference);
		}
		
		return interfaceListData;
	}

	protected void buildHierarchy(List<Classifier> hierarchy, Classifier cls) {
		if (!cls.getGeneralizations().isEmpty()) {
			for (Generalization s: cls.getGeneralizations()) {
				hierarchy.add(0, s.getGeneral());
				buildHierarchy(hierarchy, s.getGeneral());
			}
		}
	}
	
	protected Collection<Classifier> filterClasses(Stereotype stereotype, Collection<Object> classes) {
		Collection<Classifier> result = new ArrayList<>();
		for (Object o: classes) {
			Classifier c = (Classifier) o;
			if (stereotype == null || c.isStereotypeApplied(stereotype)) {
				result.add(c);
			}
		}
		
		return result;
	}

	protected Map<String, String> createEntityReference(Classifier c) {
		Map<String, String> entityReference = new HashMap<>();
		String qualifiedEntityName = getQualifiedName(c);
		entityReference.put("qualifiedEntityName", qualifiedEntityName);
		String entityPath = qualifiedEntityName.replace("::", "/");
		entityReference.put("entityPath", entityPath);
		
		return entityReference;
	}
	
	
	protected List<String> parseComments(Element e) {
		List<String> comments = new ArrayList<>();
		for (Comment comment: e.getOwnedComments()) {
			if (comment.isStereotypeApplied(documentationStereotype)) {
				String commentBody = comment.getBody();
				if (commentBody == null) {
					commentBody = "";
				}
				comments.add(processComment(commentBody));
			}
		}
		
		return comments;
	}
	
	protected String processComment(String comment) {
		Pattern tagPattern = Pattern.compile("\\$\\{(\\w*)=(.*)\\}");
		Matcher matcher = tagPattern.matcher(comment);
		if (matcher.find()) {			
			String key = matcher.group(1);
			String value = matcher.group(2);
			comment = matcher.replaceAll("");
			if ("diagram".equals(key)) {
				comment += "<br/><div class=\"diagram-div\"><embed id=\"" + sanitiseFileName(value) + "-svg\"  type=\"image/svg+xml\" class=\"diagram\" src=\"" + sanitiseFileName(value) + ".svg\" onload=\"spz('#" + sanitiseFileName(value) + "-svg')\"/></div><br/>";
			}
		}

		return comment;
	}
}
