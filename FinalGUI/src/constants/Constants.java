package constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {

	public static final String NOUN_REGEX = "([\u4E00-\u9FA5]{2,})/n";
	public static final String ADJ_REGEX = "([\u4E00-\u9FA5]+)/a";
	public static final String SOURCE_COMMENTS = "comments/source.txt";
	public static final String MIDDLE_COMMENTS = "comments/middle.txt";
	public static final String FINAL_COMMENTS = "comments/final.txt";
	public static final String FEATURE_DICTIONARY_FILE = "dictionary/featureDictionary.txt";
	public static final String SENTIMENT_DICTIONARY_FILE = "dictionary/sentimentDictionary.txt";
	public static final int FLLOR = 300;
	/*
	public static void main(String[] args) {
		String str = "5##特满意的一次购物，外观漂亮";
		Pattern pattern = Pattern.compile(STAR_REGEX);
		Matcher match = pattern.matcher(str);
		if (match.find()) {
			String strf = match.group(1);
			System.out.println(strf);
		}
	}
	*/
}
