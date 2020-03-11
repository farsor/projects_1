package parseTunes1;
import java.io.*;


import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public class apache {
	public static void main(String[] args) {
	try {
	   FileInputStream fis = new FileInputStream("Atwill n. d. in Nym Cooke collection (1).docx");
	   XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
	   XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
	   System.out.println(extractor.getText());
	   System.out.println(xdoc.toString());
	   extractor.close();
	} catch(Exception ex) {
	    ex.printStackTrace();
	}		
	
//	try {
//		File file = new File("Atwill n. d. in Nym Cooke collection (1).docx");
//		   FileInputStream fis = new FileInputStream(file.getAbsolutePath());
//		   HWPFDocument hdoc = new HWPFDocument(fis);
//		   WordExtractor extractor = new WordExtractor(hdoc);
//		   System.out.println(extractor.getText().toString());
//		} catch(Exception ex) {
//		    ex.printStackTrace();
//		}	
	
	}
}
