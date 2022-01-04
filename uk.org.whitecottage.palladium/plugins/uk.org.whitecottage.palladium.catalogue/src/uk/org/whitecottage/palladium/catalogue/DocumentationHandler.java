package uk.org.whitecottage.palladium.catalogue;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import uk.org.whitecottage.palladium.exception.NoProfileException;
import uk.org.whitecottage.palladium.util.handler.GenericModelHandler;

public class DocumentationHandler extends GenericModelHandler {

	@Override
	protected void openDialog(Shell s) {
		DocumentationDialog dialog = new DocumentationDialog(s);
		if (dialog.open() == Dialog.OK) {
            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            
    		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            CatalogueRunnable catalogueRunnable = new CatalogueRunnable(null, dialog, editor, s);
			try {
				progressMonitor.run(false, true, catalogueRunnable);
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
