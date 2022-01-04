package uk.org.whitecottage.palladium.catalogue;

import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.cardinality;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getPackageClasses;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.getQualifiedName;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.isModel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.IRunBody;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.image.ImageFileFormat;
import org.eclipse.gmf.runtime.diagram.ui.render.util.CopyToImageUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

public class CatalogueOOXML extends Catalogue {
	private static String[] _HEADING = { "Heading1", "Heading2", "Heading3" };
	private static String _NORMAL = "Normal";
	private double columnWidth;

	public CatalogueOOXML(Model model, List<Diagram> diagrams, IProgressMonitor monitor, IEditorPart editor, Shell s) {
		super(model, diagrams, monitor, editor, s);
	}

	public void buildCatalogue() {
		File templateFile = null;
		
		if (isDefaultTemplate) {
			copyTemplates("platform:/plugin/uk.org.whitecottage.palladium/resources/templates/ooxml/");
			templateFile = new File(outputFolder + "/templates/catalogue-template.docx");
		} else {
			templateFile = new File(template);
		}
		
		XWPFDocument document = openTemplate(templateFile);
		

		if (document != null) {
			
			buildCatalogue(document);
			try (FileOutputStream output = new FileOutputStream(outputFolder + "/" + model.getName() + ".docx")) {
				document.write(output);
			} catch (Exception e) {
				Activator.logError("Problem writing output document", e);
			}
		} else {
			Activator.logInfo("Could not open template");
		}
	}
	
    protected XWPFDocument openTemplate(File templateFile) {
		XWPFDocument document = null;
		
		try {
			document = new XWPFDocument(OPCPackage.open(new FileInputStream(templateFile)));
		} catch (Exception | Error e) {
			Activator.logError("Could not open template file", e);
		}
				
		return document;
	}
    
	protected void buildCatalogue(XWPFDocument document) {
		//CTSectPr sectPr = document.getDocument().getBody().getSectPr();

		double margins = 1440d;
		columnWidth = (11906d - margins) / 1440d * Units.POINT_DPI;
		
		IPackageProcessor processor = pkg -> {
			if (!isModel(pkg)) {
				createParagraph(document, _HEADING[1], getQualifiedName(pkg));
			}

			for (Comment comment: pkg.getOwnedComments()) {
				renderComment(comment, document);
			}
			
			for (Class entity: getPackageClasses(null, pkg)) {
				renderEntity(entity, document);
			}
		};
		
		traversePackages(model, processor);
	}
	
	private void renderComment(Comment comment, XWPFDocument document) {
		String commentText = comment.getBody();
		
		Pattern tagPattern = Pattern.compile("\\$\\{(\\w*)=(.*)\\}");
		Matcher matcher = tagPattern.matcher(commentText);
		if (matcher.find()) {			
			String key = matcher.group(1);
			String value = matcher.group(2);
			createParagraph(document, _NORMAL, matcher.replaceAll(""));
			if ("diagram".equals(key)) {
				Diagram diagram = getDiagram(value);
				if (diagram != null) {
					addDiagram(diagram, document);
				}
			}
		} else {
			createParagraph(document, _NORMAL, comment.getBody());
		}
	}
	
