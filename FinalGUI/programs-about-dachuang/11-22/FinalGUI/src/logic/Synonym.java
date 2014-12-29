package logic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Synonym {
	private final String url = "SynSet.txt";

	public List<String> getSimilarityWord(String word) {
		List<String> features = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(url), "GBK"));
			String line;
		    System.out.print("Ѱ��"+word+"ͬ���:");
			while ((line = br.readLine()) != null ) {
				//int i = 0;
				String[] words = line.split(" ");
				if( !words[0].equals(word)) continue;
				else {
						for (String str : words) {
					//if(i>=5) break;
					     features.add(str);
					     System.out.print(str);
					//i++;
						}
					break;
				}
			}
			System.out.println();
			return features;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return features;
		}
	}
}
