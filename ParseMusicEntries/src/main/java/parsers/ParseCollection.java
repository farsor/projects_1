package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import objects.Collection;
import objects.Entry;
import objects.RoughEntries;
import objects.RoughEntry;
import objects.Source;

/**
 * 
 * 
 * Parse music collection and source information for music collection file in .docx Word format
 * 
 * @author Andrew
 *
 */

//TODO write test that ensures correct number of sources and entries?
public class ParseCollection {
	private Collection collection;
	private FileInputStream fis;

	//Apache POI variables
	private XWPFDocument xdoc;							//.docx reader
	private List<XWPFParagraph> paragraphList;			//list of paragraphs in docx file obtained from .docx reader
	private XWPFParagraph paragraphObj;
	private String paragraphText;
	private List<XWPFRun> paragraphRuns;				//list of text "runs" contained within current paragraph,
														//containing information about formatting	
	private XWPFRun curRun;
	int curParIndex = 0;
	
	//tallies count of possible errors in entry parsing, as indicated by no melodic incipit being detected in melodic incipit field
	public static int notIncipitCount = 0;
		
	private Pattern sourceNumberPattern = Pattern.compile("^[\\d]+[\\.]");	//determines if text begins with an indeterminate number of digits followed by period,
														//	which indicates source number
	private Matcher sourceNumberMatcher;								//matcher for detecting pattern occurrence above 

	private String collectionName;
	private StringBuilder collectionDescription,
					sourceDescription,
					sourceAuthor,
					sourceTitle;
	private Source source;
	
	//variables for creating rough entries
	private RoughEntries roughEntries;
	private RoughEntry roughEntry;
	boolean entryIsSecular;
	private StringBuilder entryStrBuilder;
	
	
	public ParseCollection() {
		
	}
	
