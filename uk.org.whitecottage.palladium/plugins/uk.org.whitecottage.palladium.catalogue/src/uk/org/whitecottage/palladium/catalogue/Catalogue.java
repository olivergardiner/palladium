package uk.org.whitecottage.palladium.catalogue;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSubPackages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;

import uk.org.whitecottage.palladium.util.problems.Problem;
import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public abstract class Catalogue {
	protected SubMonitor monitor;
	protected IEditorPart editor;
	protected Shell s;
	protected Model model;
	protected Profile profile;
	protected Stereotype ignoreStereotype;
	protected Stereotype exampleStereotype;
	protected Stereotype tutorialStereotype;
	protected Stereotype documentationStereotype;
	protected List<Stereotype> ignoreStereotypes;
	protected List<Stereotype> minimalIgnoreStereotypes;
	protected List<Diagram> diagrams;
	protected Collection<Object> allClasses;

	protected String template= "";
	protected String outputFolder = "";
	protected boolean isDefaultTemplate = true;	
	protected File templateDir = null;
	
	protected List<Problem> problems;

	protected Catalogue(Model model, List<Diagram> diagrams, IProgressMonitor monitor, IEditorPart editor, Shell s) {
		this.model = model;
		this.diagrams = diagrams;
		this.monitor = SubMonitor.convert(monitor, 100);
		this.editor = editor;
		this.s = s;
		
		profile = ProfileUtil.getProfile(model);
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
		
		allClasses = EcoreUtil.getObjectsByType(model.allOwnedElements(), UMLPackage.Literals.CLASS);
		
		problems = new ArrayList<>();
	}
	
	public List<Problem> getProblems() {
		return problems;
	}

	public List<Diagram> getPackageDiagrams(Package pkg) {
		List<Diagram> packageDiagrams = new ArrayList<>();
		
		for (Diagram diagram: diagrams) {
			if (diagram.getElement() != null && diagram.getElement().equals(pkg)) {
				packageDiagrams.add(diagram);
			}
		}
				
		return packageDiagrams;
	}
	
	public Diagram getDiagram(String name) {
		for (Diagram diagram: diagrams) {
			if (diagram.getName().equals(name)) {
				return diagram;
			}
		}
				
		return null;
	}
	
	public void traversePackages(Package pkg, IPackageProcessor processor) {
		List<Package> packages = getSubPackages(pkg, ignoreStereotype);
		processor.process(pkg);
		
		for (Package subPackage: packages) {
			traversePackages(subPackage, processor);
		}
	}
	
	public Profile getProfile() {
		return profile;
	}
	
	public List<Class> getSuperClasses(Class entity) {
		List<Class> ancestors = new ArrayList<>();
		
		if (!entity.getSuperClasses().isEmpty()) {
			Class ancestor = entity.getSuperClasses().get(0);
			ancestors.addAll(getSuperClasses(ancestor));
			ancestors.add(ancestor);
		}
		
		return ancestors;
	}

	public List<Class> getSubClasses(Class entity) {
		List<Class> children = new ArrayList<>();
		
		for (Object o: allClasses) {
			Class candidate = (Class) o;
			for (Class superClass: candidate.getSuperClasses()) {
				if (superClass == entity) {
					children.add(candidate);
					break;
				}
			}
		}
		
		return children;
	}

    void copyJarResourceToPath(JarURLConnection jarConnection, File destPath) {
        try {
            JarFile jarFile = jarConnection.getJarFile();
            String jarConnectionEntryName = jarConnection.getEntryName();

            /**
             * Iterate all entries in the jar file.
             */
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
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
        }
    } 
    
	protected File copyTemplates(String resourceUrl) {
		File dir = new File(outputFolder, "templates");
		try {
			File sourceDir;
			URL url = new URL(resourceUrl);
			URI resolved = FileLocator.resolve(url).toURI();
			
			if ("jar".equals(resolved.getScheme())) {
				JarURLConnection connection = (JarURLConnection) resolved.toURL().openConnection();

				copyJarResourceToPath(connection, dir);
			} else {
				sourceDir = new File(resolved);
				FileUtils.copyDirectory(sourceDir, dir);
			}
		} catch (Exception e) {
			Activator.logError("Error copying templates", e);
		}
		
		return dir;
	}
	
	protected File copyResources(String resourceUrl, String destination) {
		File dir = new File(outputFolder, destination);
		try {
			File sourceDir;
			URL url = new URL(resourceUrl);
			URI resolved = FileLocator.resolve(url).toURI();
			
			sourceDir = new File(resolved);
			FileUtils.copyDirectory(sourceDir, dir);
		} catch (Exception e) {
			Activator.logError("Error copying contents", e);
		}
		
		return dir;
	}
	
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String templateFolder) {
		this.template = templateFolder;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public boolean isDefaultTemplate() {
		return isDefaultTemplate;
	}

	public void setDefaultTemplate(boolean isDefaultTemplate) {
		this.isDefaultTemplate = isDefaultTemplate;
	}
}
