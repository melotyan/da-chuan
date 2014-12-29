package extract;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import constants.Constants;
import java.io.File;

public class MatchFeaAndSen {
        public static String readFile(String filename) {
            File file = new File(filename);
            Long fileLen = file.length();
            char[] fileContent = new char[fileLen.intValue()];
            try {              
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file), "GBK"));               
                in.read(fileContent);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String(fileContent);
        }
	/**
	 * use the dictionary to extract the feature and sentiment in each comments, and
	 * match them correctly
	 * @param middleFile
	 * @param finalFile
	 * @param featureDictionary
	 * @param sentimentDictionary
	 */
	public static void matchFeaAndSen(String middleFile, String finalFile, 
			String featureFile, String sentimentFile) {
		try {
                        String featureDictionary = readFile(featureFile);
                        //System.out.println(featureDictionary);
                        String sentimentDictionary = readFile(sentimentFile);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream(middleFile), "GBK"));
			OutputStreamWriter output = new OutputStreamWriter(
					new FileOutputStream(finalFile), "GBK");
			LinkedHashSet<String> nounSet = new LinkedHashSet<String>();
			LinkedHashSet<String> adjSet = new LinkedHashSet<String>();
			Pattern nounPattern = Pattern.compile(Constants.NOUN_REGEX);
			Pattern adjPattern = Pattern.compile(Constants.ADJ_REGEX);
			Matcher nounMatch;
			Matcher adjMatch;
			
			String str;			
			while ((str = input.readLine()) != null) {
				String star = null;
                                //System.out.println(str);
				if (str.length() > 0)
					star = str.substring(0, 1);
				nounMatch = nounPattern.matcher(str);
				adjMatch = adjPattern.matcher(str);
				
				while (nounMatch.find()) 
					if (featureDictionary.contains(nounMatch.group(1)))
						nounSet.add(nounMatch.group(1));
				while (adjMatch.find())
					if (sentimentDictionary.contains(adjMatch.group(1)))
						adjSet.add(adjMatch.group(1));
				
				// write the noun and adjective to new file	
				output.write(star + " : ");
				for (String noun : nounSet)
					output.write(noun + " ");
				output.write(": ");
				for (String adj : adjSet)
					output.write(adj + " ");
				output.write("\r\n");
				nounSet.clear();
				adjSet.clear();
			}
			input.close();
			output.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
