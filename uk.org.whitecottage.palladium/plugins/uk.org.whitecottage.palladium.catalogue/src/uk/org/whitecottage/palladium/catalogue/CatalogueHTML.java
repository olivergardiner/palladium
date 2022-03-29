package uk.org.whitecottage.palladium.catalogue;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.image.ImageFileFormat;
import org.eclipse.gmf.runtime.diagram.ui.render.util.CopyToImageUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
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
import org.eclipse.uml2.uml.NamedElement;
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
import uk.org.whitecottage.palladium.util.problems.Problem;

public class CatalogueHTML extends Catalogue {
	protected Configuration cfg;

	public CatalogueHTML(Model model, List<Diagram> diagrams, IProgressMonitor monitor, IEditorPart editor, Shell s) {
		super(model, diagrams, monitor, editor, s);
	}

	public void buildCatalogue() {
		SubMonitor task;
		task = monitor.split(5);
		task.setWorkRemaining(4);
		
		File output = new File(outputFolder);
		if (!output.exists()) {
			try {
				Files.createDirectory(output.toPath());
			} catch (IOException e) {
				Activator.logError("Could not create output folder", e);
			}
		}
		
		if (monitor.isCanceled()) {
			return;
		}
		
		task.worked(1);
		s.getDisplay().readAndDispatch();
		
		if (isDefaultTemplate) {
			templateDir = copyTemplates("platform:/plugin/uk.org.whitecottage.palladium.catalogue/resources/templates/html/");
		} else {
			templateDir = new File(template);
		}
		
		if (monitor.isCanceled()) {
			return;
		}
		
		task.worked(1);
		s.getDisplay().readAndDispatch();

		if (editor != null) {
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile resource = ((IFileEditorInput) input).getFile();
				
				String source = resource.getProject().getRawLocationURI().toString();
				
				copyResources(source + "/content", "_content");
			}
		}
		
		if (monitor.isCanceled()) {
			return;
		}

		task.worked(1);
		s.getDisplay().readAndDispatch();

		configureFreemarker();
		
		if (monitor.isCanceled()) {
			return;
		}
		
		task.worked(1);
		s.getDisplay().readAndDispatch();

		String packagePath = "";

