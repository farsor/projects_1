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

public class PMEApp3 {
	public static void main(String[] args) {
		String curCollection = "test.docx"; //name used for collection is file name
//		String curCollection = "test.docx"; //name used for collection is file name
		String collectionDesc = "";			//description of current collection
		String curParagraphText;
		String srcCallNum = null;
		int curSrc = 0;
		Pattern pattern = Pattern.compile("^[\\d]+[\\.]");		
		Matcher matcher = null;
		int curParIndex = 0;
		XWPFParagraph curParagraph = null;
		
		try {
			FileInputStream fis = new FileInputStream(curCollection);			//file being analyzed
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));			//document reader
			List<XWPFParagraph> paragraphList = xdoc.getParagraphs();			//convert document to paragraphs
			
			//get information about collection which ends when source number occurs at beginning of paragraph
			//*create entry in collections database for current document ** later
			//*current collection as collection name
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
			System.out.println("***********End of Source Description********************\n\n\n");
			

			//source/entry variables
			String curSourceAuthor = "";
			String curSourceTitle = "";
			String curSourceDesc = "";
			String curStr = "";
			List<Integer> intSrcEntries = new ArrayList<Integer>();
			List<String[]> strSrcEntries = new ArrayList<String[]>();
			String[] curStrArr = new String[4];
			
			//source/entry operations
			while(curParIndex < paragraphList.size()) {		//perform until end of document is reached
				
				//initialize variables for current paragraph
				curParagraph = paragraphList.get(curParIndex);
				curParagraphText = curParagraph.getText();
				matcher = pattern.matcher(curParagraphText);
				List<XWPFRun> curParagraphRuns = null;
				XWPFRun curRun = null;
				
				//if current paragraph starts with source number (should always be case after collection description done)
				if(matcher.find()) {	
					
					if(curStr.length() != 0) {	//if this is not the first entry
						//record previous entry
//						System.out.println("Matcher found");
						System.out.println("Source: " + curSrc);
						System.out.println("Author: "  +  curSourceAuthor);
						System.out.println("Title: "  +  curSourceTitle);
						System.out.println("Call number: " + srcCallNum);
						System.out.println("Description: "  +  curStr);
						System.out.println("\n----------------end of source-------------\n");
						curStrArr[0] = curSourceAuthor;
						curStrArr[1] = curSourceTitle;
						curStrArr[2] = curStr;
						curStrArr[3] = srcCallNum;
						strSrcEntries.add(curStrArr);
						//reset entries for new source
						intSrcEntries.add(curSrc);
						curSourceAuthor = "";
						curSourceTitle = "";
						curSourceDesc = "";
						curStr = "";
						srcCallNum = null;
					}
						
					
					//cycle through paragraph runs and extract title and author
					curSrc = Integer.parseInt(curParagraphText.substring(matcher.start(), matcher.end() - 1));
//					System.out.println(curSrc);
					
					//analyze runs of current paragraph to extract title and author
					for (int i = 0; i < curParagraph.getRuns().size(); i++) {
						curParagraphRuns = curParagraph.getRuns();
						curRun = curParagraphRuns.get(i);
						matcher = pattern.matcher(curRun.toString());	
						System.out.println(curSourceTitle.length());
						System.out.println(!curRun.isItalic());
						if(matcher.find())			//if current curRun is source title, disregard *ideal algorithm will just start at 2nd curRun
							continue;	
				
						if(curSourceTitle.length() == 0 && !curRun.isItalic()) {		//source title not found and current curRun is not source title
							System.out.println("Adding to Author************************" + curStr);
							curStr += curRun.toString();
						}
						else if(curSourceTitle.length() != 0) {		//source title found, so text being added to description
							curStr += curRun.toString();
						}
						
						else {									//curRun that is italicized on same line as source number is book title
							curSourceTitle += curRun.toString();	//record source title
							curSourceAuthor = curStr;			//text between source number and source title is author
							while(curParagraphRuns.get(i+ 1).isItalic()) {
								curSourceTitle += curParagraphRuns.get(i + 1).toString();
								i++;
							}
							curStr = "";						//reset string to record description
						}
						
					}
//					System.out.println("Source: " + curSrc);
//					System.out.println("Author: "  +  curSourceAuthor);
//					System.out.println("Title: "  +  curSourceTitle);
//					break;
				}
				
				else if(curParagraph.getRuns().size() > 1 && curParagraph.getRuns().get(1).isBold()) {
					srcCallNum = curParagraph.getRuns().get(1).toString();
				}
				
				else if(curParagraphText.indexOf("MS. music entries:") != -1) {
					//record previous entry
					System.out.println("Source: " + curSrc);
					System.out.println("Author: "  +  curSourceAuthor);
					System.out.println("Title: "  +  curSourceTitle);
					System.out.println("Call Number: " + srcCallNum);			
					System.out.println("Description: "  +  curStr);				
					curStrArr[0] = curSourceAuthor;
					curStrArr[1] = curSourceTitle;
					curStrArr[2] = curSourceDesc;
					curStrArr[3] = srcCallNum;
					strSrcEntries.add(curStrArr);
					break;
				}
				
				else {
//					System.out.println("else");
					curStr += curParagraphText + "\n";
				}
				curParIndex++;
			}
				

			

			xdoc.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

//break;
//System.out.println(paragraph.getText());
//System.out.println(paragraph.getAlignment());
//System.out.print(paragraph.getRuns().size());
//System.out.println(paragraph.getStyle());
//// Returns numbering format for this paragraph, eg bullet or lowerLetter.
//System.out.println(paragraph.getNumFmt());
//System.out.println(paragraph.getAlignment());
//System.out.println(paragraph.isWordWrapped());
//System.out.println("********************************************************************");