package uk.org.whitecottage.palladium.catalogue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
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

import uk.org.whitecottage.palladium.exception.NoProfileException;
import uk.org.whitecottage.palladium.util.problems.Problem;
import uk.org.whitecottage.palladium.util.problems.ProblemsDialog;
import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public class CatalogueRunnable implements IRunnableWithProgress {
    @SuppressWarnings("unused")
	private IStatusLineManager statusLineManager;
    private DocumentationDialog dialog = null;
    private IEditorPart editor;
    private Shell s;

	public CatalogueRunnable(IStatusLineManager statusLineManager, DocumentationDialog dialog, IEditorPart editor, Shell s) {
        this.statusLineManager = statusLineManager;
        this.dialog = dialog;
        this.editor = editor;
        this.s = s;
    }

    @SuppressWarnings("unchecked")
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
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
				
				if (ProfileUtil.getProfile(model) == null) {
					throw new InvocationTargetException(new NoProfileException());
				}

				List<Diagram> diagrams = new ArrayList<>();
						
				for (Resource resource: model.eResource().getResourceSet().getResources()) {
					if (resource.getURI().isPlatformResource() && "notation".equals(resource.getURI().fileExtension())) {
						Activator.logInfo("Notation: " + resource.getURI().toString());
						for (Diagram diagram: (List<Diagram>)(List<?>) resource.getContents()) {
							diagrams.add(diagram);
						}
					}
				}
								
				switch (dialog.getFormat()) {
				case OOXML:
					CatalogueOOXML ooxmlCatalogue = new CatalogueOOXML(model, diagrams, monitor, editor, s);

					ooxmlCatalogue.setTemplate(dialog.getTemplateOOXML());
					ooxmlCatalogue.setOutputFolder(dialog.getFolderOOXML());
					ooxmlCatalogue.setDefaultTemplate(dialog.isDefaultTemplateOOXML());

					ooxmlCatalogue.buildCatalogue();
					break;
				case HTML:
					CatalogueHTML htmlCatalogue = new CatalogueHTML(model, diagrams, monitor, editor, s);
					
					htmlCatalogue.setTemplate(dialog.getTemplateHTML());
					htmlCatalogue.setOutputFolder(dialog.getFolderHTML() + "/" + model.getName());
					htmlCatalogue.setDefaultTemplate(dialog.isDefaultTemplateHTML());
					
					htmlCatalogue.buildCatalogue();
					monitor.done();
					
					List<Problem> problems = htmlCatalogue.getProblems();
					if (!problems.isEmpty()) {
						ProblemsDialog problemsDialog = new ProblemsDialog(s);
						problemsDialog.create();
						problemsDialog.setProblems(problems);
						problemsDialog.open();
					}
					break;
				case UNSET:
				default:
					break;
				}
			}

		} catch (ServiceException e) {
			Activator.logError("Service Exception", e);
		}
	}
}
