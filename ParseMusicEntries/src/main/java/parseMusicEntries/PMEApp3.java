package parseMusicEntries;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class PMEApp3 {
	public static void main(String[] args) {
		String curCollection = "finalized collections/CT Hartford, Watkinson Library, Trinity College--sacred music INVENTORY.docx"; //name used for collection is file name
//		String curCollection = "finalized collections/Atwill n. d. in Nym Cooke collection.docx"; //name used for collection is file name
		ParseMusicEntries2 pme = new ParseMusicEntries2(curCollection);
		Desktop desktop = Desktop.getDesktop();
	    try {
			desktop.open(new File("output.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}