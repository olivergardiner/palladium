 
package uk.org.whitecottage.palladium.util.handler;

import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

/**
 * <b>Warning</b> : As explained in <a href=
 * "http://wiki.eclipse.org/Eclipse4/RCP/FAQ#Why_aren.27t_my_handler_fields_being_re-injected.3F">this
 * wiki page</a>, it is not recommended to define @Inject fields in a handler.
 * <br/>
 * <br/>
 * <b>Inject the values in the @Execute methods</b>
 */
public abstract class GenericModelHandler {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (editor != null) {
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile resource = ((IFileEditorInput) input).getFile();
				if (resource.getLocation().getFileExtension().equals("di")) {
					openDialog(s);
				} else {
					noModelMessage(s);
				}
			} else {
				noModelMessage(s);
			}
		} else {
			noModelMessage(s);
		}
	}
	
	protected void noModelMessage(Shell s) {
		MessageDialog.openError(s, "Documentation Generator", "No model selected");
	}
	
	protected void noProfileMessage(Shell s) {
		MessageDialog.openError(s, "Documentation Generator", "Model does not have the correct Profile(s) applied");
	}
	
	protected abstract void openDialog(Shell s);
}