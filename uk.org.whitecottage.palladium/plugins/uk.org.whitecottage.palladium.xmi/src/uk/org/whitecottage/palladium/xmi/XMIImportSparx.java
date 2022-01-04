package uk.org.whitecottage.palladium.xmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMIImportSparx extends XMIImport {
	protected String umlURI;
	protected String xmiURI;
	
	public XMIImportSparx() {
		super();
	}

	public void importXMI(File file) {
		try (InputStream input = new FileInputStream(file)){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setExpandEntityReferences(false);
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			document = builder.parse(input);
			
			umlURI = document.lookupNamespaceURI("uml");
			
			xmiURI = document.lookupNamespaceURI("xmi");
			
			// Pares nodes first, then add edges - edges will need both ends to be defined before they can be created
			parseNodes();
			parseEdges();
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			Activator.logError(e.getMessage(), e);
		}
	}

	protected void parseNodes() {
		Element root = document.getDocumentElement();
		
		Element modelElement = (Element) root.getElementsByTagNameNS(umlURI, "Model").item(0);
		pkg = UMLFactory.eINSTANCE.createPackage();
		pkg.setName(modelElement.getAttribute("name"));
		parsePackageNodes(pkg, modelElement);
	}
	
	protected void parsePackageNodes(Package currentPackage, Element packageElement) {
		NodeList nodes = packageElement.getChildNodes();
		int length = nodes.getLength();
		for (int i = 0; i < length; i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getLocalName().contentEquals("ownedComment")) {
					Comment comment = currentPackage.createOwnedComment();
					comment.setBody(element.getAttribute("body"));
				} else if (element.getLocalName().equals("packagedElement")) {
					String type = element.getAttributeNS(xmiURI, "type");
					switch (type) {
					case "uml:Class":
						Class clazz = (Class) currentPackage.createPackagedElement(element.getAttribute("name"), UMLPackage.Literals.CLASS);
						parseClassNode(clazz, element);
						break;
					case "uml:AssociationClass": //Node creation only
						break;
					case "uml:Enumeration":
						break;
					case "uml:DataType":
						break;
					case "uml:PrimitiveType":
						break;
					case "uml:Package":
						Package subPackage = (Package) currentPackage.createPackagedElement(element.getAttribute("name"), UMLPackage.Literals.PACKAGE);
						parsePackageNodes(subPackage, element);
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	protected void parseClassNode(Class clazz, Element classElement) {
		classMap.put(classElement.getAttributeNS(xmiURI, "id"), clazz);
	}
	
	protected void parseEdges() {
		Element root = document.getDocumentElement();
		
		Element modelElement = (Element) root.getElementsByTagNameNS(umlURI, "Model").item(0);
		parsePackageEdges(pkg, modelElement);
	}

	protected void parsePackageEdges(Package currentPackage, Element packageElement) {
		NodeList nodes = packageElement.getChildNodes();
		int length = nodes.getLength();
		for (int i = 0; i < length; i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getLocalName().equals("packagedElement")) {
					String type = element.getAttributeNS(xmiURI, "type");
					switch (type) {
					case "uml:Association":
						Association association = (Association) currentPackage.createPackagedElement(element.getAttribute("name"), UMLPackage.Literals.ASSOCIATION);
						parseAssociation(association, element);
						break;
					case "uml:AssociationClass": // For Association ends
						break;
					case "uml:Class": // For Generalizations
						break;
					case "uml:Abstraction":
						break;
					case "uml:Package":
						Package subPackage = currentPackage.getNestedPackage(element.getAttribute("name"));
						if (subPackage != null) {
							parsePackageEdges(subPackage, element);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	protected void parseAssociation(Association association, Element associationElement) {
		NodeList ownedEnds = associationElement.getElementsByTagName("ownedEnd");

		int length = ownedEnds.getLength();
		for (int i = 0; i < length; i++) {
			Element ownedEndElement = (Element) ownedEnds.item(i);
			
			Element typeElement = (Element) ownedEndElement.getElementsByTagName("type").item(0);
			Class clazz = classMap.get(typeElement.getAttributeNS(xmiURI, "idref"));
			
			Property ownedEndProperty = association.createOwnedEnd(associationElement.getAttribute("name"), clazz);
			
			Element lowerElement = (Element) ownedEndElement.getElementsByTagName("lowerValue").item(0);
			if (lowerElement != null) {
				ownedEndProperty.setLowerValue(parseMultiplicity(lowerElement));
			}
			
			Element upperElement = (Element) ownedEndElement.getElementsByTagName("upperValue").item(0);
			if (upperElement != null) {
				ownedEndProperty.setUpperValue(parseMultiplicity(upperElement));
			}
		}
	}
	
	protected ValueSpecification parseMultiplicity(Element element) {
		ValueSpecification value = null;
		switch (element.getAttributeNS(xmiURI, "type")) {
		case "uml:LiteralInteger":
			value = UMLFactory.eINSTANCE.createLiteralInteger();
			((LiteralInteger) value).setValue(Integer.parseInt(element.getAttribute("value")));					
			break;
		case "uml:LiteralUnlimitedNatural":
			value = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
			if (element.getAttribute("value").contentEquals("*")) {
				((LiteralUnlimitedNatural) value).setValue(-1);
			} else {
				((LiteralUnlimitedNatural) value).setValue(Integer.parseInt(element.getAttribute("value")));
			}
			break;
		default:
			break;
		}
		
		return value;
	}
	
	protected Comment parseComment(Element commentElement) {
		Comment comment = UMLFactory.eINSTANCE.createComment();
		comment.setBody(commentElement.getAttribute("body"));
		
		return comment;
	}
	
	protected void logNode(Node node) {
		Activator.logInfo("Node Name: " + node.getNodeName());
		Activator.logInfo("Node Local Name: " + node.getLocalName());
		Activator.logInfo("Node Namespace: " + node.getNamespaceURI());
		Activator.logInfo("Node Prefix: " + node.getPrefix());
	}
}
