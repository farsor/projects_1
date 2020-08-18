package parseMusicCollection;

import java.util.List;

/**
 * @author Andrew
 *	array of source objects
 *	methods for writing to string, spreadsheets, or database
 */


public class Sources {
	List<Source> sources;
	
	//constructor with collection as parameter
	Sources(Collection collection){
		for(Source source: collection.getSources()) {
			sources.add(source);
		}
	}
	
	Sources(Collection[] collections){
		for(Collection collection: collections) {
			for(Source source: collection.getSources()) {
				
			}
		}
	}
	

}
