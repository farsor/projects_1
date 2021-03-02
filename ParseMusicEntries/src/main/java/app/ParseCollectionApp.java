package app;

import java.io.File;

import objects.Collections;

public class ParseCollectionApp {
	public static void main(String[] args) {
		
		
		//prepared music collection files to be parsed
		File[] collectionFiles = {new File("src/main/resources/finalized collections/CT Hartford, Connecticut Historical Society--sacred music INVENTORY.docx"),
				new File ("src/main/resources/finalized collections/CT Hartford, Watkinson Library, Trinity College--sacred music INVENTORY.docx"),
				new File ("src/main/resources/finalized collections/Atwill n. d. in Nym Cooke collection.docx"),
				new File ("src/main/resources/finalized collections/MA Petersham, Nym Cooke collection INVENTORY (1).docx"),
				new File ("src/main/resources/finalized collections/MA Boston, Congregational Library and Archives--INVENTORY (1).docx"),
				new File("src/main/resources/finalized collections/MA Boston, Boston Athenaeum--INVENTORY.docx"),
				new File ("src/main/resources/finalized collections/MA Andover, Andover Center for History and Culture--INVENTORY .docx")};
		
		Collections collections = new Collections(collectionFiles);
		//write data to spreadsheet
		String outputPath = "src/main/resources/spreadsheet outputs/";
		collections.toSpreadsheet("parsed_collections", outputPath);
		collections.getSources().toSpreadsheet("parsed_collections", outputPath);
		collections.getEntries().toSpreadsheet("parsed_collections", outputPath);

//		//write collection data to database		
//		String schema = "collections_2", 						//database 					
//				databasePath = "jdbc:mysql://localhost:3306/",		//information
//				user = "root",
//				password = "password";
//		collections.toDatabase(databasePath, schema, "collections", user, password);
//		collections.getSources().toDatabase(databasePath, schema, "sources", user, password);
//		collections.getEntries().toDatabase(databasePath, schema, "entries", user, password);
		
	}
}