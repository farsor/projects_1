package app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import objects.Collections;
import objects.SheetInfo;

public class ParseCollectionApp {
	public static void main(String[] args) {
		deleteOldSpreadhsset();		
		Collections collections = new Collections(getFinalizedCollectionFiles());
		writeSpreadsheet(collections);
		openSpreadsheet();		
	}
	
	private static void deleteOldSpreadhsset() {
		File file = new File("C:\\Users\\Andrew\\git\\projects_1\\ParseMusicEntries\\src\\main\\resources\\spreadsheet outputs\\parsed_collections.xlsx");
		file.delete();
	}
	
	@SuppressWarnings("unused")
	private static File[] getCurrentCollectionFiles() {
		File[] files = { 
			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 1.docx")
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 2.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 3.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 4.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 5.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 6.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 7.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 8.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 9.docx"),
//			new File("src/main/resources/finalized collections/AAS Split/MA Worcester, American Antiquarian Society--sacred music INVENTORY - 10.docx")
			};
		return files;
	}
	
	//write collection(s) to spreadsheet
	private static void writeSpreadsheet(Collections collections) {
		String workbookName = "parsed_collections";
		String workbookPath = "src/main/resources/spreadsheet outputs/";
		SheetInfo sheetInfo = new SheetInfo(workbookPath, workbookName, "collections");
		collections.toSpreadsheet(sheetInfo);
		sheetInfo = new SheetInfo(workbookPath, workbookName, "sources");
		collections.getSources().toSpreadsheet(sheetInfo);
		sheetInfo = new SheetInfo(workbookPath, workbookName, "entries");
		collections.getEntries().toSpreadsheet(sheetInfo);
	}
	
	private static void openSpreadsheet() {
		File file = new File("C:\\Users\\Andrew\\git\\projects_1\\ParseMusicEntries\\src\\main\\resources\\spreadsheet outputs\\parsed_collections.xlsx");
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static File[] getFinalizedCollectionFiles() {
		File [] files = {
			new File("src/main/resources/finalized collections/CT Hartford, Connecticut Historical Society--sacred music INVENTORY.docx"),
			new File ("src/main/resources/finalized collections/CT Hartford, Watkinson Library, Trinity College--sacred music INVENTORY.docx"),
			new File ("src/main/resources/finalized collections/Atwill n. d. in Nym Cooke collection.docx"),
			new File ("src/main/resources/finalized collections/MA Petersham, Nym Cooke collection INVENTORY (1).docx"),
			new File ("src/main/resources/finalized collections/MA Boston, Congregational Library and Archives--INVENTORY (1).docx"),
			new File("src/main/resources/finalized collections/MA Boston, Boston Athenaeum--INVENTORY.docx"),
			new File ("src/main/resources/finalized collections/MA Andover, Andover Center for History and Culture--INVENTORY .docx")
			};
		return files;
	}
	
	//write collections to database
	@SuppressWarnings("unused")
	private static void writeDatabase(Collections collections) {
		//write collection data to database		
		String schema = "collections_2", 						//database 					
				databasePath = "jdbc:mysql://localhost:3306/",		//information
				user = "root",
				password = "password";
		collections.toDatabase(databasePath, schema, "collections", user, password);
		collections.getSources().toDatabase(databasePath, schema, "sources", user, password);
		collections.getEntries().toDatabase(databasePath, schema, "entries", user, password);
	}
	
}


//private boolean entryFound(XWPFParagraph paragraph) {		
//	String parText = paragraph.getText();
//	//check for various indicators of entries
//	return hasCommonEntryIndicator(parText) || hasCrypticEntryIndicator(parText);
//}	
//
//private boolean hasCommonEntryIndicator(String text) {
//	return text.indexOf("MS. music entries:") != -1;
//}
//
//private boolean hasCrypticEntryIndicator(String parText) {
//	XWPFParagraph nextParagraph = paragraphList.get(curParIndex + 1);
//	return (isProbableEntryIndicator(parText) && isProbableEntry(nextParagraph));
//}
//
//private boolean isProbableEntryIndicator(String parText) {
//	//check for various signs that indicate probable presence of entry indicator
//	return (parText.indexOf("MS.") != -1 &&		//most common sign of entry indicator 
//			(parText.indexOf("music") != -1 || parText.indexOf("entries") != -1) &&	//when both words present, is often sign of indicator
//				parText.indexOf(":") != -1 &&			//colon is common sign of entry indicator
//				parText.indexOf(":")  > parText.indexOf("MS.") &&	//colon occurs after MS. (this removes false positives)
//				paragraphList.get(curParIndex + 1).getText().indexOf("MS.") == -1);	//next paragraph does not contain phrase (removes false positive)
//}
//
//private boolean isProbableEntry(XWPFParagraph paragraph) {
//	String parText = paragraph.getText();
//	//melodic incipit detected after current paragraph
//	return (hasMelodicIncipit(paragraph) || 
//			(parText.indexOf(":") < 60 //early colon indicates likely entry
//					&& parText.indexOf(":") >= 0));	//make sure -1 is not index
//	
//}
//
//private boolean hasMelodicIncipit(XWPFParagraph par) {	//loose application if isMelodicIncipit method to detect if paragraph contains incipit	
//	String curEnt = par.getText();
//		if(isMelodicIncipit(curEnt) && !hasCallNumber(par))
//			return true;
//	return false;
//}
//
//public boolean isMelodicIncipit(String str) {	//check if current entry is melodic incipit, indicated by more than three digits
//	int digitCount = 0,				//total digits in string
//			maxConsecDigits = 0,	//most consecutive digits that occur in a row in given string
//			curConsec = 0;
//	
//	if(str == null)
//		return false;
//	
//	for(char c: str.toCharArray()) {	//check if each character is digit, and increment count if so			
//		if(Character.isDigit(c)) {
//			digitCount++;
//			curConsec++;
//		}
//		
//		else {
//			if(curConsec > maxConsecDigits) {
//				maxConsecDigits = curConsec;
//				curConsec = 0;
//			}
//		}
//		if(curConsec > maxConsecDigits) {
//			maxConsecDigits = curConsec;
//		}
//	}
//	return ((digitCount >=3 && (str.indexOf("|") != -1) || str.indexOf("-") != -1) || maxConsecDigits  > 4|| digitCount >= 8) ? true: false;
//}