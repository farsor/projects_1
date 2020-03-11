package parseMusicEntries;
import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class ParseMusicEntries1_0 {
	public static void main(String[] args) {
		ParseMusicEntries pm = new ParseMusicEntries();
		char[] curParagraphChars = null;
		int curParCharsSize = 0;
		String curSrcNum = "";
		boolean possibleSrc = false;		//is possible source being analyzed?
		
		
		try {
			FileInputStream fis = new FileInputStream("MA Petersham, Nym Cooke collection INVENTORY (1).docx");
			FileInputStream fis = new FileInputStream("MSEntriesOnly.docx");
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
			List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
			for (XWPFParagraph paragraph : paragraphList) {
				curParagraphChars = paragraph.getText().toCharArray();
				curParCharsSize = curParagraphChars.length;
				for(int i = 0; i < curParCharsSize; i++) {
					//detect if starts with source number (make into class**)
					if(pm.isDigit(curParagraphChars[i])) { //is cur char digit? (class*)
						curSrcNum += curParagraphChars[i];	//add cur char to source number
					}
					else {
						if(curParagraphChars[i] == '.') {									//if period found
							if(possibleSrc) {												//and prior integers have been found
								System.out.println("Source Found: " + curSrcNum);			//source found
							}
						}
						else if(possibleSrc) {											//if character other than . found and possible source
							possibleSrc = false;										//no source found. reset to false;
							
						}
					}
				}
				System.out.println();
				for(char i: paragraph.getText().toCharArray()) {
					
				}
				System.out.println();

			}
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