 
package uk.org.whitecottage.palladium.datavault;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import uk.org.whitecottage.palladium.builder.LDMLBuilder;
import uk.org.whitecottage.palladium.util.handler.GenericModelHandler;
import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public class DataVaultHandler extends GenericModelHandler {
	@Override
	protected void openDialog(Shell s) {
		Properties properties = Activator.getProperties();

		DataVaultDialog dialog = new DataVaultDialog(s);

		if (properties.containsKey("datavaultPath")) {
			dialog.setOutputFile(properties.getProperty("datavaultPath"));
		}

		if (dialog.open() == Dialog.OK) {
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			ServicesRegistry registry = editor.getAdapter(ServicesRegistry.class);
			
			properties.put("datavaultPath", dialog.getOutputFile());
			Activator.saveProperties(properties);

			try {
				ModelSet modelSet = registry.getService(ModelSet.class);

				IEditorInput input = editor.getEditorInput();
				if (input instanceof IFileEditorInput) {
					IFile modelFile = ((IFileEditorInput) input).getFile();
					IPath modelPath = modelFile.getFullPath();

					modelPath = modelPath.removeFileExtension().addFileExtension("uml");
					URI modelUri = URI.createPlatformResourceURI(modelPath.toPortableString(), true);

					Resource modelResource = modelSet.getResource(modelUri, true);
					
					Model model = (Model) EcoreUtil.getObjectByType(modelResource.getContents(), UMLPackage.Literals.MODEL);
					
					Map<Object,Object> context = new HashMap<>();
					BasicDiagnostic diagnostics = new BasicDiagnostic();
					boolean validationOk = Diagnostician.INSTANCE.validate(UMLPackage.Literals.MODEL, model, diagnostics, context);
					
					IMarker[] markers = getFile(modelUri).findMarkers(LDMLBuilder.MARKER_TYPE, false, IResource.DEPTH_ZERO);
					
					if (ProfileUtil.getProfile(model) == null) {
						MessageDialog.openError(s, "Datavault Generator", "Model does not have the correct Profile(s) applied");
					} else if (markers.length > 0) {
						MessageDialog.openError(s, "Datavault Generator", "Model has LDML build problems which must be fixed");
					} else if (!validationOk) {
						MessageDialog.openError(s, "Datavault Generator", "Model has validation errors");
					} else {				
			            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			            
			            DataVaultRunnable dataVaultRunnable = null;
			            switch (dialog.getFormat()) {
			            case SNOWFLAKE:
				            dataVaultRunnable = new SnowflakeRunnable(null, dialog, model);
				            break;
			            case DBTVAULT:
				            dataVaultRunnable = new DbtvaultRunnable(null, dialog, model);
				            break;
			            default:
			            	break;
			            }
			            
			            if (dataVaultRunnable != null) {
			            	progressMonitor.run(false, true, dataVaultRunnable);
			            }
					}
				}
			} catch (InterruptedException e) {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
					Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
			} catch (CoreException e) {
			} catch (ServiceException e) {
				Activator.logError("Service Exception", e);
			}
		}
	}
	
	protected IFile getFile(URI uri) {
		String platformResourceString = uri.toPlatformString(true);
		
		if (platformResourceString != null) {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformResourceString));
		}
		
		return null;
	}
}