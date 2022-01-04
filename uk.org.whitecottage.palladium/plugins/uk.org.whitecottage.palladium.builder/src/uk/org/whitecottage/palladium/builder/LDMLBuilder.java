package uk.org.whitecottage.palladium.builder;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getSubPackages;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Slot;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

public class LDMLBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "uk.org.whitecottage.palladium.builder.ldmlBuilder";

	public static final String MARKER_TYPE = "uk.org.whitecottage.palladium.builder.ldmlProblem";

	class LDMLDeltaVisitor implements IResourceDeltaVisitor {
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkModel(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkModel(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class LDMLResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkModel(resource);
			//return true to continue visiting children.
			return true;
		}
	}

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new LDMLResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new LDMLDeltaVisitor());
	}
	
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}
	
	protected void checkModel(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".uml") && !resource.getName().endsWith("profile.uml")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			
			ResourceSet set = new ResourceSetImpl();
			set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
			set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

			Resource res = set.getResource(URI.createFileURI(resource.getLocation().toString()), true);
			Model model = (Model) EcoreUtil.getObjectByType(res.getContents(), UMLPackage.Literals.MODEL);
			
			validate(file, model);
		}
	}
	
	protected void validate(IFile file, Model model) {
		validateInstanceSpecifications(file, model);
	}
	
	protected void validatePackageNames(IFile file, Package pkg) {
		if (pkg.getName().contains(" ")) {
			addMarker(file, "Space in package name: " + pkg.getName(), -1, IMarker.SEVERITY_ERROR);
		}
		
		for (Package o: getSubPackages(pkg)) {
			validatePackageNames(file, o);
		}
	}

	protected void validateInstanceSpecifications(IFile file, Package pkg) {
		for (Object o: EcoreUtil.getObjectsByType(pkg.getPackagedElements(), UMLPackage.Literals.INSTANCE_SPECIFICATION)) {
			InstanceSpecification instance = (InstanceSpecification) o;
			
			for (Slot slot: instance.getSlots()) {
				if (slot.getDefiningFeature() == null) {
					addMarker(file, "Null DefiningFeature in " + instance.getQualifiedName(), -1, IMarker.SEVERITY_WARNING);
				}
			}
		}
		
		for (Package o: getSubPackages(pkg)) {
			validateInstanceSpecifications(file, o);
		}
	}
}
