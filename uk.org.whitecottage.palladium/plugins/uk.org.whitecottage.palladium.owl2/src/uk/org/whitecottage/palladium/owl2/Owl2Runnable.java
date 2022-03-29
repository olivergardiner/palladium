package uk.org.whitecottage.palladium.owl2;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;

public class Owl2Runnable implements IRunnableWithProgress {
    @SuppressWarnings("unused")
	private IStatusLineManager statusLineManager;
    private Owl2Dialog dialog = null;
    private IEditorPart editor;
    private Shell s;
	
	public Owl2Runnable(IStatusLineManager statusLineManager, Owl2Dialog dialog, IEditorPart editor, Shell s) {
        this.statusLineManager = statusLineManager;
        this.dialog = dialog;
        this.editor = editor;
        this.s = s;
    }

    @Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		Activator.logInfo("Starting conversion to Owl2");

		ServicesRegistry registry = editor.getAdapter(ServicesRegistry.class);
		
		try {
			ModelSet modelSet = registry.getService(ModelSet.class);

			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IPath modelFile = ((IFileEditorInput) input).getFile().getFullPath();

				modelFile = modelFile.removeFileExtension().addFileExtension("uml");
				URI modelUri = URI.createPlatformResourceURI(modelFile.toPortableString(), true);

				Resource modelResource = modelSet.getResource(modelUri, true);
				Model model = (Model) EcoreUtil.getObjectByType(modelResource.getContents(), UMLPackage.Literals.MODEL);
								
				// Do the conversion with parameters from the dialog
				Owl2 owl2 = new Owl2(model, dialog, monitor, s);
				owl2.createOwl2();
				
			}
								
		} catch (ServiceException e) {
			Activator.logError("Service Exception", e);
		} catch (FileNotFoundException e) {
			Activator.logError("Could not write output file", e);
		}
	}
}
