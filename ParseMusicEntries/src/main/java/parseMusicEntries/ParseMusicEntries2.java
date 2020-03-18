package parseMusicEntries;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ParseMusicEntries2 {
	

	FileInputStream fis = null;
	XWPFDocument xdoc = null;
	List<XWPFParagraph> paragraphList = null;				
	
	String curParagraphText;				//text of current paragraph being analyzed
	Pattern pattern = Pattern.compile("^[\\d]+[\\.]");		
	Matcher matcher = null;
	int curParIndex = 0;
	XWPFParagraph curParagraph = null;
	List<XWPFRun> curParagraphRuns = null;
	XWPFRun curRun = null;	

	String curStr = "";;			//make stringbuilder 
	
	//source/entry variables	
	int curSrc;
	String srcCallNum = null;;		
	String curSourceAuthor = "";
	String curSourceTitle = "";
	String curSourceDesc = "";
	List<String[]> strSrcEntries = new ArrayList<String[]>();
	String[] curStrArr = new String[4];
	
	ParseMusicEntries2() {
		
	}
	
	ParseMusicEntries2(String fileName){
		try {
			fis = new FileInputStream(fileName);			//file being analyzed
			xdoc = new XWPFDocument(OPCPackage.open(fis));	//Apache POI .docx reader
			paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
			parseCollectionInfo();		//parse information about collection (current document), which consists of all information
										//occurring before first source			
			parseSourcesAndEntries();	//parse information about sources and entries
										//sources are initiated with a number followed by a period (23.),
										//each source may contain multiple entries, indicated by "MS Music Entries: "
			xdoc.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void createTables() {		//***This may not be good idea**
										//ideally should write to document as going so as not to create stack overflow
										//this will be in optimized version
		//createCollectionTbl()
		//createSourceTbl()
		//createEntriesTbl()
	}
	
	//get information about collection which ends when source number occurs at beginning of paragraph
	private void parseCollectionInfo(){
		String collectionDesc = "";			//description of current collection
		
		while(curParIndex < paragraphList.size()) {
			curParagraphText = paragraphList.get(curParIndex).getText();
			matcher = pattern.matcher(curParagraphText);
			
			if(!matcher.find()) {											//if source number IS NOT found at beginning of paragraph
				collectionDesc += (paragraphList.get(curParIndex).getText() + "\n");					
			}
			
			else {														//if source number IS found
				System.out.println(collectionDesc);
				break;													//collection description is done. Move to source operations
			}
			curParIndex++;				
		}
		System.out.println("***********End of Collection Description********************\n\n\n");
		
	}
	
	//perform source/entries operations
	private void parseSourcesAndEntries(){ 
		
		curSrc = 0;							
		//source/entry variables
	
		List<Integer> intSrcEntries = new ArrayList<Integer>();
		intSrcEntries.add(curSrc);

		
		//source/entry operations
		while(curParIndex < paragraphList.size()) {		//perform until end of document is reached				
			//initialize variables for current paragraph
			curParagraph = paragraphList.get(curParIndex);
			curParagraphText = curParagraph.getText();
			matcher = pattern.matcher(curParagraphText);				
			//if current paragraph starts with source number (should always be case after collection description done)
			if(matcher.find()) {					
				if(curStr.length() != 0) {	//if this is not the first entry of collection
					displaySourceInfo();
					recordSourceEntry();	//record previous entry					
					resetEntries();			//reset entries for new source
				}
				
				getSourceNumber();			//extract source number from paragraph---***			
				
				//cycle through paragraph runs and extract title and author				
				//analyze runs of current paragraph to extract title and author
				for (int i = 1; i < curParagraph.getRuns().size(); i++) {
					curParagraphRuns = curParagraph.getRuns();
					curRun = curParagraphRuns.get(i);
					
					if(curSourceTitle.length() == 0 && !curRun.isItalic()) {		//source title not found and current curRun is not source title
						curStr += curRun.toString();
					}
					
					else if(curSourceTitle.length() != 0) {		//source title found, so text being added to description
						curStr += curRun.toString();
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
			
			else if(curParagraph.getRuns().size() > 1 && curParagraph.getRuns().get(1).isBold()) {
				srcCallNum = curParagraph.getRuns().get(1).toString();
			}
			
			else if(curParagraphText.indexOf("MS. music entries:") != -1) {
				//record previous entry
				displaySourceInfo();
				recordSourceEntry();
				break;
			}
			
			else {
//				System.out.println("else");
				curStr += curParagraphText + "\n";
			}
			curParIndex++;
		}		
	}
	
	private int getSrcTitleAuthor(int i) {
		curSourceTitle = curRun.toString();//record source title
		curSourceAuthor = curStr;			//text between source number and source title is author
		while(curParagraphRuns.get(i+ 1).isItalic()) {
			curSourceTitle += curParagraphRuns.get(i + 1).toString();
			i++;
		}
		curStr = "";						//reset string to record description
		return i;
	}
	
	private void getSourceNumber() {
		curSrc = Integer.parseInt(curParagraphText.substring(matcher.start(), matcher.end() - 1));
	}
	
	private void resetEntries() {
		curSourceAuthor = "";
		curSourceTitle = "";
		curSourceDesc = "";
		curStr = "";
		srcCallNum = null;
	}
	
	private void recordSourceEntry() {
		curStrArr[0] = curSourceAuthor;
		curStrArr[1] = curSourceTitle;
		curStrArr[2] = curSourceDesc;
		curStrArr[3] = srcCallNum;
		strSrcEntries.add(curStrArr);
	}
	
	private void displaySourceInfo() {
		System.out.println("Source: " + curSrc);
		System.out.println("Author: "  +  curSourceAuthor);
		System.out.println("Title: "  +  curSourceTitle);
		System.out.println("Call number: " + srcCallNum);
		System.out.println("Description: "  +  curStr);
		System.out.println("\n----------------end of source-------------\n");
	}
	
	//return array containing [0]: tune title and [1]: tune author by parsing string containing title and author
	public static String[] parseTitleAndCredit(String str) {
		String[] titleCredit = new String[2];				//array that will contain title and author
		int authorIndex = -1;
		String[] authorIndicators = {"[by", "-by"};
		for(String curStr: authorIndicators) {
			if(str.indexOf(curStr) != -1) {
			authorIndex = str.indexOf(curStr);
			}
		}				//[by is indicator of author being present
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
		return digitCount >=3 ? true: false;
	}
	

}
