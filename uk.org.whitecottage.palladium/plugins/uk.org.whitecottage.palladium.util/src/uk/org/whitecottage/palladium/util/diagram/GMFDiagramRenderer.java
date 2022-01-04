package uk.org.whitecottage.palladium.util.diagram;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.image.ImageFileFormat;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.render.clipboard.DiagramGenerator;
import org.eclipse.gmf.runtime.diagram.ui.render.util.CopyToImageUtil;
import org.eclipse.gmf.runtime.diagram.ui.util.DiagramEditorUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Shell;

public class GMFDiagramRenderer extends CopyToImageUtil implements IDiagramRenderer {
	
	private static Logger logger = Logger.getLogger(GMFDiagramRenderer.class.getCanonicalName());
	
	@Override
	public List<EditPart> renderDiagram(Diagram diagram, List<EObject> visibleElements, IPath path, FileFormat extension, NullProgressMonitor monitor) throws CoreException {
		if (visibleElements == null || visibleElements.isEmpty()) {
			return copyToImage(diagram, path, getImageFileFormat(extension),
					new NullProgressMonitor(),
					PreferencesHint.USE_DEFAULTS);
		} else {
			return copyToImage(diagram, path, visibleElements,
					getImageFileFormat(extension),
					new NullProgressMonitor(),
					PreferencesHint.USE_DEFAULTS);
		}
	}

	public List<EditPart> copyToImage(Diagram diagram, IPath destination,
			ImageFileFormat format, NullProgressMonitor monitor,
			PreferencesHint preferencesHint)
					throws CoreException {

		List<?> partInfo = Collections.EMPTY_LIST;

		DiagramEditor openedDiagramEditor = findOpenedDiagramEditor(diagram);
		if (openedDiagramEditor != null) {
			DiagramGenerator generator = copyToImage(openedDiagramEditor.getDiagramEditPart(),
					destination, format, monitor);
			partInfo = generator.getDiagramPartInfo(openedDiagramEditor.getDiagramEditPart());
		} else {
			Shell shell = new Shell();
			try {
				DiagramEditPart diagramEditPart = createDiagramEditPart(diagram,
						shell, preferencesHint);
				Assert.isNotNull(diagramEditPart);
				DiagramGenerator generator = super.copyToImage(diagramEditPart,
						destination, format, monitor);
				partInfo = generator.getDiagramPartInfo(diagramEditPart);
			} finally {
				shell.dispose();
			}
		}

		return (List)partInfo;
	}

	private DiagramEditor findOpenedDiagramEditor(Diagram diagram) {
		DiagramEditor result = DiagramEditorUtil.findOpenedDiagramEditorForID(ViewUtil.getIdStr(diagram));
		if (result != null){
			IPath iPathDiagEditor =getIPath(result.getDiagram());
			IPath iPathDiag = getIPath(diagram) ;

			if (iPathDiagEditor == null || iPathDiag == null || !iPathDiag.equals(iPathDiagEditor)){
				logger.warning("Two diagrams in separate files " + iPathDiagEditor + " and " + iPathDiag + " have the same identifier");
				return null ;
			}
		}
		return result ;
	}

	private IPath getIPath(Diagram diagram) {
		if (diagram != null){
			Resource resource = diagram.eResource();
			if (resource != null){
				IFile file = WorkspaceSynchronizer.getUnderlyingFile(resource);
				if (file != null){
					return file.getFullPath();
				}
			}
		}
		return null;
	}

	public List<EditPart> copyToImage(Diagram diagram, IPath destination,
			List<EObject> visibleElements, ImageFileFormat format,
			NullProgressMonitor monitor, PreferencesHint preferencesHint)
					throws CoreException {
		Shell shell = null ;
		try {
			List partInfo = Collections.EMPTY_LIST;

			DiagramEditor openedDiagramEditor = findOpenedDiagramEditor(diagram);
			DiagramEditPart diagramEditPart = null ;

			if (openedDiagramEditor != null) {
				diagramEditPart = openedDiagramEditor.getDiagramEditPart(); 
			} else {
				shell = new Shell();
				diagramEditPart = createDiagramEditPart(
						diagram, shell, preferencesHint);
			}
			Assert.isNotNull(diagramEditPart);
			copyToImage(diagramEditPart,
					GMFEditPartUtils.getEditParts(visibleElements, diagramEditPart),
					destination, format, monitor);
			return partInfo;
		} finally {
			if (shell != null && !shell.isDisposed())
			{
				shell.dispose();
			}
		}
	}

	private ImageFileFormat getImageFileFormat(FileFormat format) {
		return ImageFileFormat.resolveImageFormat(format.name());
	}

	private String id;
}
