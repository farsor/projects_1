/**
		Contains information for individual entry and has capability of parsing entry
 * 
 */
package parseMusicEntries;

import java.util.List;

public class Entry {
	List<Boolean> isSecularList = null;		//list containing whether entry is secular					
	List<String> entries = null;
	String[] titleAndCredit = null;
	String[] splitEntries = null;
	String[] fullTuneEntry = null;			//entry containing all string values for current entry
	String tunePage,
			tuneTitle,
			tuneCredit,
			tuneVocalPart,
			tuneKey,
			tuneMelodicIncipit,
			tuneTextIncipit;
			
	String[] entryLabels = {"tune_page", "tune_title", "tune_credit", "tune_vocal_part",		//labels corresponding to fields
								"tune_key", "tune_melodic_incipit", "tune_text_incipit"};
	
	Entry(){
		
	}
	
	

}
