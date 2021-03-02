package writers;

import java.io.File;
import java.io.FileInputStream;
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
	
	//constructor
	public SpreadsheetWriter(String[] columnLabels, String fileName, String sheetName, String path) throws Exception{
		File file = new File(path + fileName + ".xlsx");		//excel file being read/written to
		if(file.exists()) {										//check if file exists
			FileInputStream fis = new FileInputStream(file);
			workbook = new XSSFWorkbook(fis);					//if it does exist, open file
			sheet = workbook.getSheet(sheetName);				//get sheet that will be written to
			if(sheet == null) {									//if sheet does not exist										
				sheet = workbook.createSheet(sheetName);			//create sheet
			}
		}
		else {													//if file does not exist
			workbook = new XSSFWorkbook();						//create blank workbook
			sheet = workbook.createSheet(sheetName);				//and blank sheet
		}
		fos = new FileOutputStream(file);						//create output stream
		curRow = sheet.getLastRowNum() + 1;						//first row that will be written to
		if(curRow == 1) curRow--;								//if new document, start at 0 instead of 1(no last row)		
		row = sheet.createRow(curRow++);						//create new row to be written to
		if(curRow == 1) {
			for(int i = 0; i < columnLabels.length; i++) {			//for the length of column label array			
				cell = row.createCell(i);							//create cell
				cell.setCellValue(columnLabels[i]);					//then write column label
			}
		}
	}
	
	//write row with string array as parameter
	public void writeRow(String[] dataArr) throws Exception {
		row = sheet.createRow(curRow++);			//create new row that will be written to
		for(int i = 0; i < dataArr.length; i++) {	//for all strings in data array
			cell = row.createCell(i);				//create cell to be written to
			cell.setCellValue(dataArr[i]);			//write string to cell
		}
	}
	
	//finalize document and close stream
	public void closeStream() throws IOException {
		workbook.write(fos);						//write data to document
		fos.close();								//close stream
	}
	
	
	

}
