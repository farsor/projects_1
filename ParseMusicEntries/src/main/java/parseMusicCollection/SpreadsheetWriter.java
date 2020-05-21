package parseMusicCollection;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SpreadsheetWriter {
	
	
	private Workbook workbook;			//workbook being created
	private Sheet sheet;					//sheet within workbook
	private FileOutputStream fos;		//excel file output
	private Row row;						//spreadsheet row
	private Cell cell;					//spreadsheet cell
	int curRow;									//row to which next entry will be written
	
	
	SpreadsheetWriter(String[] columnLabels, String fileName) throws Exception{
		fos = new FileOutputStream("finalized collections/spreadsheet outputs/" + fileName + ".xlsx");
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Random Numbers");
		row = sheet.createRow(0);		
		for(int i = 0; i < columnLabels.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(columnLabels[i]);
		}
		curRow = 1;
	}
	
	
	public void writeRow(String[] dataArr) throws Exception {
		row = sheet.createRow(curRow++);
		for(int i = 0; i < dataArr.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(dataArr[i]);
		}
	}
	
	public void closeStream() throws IOException {
		workbook.write(fos);
		fos.close();
	}
	
	
	

}
