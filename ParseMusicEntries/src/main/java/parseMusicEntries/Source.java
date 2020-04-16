/**
 * 
 */
package parseMusicEntries;

/**
 * @author Andrew
 *	Contains information about source within a music collection. Each source may contain tune entries	
 *
 */
public class Source {
	//source/entry variables	
	private int sourceNumber;											//current source number
	private String callNumber;							//call number for current source, indicated by bold text
	private String author;						//
	private String title;							//
	private String inscription;
	private String description;							//description of current source, containing all details that cannot be parsed
	
	Source(int src){
		sourceNumber = src;
		callNumber = null;							//call number for current source, indicated by bold text
		author = null;						//
		title = null;							//
		inscription = null;
		description = null;							//description of current source, containing all details that cannot be parsed		
	}
	//--------------------------------------------------------------------------------
	public int getSourceNumber() {
		return sourceNumber;
	}
	//--------------------------------------------------------------------------------
	public void setCallNumber(String callNumber){
		this.callNumber = callNumber;		
	}
	//--------------------------------------------------------------------------------
	public String getCallNumber() {
		return callNumber;
	}
	//--------------------------------------------------------------------------------
	public void setAuthor(String author) {
		this.author = author;
	}
	//--------------------------------------------------------------------------------
	public String getAuthor() {
		return author;
	}
	//--------------------------------------------------------------------------------
	public void setTitle(String title) {
		this.title = title;
	}
	//--------------------------------------------------------------------------------
	public String getTitle() {
		return title;
	}
	//--------------------------------------------------------------------------------
	public void setInscription(String inscription) {
		this.inscription = inscription;
	}
	//--------------------------------------------------------------------------------
	public String getInscription() {
		return inscription;
	}
	//--------------------------------------------------------------------------------
	public void setDescription(String description) {
		this.description = trimmedDescription(description);
	}
	//--------------------------------------------------------------------------------
	public String getDescription() {
		return description;
	}
	//--------------------------------------------------------------------------------
	public String toString() {
		return "Source: " + sourceNumber +
				"\nAuthor: "  +  author + 
				"\nTitle: "  +  title +
				"\nDescription: "  +  description +
				"\nCall number: " + callNumber + 				
				"\nInscription(s): " + inscription  +
				"\n----------------end of source-------------\n\n";
	}
	//--------------------------------------------------------------------------------
	private String trimmedDescription(String description) {
		if(description.startsWith(".")) {
			return description.substring(2).trim();
		}
		else if(description.startsWith(".]")) {
			if(title != null) {
				title += "]";
			}
			return description.substring(3).trim();			
		}
		else if (description.startsWith("?]")){
			if(title != null) {
				title += "?]";
			}
			return description.substring(3).trim();
		}
		else {	 
			return description;
		}		
	}
}
