package uk.org.whitecottage.palladium.util.diagram;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;

public interface IDiagramRenderer {
	public enum FileFormat {
		PNG, JPEG, GIF, BMP, JPG, SVG, EMF;
		
		private static Logger logger = Logger.getLogger(IDiagramRenderer.class.getCanonicalName());
		
		public static FileFormat transformToFormat(String ext) {
			FileFormat format;
			try {
				format = FileFormat.valueOf(ext.toUpperCase());
				return format;
			} catch (IllegalArgumentException e) {
				String message = "The format " + ext + " is not supported";
				logger.info(message);
				
				return FileFormat.PNG;
			}

		}
	}

	/**
	 * Render the diagram into an image.
	 * 
	 * @param diagram the diagram to render
	 * @param visibleElements a list with the visible elements in the diagram
	 * @param destination the path where the image will be stored
	 * @param imageFormat the format of the image
	 * @param monitor the progress monitor
	 * @return a list with the top level visible 
	 * @throws CoreException
	 * 
	 */
	public List<EditPart> renderDiagram(Diagram diagram, List<EObject> visibleElements, IPath destination, FileFormat imageFormat, NullProgressMonitor monitor) throws CoreException;
}
