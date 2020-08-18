/**
		Parses string containing entry information into fields
 * 
 */
package parseMusicCollection;


public class Entry {
	private boolean isSecular;
	private String collection,
					source,
					location,
					title,
					credit,
					vocalPart,
					key,
					melodicIncipit,
					textIncipit;	
	private static String[] fields = {"collection_name", "source_number", "entry_location", 	//labels for entry table columns
			"entry_title", "entry_credit", "entry_vocal_part",
			"entry_key", "entry_melodic_incipit", "entry_text_incipit", "entry_is_secular"};
	Entry(){		
	}
	
	Entry(String collection, String source, String entryStr, boolean isSecular){
		//prepare data for entry array construction
		this.collection = collection;
		this.source = source;
		this.isSecular = isSecular;
		String tunePage = getTunePage(entryStr);		//pull tune page from entry string	
		entryStr = formatEntryStr(entryStr);			//format current entry, removing tunepage, whitespace and unwanted commas
		String[] splitEntries = entryStr.split(", ");	//split entry into fields using ", " as delimiter
		String[] titleCredit = parseTitleCredit(splitEntries[0]);	//get title and credit from first entry of split array, which
																//contains both title and credit
		
		//parse entry
		parseEntry(tunePage, titleCredit, splitEntries);		
		
	}
	//---------------------------------------------------------------------------
	//sort  entry so that each piece of data is in its respective field
	private void parseEntry(String tunePage, String[] titleCredit, String[] splitEntries) {	

		String[] entryArr = new String[7];
		entryArr[0] = tunePage;
		entryArr[1] = titleCredit[0];
		entryArr[2] = titleCredit[1];
		int shifts = 0;				//amount of shifts that have been made to full array (left is negative, right positive)
		
		//convert split array to full array
		for(int index = 1, arrLimit = 5, fullEntryAdjust = 2; index < splitEntries.length && index < arrLimit; index++) {
			//index - index of entries split being added, arrLimit - when to end additions to entryArr. increased by from leftward shifts
			//	if no shifts, extra indices will later be appended to text incipit index in entryArr
			//fullEntryAdjust - adjust difference between cur index being added from entries split and index of entryArr 
			//that current value is being placed. Starts at 2 because of tunepage already being added and title/credit being in same index
			
			//if not first index (which is default vocal part index) and previous index was recorded as vocal part index
			if(index > 1 && index + fullEntryAdjust == 4) {				
				if(isVocalPart(splitEntries, index)) {	//checks to see if multiple vocal parts in split entry array
					//method for combining multiple values for vocal part, represented in index 4
					shifts--;					//record leftward shift of indices relative to entryArr for later operations
					fullEntryAdjust--;			//record leftward shift for current for loop so that next index will be checked for vocal part
					arrLimit++;					//increment limit of how high of index in entriesSplit will be added to fullEntries
					entryArr[index + fullEntryAdjust] += " " + splitEntries[index];	//combine extra vocal part value 
				}
				else {
					entryArr[index + fullEntryAdjust] = splitEntries[index];			//if no extra vocal part value detected, proceed as normal
				}
			}
			else {
				entryArr[index + fullEntryAdjust] = splitEntries[index];		//add entry to entryArr
			}
		}
		
		//if no vocal part was entered and  tune key was placed vocal part field, shift cells right
		if(entryArr[3] != null && entryArr[3].length() < 4 && entryArr[3].indexOf("TTB") == -1) {
			shiftCellsRight(entryArr, 3);
			shifts++;
		}			
		
		//if melodic incipit index is empty or has data that is not melodic incipit, which will occur if
		//there was no vocal part or key in entry string
		if(entryArr[5] == null || !isMelodicIncipit(entryArr[5])) {
			//start at first position where melodic incipit could be, which is in vocal part index
			for(int i = 3; i < 5; i++) {			
				if(isMelodicIncipit(entryArr[i])){	//if melodic incipit is in current field
					shiftCellsRight(entryArr, i);	//shift cells right
					shifts++;						//account for shift for other operations
				}	
			}
		}
		
		//if extra information was given for incipit field, as indicated by mm., add incipit to correct field
		//by shifting left
		if(entryArr[5] != null && entryArr[5].indexOf("mm.") != -1 && isMelodicIncipit(entryArr[6])) {
			entryArr[5] += (", " + entryArr[6]);
			entryArr[6] = null;
			shifts--;
		}
		

		
		//add all unrecorded entries that occur after melodic incipit to text field 
		if(isMelodicIncipit(entryArr[5])) {
			for(int i = 5 - shifts; i < splitEntries.length; i++) {
				entryArr[6] += (", " + splitEntries[i]);
			}
		}
		
		//debugger to detect non-incipits in melodic incipit field
		else {
			for(int i = 5 - shifts; i < splitEntries.length; i++) {
				entryArr[6] += (", " + splitEntries[i]);
			}

		}		
		
		//replace commas and colons that were substituted in source document
		for(int i = 0; i < entryArr.length; i++) {
			if(entryArr[i] != null) {
				//melodic incipits that contained commas had commas replaced by -*- 
				//and colons replaced by **&
				entryArr[i] = entryArr[i].replace("-*-", ",").replace("**&", ":");					
			}
		}
		
		if(!isMelodicIncipit(entryArr[5])) {
			for(int i = 0; i < entryArr.length; i++) {
//				System.out.println(fields[i] + ": " + entryArr[i]);
			}
			Collection.notIncipitCount++;
			System.out.println();
		}
		
		setVariables(entryArr);

	}	
	//-----------------------------------------------------------------------------
	//format string of entry to format optimal for parsing
	private String formatEntryStr(String entryStr) {
		int adj = 2;			//number of characters between colon and starting character of title
								// there is a strange reason why some spaces are not being counted, and this adjusts for that
		if(entryStr.indexOf(":") != -1){			//if entry includes page number, as indicated by ":"
			if((int) entryStr.charAt(entryStr.indexOf(":") + 1) != 32) {
				adj--;
			}
	
			return entryStr.substring(entryStr.indexOf(":") + adj, entryStr.length())		//remove page number from current entry to analyze rest of text
								.replace(",”", "”,")			//replace ," with ", so quotes are not put in next field
								.replace(", so", "-*- so")			//remove false delimiter and replace with temporary symbol
								.replace(", but", "-*- but")		// remove false delimiter and replace with temporary symbol
								.replace(", by ", "-*- by ")		//remove false delimiter and replace with temporary symbol
								.trim().replaceAll(" +", " ")	//trim extra spaces
								.replace("i.e.,", "i.e.-*-")
								.replace("solo:", "solo**&");
		}
		
		else {										//if no page number, no need to remove page number
			return entryStr.replace(",”", "”,")		//replace ," with ", so quotes are contained together
					.replace(", so", "-*- so")			//remove false delimiter and replace with temporary symbol
					.replace(", but", "-*- but")		//remove false delimiter and replace with temporary symbol
					.replace(", by",  "-*- by")		//remove false delimiter and replace with temporary symbol
					.trim().replaceAll(" +", " ")	//trim extra spaces
					.replace("i.e.,", "i.e.-*-")
					.replace("solo:", "solo**&");	
		}
	}
	//-----------------------------------------------------------------------------
	//return array containing [0]: tune title and [1]: tune author by parsing string containing title and author
	public String[] parseTitleCredit(String str) {
		
		String[] titleCredit = new String[2];				//array that will contain title and author
		String[] authorIndicators = {"[by", "-by", " by "};	//strings that indicate presence of author
		//check to see if any author indicators occur in parameter string
		int authorIndex = -1;						//no matches by default
		
		for(String indicatorStr: authorIndicators) {
			if(str.indexOf(indicatorStr) != -1) {			//if there is match
			authorIndex = str.indexOf(indicatorStr);		//record index of match
			}
		}
		
		//separate tune title and author into individual strings
		if(authorIndex != -1) {								//if author name is present
			titleCredit[0] = str.substring(0, authorIndex);	//text before [by is tune title [0]
			titleCredit[1] = str.substring(authorIndex, str.length());	//text where [by starts to end of string is tune author [1]
			return titleCredit;	
		}
		
		titleCredit[0] = str;								//if no author detected, return entire string as title with null author
		return titleCredit;				
	}	
	//--------------------------------------------------------------------------------
		//check if current entry is melodic incipit
		private boolean isMelodicIncipit(String str) {	
			int digitCount = 0,				//counter of digits in string
					maxConsecDigits = 0,	//most consecutive digits that occur in a row in given string
					curConsec = 0;			//
			
			if(str == null)
				return false;
			
			for(char c: str.toCharArray()) {	//check if each character is digit, and increment count if so
				
				if(Character.isDigit(c)) {
					digitCount++;
					curConsec++;
				}
				
				else {
					if(curConsec > maxConsecDigits) {
						maxConsecDigits = curConsec;
						curConsec = 0;
					}
				}
				if(curConsec > maxConsecDigits) {
					maxConsecDigits = curConsec;
				}
			}
			//if there are more than 3 digits combined with pipes, or
			return ((digitCount >=3 && (str.indexOf("|") != -1) || str.indexOf("-") != -1) || maxConsecDigits  > 4|| digitCount >= 8) ? true: false;
		}
	//--------------------------------------------------------------------------------------
	//determine if string is vocal part
	private boolean isVocalPart(String[] entriesSplit, int index) {
		String[] vocalPartKeywords = {"tenor", "counter", "bass", "treble", "cantus", "medus", "basus", "meaudus", "voice", "TCTB", "fragment"};
			//^^terms that represent vocal part description
		if(entriesSplit[index].indexOf("“") != -1 )		//quote character that indicates vocal part
			return true;
		for(String kw: vocalPartKeywords) {				
			//if current string contains any of the keywords that represent vocal part description
			if(entriesSplit[index].toLowerCase().indexOf(kw) != -1) {
				return true;
			}
		}
		return false;				
	}
	//-------------------------------------------------------------------------------------------
	public String[] shiftCellsRight(String[] strArr, int startIndex) {
		for(int i = strArr.length - 1; i > startIndex; i--) {
			strArr[i] = strArr[i - 1];
		}
		strArr[startIndex] = null;
		return strArr;
	}
	//-----------------------------------------------------------------------------------------
	//return string containing entry information
	public String toString() {		
		return "Entry Location: " + location +
				"\nEntry Title: "  +  title + 
				"\nSecular Entry: " + isSecular +
				"\nEntry Credit: "  +  credit +
				"\nEntry Vocal Part: " + vocalPart + 				
				"\nEntry Key: " + key +
				"\nEntry Melodic Incipit: "  +  melodicIncipit +
				"\nEntry Text Incipit: "  +  textIncipit + 
				"\n\n";
	}
	//---------------------------------------------------------------------------------------
	//return array containing parsed entries
	public String[] toArray(){
		String[] arr = {collection, source, location, title, credit, vocalPart, key, melodicIncipit, textIncipit, Boolean.toString(isSecular)};
		return arr;
	}
	//---------------------------------------------------------------------------------------
	//return whether entry is secular
	public boolean isSecular() {
		return isSecular;
	}
	//------------------------------------------------------------------------------------
	private String getTunePage(String entryStr) {
		if(entryStr.indexOf(":") != -1){				//colon indicates presence of page number
			return entryStr.substring(0, entryStr.indexOf(":"));	//get page number, which occurs up until colon character
		}
		else {											//no page number indicated
			return null;			
		}	
	}
	//---------------------------------------------------------------------------------------
	private void setVariables(String[] entryArr) {
		location = entryArr[0];
		setTitle(entryArr[1]);
		setCredit(entryArr[2]);
		vocalPart = entryArr[3];
		key = entryArr[4];
		melodicIncipit = entryArr[5];
		textIncipit = entryArr[6];						
	}
	//---------------------------------------------------------------------------------------
	private void setCredit(String credit) {
		if(credit != null) {
			credit = credit.trim();
			if(credit.endsWith("]") && !credit.startsWith("[") && !credit.endsWith("[sic]"))
				credit = "[" + credit;
		}
		this.credit = credit;
	}
	//---------------------------------------------------------------------------------------
	private void setTitle(String title) {
		if(title != null) {
			title = title.trim();
			if(title.startsWith("[") && !title.endsWith("]"))
				title +=  "]";
		}
		this.title = title;
	}
	
	public static String[] getFields() {
		return fields;
	}
	
}
