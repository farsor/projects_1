package parseMusicEntries;

import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ParseMusicEntriesApp2 {
	public static void main(String[] args) {
		String curCollection = "MA Boston, Congregational Library and Archives--INVENTORY (1).docx"; //name used for collection is file name
		String collectionDesc = "";			//description of current collection
		String curParagraphText;
		int curSrc;
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
			
			//source/entry operations
			while(curParIndex < paragraphList.size()) {
				curParagraph = paragraphList.get(curParIndex);
				curParagraphText = curParagraph.getText();
				matcher = pattern.matcher(curParagraphText);
				//if current paragraph starts with source number (should always be case after collection description done)
				if(matcher.find()) {					
					curSrc = Integer.parseInt(curParagraphText.substring(matcher.start(), matcher.end() - 1));
					System.out.println(curSrc);
					for (XWPFRun rn : paragraphList.get(curParIndex).getRuns()) {}
					break;
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