package uk.org.whitecottage.palladium.datavault;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getAllAttributes;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getRoot;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSubClassifiers;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSubTypes;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSuperClassifiers;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSuperTypes;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.isReferenceData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public abstract class DataVaultRunnable implements IRunnableWithProgress {
	@SuppressWarnings("unused")
	private IStatusLineManager statusLineManager;
    private DataVaultDialog dialog = null;

	private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    
	protected SubMonitor monitor;
	protected Model model;
	protected Preferences preferences;
	protected Configuration cfg;
	protected Profile profile;
	protected Stereotype ignoreStereotype;
	protected Stereotype exampleStereotype;
	protected Stereotype tutorialStereotype;
	protected Stereotype patternStereotype;
	protected Stereotype documentationStereotype;
	protected Stereotype conceptualStereotype;
	protected List<Stereotype> ignoreStereotypes;
	
	protected List<Classifier> entities;
	protected List<Class> referenceEntities;
	protected List<Enumeration> enumerations;
	
	public DataVaultRunnable(IStatusLineManager statusLineManager, DataVaultDialog dialog, Model model) {
        this.statusLineManager = statusLineManager;
        this.dialog = dialog;
		this.model = model;
		this.preferences = dialog.getPreferences();
		
		entities = new ArrayList<>();
		referenceEntities = new ArrayList<>();
		enumerations = new ArrayList<>();

		profile = ProfileUtil.getProfile(model);
		ignoreStereotype = profile.getOwnedStereotype("Ignore");
		exampleStereotype = profile.getOwnedStereotype("Example");
		tutorialStereotype = profile.getOwnedStereotype("Tutorial");
		patternStereotype = profile.getOwnedStereotype("Pattern");
		documentationStereotype = profile.getOwnedStereotype("Documentation");
		conceptualStereotype = profile.getOwnedStereotype("Conceptual");
		
		ignoreStereotypes = new ArrayList<>();
		ignoreStereotypes.add(ignoreStereotype);
		ignoreStereotypes.add(exampleStereotype);
		ignoreStereotypes.add(documentationStereotype);
		ignoreStereotypes.add(tutorialStereotype);
		ignoreStereotypes.add(patternStereotype);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		this.monitor = SubMonitor.convert(monitor, 100);
		
		export(dialog.getOutputFile());
	}
	
	protected abstract void export(String output);

	protected void copyPluginResource(String resourceUrl, File target) {
		try {
			URL url = new URL("platform:/plugin/" + Activator.PLUGIN_ID + "/" + resourceUrl);
			URI resolved = FileLocator.resolve(url).toURI();
			
			if ("jar".equals(resolved.getScheme())) {
				JarURLConnection connection = (JarURLConnection) resolved.toURL().openConnection();

				copyJarResourceToPath(connection, target);
			} else {
				File source = new File(resolved);
				FileUtils.copyFile(source, target);
			}
		} catch (Exception e) {
			Activator.logError("Error copying resource", e);
		}
	}

    protected void copyJarResourceToPath(JarURLConnection jarConnection, File destPath) {
        try {
            JarFile jarFile = jarConnection.getJarFile();
            String jarConnectionEntryName = jarConnection.getEntryName();

            /**
             * Iterate all entries in the jar file.
             */
            for (java.util.Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                JarEntry jarEntry = e.nextElement();
                String jarEntryName = jarEntry.getName();

                /**
                 * Extract files only if they match the path.
                 */
                if (jarEntryName.startsWith(jarConnectionEntryName)) {
                    String filename = jarEntryName.substring(jarConnectionEntryName.length());
                    File currentFile = new File(destPath, filename);

                    if (jarEntry.isDirectory()) {
                        currentFile.mkdirs();
                    } else {
                        currentFile.deleteOnExit();
                        InputStream is = jarFile.getInputStream(jarEntry);
                        OutputStream out = FileUtils.openOutputStream(currentFile);
                        IOUtils.copy(is, out);
                        is.close();
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
        	e.getMessage(); // Fail silently
        }
    } 
    
	protected void applyTemplate(Template template, Map<String, Object> map, File path) {
		try (FileWriter writer = new FileWriter(path)) {
			template.process(map, writer);
		} catch (IOException | TemplateException e) {
			Activator.logError("Error processing " + path, e);
		}
	}

	protected void configureFreemarker(File templateDir) {		
		cfg = new Configuration(Configuration.VERSION_2_3_22); // Latest version Orbit (was 2.3.28)
		
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

	public static String createName(String qualifiedName) {
		if (qualifiedName == null) {
			return "__Unknown__";
		}
		
		StringBuilder name = new StringBuilder();
		
		String path[] = qualifiedName.split("::");
		for (int i = 1; i < path.length; i++) {
			if (i > 1) {
				name.append("_");
			}
			
			name.append(path[i]);
		}
		
		return name.toString().toUpperCase();
	}
	
	protected void scanForReferenceData(Classifier c, boolean withHierarchy) {
		List<Classifier> ancestorOrSelf = getSuperTypes(c, false);
		ancestorOrSelf.add(c);
		
		List<Property> attributes;
		if (c.eClass() == UMLPackage.Literals.CLASS) {
			attributes = (List<Property>) getAllAttributes((Class) c);
		} else {
			attributes = c.getAttributes();
		}
		
		for (Property p: attributes) {
			// Check for recursion
			Type t = p.getType();
			if (t == null) {
				Activator.logInfo("No type for Property: " + p.getName() + " in Class: " + c.getQualifiedName());
			}
			
			if (UMLPackage.Literals.DATA_TYPE == t.eClass() && !ancestorOrSelf.contains(t)) {
				scanForReferenceData((DataType) t, true);
			} else if (UMLPackage.Literals.ENUMERATION == t.eClass()) {
				addEnumeration((Enumeration) t);
			} else if (isReferenceData(p.getType()) && !ancestorOrSelf.contains((Classifier) p.getType())) {
				addReferenceData(p);
				scanForReferenceData((Class) p.getType(), true);
			}
		}
		
		if (withHierarchy) {
			for (Classifier sc: getSuperTypes(c, false)) {
				scanForReferenceData(sc, false);
			}
			
			for (Classifier sc: getSubTypes(c, false)) {
				scanForReferenceData(sc, false);
			}
		}
	}

	protected void addEnumeration(Enumeration e) {
		if (!enumerations.contains(e)) {
			enumerations.add(e);
		}
	}

	protected void addReferenceData(Property p) {
		Class root = (Class) getRoot((Class) p.getType());
		if (!referenceEntities.contains(root)) {
			referenceEntities.add(root);
		}
	}

	protected void addReferenceData(List<Class> referenceData, List<Class> processed) {
		for (Class c: referenceData) {
			Class root = (Class) getRoot(c);
			if (!referenceEntities.contains(root) && !processed.contains(root)) {
				referenceEntities.add(root);
			}
		}
	}

	protected String computeHash(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes());
			
			return bytesToHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.getMessage(); // Fail silently
		}
		
		return "";
	}

	protected static String bytesToHex(byte[] bytes) {
	    byte[] hexChars = new byte[bytes.length * 2];
	    
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	protected Class getParent(Class c, boolean isRoot) {
		if (c.getSuperClasses().isEmpty()) {
			return null;
		}
		
		Class parent = c.getSuperClasses().get(0);
		
		if (isRoot) {
			return getAncestor(parent);
		}
		
		return parent;
	}
	
	protected Class getAncestor(Class c) {
		if (c.getSuperClasses().isEmpty()) {
			return c;
		}
		
		Class parent = c.getSuperClasses().get(0);
		
		return getAncestor(parent);
	}
	
	protected List<Interface> getRealizedInterfaces(Class c) {
		List<Interface> interfaces = new ArrayList<>();
		
		for (Interface i : c.directlyRealizedInterfaces()) {
			interfaces.add(i);

			for (Classifier superInterface: getSuperClassifiers(i)) {
				if (superInterface instanceof Interface) {
					interfaces.add((Interface) superInterface);
				}
			}
			
			for (Classifier subInterface: getSubClassifiers(i)) {
				if (subInterface instanceof Interface) {
					interfaces.add((Interface) subInterface);
				}
			}
		}
		
		return interfaces;
	}

	protected List<Class> getClassesFromType(Type type) {
		List<Class> classes = new ArrayList<>();
		
		if (type.eClass() == UMLPackage.Literals.CLASS) {
			classes.add((Class) type);
		} else if (type.eClass() == UMLPackage.Literals.INTERFACE) {
			for (Classifier entity: entities) {
				if (entity.eClass() == UMLPackage.Literals.CLASS) {
					if (getRealizedInterfaces((Class) entity).contains(type)) {
						classes.add((Class) entity);
					}
				}
			}
		}
		
		return classes;
	}

	protected List<Class> getTargetClassesFromType(Type type) {
		List<Class> classes = new ArrayList<>();
		
		if (type.eClass() == UMLPackage.Literals.CLASS) {
			classes.add((Class) type);
		} else if (type.eClass() == UMLPackage.Literals.INTERFACE) {
			for (Classifier entity: entities) {
				if (entity.eClass() == UMLPackage.Literals.CLASS) {
					if (getRealizedInterfaces((Class) entity).contains(type)) {
						Class root = getAncestor((Class) entity);
						if (!classes.contains(root)) {
							classes.add(root);
						}
					}
				}
			}
		}
		
		return classes;
	}
}
