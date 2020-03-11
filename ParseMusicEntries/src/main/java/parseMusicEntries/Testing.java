package parseMusicEntries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testing {
	public static void main(String args[]) {
		
		//pattern/matcher testing
		Pattern pattern = Pattern.compile("^[\\d]+[\\.]");
		String str3 = "142.  Albee, Amos.  The Norfolk Collection"
				+ " of Sacred Harmony.  Dedham: Herman Mann, 1805.  160 pp.; "
				+ "lacks 4 unnumbered leaves at end, both covers; corne";
		Matcher matcher = pattern.matcher(str3);
		System.out.println(matcher.find());
		System.out.println(matcher.end());
		

	////reg exp tests
	//String regexp = "^[\\d]+[\\.]";
	//String str= "123.";
	//String str2 = "415.";
	//System.out.println(str.indexOf(regexp));
	//System.out.println(str2.matches(regexp));
	//System.out.println(str3.matches(regexp));

	}

}
