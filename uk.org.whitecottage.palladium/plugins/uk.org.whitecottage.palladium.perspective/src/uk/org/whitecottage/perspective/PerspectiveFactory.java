package uk.org.whitecottage.perspective;

import org.eclipse.papyrus.uml.perspective.PapyrusPerspective;
import org.eclipse.papyrus.views.validation.internal.ModelValidationView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.egit.ui.internal.repository.RepositoriesView;

public class PerspectiveFactory extends PapyrusPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		super.createInitialLayout(layout);
	}

	@Override
	public void defineLayout(IPageLayout layout) {
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		// Place the the Resource Navigator to the top left of editor area.
		//layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.2f, editorArea);

		// Place the ModelExplorer under the Navigator
		layout.addView(ID_MODELEXPLORER, IPageLayout.LEFT, 0.2f, editorArea);

		// place properties and problem views under the editor
		//IFolderLayout bottomFolder = layout.createFolder(ID_BOTTOM_FOLDER, IPageLayout.BOTTOM, 0.70f, editorArea);

		//bottomFolder.addView(IPageLayout.ID_PROP_SHEET);
		//bottomFolder.addView(ModelValidationView.VIEW_ID);

		// bottom.addView("org.eclipse.pde.runtime.LogView");

		// place outline under the model explorer
		// open the outline after all other views, since it is slower to refresh
		layout.addView(RepositoriesView.VIEW_ID, IPageLayout.BOTTOM, 0.5f, ID_MODELEXPLORER);
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.BOTTOM, 0.5f, RepositoriesView.VIEW_ID);
	}
}
