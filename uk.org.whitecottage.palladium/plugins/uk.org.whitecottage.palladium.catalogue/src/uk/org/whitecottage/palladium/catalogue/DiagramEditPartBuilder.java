package uk.org.whitecottage.palladium.catalogue;

import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.OffscreenEditPartFactory;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Shell;

public class DiagramEditPartBuilder {
	protected OffscreenEditPartFactory factory;
	protected Shell shell;
	
	public DiagramEditPartBuilder(Shell shell) {
		factory = OffscreenEditPartFactory.getInstance();
		this.shell = shell;
	}

    public DiagramEditPart createDiagramEditPart(Diagram diagram, PreferencesHint preferencesHint) {
    	return factory.createDiagramEditPart(diagram, shell, preferencesHint);
    }
}
