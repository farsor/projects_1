package parseMusicEntries;

import java.io.File;

public class ParseMusicEntries {
	
	
	
	ParseMusicEntries(){
		
	}
	ParseMusicEntries(File f){
		
	}
	
	//return array containing [0]: tune title and [1]: tune author by parsing string containing title and author
	public static String[] parseTitleCredit(String str) {
		String[] titleCredit = new String[2];				//array that will contain title and author
		int authorIndex = str.indexOf("[by");				//[by is indicator of author being present
		if(authorIndex != -1) {								//if author name is present
			titleCredit[0] = str.substring(0, authorIndex);	//text before [by is tune title [0]
			titleCredit[1] = str.substring(authorIndex, str.length());	//text where [by starts to end of string is tune author [1]
			return titleCredit;	
		}
		titleCredit[0] = str;								//if no author detected, return entire string as title with null author
		return titleCredit;									
		
	}
	
	public String 


}
