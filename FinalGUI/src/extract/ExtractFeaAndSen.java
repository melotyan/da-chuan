package extract;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import constants.Constants;

public class ExtractFeaAndSen {

	/**
	 * extract the noun in all files, and count the frequency of each noun's
	 * occurrece, then store them in avert order
	 * @param middlePath
	 * @param featureDictionary
	 */
	public static Map<String, Integer> extractFeature(String middlePath,
			String featureDictionary) {
		List<String> files = DealComments.getFile(middlePath);
		String line;
		Map<String, Integer> featureMap = new HashMap<String, Integer>();
		Pattern pattern = Pattern.compile(Constants.NOUN_REGEX);
		BufferedReader reader;

		try {
			for (String file : files) {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(middlePath + file), "GBK"));
				while ((line = reader.readLine()) != null) {
					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						String feature = matcher.group(1);
						if (featureMap.get(feature) == null) {
							featureMap.put(feature, 1);
						} else {
							int count = featureMap.get(feature);
							featureMap.put(feature, count + 1);
						}
					}
				}
				reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Iterator iterator = featureMap.values().iterator();
		while(iterator.hasNext()) {
			if ((Integer)iterator.next() < Constants.FLLOR) {
				iterator.remove();
			}
		}
		List countList = DealComments.sortMapByValue(featureMap);
		// write the map to final file
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(featureDictionary), "GBK");
			for (int i = 0; i < countList.size(); i++) {
				writer.write(((Entry) countList.get(i)).getKey() + "\t");
				writer.write(((Entry) countList.get(i)).getValue() + "\r\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return featureMap;
	}

	/**
	 * extract the adjective in all files, and count the frequency of each adjective's
	 * occurrece, then store them in avert order
	 * @param middlePath
	 * @param sentimentDictionary
	 */
	public static Map<String, Integer> extractSentiment(String middlePath,
			String sentimentDictionary) {
		List<String> files = DealComments.getFile(middlePath);
		String line;
		Map<String, Integer> sentimentMap = new HashMap<String, Integer>();
		Pattern pattern = Pattern.compile(Constants.ADJ_REGEX);
		BufferedReader reader;

		try {
			for (String file : files) {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(middlePath + file), "GBK"));
				while ((line = reader.readLine()) != null) {
					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						String feature = matcher.group(1);
						if (sentimentMap.get(feature) == null) {
							sentimentMap.put(feature, 1);
						} else {
							int count = sentimentMap.get(feature);
							sentimentMap.put(feature, count + 1);
						}
					}
				}
				reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator iterator = sentimentMap.values().iterator();
		while(iterator.hasNext()) {
			if ((Integer)iterator.next() < Constants.FLLOR) {
				iterator.remove();
			}
		}
		List countList = DealComments.sortMapByValue(sentimentMap);
		// write the map to final file
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(sentimentDictionary), "GBK");
			for (int i = 0; i < countList.size(); i++) {
				writer.write(((Entry) countList.get(i)).getKey() + "\t");
				writer.write(((Entry) countList.get(i)).getValue() + "\r\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sentimentMap;
	}
}
