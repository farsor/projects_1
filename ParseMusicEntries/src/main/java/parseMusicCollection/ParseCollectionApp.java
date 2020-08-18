package parseMusicCollection;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ParseCollectionApp {
	public static void main(String[] args) {
		
		//music collection files to be parsed
		String[] collectionFiles = {"finalized collections/CT Hartford, Connecticut Historical Society--sacred music INVENTORY.docx",
				"finalized collections/CT Hartford, Watkinson Library, Trinity College--sacred music INVENTORY.docx",
				"finalized collections/Atwill n. d. in Nym Cooke collection.docx",
				"finalized collections/MA Petersham, Nym Cooke collection INVENTORY (1).docx",
				"finalized collections/MA Boston, Congregational Library and Archives--INVENTORY (1).docx"}; 
		
		ParseMusicCollection pmc = new ParseMusicCollection(collectionFiles); //parse files
		pmc.toSpreadsheets("collections", "sources", "entries");
//		pmc.entriesToDatabase();		//write to database
//		pmc.collectionToDatabase();
//		pmc.sourcesToDatabase();
		
		//open file automatically
//		Desktop desktop = Desktop.getDesktop();
//	    try {
//			desktop.open(new File("output.txt"));
//			desktop.open(new File("dump.txt"));
//		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	}
}