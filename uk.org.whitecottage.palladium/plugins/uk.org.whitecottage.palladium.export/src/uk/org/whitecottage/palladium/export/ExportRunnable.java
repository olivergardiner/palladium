package uk.org.whitecottage.palladium.export;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.uml2.uml.Model;

public class ExportRunnable implements IRunnableWithProgress {
    @SuppressWarnings("unused")
	private IStatusLineManager statusLineManager;
    private ExportDialog dialog = null;
    private Model model;

	public ExportRunnable(IStatusLineManager statusLineManager, ExportDialog dialog, Model model) {
        this.statusLineManager = statusLineManager;
        this.dialog = dialog;
        this.model = model;
    }

    @Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		
		Activator.logInfo("Starting export");
		switch (dialog.getFormat()) {
		case COLLIBRA:
			Activator.logInfo("Exporting Collibra catalogue");
			ExportCollibra collibra = new ExportCollibra(model, monitor);
			collibra.export(dialog.getOutputFile());
			break;
		case EXCEL:
			
			break;
		case CSV:
			
			break;
		case UNSET:
		default:
			break;
		}
	}   
}
