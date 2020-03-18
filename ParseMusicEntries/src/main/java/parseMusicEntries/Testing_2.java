package parseMusicEntries;

public class Testing_2 {
	public static void main(String[] args) {
		
		ParseMusicEntries2 pme = new ParseMusicEntries2();
		String[] strArr = {"dog", "boi", "was", "here", null};
		strArr = pme.shiftRight(strArr, 1);
		for(String str: strArr) {
			System.out.println(str);
		}
		
		
//		ParseMusicEntries2 pme = new ParseMusicEntries2();
//		String str = "Anthem Luke 2nd Chap [by Stephenson]";
//		String[] titleCredit = pme.parseTitleAndCredit(str);
//		System.out.println("Title: " + titleCredit[0]);
//		System.out.println("Author: " + titleCredit[1]);
		
		
	}
}
