package parseTunes1;

import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class GetParagraphs {
	public static void main(String[] args) {
		try {
//			FileInputStream fis = new FileInputStream("MA Petersham, Nym Cooke collection INVENTORY (1).docx");
			FileInputStream fis = new FileInputStream("MSEntriesOnly.docx");
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
			List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
			for (XWPFParagraph paragraph : paragraphList) {
				for(char i: paragraph.getText().toCharArray()) {
					System.out.format("%5s", i);
				}
				System.out.println();
				for(char i: paragraph.getText().toCharArray()) {
					System.out.format("%5s", (int)i);
				}
				System.out.println();
//				break;
//				System.out.println(paragraph.getText());
//				System.out.println(paragraph.getAlignment());
//				System.out.print(paragraph.getRuns().size());
//				System.out.println(paragraph.getStyle());
//				// Returns numbering format for this paragraph, eg bullet or lowerLetter.
//				System.out.println(paragraph.getNumFmt());
//				System.out.println(paragraph.getAlignment());
//				System.out.println(paragraph.isWordWrapped());
//				System.out.println("********************************************************************");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