		try {
			task = monitor.split(5);
			
			Map<String, Object> modelOverviewData = buildModelOverviewData(model);
			
			Template packageOverviewTmpl = cfg.getTemplate("package.ftl");
			processTemplate(packageOverviewTmpl, modelOverviewData, outputFolder + "/package.html");
			
			if (monitor.isCanceled()) {
				return;
			}
			
			task = monitor.split(5);
			s.getDisplay().readAndDispatch();
			
			Map<String, Object> modelTreeData = buildModelTree(model);
			
			Template indexTmpl = cfg.getTemplate("index.ftl");
			processTemplate(indexTmpl, modelTreeData, outputFolder + "/index.html");
			
			if (monitor.isCanceled()) {
				return;
			}
			
			task = monitor.split(5);
			s.getDisplay().readAndDispatch();
			
			List<Map<String, Object>> packageData = buildPackageData(model, true);

			task.setWorkRemaining(packageData.size());

			for (Map<String, Object> pkg: packageData) {
				packagePath = (String) pkg.get("packagePath");		
				packagePath = "/" + packagePath;
				File folder = new File(outputFolder + packagePath);
				createFolder(folder);
				processTemplate(packageOverviewTmpl, pkg, outputFolder + packagePath + "/package.html");
				
				task.worked(1);
				s.getDisplay().readAndDispatch();
			}
			
			if (monitor.isCanceled()) {
				return;
			}

			task = monitor.split(5);
			s.getDisplay().readAndDispatch();
			
			Template entityTmpl = cfg.getTemplate("entity.ftl");

			Activator.logInfo("Building entities");
			processClassifier(buildEntityData(model, null, UMLPackage.Literals.CLASS, "Entity"), entityTmpl, "E_");
			
			if (monitor.isCanceled()) {
				return;
			}

			task = monitor.split(5);
			s.getDisplay().readAndDispatch();
						
			Activator.logInfo("Building interfaces");
			processClassifier(buildEntityData(model, null, UMLPackage.Literals.INTERFACE, "Interface"), entityTmpl, "I_");
			
			if (monitor.isCanceled()) {
				return;
			}

			task = monitor.split(5);
			s.getDisplay().readAndDispatch();
			
			Activator.logInfo("Building data types");
			processClassifier(buildEntityData(model, null, UMLPackage.Literals.DATA_TYPE, "DataType"), entityTmpl, "D_");
			
			if (monitor.isCanceled()) {
				return;
			}
			
			task = monitor.split(5);
			s.getDisplay().readAndDispatch();
			
			Activator.logInfo("Building reference data");
			processClassifier(buildEntityData(model, profile.getOwnedStereotype("ReferenceData"), UMLPackage.Literals.CLASS, "ReferenceEntity"), entityTmpl, "R_");
			
			if (monitor.isCanceled()) {
				return;
			}

			task = monitor.split(5);
			s.getDisplay().readAndDispatch();
			
			Activator.logInfo("Building enumerations");
			processClassifier(buildEntityData(model, null, UMLPackage.Literals.ENUMERATION, "Enumeration"), entityTmpl, "L_");

			saveDiagrams(monitor.split(55));
			
			copyTemplateFiles(templateDir, output);

			s.getDisplay().readAndDispatch();
		} catch (IOException e) {
			Activator.logError("Error writing output files", e);
		} catch (Error e) {
			// Caught because otherwise they get swallowed by Eclipse!
			Activator.logError("Error thrown parsing model", e);
		}
	}
	
	protected void processClassifier(List<Map<String, Object>> classifierData, Template tmpl, String prefix) {
		String packagePath;
		String classifierName;

		for (Map<String, Object> classifier: classifierData) {
			packagePath = (String) classifier.get("packagePath");
			packagePath = "/" + packagePath;
			classifierName = (String) classifier.get("entityName");
			processTemplate(tmpl, classifier, outputFolder + packagePath + "/" + prefix + classifierName + ".html");
		}
	}
	
	protected Map<String, Object> buildModelTree(Model model) {
		Map<String, Object> modelTreeData = new HashMap<>();
		
		modelTreeData.put("name", model.getName());
		
		List<Map<String, Object>> packageList = new ArrayList<>();
		
		for (Package pkg: getSubPackages(model, ignoreStereotypes)) {
			packageList.add(buildPackage(pkg, false));
		}
		
		modelTreeData.put("packages", packageList);

		modelTreeData.put("documentation", getDocumentation(model));

		modelTreeData.put("tutorials", getTutorials(model));

		return modelTreeData;
	}
	
	protected List<Map<String, Object>> getDocumentation(Model model) {
		List<Map<String, Object>> documentation = new ArrayList<>();
		
		List<Package> documentationList = findPackages(model, documentationStereotype);
		for (Package doc: documentationList) {
			documentation.add(buildPackage(doc, true));
		}
		
		return documentation;
	}	
	
	protected List<Map<String, Object>> getTutorials(Model model) {
		List<Map<String, Object>> tutorials = new ArrayList<>();
				
		List<Package> tutorialList = findPackages(model, tutorialStereotype);
		for (Package tutorial: tutorialList) {
			tutorials.add(buildTutorialPackage(tutorial));
		}
		
		return tutorials;
	}
	
	protected Map<String, Object> buildPackage(Package pkg, boolean convert) {
		Map<String, Object> packageData = new HashMap<>();
		
		String name = pkg.getName();
		if (convert) {
			String label = pkg.getLabel();
			if (!label.equals(name)) {
				name = label;
			} else {
				name = camelCaseToSpaces(name);
			}
		}
		
		packageData.put("name", name);

		String qualifiedPackageName = getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");
		
		packageData.put("path", packagePath);
		
		List<Map<String, Object>> entities = buildTypeList(pkg, null, UMLPackage.Literals.CLASS);
		packageData.put("entities", entities);

		List<Map<String, Object>> interfaces = buildTypeList(pkg, null, UMLPackage.Literals.INTERFACE);
		packageData.put("interfaces", interfaces);

		List<Map<String, Object>> dataTypes = buildTypeList(pkg, null, UMLPackage.Literals.DATA_TYPE);
		packageData.put("data_types", dataTypes);

		List<Map<String, Object>> referenceEntities = buildTypeList(pkg, profile.getOwnedStereotype("ReferenceData"), UMLPackage.Literals.CLASS);
		packageData.put("reference_entities", referenceEntities);

		List<Map<String, Object>> enumerations = buildTypeList(pkg, null, UMLPackage.Literals.ENUMERATION);
		packageData.put("enumerations", enumerations);

		List<Map<String, Object>> packageList = new ArrayList<>();
		
		for (Package childPkg: getSubPackages(pkg, minimalIgnoreStereotypes)) {
			packageList.add(buildPackage(childPkg, convert));
		}

		packageData.put("packages", packageList);
		
		int contents = entities.size() + interfaces.size() + dataTypes.size() + referenceEntities.size() + enumerations.size() + packageList.size();
		packageData.put("contents", contents);

		return packageData;
	}

	protected Map<String, Object> buildTutorialPackage(Package pkg) {
		Map<String, Object> packageData = new HashMap<>();
		
		String name = pkg.getName();
		String label = pkg.getLabel();
 		if (!label.equals(name)) {
			name = label;
		} else {
			name = camelCaseToSpaces(name);
		}
		
		packageData.put("name", name);

		String qualifiedPackageName = getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");
		
		packageData.put("path", packagePath);

		List<Map<String, Object>> packageList = new ArrayList<>();
		
		for (Package childPkg: getSubPackages(pkg, minimalIgnoreStereotypes)) {
			packageList.add(buildTutorialPackage(childPkg));
		}

		packageData.put("packages", packageList);
		
		packageData.put("contents", packageList.size());
		
		return packageData;
	}
	
	protected List<Map<String, Object>> buildTypeList(Package pkg, Stereotype s, EClass eClass) {
		List<Map<String, Object>> typeList = new ArrayList<>();

		Collection<Classifier> classes = filterClasses(s, getObjectsByType(pkg, eClass));
		for (Classifier c: classes) {
			if ((s == null && c.getAppliedStereotypes().isEmpty()) || (s != null && c.isStereotypeApplied(s))) {
				Map<String, Object> type = new HashMap<>();
				type.put("name", c.getName());
				typeList.add(type);
			}
		}
		
		return typeList;
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
		    		try {
		    			Files.createDirectory(newDir.toPath());
		    		} catch (FileAlreadyExistsException e) {
		    			// Silently ignore as this is not a problem
		    		}
					
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

	private void saveDiagrams(SubMonitor monitor) throws IOException {
		monitor.setWorkRemaining(diagrams.size());
		
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
			
			monitor.worked(1);
		}
	}
	
	private String sanitiseFileName(String fileName) {
		//fileName = fileName.toLowerCase();
		fileName = fileName.replace(' ', '-');
		//fileName = fileName.replace('/', '-');

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
		cfg = new Configuration(Configuration.VERSION_2_3_22); // Latest version in Orbit (was 2.3.28)

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
		
		for (Package o: getSubPackages(pkg)) {
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
	
	protected List<Map<String, Object>> buildPackageData(Package pkg, boolean isModel) {
		List<Map<String, Object>> packageData = new ArrayList<>();
		Map<String, Object> packageListData = new HashMap<>();
		
		String qualifiedPackageName = getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");

		String rootPath = getRootPath(packagePath);
		
		if (!isModel) {
			qualifiedPackageName = camelCaseToSpaces(pkg.getName());
		}
		
		packageListData.put("qualifiedPackageName", qualifiedPackageName);		
		packageListData.put("packagePath", packagePath);
		packageListData.put("rootPath", rootPath);
		
		packageListData.put("entities", buildEntityList(pkg, null, UMLPackage.Literals.CLASS));
		
		packageListData.put("interfaces", buildEntityList(pkg, null, UMLPackage.Literals.INTERFACE));
		
		packageListData.put("dataTypes", buildEntityList(pkg, null, UMLPackage.Literals.DATA_TYPE));
		
		packageListData.put("referenceEntities", buildEntityList(pkg, profile.getOwnedStereotype("ReferenceData"), UMLPackage.Literals.CLASS));
		
		packageListData.put("enumerations", buildEntityList(pkg, null, UMLPackage.Literals.ENUMERATION));
		
		packageListData.put("comments", parseComments(pkg));

		for (Package o: getSubPackages(pkg)) {
			boolean isStillModel = isModel;
			if (o.isStereotypeApplied(documentationStereotype) || o.isStereotypeApplied(tutorialStereotype)) {
				isStillModel = false;
			}
			packageData.addAll(buildPackageData(o, isStillModel));
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
		
		for (Package o: getSubPackages(pkg)) {
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
		
	protected List<Map<String, Object>> buildEntityData(Package pkg, Stereotype s, EClass eClass, String entityType) {
		List<Map<String, Object>> entityData = new ArrayList<>();
		Collection<Classifier> classes = filterClasses(s, EcoreUtil.getObjectsByType(pkg.getPackagedElements(), eClass));

		String qualifiedPackageName = getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");

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
				entity.put("type", entityType);
				//entity.put("stereotype", s.getQualifiedName());
				//entity.put("class", s.getName());
				buildEntity(entity, (Classifier) o);
				entityData.add(entity);
			}
		}
		
		for (Package o: getSubPackages(pkg)) {
			entityData.addAll(buildEntityData(o, s, eClass, entityType));
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
		
		entity.put("inheritedEnumerationValues", buildInheritedEnumerationData(hierarchy));
		
		entity.put("associations", buildAssociationListData(c));
		
		entity.put("inheritedAttributes", buildInheritedAttributesListData(hierarchy));
		
		entity.put("implementedAttributes", buildImplementedAttributesListData(c));
		
		entity.put("inheritedAssociations", buildInheritedAssociationsListData(hierarchy));

		entity.put("implementedAssociations", buildImplementedAssociationsListData(c));
	}
	
protected List<Map<String, Object>> buildInheritedEnumerationData(List<Classifier> hierarchy) {
		List<Map<String, Object>> inheritedAssociationsListData = new ArrayList<>();
		
		for (Classifier c: hierarchy) {
			Map<String, Object> inheritedAssociations = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(c);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			inheritedAssociations.put("entityName", qualifiedEntityName);
			inheritedAssociations.put("entityPath", entityPath);
			
			inheritedAssociations.put("enumerationValues", buildEnumerationData(c));
			
			inheritedAssociationsListData.add(inheritedAssociations);
		}

		return inheritedAssociationsListData;
	}
		
	protected List<Map<String, Object>> buildInheritedAssociationsListData(List<Classifier> hierarchy) {
		List<Map<String, Object>> inheritedAssociationsListData = new ArrayList<>();
		
		for (Classifier c: hierarchy) {
			Map<String, Object> inheritedAssociations = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(c);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			inheritedAssociations.put("entityName", qualifiedEntityName);
			inheritedAssociations.put("entityPath", entityPath);
			
			inheritedAssociations.put("associations", buildAssociationListData(c));
			
			inheritedAssociationsListData.add(inheritedAssociations);
		}

		return inheritedAssociationsListData;
	}
	
	protected List<Map<String, Object>> buildImplementedAssociationsListData(Classifier c) {
		List<Map<String, Object>> implementedAssociationListData = new ArrayList<>();

		List<Classifier> interfaces = new ArrayList<>();
		for (Interface i: c.allRealizedInterfaces()) {
			interfaces.add(i);
			buildHierarchy(interfaces, i);
		}
		
		for (Classifier i: interfaces) {
			Map<String, Object> implementedAssociations = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(i);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			implementedAssociations.put("entityName", qualifiedEntityName);
			implementedAssociations.put("entityPath", entityPath);
			
			implementedAssociations.put("associations", buildAssociationListData(i));
			
			implementedAssociationListData.add(implementedAssociations);
		}

		return implementedAssociationListData;
	}
	
	protected List<Map<String, Object>> buildInheritedAttributesListData(List<Classifier> hierarchy) {
		List<Map<String, Object>> inheritedAttributeListData = new ArrayList<>();
		
		for (Classifier c: hierarchy) {
			Map<String, Object> inheritedAttributes = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(c);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			inheritedAttributes.put("entityName", qualifiedEntityName);
			inheritedAttributes.put("entityPath", entityPath);
			
			inheritedAttributes.put("attributes", buildAttributeListData(c));
			
			inheritedAttributeListData.add(inheritedAttributes);
		}

		return inheritedAttributeListData;
	}
	
	protected List<Map<String, Object>> buildImplementedAttributesListData(Classifier c) {
		List<Map<String, Object>> implementedAttributeListData = new ArrayList<>();

		List<Classifier> interfaces = new ArrayList<>();
		for (Interface i: c.allRealizedInterfaces()) {
			interfaces.add(i);
			buildHierarchy(interfaces, i);
		}
		
		for (Classifier i: interfaces) {
			Map<String, Object> implementedAttributes = new HashMap<>();
			
			String qualifiedEntityName = getQualifiedName(i);
			String entityPath = qualifiedEntityName.replace("::", "/");
			
			implementedAttributes.put("entityName", qualifiedEntityName);
			implementedAttributes.put("entityPath", entityPath);
			
			implementedAttributes.put("attributes", buildAttributeListData(i));
			
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
			if (association.eClass() == UMLPackage.Literals.ASSOCIATION_CLASS) {
				cardinality = "0..* to 0..* [" + association.getName() + "]";
			}
			associationData.put("cardinality", cardinality);

			List<String> comments = new ArrayList<>();
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
	
	protected Map<String, String> createEntityReference(Classifier c) {
		Map<String, String> entityReference = new HashMap<>();
		String qualifiedEntityName = getQualifiedName(c);
		entityReference.put("qualifiedEntityName", qualifiedEntityName);
		String entityPath = qualifiedEntityName.replace("::", "/");
		entityReference.put("entityPath", entityPath);
		
		return entityReference;
	}
	
	
	protected List<String> parseComments(Element e) {
		Package pkg = e.getNearestPackage();
		String qualifiedPackageName = ModelUtil.getQualifiedName(pkg);
		String packagePath = qualifiedPackageName.replace("::", "/");
		
		List<String> comments = new ArrayList<>();
		for (Comment comment: e.getOwnedComments()) {
			if (comment.isStereotypeApplied(documentationStereotype)) {
				comments.add(processComment(comment, packagePath));
			}
		}
		
		return comments;
	}
	
	protected String processComment(Comment comment, String path) {
		String commentBody = comment.getBody();
		if (commentBody == null) {
			commentBody = "";
		}
		
		if (path.isEmpty()) {
			path = ".";
		}
		
		String ownerPath = "Unnamed element in " + ModelUtil.getQualifiedName(comment.getNearestPackage());
		Element owner = comment.getOwner();
		if (owner instanceof NamedElement) {
			ownerPath = ModelUtil.getQualifiedName((NamedElement) owner);
		}

		String original = commentBody;
		String pattern = "\\$\\{(\\w*)=(.*)\\}";
		Pattern tagPattern = Pattern.compile(pattern);
		Matcher matcher = tagPattern.matcher(commentBody);
		while (matcher.find()) {			
			String key = matcher.group(1);
			String value = matcher.group(2);

			try {
				if (matcher.groupCount() == 2) {
					switch (key) {
					case "diagram":
						String[] parts = sanitiseFileName(value).split("@");
						String filename = parts[0];
						String scale = "100%";
						Boolean zoom = true;
						if (parts.length > 1) {
							scale = parts[1];
							if (scale.endsWith("Z")) {
								scale = scale.substring(0, scale.length() - 1);
							} else {
								zoom = false;
							}
						}
						String dPath;
						if (filename.startsWith("/")) {
							dPath = ".";
						} else {
							dPath = path + "/";
						}
						String id = filename.replace('/', '-');
						String embed = "";
						if (zoom) {
							embed = "<br/><div style=\"margin: auto; width: " + scale + ";\" class=\"diagram-div\"><embed id=\"" + id + "-svg\" type=\"image/svg+xml\" class=\"diagram col-12\" src=\"" + dPath + filename + ".svg\" onload=\"spz('#" + id + "-svg')\"/></div><br/>";
						} else {
							embed = "<br/><div style=\"margin: auto; width: " + scale + ";\" class=\"diagram-div\"><embed type=\"image/svg+xml\" class=\"diagram col-12\" src=\"" + dPath + filename + ".svg\"></div><br/>";
						}
						commentBody = matcher.replaceAll(embed);
						break;
					case "content":
						String[] contentParts = sanitiseFileName(value).split("@");
						String contentFilename = contentParts[0];
						String contentScale = "100%";
						if (contentParts.length > 1) {
							contentScale = contentParts[1];
						}
						String contentEmbed = "<br/><div style=\"margin: auto; width: " + contentScale + ";\"><embed style=\"width: 100%;\" src=\"" + "_content/" + contentFilename + "\"/></div><br/>";
						commentBody = matcher.replaceAll(contentEmbed);
						break;
					case "link":
						NamedElement element = findElement(comment, value);
						String linkEmbed = "<span>Link target not found</span>";
						if (element != null) {
							String elementPath = ModelUtil.getQualifiedName(element.getNearestPackage()).replace("::", "/");
							String elementName = element.getName();
							
							if (element instanceof Package) {
								elementPath += "/package.html";
							} else if (element instanceof Class) {
								if (isReferenceData((Class) element)) {
									elementPath += "/R_" + elementName + ".html"; 
								} else {
									elementPath += "/E_" + elementName + ".html"; 
								}
							} else if (element instanceof Interface) {
								elementPath += "/I_" + elementName + ".html"; 
							} else if (element instanceof DataType) {
								elementPath += "/D_" + elementName + ".html"; 
							} else if (element instanceof Enumeration) {
								elementPath += "/L_" + elementName + ".html"; 
							}
							
							linkEmbed = "<span class=\"pointer\" onclick=\"\\$('#main').load('" + elementPath + "');\">" + elementName + "</span>";
						} else {
							problems.add(new Problem("Invalid link target", Problem.Severity.WARNING, ownerPath));
						}
						
						commentBody = matcher.replaceFirst(linkEmbed);
						break;
					default:
					}
					
					matcher = tagPattern.matcher(commentBody);
				}
			} catch(Exception e) {
				return original;
			}
		}

		return commentBody;
	}
}
