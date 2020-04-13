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

public class ParseMusicEntries3 {
	 //TODO 195 not recorded
	//TODO source 97 with 11 sources
	//TODO remove periods at beginning of descriptions that spill over from title
	//TODO inscription no spillover

	FileInputStream fis = null;					//input stream to contain .docx file being analyzed
	XWPFDocument xdoc = null;					//.docx reader
	List<XWPFParagraph> paragraphList = null;	//list of paragraphs in docx file obtained from .docx reader
	FileOutputStream fos = null,				//output stream for parsed information
					dataDumpStream = null;		//output stream for data that could not be parsed
	String file;
	int dumpCount = 0,
			entryCount = 0,
			notIncipitCount = 0;							//number of entries placed in dump file
	String collection = null;						//collection name, which will be name of file being parsed
	byte[] buf = null;							//buffer for writing text to output stream
	
	String curParagraphText;				//text of current paragraph being analyzed
	int curParIndex = 0;								//index pf current paragraph in paragraph list
	XWPFParagraph curParagraph = null;					//actual current paragraph
	List<XWPFRun> curParagraphRuns = null;				//list of text "runs" contained within current paragraph, 
														//	which enable analysis of text properties, such as italic/bold
	XWPFRun curRun = null;								//current run being analyized
	Pattern pattern = Pattern.compile("^[\\d]+[\\.]");	//regexp that determines if text begins with an indeterminate number of digits followed by period
														//	which indicates source number
	Matcher matcher = null;								//matcher for detecting pattern occurrence above 

	StringBuilder curStr;			//used for building strings for various fields on as-needed basis -make string builder*** 
	
	//source/entry variables	
	int curSrc;											//current source number
	String srcCallNum = null;;							//call number for current source, indicated by bold text
	String curSourceAuthor = "";						//
	String curSourceTitle = "";							//
	String curSourceInscription = null;
	String curSourceDesc = "";							//description of current source, containing all details that cannot be parsed
														//	into individual fields
	//entries variables
	List<Boolean> isSecularList = null;		//list containing whether entry is secular					
	List<String> entries = null;			//list of text of entries for current source
	
	
	ParseMusicEntries3() {								//constructor with no parameters, mostly for testing purposes
		
	}
	
