package parseTunes1;

import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class StyleReader {
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("MA Boston, Congregational Library and Archives--INVENTORY.docx");
//			FileInputStream fis = new FileInputStream("Atwill n. d. in Nym Cooke collection (1).docx");
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
			List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
			for (XWPFParagraph paragraph : paragraphList) {

				for (XWPFRun rn : paragraph.getRuns()) {
//					if(rn.isSmallCaps()) {
//						System.out.println(rn.toString() + " is small caps");
//					}
					if(rn.isBold()) {
						System.out.println(rn.toString() + " is bold");
					}
//					System.out.println(rn.isItalic());
					System.out.println(rn.toString());
//					System.out.println("Small caps?: " + rn.isSmallCaps());
//					System.out.println(rn.isBold());
//					System.out.println(rn.isHighlighted());
//					System.out.println(rn.isCapitalized());
//					System.out.println(rn.getFontSize());
//					System.out.println();
				}

				System.out.println("********************************************************************");
			}
			xdoc.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
