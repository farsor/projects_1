package parseMusicEntries;
/**
 * 
 * @author Andrew
 *
 *	Music collection containing possible multitude of sources that contain music entries
 *
 */
public class Collection {
	String collectionName,
			collectionDesc;
	String[] fieldLabels = {"collection_name", "collection_description"};
	
	Collection(){
		
	}
	//---------------------------------------------------
	Collection(String name, String desc){
		collectionName = name.substring(0, name.indexOf("."));
		collectionDesc = desc;		
	}
	//--------------------------------------------------------------
	public String toString() {
		return "Collection Name: " + collectionName + 
				"\n\n------------------------------------Collection Description------------------------------------\n" + collectionDesc + 
				"\n\n------------------------------------End of Collection Description------------------------------------";
	}
	
	public String[] getFieldLabels() {		
		return fieldLabels;
	}
	
	public String[] toArray() {
		String[] arr = {collectionName, collectionDesc};
		return arr;
	}
	
	public String getName() {
		return collectionName;
	}
	
	
}
