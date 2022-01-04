package uk.org.whitecottage.palladium.poi;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class XSSFUtil {
	private XSSFUtil() {
		throw new IllegalStateException("Utility class");
	}
	
	public static XSSFRow nextRow(XSSFRow row) {
		XSSFSheet sheet = row.getSheet();
		int nextRow = row.getRowNum() + 1;
		
		XSSFRow newRow = sheet.getRow(nextRow);
		
		if (newRow == null) {
			newRow = sheet.createRow(nextRow);
		}
		
		return newRow;
	}
}
