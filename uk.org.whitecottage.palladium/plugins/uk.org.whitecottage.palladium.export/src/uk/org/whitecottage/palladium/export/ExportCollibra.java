package uk.org.whitecottage.palladium.export;

import static uk.org.whitecottage.palladium.poi.XSSFUtil.nextRow;
import static uk.org.whitecottage.palladium.util.papyrus.ModelUtil.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;

import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public class ExportCollibra {
	protected SubMonitor monitor;
	protected Model model;
	protected Stereotype referenceData;

	public ExportCollibra(Model model, IProgressMonitor monitor) {
		this.model = model;
		this.monitor = SubMonitor.convert(monitor, 100);
		
		Profile profile = ProfileUtil.getProfile(model);
		if (profile != null) {
			referenceData = profile.getOwnedStereotype("ReferenceData");
		}
	}
	
	public void export(String output) {
		Activator.logInfo("Exporting " + output);
		File outputFile = new File(output);
		
		if (outputFile.exists()) {
			outputFile.delete();
		}
		

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet subjectAreaModel = workbook.createSheet("Subject Area Model");
			XSSFSheet dataCatalogue = workbook.createSheet("Data Catalogue");
			XSSFSheet referenceDataCatalogue = workbook.createSheet("Reference Data");
			
			SubMonitor task = monitor.split(10);
			buildSubjectAreaModel(subjectAreaModel, task);
			
			task = monitor.split(70);
			buildDataCatalogue(dataCatalogue, task);
			
			task = monitor.split(15);
			buildReferenceDataCatalogue(referenceDataCatalogue, task);
			
			task = monitor.split(5);
			Activator.logInfo("Writing catalogue");
			workbook.write(new FileOutputStream(outputFile));
			
			task.done();			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			Activator.logError(e.toString(), e);
		}
		
		if (monitor.isCanceled()) {
			return;
		}
		
		monitor.done();
	}

	private void buildSubjectAreaModel(XSSFSheet subjectAreaModel, SubMonitor task) {
		XSSFRow row = subjectAreaModel.createRow(0);
		
		List<Package> subPackages = getPlainSubPackages(model);
		task.setWorkRemaining(subPackages.size());
		
		for (Package subPackage: subPackages) {
			row = nextRow(buildPackage(row, subPackage, 0, task.split(1)));
		}
	}
	
	private XSSFRow buildPackage(XSSFRow row, Package pkg, int level, SubMonitor task) {
		row.createCell(level, CellType.STRING).setCellValue(pkg.getName());
		row.getSheet().setColumnWidth(level, 256 * 20);

		List<Package> subPackages = getPlainSubPackages(pkg);
		task.setWorkRemaining(subPackages.size());
		
		for (Package subPackage: subPackages) {
			row = buildPackage(nextRow(row), subPackage, level + 1, task.split(1));
		}
		
		task.done();
		
		return row;
	}

	private void buildDataCatalogue(XSSFSheet dataCatalogue, SubMonitor task) {
		XSSFRow row = dataCatalogue.createRow(0);
		
		row.createCell(0, CellType.STRING).setCellValue("Package");
		dataCatalogue.setColumnWidth(0, 256 * 80);
		
		row.createCell(1, CellType.STRING).setCellValue("Element Type");
		dataCatalogue.setColumnWidth(1, 256 * 16);
		
		row.createCell(2, CellType.STRING).setCellValue("Element Name");
		dataCatalogue.setColumnWidth(2, 256 * 40);
		
		row.createCell(3, CellType.STRING).setCellValue("Is Abstract");
		dataCatalogue.setColumnWidth(3, 256 * 16);
		
		row.createCell(4, CellType.STRING).setCellValue("Parent Element");
		dataCatalogue.setColumnWidth(4, 256 * 80);
		
		buildPackageData(nextRow(row), model, task);
	}
	
	private XSSFRow buildPackageData(XSSFRow row, Package pkg, SubMonitor task) {
		List<Package> subPackages = getPlainSubPackages(pkg);
		task.setWorkRemaining(subPackages.size() + 1);
		
		row = buildEntity(row, pkg, null, UMLPackage.Literals.CLASS, "Entity");
		
		row = buildEntity(row, pkg, null, UMLPackage.Literals.INTERFACE, "Interface");
		
		row = buildEntity(row, pkg, null, UMLPackage.Literals.DATA_TYPE, "Data Type");
		
		row = buildEntity(row, pkg, null, UMLPackage.Literals.ENUMERATION, "Enumeration");
		
		task.worked(1);
		
		for (Package subPackage: subPackages) {
			row = buildPackageData(row, subPackage, task.split(1));
		}
		
		task.done();
		
		return row;
	}
	
	private void buildReferenceDataCatalogue(XSSFSheet referenceDataCatalogue, SubMonitor task) {
		XSSFRow row = referenceDataCatalogue.createRow(0);
		
		row.createCell(0, CellType.STRING).setCellValue("Package");
		referenceDataCatalogue.setColumnWidth(0, 256 * 80);
		
		row.createCell(1, CellType.STRING).setCellValue("Element Type");
		referenceDataCatalogue.setColumnWidth(1, 256 * 16);
		
		row.createCell(2, CellType.STRING).setCellValue("Element Name");
		referenceDataCatalogue.setColumnWidth(2, 256 * 40);
		
		row.createCell(3, CellType.STRING).setCellValue("Is Abstract");
		referenceDataCatalogue.setColumnWidth(3, 256 * 16);
		
		row.createCell(4, CellType.STRING).setCellValue("Parent Element");
		referenceDataCatalogue.setColumnWidth(4, 256 * 80);
		
		buildPackageReferenceData(nextRow(row), model, task);
	}
	
	private XSSFRow buildPackageReferenceData(XSSFRow row, Package pkg, SubMonitor task) {
		List<Package> subPackages = getPlainSubPackages(pkg);
		task.setWorkRemaining(subPackages.size() + 1);
		
		row = buildEntity(row, pkg, referenceData, UMLPackage.Literals.CLASS, "Reference Data");
		
		task.worked(1);
		
		for (Package subPackage: subPackages) {
			row = buildPackageReferenceData(row, subPackage, task.split(1));
		}
		
		task.done();
		
		return row;
	}

	protected XSSFRow buildEntity(XSSFRow row, Package pkg, Stereotype s, EClass eClass, String label) {
		Collection<Classifier> types = filterClasses(s, EcoreUtil.getObjectsByType(pkg.getPackagedElements(), eClass));

		String qualifiedPackageName = getQualifiedName(pkg);
		
		for (Classifier c: types) {
			if ((s == null && c.getAppliedStereotypes().isEmpty()) || (s != null && c.isStereotypeApplied(s))) {
				row.createCell(0, CellType.STRING).setCellValue(qualifiedPackageName);
				
				row.createCell(1, CellType.STRING).setCellValue(label);
				
				row.createCell(2, CellType.STRING).setCellValue(c.getName());

				if (c.isAbstract()) {
					row.createCell(3, CellType.STRING).setCellValue("abstract");
				}
				
				EList<Generalization> generalizations = c.getGeneralizations();
				if (!generalizations.isEmpty()) {
					Classifier parent = generalizations.get(0).getGeneral();
					row.createCell(4, CellType.STRING).setCellValue(getQualifiedName(parent));
				}
				
				row = nextRow(row);
			}
		}

		return row;
	}
}
