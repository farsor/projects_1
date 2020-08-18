/**
 * 
 */
package parseMusicCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew
 *	Array of entries objects
 */
public class Entries {
	
	List<Entry> entries;
	
	//constructor with single array source
	Entries(){
		entries = new ArrayList<Entry>();
	}
	
	//constructor with array of source objects as parameter
	Entries(Source[] sources){
		entries = new ArrayList<Entry>();
		for(Source source: sources) {
			for(Entry entry: source.getEntries()) {
				entries.add(entry);
			}
		}
	}
	
	//constructor with collection object as parameter
	Entries(Collection collection){
		entries = new ArrayList<Entry>();
		for(Source source: collection.getSources()) {
			for(Entry entry: source.getEntries()) {
				entries.add(entry);
			}
		}
	}
	
	//constructor with array of collections as parameter
	Entries(Collection[] collections){
		entries = new ArrayList<Entry>();
		for(Collection collection: collections) {
			for(Source source: collection.getSources()) {
				for(Entry entry: source.getEntries()) {
					entries.add(entry);
				}
			}
		}
	}
	
	//add entries within entry object to spreadsheet
	public void toSpreadsheet(String fileName) {
		try {
			SpreadsheetWriter sw = new SpreadsheetWriter(Entry.getFields(), fileName);
			for(Entry entry: entries) {
				sw.writeRow(entry.toArray());	//write entry information to spreadsheet
			}
			sw.closeStream();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//------------------------------------------------------------------
	//return entries array
	public List<Entry> toArrayList(){
		return entries;
	}
	
	//-----------------------------------------------------------------------
	//add entry to entries object
	public void add(Entry entry) {
		entries.add(entry);
	}

}
