package parseMusicEntries;

import java.io.File;

public class ParseMusicEntries {
	ParseMusicEntries(){
		
	}
	ParseMusicEntries(File f){
		
	}
	
	
	
	public boolean isDigit(char c) {			//determine if character is digit
		return (int)c >= 48 && (int)c <= 57;	//do ascii codes fall into digit range?
	}

}
