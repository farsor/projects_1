package parseMusicCollection;

/**
 * parses music handwritten inscriptions in music collections. Within each collection are numerous sources.
 * Each source may contain numerous entries (handwritten melodic incipits, which are sometimes accompanied by text incipits)
 * Author recorded these into .docx Word files with consistent syntax, making it possible to parse collections into table format
 */

import java.io.FileInputStream;
import java.util.List;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class ParseMusicCollection {
	
	FileInputStream fis = null;					//input stream to contain .docx file being analyzed
	XWPFDocument xdoc = null;					//.docx reader
	List<XWPFParagraph> paragraphList = null;	//list of paragraphs in docx file obtained from .docx reader
	Collection[] collections;
	private String[] entryFields = {"collection_name", "source_number", "entry_location", 	//labels for entry table columns
			"entry_title", "entry_credit", "entry_vocal_part",
			"entry_key", "entry_melodic_incipit", "entry_text_incipit", "entry_is_secular"};
	private String[] sourceFields = {"collection_name", "source_number", "source_call_number", //labels for source table columns
			"source_author", "source_title", "source_inscription", "source_description"};
	private String[] collectionFields = {"collection_name", "collection_description"};					//labels for collection table columns
	
	ParseMusicCollection(String file){
		try {
			fis = new FileInputStream(file);			//file being analyzed
			xdoc = new XWPFDocument(OPCPackage.open(fis));	//Apache POI .docx reader
			paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
			collections = new Collection[1];
			collections[0]= new Collection(paragraphList, file);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	ParseMusicCollection(String[] files){
		collections = new Collection[files.length];
		try {			
			long startTime = System.currentTimeMillis();
			for(int i = 0; i < files.length; i++) {
				fis = new FileInputStream(files[i]);			//file being analyzed
				xdoc = new XWPFDocument(OPCPackage.open(fis));	//Apache POI .docx reader
				paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
				collections[i]= new Collection(paragraphList, files[i]);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("It took " + (endTime - startTime) + " milliseconds");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void toSpreadsheets(String collectionFileName, String sourceFileName, String entryFileName) {
		collectionToSpreadSheet(collectionFileName);
		sourcesToSpreadsheet(sourceFileName);
		entriesToSpreadsheet(entryFileName);		
	}	
	//---------------------------------------------------------------------------------
	public void collectionToSpreadSheet(String fileName) {				//record all sources within collection to spreadsheet
		try {
			SpreadsheetWriter sw = new SpreadsheetWriter(collectionFields, fileName);
			Collection curCollection;
			for(int i = 0; i < collections.length; i++) {
				curCollection = collections[i];
				sw.writeRow(curCollection.toArray());
			}
				sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	//---------------------------------------------------------------------------------
	public void sourcesToSpreadsheet(String fileName) {				//record all sources within collection to spreadsheet
		try {
		
			SpreadsheetWriter sw = new SpreadsheetWriter(sourceFields, fileName);
			for(Collection collection: collections) {				//for all collections
				for(Source source:  collection.getSources()) {		//for all sources within collection
					sw.writeRow(source.toArray());					//write source information to spreadsheet
					}
			}
			sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	//---------------------------------------------------------------------------------
	public void entriesToSpreadsheet(String fileName) {
		try {
			SpreadsheetWriter sw = new SpreadsheetWriter(entryFields, fileName);
			for(Collection collection: collections) {
				for(Source source: collection.getSources()) {		//for all sources in collectioncurSource = collection.getSources().get(i);					//current source
					for(Entry entry: source.getEntries()) {	//for all entries within current sources
						sw.writeRow(entry.toArray());	//write entry information to spreadsheet
						}
					}
			}
			sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	public void collectionToDatabase() {
		DatabaseWriter db = new DatabaseWriter("collections");
		String fields = db.fieldsToSQLStr(collectionFields);
		String rowStr;
		for(Collection collection: collections) {
			rowStr = db.entryToSQLStr(collection.toArray());
			db.writeData("collections", fields, rowStr);
		}
	}
	
	public void sourcesToDatabase() {
		DatabaseWriter db = new DatabaseWriter("collections");
		String fields = db.fieldsToSQLStr(sourceFields);
		String rowStr;
		for(Collection collection: collections) {
			for(Source source: collection.getSources()) {
				rowStr = db.entryToSQLStr(source.toArray());
				db.writeData("sources", fields, rowStr);
			}
		}
	}
	
	public void entriesToDatabase() {
		DatabaseWriter db = new DatabaseWriter("collections");
		String fields = db.fieldsToSQLStr(entryFields);
		String rowStr;
		for(Collection collection: collections) {
			for(Source source: collection.getSources()) {
				for(Entry entry: source.getEntries()) {
					rowStr = db.entryToSQLStr(entry.toArray());
					db.writeData("entries", fields, rowStr);
				}
			}
		}
	}
	

	
	
	
}
