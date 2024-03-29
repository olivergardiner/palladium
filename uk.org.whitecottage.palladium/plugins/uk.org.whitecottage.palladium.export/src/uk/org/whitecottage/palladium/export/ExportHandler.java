 
package uk.org.whitecottage.palladium.export;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import uk.org.whitecottage.palladium.exception.NoProfileException;
import uk.org.whitecottage.palladium.util.handler.GenericModelHandler;

public class ExportHandler extends GenericModelHandler {

	@Override
	protected void openDialog(Shell s) {
		ExportDialog dialog = new ExportDialog(s);
		if (dialog.open() == Window.OK) {
            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            
    		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		    ExportRunnable exportRunnable = new ExportRunnable(null, dialog, editor, s);
		    
		    try {
				progressMonitor.run(true, true, exportRunnable);
			} catch (InterruptedException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof NoProfileException) {
					noProfileMessage(s);
				}
			}
		}
	}
}