package RealityBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CAssociation {
	public static class FeaPair{
		public String feature;
		public String nearfeature;
	}
	public static float BMI = (float)0.6;//0.5~0.7
	//linear normalization process
	public static void CSimilarity(int n, FeaPair[] subFeaPair, ArrayList<ArrayList<String>> sentencelist) throws IOException{
		int times = 3;
		float ei = (float)0.9;
		float[] BMIArray = new float[n];
		float A = (float)0.5, B = (float)0.5;
		float max = 0, min = 1;
		byte[] buff=new byte[]{};
		FileOutputStream out = new FileOutputStream("D:/programs-about-dachuang/11-22/特别度计算库/SimilarDegree.txt", true);
		for(int j = 0; j < n; j++){
			BMIArray[j] = tempBMI(subFeaPair[j], sentencelist);
		}
		for(int i = 0; i < times; i++){
			for(int j = 0; j < n; j++){
				if(BMIArray[j] > max)
					max = BMIArray[j];
				if(BMIArray[j] < min)
					min = BMIArray[j];
			}
			for(int j = 0; j < n; j++)
				BMIArray[j] = A*BMIArray[j] + B*(BMIArray[j] - min)/(max - min);
			max = 0;
			min = 1;
		}
		HashMap<String, Float> indiBMI= new HashMap<String, Float>();
		for(int i = 0; i < n; i++){
			indiBMI.put(subFeaPair[i].nearfeature, BMIArray[i]);
		}
		
		Iterator iter = indiBMI.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entrys = (Map.Entry)iter.next();
			FeaPair tmp = new FeaPair();
			tmp.feature = subFeaPair[0].feature;
			tmp.nearfeature = (String)entrys.getKey();
			String str = subFeaPair[0].feature + '|' + entrys.getKey() + String.valueOf(entrys.getValue()) + "\r\n";
			buff = str.getBytes();
			out.write(buff, 0, buff.length);
			System.out.println(subFeaPair[0].feature + '|' + entrys.getKey() + String.valueOf(entrys.getValue()));
		}
	}
	//probability that t1 and t2 both appear in a text window
	public static float both_count(FeaPair subFeaPair, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		for(ArrayList<String> wordlist: sentencelist)
			if(wordlist.contains(subFeaPair.feature) & wordlist.contains(subFeaPair.nearfeature))
				appearstimes++;
		
		return (float)appearstimes/alltimes;
	}
	//probability that t1 and t2 neither appear in a text window
	public static float neither_count(FeaPair subFeaPair, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		for(ArrayList<String> wordlist: sentencelist)
			if(!wordlist.contains(subFeaPair.feature) & !wordlist.contains(subFeaPair.nearfeature))
				appearstimes++;
		
		return (float)appearstimes/alltimes;
	}
	//probability that a term appear in a text window
	public static float presence_count(String term, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		for(ArrayList<String> wordlist: sentencelist)
			if(wordlist.contains(term))
				appearstimes++;
		
		return (float)appearstimes/alltimes;
	}
	//probability that t1 appear and t2 does not appear in a text window
	public static float contray_count(String term1, String term2, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		for(ArrayList<String> wordlist: sentencelist)
			if(wordlist.contains(term1) & !wordlist.contains(term2))
				appearstimes++;
		
		return (float)appearstimes/alltimes;
	}
	//estimate the degree of association between two feature
	public static float tempBMI(FeaPair subFeaPair, ArrayList<ArrayList<String>> sentencelist){
		float both = both_count(subFeaPair, sentencelist);
		float neither = neither_count(subFeaPair, sentencelist);
		float spresence = presence_count(subFeaPair.nearfeature, sentencelist);
		float fpresence = presence_count(subFeaPair.feature, sentencelist);
		float sabsence = 1 - spresence;
		float fabsence = 1 - fpresence;
		float fscontray = contray_count(subFeaPair.feature, subFeaPair.nearfeature, sentencelist);
		float sfcontray = contray_count(subFeaPair.nearfeature, subFeaPair.feature, sentencelist);
		return (  BMI*( log( (both + 1)/spresence/fpresence, 2 )*both + 
				neither*log( (neither + 1)/sabsence/fabsence, 2 ) )
			- (1 - BMI)*( log( (fscontray + 1)/fpresence/sabsence, 2 )*fscontray + 
				sfcontray*log( (sfcontray + 1)/spresence/fabsence, 2 ) )  );
	}
	//compute the logarithm
	public static float log(float value, float base){
		return (float)( Math.log(value) / Math.log(base) );
	}
}