	ParseMusicEntries3(String fileName){
		curStr = new StringBuilder();			//used for building strings for various fields on as-needed basis -make string builder*** 
		file = fileName;
		try {
			
			long startTime = System.currentTimeMillis();
			fos = new FileOutputStream("output.txt");	//output for parsed information
			dataDumpStream = new FileOutputStream("dump.txt");	//output for information that could not be parsed
			fis = new FileInputStream(file);			//file being analyzed
			xdoc = new XWPFDocument(OPCPackage.open(fis));	//Apache POI .docx reader
			paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
			parseCollectionInfo();		//parse information about collection (current document), which consists of all information
										//occurring before first source			
			parseSourcesAndEntries();	//parse information about sources and entries
										//sources are initiated with a number followed by a period (23.),
										//each source may contain multiple entries, indicated by "MS Music Entries: "
			System.out.println("Total entries dumped: " + dumpCount);
			System.out.println("Total entries recorded: " + entryCount);
			System.out.println("Total not incipit: " + notIncipitCount);
			long endTime = System.currentTimeMillis();
			System.out.println("It took " + (endTime - startTime) + " milliseconds");
			//4/9 > 2827 for long doc
				//2716 after converting to stringbuilder
				//1542 after switching to entry objects instead of arrays
			
			xdoc.close();
			fos.close();
			fis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//get information about collection which ends when source number occurs at beginning of paragraph
	private void parseCollectionInfo(){
		StringBuilder collectionDesc = new StringBuilder();//description of current collection
		
		
		while(curParIndex < paragraphList.size()) {	//perform until beginning of source is found (see break below) or end of document reached
			curParagraphText = paragraphList.get(curParIndex).getText();	
			matcher = pattern.matcher(curParagraphText);	//matcher which utilizes regex to detect beginning of source
			//TODO delete matcher
			if(!matcher.find()) {											//if source number IS NOT found at beginning of paragraph
				collectionDesc.append(curParagraphText + "\n");				//add current paragraph to collection description	
			}
			
			else {														//if source number IS found
//				System.out.println(collectionDesc);			
				Collection collection = new Collection(file, collectionDesc.toString());
				strToFile(collection.toString() + "\n");						
				break;													//collection description is done. Move to source operations
			}
			curParIndex++;
		}
	}	
	//----------------------------------------------------------------------------------------------------	
	//perform source/entries operations
	private void parseSourcesAndEntries(){ 		
		curSrc = 0;							
//		List<Integer> intSrcEntries = new ArrayList<Integer>();			//list of source entries, to be deleted later for memory purposes**
//		intSrcEntries.add(curSrc);										//

		
		//source/entry operations
		while(curParIndex < paragraphList.size()) {		//perform until end of document is reached	
			
			//initialize variables for current paragraph
			curParagraph = paragraphList.get(curParIndex);		//get current xwpf paragraph being analyzed
			curParagraphText = curParagraph.getText();			//get current paragraph in string form
			matcher = pattern.matcher(curParagraphText);		//set matcher to current paragraph
			
			//if current paragraph starts with source number (should always be case after collection description done)
			
			//TODO set up sourceFound method
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
					
					//source title not found and current curRun is not italicized, or if it is italicized, text is not "sic"
					//(sic can give false positive for title detection, as it is written in italic but does not represent title)
					if(curSourceTitle.length() == 0 && (!curRun.isItalic() || curRun.toString().toLowerCase().indexOf("sic") == 0)) {		
						curStr.append(curRun.toString());			//		^^indicates text between source no. and title, which is author
					}
					
					else if(curSourceTitle.length() != 0) {		//if title has been recorded, all proceeding text is source description
						curStr.append(curRun.toString());			//		add to current description

						if(curSrc == 157) System.out.println(curParagraphText);
					}
					
					else {									//curRun that is italicized on same line as source number is book title
						i = getSrcTitleAuthor(i);			//record title and author and set index to where method left off
					}					
				}

				curStr.append("\n");
//				System.out.println("Source: " + curSrc);
//				System.out.println("Author: "  +  curSourceAuthor);
//				System.out.println("Title: "  +  curSourceTitle);
//				break;
			}
			
			else if(curParagraphText.toLowerCase().indexOf("inscription:") != -1 || curParagraphText.toLowerCase().indexOf("inscriptions:") != -1) {	//if inscription description detected
				curSourceInscription = curParagraphText.substring(curParagraphText.indexOf(":") + 2);	//pull text from paragraph, discard
																									//"inscriptions: "
			}
			
			//if current paragraph contains call number, which is denoted by a run of bold text
			
			else if(hasCallNumber(curParagraph)) {
				srcCallNum = getCallNumber(curParagraph);		//record call number
			}
			
			//if start of music entries are indicated to be present in source
			//parse music entries for current source
//			else if(curParagraphText.indexOf("MS. music entries:") != -1) {
			else if(entryFound(curParIndex)) {
				//record previous entry
//				System.out.println(sourceInfoToString());
				if(curParagraphText.indexOf("MS. music entries:") == -1) {	//if "MS. music entries:" is used to indicate start of entries in next paragraph,
				//this indicates that no relevant information is present in "indicator" (as opposed to ways entries are indicated, which contain
				// info relevant to entries in that source. so if this is not the case, record indicator
					
					
				}		
				curParIndex++;						//increment paragraph index. "ms music entries" line does not need to be recorded	
//				strToFile(sourceInfoToString());	//write current source information to file
				
				strToFile("\nxxxxxxxxxxxxxxxx Start of Source Entries xxxxxxxxxxxxxxxx\n\n");
				parseEntrySection();			//parse entries for current
				strToFile("\nxxxxxxxxxxxxxxxx End of Source Entries xxxxxxxxxxxxxxxx\n\n");			
				curParIndex--;
			}
			
			else {
//				System.out.println("else");
				if(curSrc == 157) System.out.println(curParagraphText);
				curStr.append(curParagraphText + "\n");
			}
			curParIndex++;
		}		
	}
	//----------------------------------------------------------------------
	//search current run for call number and return call number in string form
	private String getCallNumber(XWPFParagraph par) {	
		String callNumStr = "";
		for(XWPFRun run: par.getRuns()) {
			if(run.isBold())	//call number indicated by bold text
				callNumStr += run.toString();
		}
		return callNumStr.length() > 0 ? callNumStr : null; 
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
		Entry entry;

//		ArrayList<Entry> entryList = new ArrayList<Entry>();		
		
		
		//format entries into individual strings in preparation for analysis
		getEntryStringListAndSecular();
		
		//separate entries into fields
		System.out.println("Current source: " + curSrc + ", " + entries.size());
		
		//parse each entry from string form into array organized by field
		for(int i = 0; i < entries.size(); i++) {
			
//			entryList.add(new Entry(entries.get(i), isSecularList.get(i)));
			entry = new Entry(entries.get(i), isSecularList.get(i));
			strToFile(entry.toString() + "\n");
//			parseEntry(entries.get(i), i);
		}
	}	
	//----------------------------------------------------------------	
	//construct arrayList of 
	private void getEntryStringListAndSecular() {
		isSecularList = new ArrayList<Boolean>();								//array containing whether each entry is secular
		entries = new ArrayList<String>();										//list with entries in string form
		boolean curEntrySecular = true;											//detects if entry is secular, true by default, false when small caps detected
		StringBuilder entryStrBuilder = new StringBuilder();					//sb for progressively building entry string
		matcher = pattern.matcher(paragraphList.get(curParIndex).getText());	//matcher that detects beginning of source, indicated by a number followed by a period	
		//while end of document has not been reached and new source is not found (as indicated by matcher), 
		//and no call number detected (call numbers can occur after entry selection and before new source)
		//separate each entry into a text of its own in preparation for analysis
		while(curParIndex < paragraphList.size() && !matcher.find() && !hasCallNumber(paragraphList.get(curParIndex))) {			
			curParagraph = paragraphList.get(curParIndex);		//current XWPFparagraph
			curParagraphText = curParagraph.getText()			//get text of current paragraph
					.replaceAll(Character.toString((char)160),"")
					.replace("	", "");							//remove whitespace caused by tabbing
			
			//separate each entry into its own string and record if secular
			if(curParagraphText.indexOf(":") != -1 && entryStrBuilder.length() > 0) {			
				//: indicates start of new entry and > 0 indicates non-first entry
				// (first sources sometimes do not have a : 
				
				//record previous entry information
				entries.add(entryStrBuilder.toString());			//add previous entry to arraylist	
				isSecularList.add(curEntrySecular);			//record if entry was secular
				curEntrySecular = true;						//current entry secular by default (may have been changed by isSecularArray()
				entryStrBuilder = new StringBuilder();		//initiate next entry
				
			}	
			
			entryStrBuilder.append(curParagraphText);		//add current paragraph to current entry string
			
			if(!isSecular(curParagraph)) {					//determine if current run indicates non-secular entry
				curEntrySecular = false;					//	if so, entry is not secular
			}	
			
			curParIndex++;									//move to next paragraph
			if(curParIndex < paragraphList.size()) {
				matcher = pattern.matcher(paragraphList.get(curParIndex).getText());	//prepare matcher prior to next iteration
			}
		}
		
		entries.add(entryStrBuilder.toString());			//add last entry of current source		
		isSecularList.add(curEntrySecular);					//^^
	}	
	
	
	//--------------------------------------------------------------------
	
