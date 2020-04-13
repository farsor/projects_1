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
	int curSrc;											//current source number
	String srcCallNum = null;							//call number for current source, indicated by bold text
	String curSourceAuthor = "";						//
	String curSourceTitle = "";							//
	String curSourceInscription = null;
	String curSourceDesc = "";							//description of current source, containing all details that cannot be parsed
}