	protected void addDiagram(Diagram diagram, XWPFDocument document) {
		XWPFParagraph p = createParagraph(document, _NORMAL);
		XWPFRun r = addRun(p, "");

		CopyToImageUtil renderer = new CopyToImageUtil();
		try {
			byte[] imageData = renderer.copyToImageByteArray(diagram, 10000, 10000, ImageFileFormat.PNG, new NullProgressMonitor(), PreferencesHint.USE_DEFAULTS, false);
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
			double aspectRatio = ((double) image.getWidth()) / ((double) image.getHeight());
			
			r.addPicture(new ByteArrayInputStream(imageData), Document.PICTURE_TYPE_PNG, diagram.getName(), Units.toEMU(columnWidth), Units.toEMU(columnWidth / aspectRatio));
			r.addBreak();
		} catch (CoreException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void renderEntity(Class entity, XWPFDocument document) {
		createParagraph(document, _HEADING[2], entity.getName());

		renderSuperClasses(entity, document);
		
		renderSubClasses(entity, document);
		
		for (Comment comment: entity.getOwnedComments()) {
			renderComment(comment, document);
		}
		
		renderRealizations(entity, document);

		renderInformationFlows(entity, document);

		renderProperties(entity, document);
		
		renderAssociations(entity, document);
	}
	
	private void renderAssociations(Class entity, XWPFDocument document) {
		if (!entity.getAssociations().isEmpty()) {
			renderSubHeading("Associations", document);

			for (Association association : entity.getAssociations()) {
				Property source = null;
				Property target = null;
				XWPFParagraph p = createParagraph(document, _NORMAL);

				if (association instanceof AssociationClass) {
					for (Property end : association.getMemberEnds()) {
						if (source == null && entity == end.getType()) {
							source = end;
						} else {
							target = end;
						}
					}

					if (source != null && target != null) {
						addRun(p, getQualifiedName(target.getType(), entity));
						addRun(p, " (* to *) - Association entity: " + getQualifiedName(association, entity));
					}
				} else {
					for (Property end : association.getOwnedEnds()) {
						if (source == null && entity == end.getType()) {
							source = end;
						} else {
							target = end;
						}
					}

					if (source != null && target != null) {
						addRun(p, target.getType().getName());
						addRun(p, " (" + cardinality(source) + " to " + cardinality(target) + ")");
					}
				}

				for (Comment comment : association.getOwnedComments()) {
					renderComment(comment, document);
				}
			}
		}
	}

	private void renderProperties(Class entity, XWPFDocument document) {
		if (!entity.getOwnedAttributes().isEmpty()) {
			renderSubHeading("Attributes", document);
			
			for (Property attribute: entity.getOwnedAttributes()) {
				XWPFParagraph p = createParagraph(document, _NORMAL, attribute.getName() + " [" + cardinality(attribute) + "] : ");
				
				Type type = attribute.getType();
				
				if (type == null) {
					addRun(p, "Undefined");
				} else {
					addRun(p, getQualifiedName(type, entity));
					if (type.isStereotypeApplied(profile.getOwnedStereotype("ReferenceData"))) {
						addRun(p, " (Reference Data)");
					}
				}
	
				for (Comment comment: attribute.getOwnedComments()) {
					renderComment(comment, document);
				}
			}
		}
	}

	private void renderSubClasses(Class entity, XWPFDocument document) {
		List<Class> children = getSubClasses(entity);
		
		if (!children.isEmpty()) {
			renderSubHeading("Child Entities", document);
			
			XWPFParagraph p = createParagraph(document, _NORMAL);
			ListIterator<Class> i = children.listIterator();
			while (i.hasNext()) {
				Class child = i.next();
				String text  = getQualifiedName(child, entity);
				if (i.hasNext()) {
					text += ", ";
				}
				addRun(p, text);				
			}
		}
	}

	private void renderSuperClasses(Class entity, XWPFDocument document) {
		List<Class> ancestors = getSuperClasses(entity);
		String tab = "     ";
		
		if (!ancestors.isEmpty()) {
			renderSubHeading("Parent Entities", document);
			
			StringBuilder indent = new StringBuilder();
			indent.append(tab);
			for (Class ancestor: ancestors) {
				XWPFParagraph p = createParagraph(document, _NORMAL);
				addRun(p, indent.toString() + getQualifiedName(ancestor, entity));
				indent.append(tab);
			}
		}
	}

	private void renderRealizations(Class entity, XWPFDocument document) {

	}

	private void renderInformationFlows(Class entity, XWPFDocument document) {

	}

	protected void renderSubHeading(String heading, XWPFDocument document) {
		XWPFParagraph p = createParagraph(document, _NORMAL);
		addRun(p, heading).setBold(true);
	}
	
	protected XWPFRun addRun(IRunBody para, String text) {
		XWPFRun r = ((XWPFParagraph) para).createRun();
		r.setText(text);

		return r;
	}
	
	protected XWPFParagraph createParagraph(XWPFDocument docx, String style, String text) {
		XWPFParagraph para = docx.createParagraph();
		addRun(para, text);
		para.setStyle(style);

		return para;
	}
	
	protected XWPFParagraph createParagraph(XWPFDocument docx, String style) {
		XWPFParagraph para = docx.createParagraph();
		para.setStyle(style);

		return para;
	}	
}
