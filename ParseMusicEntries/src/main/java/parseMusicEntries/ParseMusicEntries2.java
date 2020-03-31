package parseMusicEntries;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ParseMusicEntries2 {
	

	FileInputStream fis = null;					//input stream to contain .docx file being analyzed
	XWPFDocument xdoc = null;					//.docx reader
	List<XWPFParagraph> paragraphList = null;	//list of paragraphs in docx file obtained from .docx reader
	FileOutputStream fos = null,				//output stream for parsed information
					dataDumpStream = null;		//output stream for data that could not be parsed
	byte[] buf = null;							//buffer for writing text to output stream
	
	String curParagraphText;				//text of current paragraph being analyzed
	int curParIndex = 0;								//index pf current paragraph in paragraph list
	XWPFParagraph curParagraph = null;					//actual current paragraph
	List<XWPFRun> curParagraphRuns = null;				//list of text "runs" contained within current paragraph, 
														//	which enable analysis of text properties, such as italic/bold
	XWPFRun curRun = null;								//current run being analyized
	Pattern pattern = Pattern.compile("^[\\d]+[\\.]");	//regexp that determines if text begins with a number of indeterminate digits followed by period
														//	which indicates source number
	Matcher matcher = null;								//matcher for detecting pattern occurrence above 

	String curStr = "";			//used for building strings for various fields on as-needed basis -make string builder*** 
	
	//source/entry variables	
	int curSrc;											//current source number
	String srcCallNum = null;;							//call number for current source, indicated by bold text
	String curSourceAuthor = "";						//
	String curSourceTitle = "";							//
	String curSourceDesc = "";							//description of current source, containing all details that cannot be parsed
														//	into individual fields
//	List<String[]> strSrcEntries = new ArrayList<String[]>();	//list of all source information, delete eventually **********
	String[] curStrArr = new String[4];					//array containing source information
	
	//entries variables
	List<Boolean> isSecularArr = null;		//list containing whether entry is secular					
	List<String> entries = null;			//list of text of entries for current source
	String tunePage = null;
	String[] titleAndCredit = null;
	String[] splitEntries = null;
	String[] fullTuneEntry = null;			//entry containing all string values for current entry
	String[] entryLabels = {"tune_page", "tune_title", "tune_credit", "tune_vocal_part",		//labels corresponding to fields
								"tune_key", "tune_melodic_incipit", "tune_text_incipit"};
	
	ParseMusicEntries2() {								//constructor with no parameters, mostly for testing purposes
		
	}
	
	ParseMusicEntries2(String fileName){
		try {
			fos = new FileOutputStream("output.txt");	//output for parsed information
			dataDumpStream = new FileOutputStream("dump.txt");	//output for information that could not be parsed
			fis = new FileInputStream(fileName);			//file being analyzed
			xdoc = new XWPFDocument(OPCPackage.open(fis));	//Apache POI .docx reader
			paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
			parseCollectionInfo();		//parse information about collection (current document), which consists of all information
										//occurring before first source			
			parseSourcesAndEntries();	//parse information about sources and entries
										//sources are initiated with a number followed by a period (23.),
										//each source may contain multiple entries, indicated by "MS Music Entries: "
			
			xdoc.close();
			fos.close();
			fis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//get information about collection which ends when source number occurs at beginning of paragraph
	private void parseCollectionInfo(){
		String collectionDesc = "";			//description of current collection
		
		
		while(curParIndex < paragraphList.size()) {	//perform until beginning of source is found (see break below) or end of document reached
			curParagraphText = paragraphList.get(curParIndex).getText();	
			matcher = pattern.matcher(curParagraphText);	//matcher which utilizes regex to detect beginning of source
			
			if(!matcher.find()) {											//if source number IS NOT found at beginning of paragraph
				collectionDesc += (curParagraphText + "\n");				//add current paragraph to collection description	
			}
			
			else {														//if source number IS found
//				System.out.println(collectionDesc);			
				strToFile(collectionDesc + "\n");						
				break;													//collection description is done. Move to source operations
			}
			curParIndex++;
		}
//		System.out.println("***********End of Collection Description********************\n\n\n");
		strToFile("***********End of Collection Description********************\n\n\n\n");
		
	}	
	//----------------------------------------------------------------------------------------------------	
	//perform source/entries operations
	private void parseSourcesAndEntries(){ 		
		curSrc = 0;							
		List<Integer> intSrcEntries = new ArrayList<Integer>();			//list of source entries, to be deleted later for memory purposes**
		intSrcEntries.add(curSrc);										//

		
		//source/entry operations
		while(curParIndex < paragraphList.size()) {		//perform until end of document is reached	
			
			//initialize variables for current paragraph
			curParagraph = paragraphList.get(curParIndex);		//get current xwpf paragraph being analyzed
			curParagraphText = curParagraph.getText();			//get current paragraph in string form
			matcher = pattern.matcher(curParagraphText);		//set matcher to current paragraph
			
			//if current paragraph starts with source number (should always be case after collection description done)
			if(matcher.find()) {	//check for source number, which indicates end of last source and beginning of new source				
				
				//if this is not the first entry of collection, record previous source information
				if(curStr.length() != 0) {	
//					System.out.println(sourceInfoToString());
					strToFile(sourceInfoToString());		
					resetEntries();			//reset entries for new source
				}
				
				curSrc = getSourceNumber(curParagraphText, matcher);		//extract source number from paragraph-needs to be updated for title spanning more than one paragraph***
				
				curParagraphRuns = curParagraph.getRuns();						//get list of runs for current paragraph							
				//analyze runs of current paragraph to extract title and author
				//start at index 1 because first run contains source number and does not need to be analyzed
				for (int i = 1; i < curParagraph.getRuns().size(); i++) {
					curRun = curParagraphRuns.get(i);								//get run at current index from list of runs
					
					if(curSourceTitle.length() == 0 && !curRun.isItalic()) {		//source title not found and current curRun is not source title
						curStr += curRun.toString();								//		^^indicates text between source no. and title, which is author
					}
					
					else if(curSourceTitle.length() != 0) {		//if title has been recorded, all proceeding text is source description
						curStr += curRun.toString();			//		add to current description
					}
					
					else {									//curRun that is italicized on same line as source number is book title
						i = getSrcTitleAuthor(i);
					}
					
				}
//				System.out.println("Source: " + curSrc);
//				System.out.println("Author: "  +  curSourceAuthor);
//				System.out.println("Title: "  +  curSourceTitle);
//				break;
			}
			
			//if current paragraph contains call number, which is denoted by a run of bold text
			
			else if(hasCallNumber(curParagraph)) {
				srcCallNum = getCallNumber(curParagraph);		//record call number
			}
			
			//if start of music entries are indicated to be present in source
			//parse music entries for current source
//			else if(curParagraphText.indexOf("MS. music entries:") != -1) {
			else if((curParagraphText.indexOf("MS.") != -1) && curParagraphText.indexOf(":") != -1 && hasMelodicIncipit(paragraphList.get(curParIndex + 1))) {
				//record previous entry
//				System.out.println(sourceInfoToString());
				curParIndex++;						//increment paragraph index. "ms music entries" line does not need to be recorded
				
//				strToFile(sourceInfoToString());	//write current source information to file
				
				strToFile("\nxxxxxxxxxxxxxxxx Start of Source Entries xxxxxxxxxxxxxxxx\n\n");
				parseEntrySection();			//parse entries for current
				strToFile("\nxxxxxxxxxxxxxxxx End of Source Entries xxxxxxxxxxxxxxxx\n\n");
				curParIndex--;
			}
			
			else {
//				System.out.println("else");
				curStr += curParagraphText + "\n";
			}
			curParIndex++;
		}		
	}
	//----------------------------------------------------------------------
	//search current run for call number and return call number in string form
	private String getCallNumber(XWPFParagraph par) {	
		for(XWPFRun run: par.getRuns()) {
			if(run.isBold())	//call number indicated by bold text
				return run.toString();
		}
		return null;
	}
				
	//-----------------------------------------------------------------------
	//check to see if current paragraph contains a call number, as indicated by bold text
	private boolean hasCallNumber(XWPFParagraph par) {
		for(XWPFRun run: par.getRuns()) {
			if(run.isBold())
				return true;
		}			
		return false;		
	}
	
	//------------------------------------------------------------------
	//parse current section of entries
	private void parseEntrySection() {		
		
		//format entries into individual strings in preparation for analysis
		getEntryStringList();
		
		//separate entries into fields
		System.out.println(entries.size());
		
		//parse each entry from string form into array organized by field
		for(String entry: entries) {	
			parseEntry(entry);
		}
	}
	//------------------------------------------------------------------
	
		//parse current entry from string format
		//write information to text file
		private void parseEntry(String curEntry) {
			if(curEntry.indexOf(":") != -1){
				tunePage = curEntry.substring(0, curEntry.indexOf(":"));				//get page number, which occurs up until colon character
			}
			else {
				tunePage = null;
			}
			
//			parseEntry(entry);
			
			curEntry = formatEntryStr(curEntry);								//format current entry, removing whitespace and setting up to optimize parsing operations
			
			
			splitEntries = curEntry.split(", ");							//split entry into fields using ", " as delimiter			
			
			//entries to string				
			//display/testing
//			if(splitEntries.length == 6) {					
				//display entries as separated
			for(String str: splitEntries) {					
//				System.out.print(str + "---");
				strToFile(str + "---");
			}
//			System.out.println();
			strToFile("\n");
//			}
			
			
//			System.out.println(splitEntries[3]);
			titleAndCredit = parseTitleAndCredit(splitEntries[0]);
			fullTuneEntry = new String[7];				//reset entry for current tune
			fullTuneEntry[0] = tunePage;
			fullTuneEntry[1] = titleAndCredit[0];
			fullTuneEntry[2] = titleAndCredit[1];
			if(splitEntries.length < 6){
				
				//splitArr to fullArr **create method
				for(int i = 1; i < splitEntries.length; i++) {
					fullTuneEntry[i + 2] = splitEntries[i];
				}
				
				//check to see if 
				if(fullTuneEntry[3] != null && fullTuneEntry[3].length() < 4 ) {
					shiftCellsRight(fullTuneEntry, 3);
				}						
				
				if(fullTuneEntry[4] != null && isMelodicIncipit(fullTuneEntry[4])) {
					shiftCellsRight(fullTuneEntry, 3);
				}
				
				if(fullTuneEntry[5] != null && fullTuneEntry[5].indexOf("mm.") != -1) {
					fullTuneEntry[5] += (", " + fullTuneEntry[6]);
					fullTuneEntry[6] = null;
				}
				
//				//display exceptions
//				if(!pme.isMelodicIncipit(fullTuneEntry[5])) {
//					for(int i = 0; i < fullTuneEntry.length; i++) {
//						System.out.println(entryLabels[i] + ": " + fullTuneEntry[i]);
//					}
//				}
				
				for(int i = 0; i < fullTuneEntry.length; i++) {
//					System.out.println(entryLabels[i] + ": " + fullTuneEntry[i]);
					strToFile(entryLabels[i] + ": " + fullTuneEntry[i] + "\n");
				}
				
//			System.out.println("*******************");
			strToFile("*******************\n");
				
			}
			
			else {
//				System.out.println("********Skipped**********");
				strToFile("********Skipped********** + \n");
			}
		}
		
	
	//----------------------------------------------------------------	
	//construct arrayList of 
	private void getEntryStringList() {
		isSecularArr = new ArrayList<Boolean>();					
		entries = new ArrayList<String>();
		boolean curEntrySecular = true;
		StringBuilder entryStrBuilder = new StringBuilder();
		matcher = pattern.matcher(paragraphList.get(curParIndex).getText());		
		//separate each entry into a text of its own in preparation for analysis
		while(curParIndex < paragraphList.size() && !matcher.find() && !hasCallNumber(paragraphList.get(curParIndex))) {			
			
			curParagraph = paragraphList.get(curParIndex);		//current XWPFparagraph
			curParagraphText = curParagraph.getText()			//get text of current paragraph
					.replace("	", "");							//remove whitespace caused by tabbing	
			
			//separate each entry into its own string and record if secular
			if(curParagraphText.indexOf(":") != -1 && entryStrBuilder.length() > 0) {			//: indicates start of new entry and > 0 indicates non-first entry
																		// (first sources sometimes do not have a : 
																//		if not, record previous source before proceeding
				entries.add(entryStrBuilder.toString());			//add previous entry to arraylist						
				isSecularArr.add(curEntrySecular);			//record if entry was secular
				curEntrySecular = true;						//current entry secular by default (may have been changed by isSecularArray()
				entryStrBuilder = new StringBuilder();									//initiate next entry
			}
			entryStrBuilder.append(curParagraphText);							//add current paragraph to current entry string
			if(!isSecular(curParagraph)) {					//determine if current run indicates non-secular entry
				curEntrySecular = false;						//	if so, entry is not secular
			}	
			curParIndex++;										//move to next paragraph
			matcher = pattern.matcher(paragraphList.get(curParIndex).getText());	//prepare matcher prior to next iteration
		}
	}	
	
	
	//--------------------------------------------------------------------
	
	//invoked when first italicized text run occurrence is found after source number, which represents source title
	//returns integer containing index of where title left off
	//parameter is starting index of source title
	private int getSrcTitleAuthor(int index) {		
		curSourceTitle = curRun.toString();		//record source title
		curSourceAuthor = curStr;				//text between source number and source title is author
		while(curParagraphRuns.get(index + 1).isItalic()) {	//add each text run to source title while text is still italicized
			curSourceTitle += curParagraphRuns.get(index + 1).toString();	
			index++;										//increment
		}
		curStr = "";							//reset string to record description
		return index;
	}
	
	private int getSourceNumber(String parText, Matcher match) {
		return Integer.parseInt(parText.substring(match.start(), match.end() - 1));
	}
	
	private void resetEntries() {
		curSourceAuthor = "";
		curSourceTitle = "";
		curSourceDesc = "";
		curStr = "";
		srcCallNum = null;
	}
	
//	private void recordSourceInfo() {
//		curStrArr[0] = curSourceAuthor;
//		curStrArr[1] = curSourceTitle;
//		curStrArr[2] = curSourceDesc;
//		curStrArr[3] = srcCallNum;
//		strSrcEntries.add(curStrArr);
//	}
	
	private String sourceInfoToString() {
		return "Source: " + curSrc +
				"\nAuthor: "  +  curSourceAuthor + 
				"\nTitle: "  +  curSourceTitle +
				"\nCall number: " + srcCallNum + 
				"\nDescription: "  +  curStr +
				"\n----------------end of source-------------\n\n";
	}
	
	//return array containing [0]: tune title and [1]: tune author by parsing string containing title and author
	public static String[] parseTitleAndCredit(String str) {
		String[] titleCredit = new String[2];				//array that will contain title and author
		String[] authorIndicators = {"[by", "-by"};			//strings that indicate presence of author
		//check to see if any author indicators occur in parameter string
		int authorIndex = -1;						//no matches by default
		for(String curStr: authorIndicators) {
			if(str.indexOf(curStr) != -1) {			//if there is match
			authorIndex = str.indexOf(curStr);		//record index of match
			}
		}
		//separate tune title and author into individual strings
		if(authorIndex != -1) {								//if author name is present
			titleCredit[0] = str.substring(0, authorIndex);	//text before [by is tune title [0]
			titleCredit[1] = str.substring(authorIndex, str.length());	//text where [by starts to end of string is tune author [1]
			return titleCredit;	
		}
		titleCredit[0] = str;								//if no author detected, return entire string as title with null author
		return titleCredit;									
		
	}
	
	//cycle through text runs in first paragraph of entry to determine if it is secular or not
	//secular entries are denoted by titles that are written in small caps
	boolean isSecular(XWPFParagraph par) {
		for(XWPFRun run: par.getRuns()) {	//check runs in current paragraph to see if any contain small caps font
			if(run.isSmallCaps()) 			//if any are small caps
				return false;				//return nonsecular
			}		
		return true;				//if no small caps detected in paragraph, entry is secular
	}
	
	public String[] shiftCellsRight(String[] strArr, int startIndex) {
		for(int i = strArr.length - 1; i > startIndex; i--) {
			strArr[i] = strArr[i - 1];
		}
		strArr[startIndex] = null;
		return strArr;
	}
	
	public boolean isMelodicIncipit(String str) {	//check if current entry is melodic incipit, indicated by more than three digits
		int digitCount = 0;		
		if(str == null)
			return false;
		for(char c: str.toCharArray()) {	//check if each character is digit, and increment count if so
			if(Character.isDigit(c)) {
				digitCount++;
			}
		}
		return (digitCount >=3 && str.indexOf("|") != -1) ? true: false;
	}
	
	public boolean hasMelodicIncipit(XWPFParagraph par) {	//loose application if isMelodicIncipit method to detect if paragraph contains incipit	
		String curEnt = par.getText();
			if(isMelodicIncipit(curEnt))
				return true;
		return false;
	}
	
	//write string to .txt file
	private void strToFile(String str) { 
		buf = str.getBytes();
		try {
			fos.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//format string of entry to format optimal for parsing
	private String formatEntryStr(String entryStr) {
		if(entryStr.indexOf(":") != -1){			//if entry includes page number, as indicated by ":"
			return entryStr.substring(entryStr.indexOf(": ") + 2, entryStr.length())		//remove page number from current entry to analyze rest of text
								.replace(",”", "”,")	//replace ," with ", so quotes are contained together
								.replace(", so", " so")	//remove false delimiter
								.replace(", but", " but")	// 	remove false delimiter
								.trim().replaceAll(" +", " ");	//trim extra spaces	
		}
		else {					//if no page number, no need to remove page number
			return entryStr.replace(",”", "”,")	//replace ," with ", so quotes are contained together
					.replace(", so", " so")	//remove false delimiter
					.replace(", but", " but")	// 	remove false delimiter
					.trim().replaceAll(" +", " ");	//trim extra spaces	
		}
	}
	

}
