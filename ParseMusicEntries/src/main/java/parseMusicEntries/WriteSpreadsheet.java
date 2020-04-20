/**
 * 
 */
package parseMusicEntries;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Random;
/**
 * @author Andrew
 *
 */
public class WriteSpreadsheet {
	
	private static Workbook workbook;
	private static Sheet sheet;
	private static FileOutputStream fos;
	private static Row row;
	private static Cell cell;
	
	WriteSpreadsheet(String[] columnLabels) throws Exception{
		for(int i = 0; i < columnLabels.length; i++) {
			
		}
		fos = new FileOutputStream("finalized collections/spreadsheet outputs/test.xlsx");
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Random Numbers");
		for(int i = 0; i < 20; i++) {
			row = sheet.createRow(i);
			for(int j = 0; j < 8; j++) {
				cell = row.createCell(j);
				num = random.nextInt(100);
				cell.setCellValue(num);
			}
		}
		workbook.write(fos);
		fos.flush();
		fos.close();
	}
	
	
	public static void main(String[] args) throws Exception {
		fos = new FileOutputStream("finalized collections/spreadsheet outputs/test.xlsx");
		Random random = new Random();
		int num;	
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Random Numbers");
		for(int i = 0; i < 20; i++) {
			row = sheet.createRow(i);
			for(int j = 0; j < 8; j++) {
				cell = row.createCell(j);
				num = random.nextInt(100);
				cell.setCellValue(num);
			}
		}
		workbook.write(fos);
		fos.flush();
		fos.close();
		
		
		
//		fis = new FileInputStream("finalized collections/spreadsheet outputs/test.xlsx");
//		workbook = WorkbookFactory.create(fis);
//		sheet = workbook.getSheet("Sheet1");
//		fos = new FileOutputStream("finalized collections/spreadsheet outputs/test.xlsx");
//		
//		
//		Random random = new Random();
//		int num;
//		
//		for(int i = 0; i < 20; i++) {
//			row = sheet.createRow(i);
//			for(int j = 0; j < 8; j++) {
//				cell = row.createCell(j);
//				num = random.nextInt(100);
//				cell.setCellValue(num);
//				workbook.write(fos);
//				System.out.println(num);
//			}
//		}
//		
//
//		fos.flush();
//		fos.close();
		
//		workbook = WorkbookFactory.create(fis);
//		sheet = workbook.getSheet("Sheet1");
//		int numRows = sheet.getLastRowNum();
//		System.out.println(numRows);
		
//		row = sheet.createRow(1);
//		cell = row.createCell(0);
//		cell.setCellValue("dogboi");
//		System.out.println(cell.getStringCellValue());
//		fos = new FileOutputStream("finalized collections/spreadsheet outputs/test.xlsx");
//		workbook.write(fos);
//		fos.flush();
//		fos.close();
//		System.out.println("Done");
//		for(int i = 1; i <= numRows; i++){
//			System.out.println(sheet.getRow(i).getCell(0));
//			System.out.println(sheet.getRow(i).getLastCellNum());
//		}
		
	}

}