	public ParseCollection(File file, Collection collection){
		this.collection = collection;
		parseAndSaveCollectionName(file);
		try {
			fis = new FileInputStream(file);				//Word file being parsed
			xdoc = new XWPFDocument(OPCPackage.open(fis));	//Apache POI .docx reader
			paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void execute() {
		try {
			initializeStringBuilders();
			parseCollectionDescription();	
			parseSourcesAndEntries();		
			logParsePerformance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println(collectionName);
	}
	
	private void parseAndSaveCollectionName(File file){
		//collection name derived from file name
		collectionName = file.getName().substring(0, file.getName().lastIndexOf("."));
		collection.setName(collectionName);
	}
	
	//get information about collection which ends when source number occurs at beginning of a paragraph
	private void parseCollectionDescription() throws Exception{
		initializeParagraphVariables();
		//all information prior to source number being found is collection info
		while(!endOfDocumentReached() && !sourceFound(paragraphObj)) {
			collectionDescription.append(paragraphText + "\n");	//add current paragraph to collection description			
			curParIndex++;
			initializeParagraphVariables();	//prep for next iteration
		}
		collection.setDescription(collectionDescription);
	}	
	private boolean endOfDocumentReached() {
		return curParIndex >= paragraphList.size();
	}	
	
	//determine if source number is found in string, as indicated by a number followed by a period
	private boolean sourceFound(XWPFParagraph paragraph) {
		sourceNumberMatcher = sourceNumberPattern.matcher(paragraph.getText());
		return sourceNumberMatcher.find();
	}
	

	//perform source/entries operations
	private void parseSourcesAndEntries() throws Exception{
		while(!endOfDocumentReached()) {
			initializeParagraphVariables();					//prepare variables for parsing
			if(sourceFound(paragraphObj)) {	//indicates end of last source and beginning of new source
				try {
					finalizePreviousSource();
				} catch (Exception e) {
					System.out.println("No previous source exists because this is first source found.");
				}
				initializeSource();	
				initializeStringBuilders();
				//author, title, and most of description to be found in same paragraph as source number
				parseAuthorTitleDescription();
			}			
			else if(isInscription(paragraphObj)) {	
				parseAndSaveInscription();
			}			
			else if(hasCallNumber(paragraphObj)) {	//TODO switch all to paragraph to alleviate confusion (parameter)
				parseAndSaveCallNumber();
			}
			else if(entryFound(paragraphObj)) {	
				parseAndSaveSourceEntries();
			}
			
			else {	//miscellaneous text added to description by default
				sourceDescription.append(paragraphText + "\n");
			}
			curParIndex++;
		}		
		finalizePreviousSource();	//final source
	}
	
	private void initializeStringBuilders() {
		collectionDescription = new StringBuilder();
		sourceDescription = new StringBuilder();
		sourceAuthor = new StringBuilder();
		sourceTitle = new StringBuilder();
	}
	
	//initialize paragraph information to prepare for analysis
	private void initializeParagraphVariables() {
		paragraphObj = paragraphList.get(curParIndex);		//get current xwpf paragraph being analyzed
		paragraphText = paragraphObj.getText();			//get current paragraph in string form
		paragraphRuns = paragraphObj.getRuns();						//get list of runs for current paragraph	
	}
	
	private int getSourceNumber(String text) {
		//extract source number from paragraph text
		return Integer.parseInt(text.substring(sourceNumberMatcher.start(), sourceNumberMatcher.end() - 1));
	}
	
	//record source information and reset string builder for next source
	private void finalizePreviousSource() {
		source.setAuthor(sourceAuthor.toString());		//text between source number and source title is author
		source.setTitle(sourceTitle.toString());
		source.setDescription(sourceDescription.toString().replace("**&", ":"));	//replace temp false delimiter colon symbol with real colon
		collection.addSource(source);
	}
	
	private void initializeSource() {
		source = new Source(collectionName, getSourceNumber(paragraphText));		
	}
	
	private void parseAuthorTitleDescription() {
		//analyze runs of current paragraph to extract title and author
		//start at index 1 because first run contains source number and does not need to be analyzed		
		for (int i = 1; i < paragraphObj.getRuns().size(); i++) {					
			curRun = paragraphRuns.get(i);								//get run at current index from list of runs
			if(isDescription(curRun)) {
				sourceDescription.append(curRun.toString());
			}
			else if(isAuthor(curRun)) {				//current run contains author information
				sourceAuthor.append(curRun.toString());
			}
			else if(isTitle(curRun)){
				sourceTitle.append(curRun.toString());
			}					
		}
		sourceDescription.append("\n");
	}
	
	private boolean isAuthor(XWPFRun run) {
		return ( source.getTitle() == null 					//title yet to be recorded (author information occurs before title information)
			&& (!run.isItalic()							//text not italicized (indicator of title)							
			|| (run.toString().toLowerCase().indexOf("sic") == 0)));	//or if italicized, italicized word is sic, which can occur int itle
	}
	
	private boolean isDescription(XWPFRun run) {
		//if title has been recorded, all other information in paragraph will be part of description
		return sourceTitle.length() != 0;
	}
	
	private boolean isTitle(XWPFRun run) {
		return (source.getTitle() == null						//if title has already been recorded, not title
			&& run.isItalic()									//first occurrence of italicized text in source is title							
			&& !(run.toString().toLowerCase().indexOf("sic") == 0));	//but make sure false indicator of title (sic) is not case 
	}
	
	private boolean isInscription(XWPFParagraph par) {
		
		return par.getText().toLowerCase().indexOf("inscription:") != -1 || //text contains one of two
				par.getText().toLowerCase().indexOf("inscriptions:") != -1;	//	indicators of inscription
	}
	
	private void parseAndSaveInscription() {
		String inscriptionText = getParsedInscription();
		source.setInscription(inscriptionText);
		
	}
	
	//pull inscription from document; invoked after inscription has already been detected
	private String getParsedInscription() {
		String inscription = paragraphText.substring(paragraphText.indexOf(":") + 2);	//inscription content starts after colon followed by space
		while(inscriptionContinues()) {
			inscription += "\n" + paragraphList.get(++curParIndex).getText();
		}
		return inscription;
	}
	
	private boolean inscriptionContinues() {
		//indentation indicates that inscription continues
		return paragraphList.get(curParIndex + 1).getText().startsWith("		");
	}
				
	private boolean hasCallNumber(XWPFParagraph paragraph) {
		for(XWPFRun run: paragraph.getRuns()) {
			if(run.isBold())					//call number indicated by bold text
				return true;
		}			
		return false;		
	}
	
	private void parseAndSaveCallNumber() {
		String callNumber = getCallNumber(paragraphObj);
		source.setCallNumber(callNumber);		
	}

	//search current run for call number and return call number in string form
	private String getCallNumber(XWPFParagraph paragraph) {	
		String callNumStr = "";
		for(XWPFRun run: paragraph.getRuns()) {
			if(run.isBold())	//call number indicated by bold text
				callNumStr += run.toString();
		}
		return callNumStr.length() > 0 ? callNumStr : null; 
	}

	private boolean entryFound(XWPFParagraph paragraph) {		
		String paragraphText = paragraph.getText();
		//check for various indicators of entries
		return hasCommonEntryIndicator(paragraphText) || //most common indicator of music entries
		(paragraphText.indexOf("MS.") != -1 &&		//contains keyword 
		(paragraphText.indexOf("music") != -1 || paragraphText.indexOf("entries") != -1) &&	//contains either keyword
		paragraphText.indexOf(":") != -1 &&
		paragraphText.indexOf(":")  > paragraphText.indexOf("MS.") &&	//coloN occurs after MS. this removes false positives
		paragraphList.get(curParIndex + 1).getText().indexOf("MS.") == -1 &&	//indication of common false positive when MS. in next par		
		//melodic incipit detected after current paragraph
		(hasMelodicIncipit(paragraphList.get(curParIndex + 1)) || 
				(paragraphList.get(curParIndex + 1).getText().indexOf(":") < 60 && paragraphList.get(curParIndex + 1).getText().indexOf(":") >= 0)));		
	}	
	
	private boolean hasCommonEntryIndicator(String text) {
		return text.indexOf("MS. music entries:") != -1;
	}
	
	private boolean hasMelodicIncipit(XWPFParagraph par) {	//loose application if isMelodicIncipit method to detect if paragraph contains incipit	
		String curEnt = par.getText();
			if(isMelodicIncipit(curEnt) && !hasCallNumber(par))
				return true;
		return false;
	}
	
	public boolean isMelodicIncipit(String str) {	//check if current entry is melodic incipit, indicated by more than three digits
		int digitCount = 0,				//total digits in string
				maxConsecDigits = 0,	//most consecutive digits that occur in a row in given string
				curConsec = 0;
		
		if(str == null)
			return false;
		
		for(char c: str.toCharArray()) {	//check if each character is digit, and increment count if so			
			if(Character.isDigit(c)) {
				digitCount++;
				curConsec++;
			}
			
			else {
				if(curConsec > maxConsecDigits) {
					maxConsecDigits = curConsec;
					curConsec = 0;
				}
			}
			if(curConsec > maxConsecDigits) {
				maxConsecDigits = curConsec;
			}
		}
		return ((digitCount >=3 && (str.indexOf("|") != -1) || str.indexOf("-") != -1) || maxConsecDigits  > 4|| digitCount >= 8) ? true: false;
	}

	//parse current section of entries
	private void parseAndSaveSourceEntries() throws Exception{
		constructRoughEntries();
		parseAndSaveEntries();
	}
	
	private void constructRoughEntries() {		
		curParIndex++;						//this will make erroneous text(entry indicator) be discarded
		roughEntries = new RoughEntries();
		initializeEntry();
		//separate each entry into a text of its own in preparation for analysis
		initializeParagraphVariables();
		while(isEntry(paragraphObj)) {			
			initializeEntryParagraph();
			if(isNewEntry(paragraphText) && !isFirstSourceEntry(entryStrBuilder)) {
				saveRoughEntry();
				initializeEntry();	//for next iteration
			}		
			if(!isSecular(paragraphObj)) {
				entryIsSecular = false;
			}			
			entryStrBuilder.append(paragraphText);	//add current paragraph to current entry string						
			curParIndex++;	
		}		
		curParIndex--;		//decrement index because it will be incremented an extra time in higher-level loop
		saveRoughEntry();	///last rough entry for source
	}	
	
	private void initializeEntry() {
		entryIsSecular = true;					//entry secular by default
		entryStrBuilder= new StringBuilder();			
	}
	
	private boolean isEntry(XWPFParagraph paragraph) {
		return curParIndex < paragraphList.size() && //end of document not reached
				!sourceFound(paragraph) 			//new source not found
				&& !hasCallNumber(paragraph);		//call number not found
	}
	
	private void initializeEntryParagraph() {
		paragraphObj = paragraphList.get(curParIndex);			//current XWPFparagraph
		//format text
		paragraphText = paragraphObj.getText()
				.replaceAll(Character.toString((char)160),"")	//remove non-whitespace space
				.replace("	", "");								//remove whitespace caused by tabbing
	}
	
	private boolean isNewEntry(String paragraphText) {
		return paragraphText.indexOf(":") != -1; //: indicates start of new entry within entry section
				
	}
	
	//determines if first entry of current source
	private boolean isFirstSourceEntry(StringBuilder entryText) {
		return entryText.length() == 0;			//if no entry text previously recorded, it is first entry
	}	
	
	private void saveRoughEntry() {
		roughEntry = new RoughEntry(entryStrBuilder, entryIsSecular);
		roughEntries.add(roughEntry);		
	}
	
	private void parseAndSaveEntries(){
		Entry entry;		
		for(RoughEntry roughEntry: roughEntries.toArrayList()) {
			//entry object parses rough entry upon construction
			entry = new Entry(collectionName, source.getSourceNumber(), roughEntry);  
			source.addEntry(entry);		//save to current source object
		}
	}
	
	@SuppressWarnings("unused")
	private void logSourceStats() {
		System.out.println("Current source: " + source.getSourceNumber() + ", Entries: " + roughEntries.getCount());
	}

	boolean isSecular(XWPFParagraph paragraph) {
		for(XWPFRun run: paragraph.getRuns()) {	
			if(run.isSmallCaps()) 			//presence of small caps denotes nonsecular entry
				return false;
			}		
		return true;	//if no small caps found
	}
	
	private void logParsePerformance() {
		System.out.println("Total entries recorded: " + collection.getSources().getEntryCount());
		System.out.println("Total not incipit: " + notIncipitCount);
	}
	
	
	
}
	