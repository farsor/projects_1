package parseMusicEntries;

public class Testing_2 {
	public static void main(String[] args) {
		
		String str = "Let every                                     creature Join to praise the eternal god";
		char[] strChar = str.toCharArray();
//		for(char c: strChar) {
//			System.out.print((int) c + "	");
//		}
//		System.out.println(str.trim().replaceAll(Character.toString((char) 160), " "));
		System.out.println(str.replaceAll("(^\\h*)|(\\h*$)"," "));
		
//		//test shift method in PME class
//		ParseMusicEntries2 pme = new ParseMusicEntries2();
//		String[] strArr = {"dog", "boi", "was", "here", null};
//		strArr = pme.shiftCellsRight(strArr, 1);
//		for(String str: strArr) {
//			System.out.println(str);
//		}		
		
//		ParseMusicEntries2 pme = new ParseMusicEntries2();
//		String str = "Anthem Luke 2nd Chap [by Stephenson]";
//		String[] titleCredit = pme.parseTitleAndCredit(str);
//		System.out.println("Title: " + titleCredit[0]);
//		System.out.println("Author: " + titleCredit[1]);
		
		//list operations
//		List<String[]> strEntries = new ArrayList<String[]>();
//		String[] strArr = {"dog", "boi"};
//		strEntries.add(strArr);
//		System.out.println(strEntries.get(0)[1]);
		
//		//pattern/matcher testing
//		Pattern pattern = Pattern.compile("^[\\d]+[\\.]");
//		String str3 = "142.  Albee, Amos.  The Norfolk Collection"
//				+ " of Sacred Harmony.  Dedham: Herman Mann, 1805.  160 pp.; "
//				+ "lacks 4 unnumbered leaves at end, both covers; corne";
//		Matcher matcher = pattern.matcher(str3);
//		System.out.println(matcher.find());
//		System.out.println(matcher.end());
		

	////reg exp tests
	//String regexp = "^[\\d]+[\\.]";
	//String str= "123.";
	//String str2 = "415.";
	//System.out.println(str.indexOf(regexp));
	//System.out.println(str2.matches(regexp));
	//System.out.println(str3.matches(regexp));
		
		
	}
}