	//invoked when first italicized text run occurrence is found after source number, which represents source title
	//returns integer containing index of where title left off
	//parameter is starting index of source title
	private int getSrcTitleAuthor(int index) {		
		
		curSourceTitle = curRun.toString();		//record source title
		curSourceAuthor = curStr.toString();				//text between source number and source title is author
		
		while(curParagraphRuns.get(index + 1).isItalic()) {	//add each text run to source title while text is still italicized
			curSourceTitle += curParagraphRuns.get(index + 1).toString();	
			index++;										//increment
		}
		
		curStr.setLength(0);							//reset string to record description
		return index;
	}
	
	private int getSourceNumber(String text) {
		matcher = pattern.matcher(text);		//set matcher to current paragraph
		System.out.println("Get source text " + text);
		return Integer.parseInt(text.substring(matcher.start(), matcher.end() - 1));		
	}
	
	private void resetEntries() {		
		curSourceAuthor = "";
		curSourceTitle = "";
		curSourceDesc = "";
		curSourceInscription = null;
		curStr.setLength(0);
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
				"\nDescription: "  +  curStr.toString() +
				"\nCall number: " + srcCallNum + 				
				"\nInscription(s): " + curSourceInscription +
				"\n----------------end of source-------------\n\n";
	}
	
	//TODO entry app
	//return array containing [0]: tune title and [1]: tune author by parsing string containing title and author
	public static String[] parseTitleAndCredit(String str) {
		
		String[] titleCredit = new String[2];				//array that will contain title and author
		String[] authorIndicators = {"[by", "-by", " by "};			//strings that indicate presence of author
		//check to see if any author indicators occur in parameter string
		int authorIndex = -1;						//no matches by default
		
		for(String indicatorStr: authorIndicators) {
			if(str.indexOf(indicatorStr) != -1) {			//if there is match
			authorIndex = str.indexOf(indicatorStr);		//record index of match
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
	
	//TODO entry app, delete
	//shift cells of input array right starting at index parameter
	public String[] shiftCellsRight(String[] strArr, int startIndex) {
		for(int i = strArr.length - 1; i > startIndex; i--) {
			strArr[i] = strArr[i - 1];
		}
		strArr[startIndex] = null;
		return strArr;
	}
	
	//TODO entry app...either make void or leave copy here?
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
	
	public boolean hasMelodicIncipit(XWPFParagraph par) {	//loose application if isMelodicIncipit method to detect if paragraph contains incipit	
		String curEnt = par.getText();
			if(isMelodicIncipit(curEnt) && !hasCallNumber(par))
				return true;
		return false;
	}
	
	//TODO rewrite this somehow so that works for collection, source, and entry objects
	//write string to .txt file
	private void strToFile(String str) { 
		buf = str.getBytes();
		try {
			fos.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//-------------------------------------------------------------------------------------------
	//determine next paragraph will contain entry
	private boolean entryFound(int index) {		
		
		return (curParagraphText.indexOf("MS. music entries:") != -1) || //most direct indication of music entries
				
		//less direct indications
		(curParagraphText.indexOf("MS.") != -1 &&		//contains keyword 
		(curParagraphText.indexOf("music") != -1 || curParagraphText.indexOf("entries") != -1) &&	//contains either keyword
		curParagraphText.indexOf(":") != -1 &&
		curParagraphText.indexOf(":")  > curParagraphText.indexOf("MS.") &&	//color occurs after MS. this removes false positives
		paragraphList.get(curParIndex + 1).getText().indexOf("MS.") == -1 &&	//indication of common false positive when MS. in next par
		
		//melodic incipit detected after current paragraph
		(hasMelodicIncipit(paragraphList.get(curParIndex + 1)) || 
				(paragraphList.get(curParIndex + 1).getText().indexOf(":") < 60 && paragraphList.get(curParIndex + 1).getText().indexOf(":") >= 0)));
		
	}
	
	
//	private boolean sourceFound(String text) {
////		Pattern pattern = Pattern.compile("^[\\d]+[\\.]");	//regexp that determines if text begins with an indeterminate number of digits followed by period
//		//	which indicates source number
//		Matcher matcher = pattern.matcher(text);			//matcher for detecting pattern occurrence above
//		if(matcher.find()) {
//			System.out.println("Sourcefound text " + text + " " + Integer.parseInt(text.substring(matcher.start(), matcher.end() - 1)));
//		}
//		boolean found = matcher.find();
//		return found;
//	}
	
	private int getSourceNumber(String parText, Matcher match) {
		return Integer.parseInt(parText.substring(match.start(), match.end() - 1));
	}
	
}
	