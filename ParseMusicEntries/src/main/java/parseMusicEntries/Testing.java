package parseMusicEntries;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class Testing {
	public static void main(String[] args) {
		ParseMusicEntries2 pme = new ParseMusicEntries2();
		String curCollection = "MA Boston test.docx"; //name used for collection is file name
//		String curCollection = "test.docx"; //name used for collection is file name
		String curParagraphText;
//		String collectionDesc = "";			//description of current collection
//		String srcCallNum = null;
//		int curSrc = 0;
//		Pattern pattern = Pattern.compile("^[\\d]+[\\.]");		
//		Matcher matcher = null;
		int curParIndex = 0;
		XWPFParagraph curParagraph = null;
		
		try {
			FileInputStream fis = new FileInputStream(curCollection);			//file being analyzed
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));			//document reader
			List<XWPFParagraph> paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
			
			String curSourceAuthor = "";
			String curSourceTitle = "";
			String curSourceDesc = "";
			List<Integer> intSrcEntries = new ArrayList<Integer>();
			List<String[]> strSrcEntries = new ArrayList<String[]>();
			List<Boolean> isSecularArr = new ArrayList<Boolean>();
			String[] curStrArr = new String[4];
			List<String> entries = new ArrayList<String>();
			boolean curEntrySecular = true;

			

			String curStr = "";
			//get entries
			//separate each entry into a text of its own in preparation for analysis
			while(curParIndex < paragraphList.size()) {				//for all paragraphs
				curParagraph = paragraphList.get(curParIndex);		//current XWPFparagraph
				curParagraphText = curParagraph.getText()			//get text of current paragraph
						.replace("	", "");							//remove whitespace caused by tabbing		
				//separate each entry into its own string and record if secular
				if(curParagraphText.indexOf(":") != -1) {			//: indicates start of new entry
																	//for current source
					if(curStr.length() > 0) {						//if length of curStr is 0, this is first entry for source, 
																	//		so no need to record previous entry
						entries.add(curStr);						//add previous entry to arraylist						
						isSecularArr.add(curEntrySecular);			//record if entry was secular
						curEntrySecular = true;						//current entry secular by default
					}
					curStr = "";									//initiate next entry
				}
				curStr += curParagraphText;							//add current paragraph to current entry string
				if(!pme.isSecular(curParagraph)) {					//determine if current run indicates non-secular entry
					curEntrySecular = false;						//	if so, entry is not secular
				}	
				curParIndex++;										//move to next paragraph
			}
			
			String tunePage = null,
					tuneTitle = null,
					tuneCredit = null,
					tuneVocalPart = null,
					tuneKey = null,
					tuneMelodic = null,
					tuneText = null;	
			String[] titleAndCredit = null;
			String[] splitEntries = null;
			String[] fullTuneEntry = null;			//entry containing all string values for current entry
			String[] entryLabels = {"tune_page", "tune_title", "tune_credit", "tune_vocal_part",		//labels corresponding to fields
										"tune_key", "tune_melodic_incipit", "tune_text_incipit"};
			//separate entries into fields
			System.out.println(entries.size());
			for(String entry: entries) {				
//				System.out.println(entry);
				tunePage = entry.substring(0, entry.indexOf(":"));				//get page number, which occurs up until colon character
				entry = entry.substring(entry.indexOf(": ") + 2, entry.length())		//remove page number from current entry to analyze rest of text
																.replace(",”", "”,")	//replace ," with ", for proper delimiter
																.replace(", so", " so")	//remove 
																.replace(", but", " but");	// 	false delimiters
				splitEntries = entry.split(", ");							//split entry into fields using ", " as delimiter	
				
				//entries to string				
				//display/testing
//				if(splitEntries.length == 6) {					
					//display entries as separated
				for(String str: splitEntries) {					
					System.out.print(str + "---");
				}
				System.out.println();
//				}
				
				
//				System.out.println(splitEntries[3]);
				titleAndCredit = pme.parseTitleAndCredit(splitEntries[0]);
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
						pme.shiftCellsRight(fullTuneEntry, 3);
					}						
					
					if(fullTuneEntry[4] != null && pme.isMelodicIncipit(fullTuneEntry[4])) {
						pme.shiftCellsRight(fullTuneEntry, 3);
					}
					
					if(fullTuneEntry[5] != null && fullTuneEntry[5].indexOf("mm.") != -1) {
						fullTuneEntry[5] += (", " + fullTuneEntry[6]);
						fullTuneEntry[6] = null;
					}
					
//					//display exceptions
//					if(!pme.isMelodicIncipit(fullTuneEntry[5])) {
//						for(int i = 0; i < fullTuneEntry.length; i++) {
//							System.out.println(entryLabels[i] + ": " + fullTuneEntry[i]);
//						}
//					}
					
					for(int i = 0; i < fullTuneEntry.length; i++) {
						System.out.println(entryLabels[i] + ": " + fullTuneEntry[i]);
					}
					
				System.out.println("*******************");
				
				}
				
				else {
					System.out.println("********Skipped**********");
				}
			}
			
			
		
			
			//display entries
//			for(int i = 0; i < entries.size(); i++) {
//				System.out.println(entries.get(i) + "			" + isSecularArr.get(i));				
//			}
			

			//list operations
//		List<String[]> strEntries = new ArrayList<String[]>();
//		String[] strArr = {"dog", "boi"};
//		strEntries.add(strArr);
//		System.out.println(strEntries.get(0)[1]);
		
//		//pattern/matcher testing
//		Pattern pattern = Pattern.compile("^[\\d]+[\\.]");
//		String str3 = "142.  Albee, Amos.  The Norfolk Collection"
//				+ " of Sacred Harmony.  Dedham: Herman Mann, 1805.  160 pp.; "
//				+ "lacks 4 unnumbered leaves at end, both covers; corne";
//		Matcher matcher = pattern.matcher(str3);
//		System.out.println(matcher.find());
//		System.out.println(matcher.end());
		

	////reg exp tests
	//String regexp = "^[\\d]+[\\.]";
	//String str= "123.";
	//String str2 = "415.";
	//System.out.println(str.indexOf(regexp));
	//System.out.println(str2.matches(regexp));
	//System.out.println(str3.matches(regexp));
		} catch (Exception ex) {
			ex.printStackTrace();
		}


	}

}


