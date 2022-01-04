 
package uk.org.whitecottage.palladium.export;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;

import uk.org.whitecottage.palladium.exception.NoProfileException;
import uk.org.whitecottage.palladium.util.handler.GenericModelHandler;

public class ExportHandler extends GenericModelHandler {

	@Override
	protected void openDialog(Shell s) {
		ExportDialog dialog = new ExportDialog(s);
		if (dialog.open() == Dialog.OK) {
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
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
					
					if (model.getAppliedProfile("pldm.profile", true) == null) {
						throw new InvocationTargetException(new NoProfileException());
					}
					
		            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		            ExportRunnable exportRunnable = new ExportRunnable(null, dialog, model);		            
					progressMonitor.run(true, true, exportRunnable);
				}
			} catch (InterruptedException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof NoProfileException) {
					noProfileMessage(s);
				}
			} catch (ServiceException e) {
				Activator.logError("Service Exception", e);
			}
		}
	}
}