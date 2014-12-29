package compute;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compute.RelevancyDegree.SenFea;
import java.io.File;
import java.util.Arrays;

public class PolarityDegree {
	public static enum Polarity{
		negative(-1), neutral(0), positive(1);
		public int value;
		private Polarity(int value){
			this.value = value;
		}
	}
	public static class PolaritySentence{
		ArrayList<String> sentence;
		Polarity polarity;
	}
	public static float ppos = 0;//initial the weight of positive sentences of all
	public static float pneg = 0;//initial the weight of negative sentences of all
	private final static String sentencefile = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/testfiles1";
	private final static String polarityfile = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/testfiles2/NewSentence.txt";
	public static HashMap<String, Float> SenFeaKC = new HashMap<String, Float>();
	public PolarityDegree(ArrayList<SenFea> comSenFea) throws IOException{
		
		SenFea[] pSenFeaArray = new SenFea[comSenFea.size()];
		comSenFea.toArray(pSenFeaArray);
		ArrayList<PolaritySentence> pSentenceList = new ArrayList<PolaritySentence>(PolarityDegree.extractAllPolarityFiles(sentencefile));
		ArrayList<String> polaritylist = new ArrayList<String>(readUFile(polarityfile));
		int i = 0;
		for (String text : polaritylist) {
			//text = new String( text.toString().getBytes( "utf-8" ), "gbk");
			Polarity polarity = polaritycheck((text.split(":"))[0]);
			pSentenceList.get(i).polarity = polarity;
			i++;
		}

		PolarityDegree.feaSenPolarity(comSenFea.size(), pSenFeaArray, pSentenceList);
	}
	//transform polarity in text to enum Polarity type.
	public static Polarity polaritycheck(int initpolarity){
		int finalpolarity = initpolarity*5/9 - 1;
		if(finalpolarity <= 0)
			return Polarity.negative;
		else if(finalpolarity > 0)
			return Polarity.positive;
		else
			return Polarity.neutral;
	}
	public static Polarity polaritycheck(String initpolarity){
		if(initpolarity.equals("正面"))
			return Polarity.positive;
		else if(initpolarity.equals("负面"))
			return Polarity.negative;
		else
			return Polarity.neutral;
	}
	//probability that a review positive given that it contains the particular sen_fea_pair term(f, s)
	public static float pos_count(SenFea subSenFea, ArrayList<PolaritySentence> polaritySenList){
		
		int alltimes = 0, appearstimes = 0;
		for(PolaritySentence pSentence : polaritySenList)
			if(pSentence.sentence.contains(subSenFea.feature) & pSentence.sentence.contains(subSenFea.sentiment)){
				alltimes++;
				if(pSentence.polarity == Polarity.positive)
					appearstimes++;
			}
		if(alltimes == 0)
			return 0;
		return (float)appearstimes/alltimes;
	}
	//probability that a review negative given that it contains the particular sen_fea_pair term(f, s)	
	public static float neg_count(SenFea subSenFea, ArrayList<PolaritySentence> polaritySenList){
		
		int alltimes = 0, appearstimes = 0;
		for(PolaritySentence pSentence : polaritySenList)
			if(pSentence.sentence.contains(subSenFea.feature) & pSentence.sentence.contains(subSenFea.sentiment)){
				alltimes++;
				if(pSentence.polarity == Polarity.negative)
					appearstimes++;
			}
		if(alltimes == 0)
			return 0;
		return (float)appearstimes/alltimes;
	}
	//count the times of term pair in presence.	
	public static int times_presence(SenFea subSenFea, ArrayList<PolaritySentence> polaritySenList){
		int appearstimes = 0;
		for(PolaritySentence wordlist : polaritySenList)
			if(wordlist.sentence.contains(subSenFea.feature) && wordlist.sentence.contains(subSenFea.sentiment))
				appearstimes++;
		
		return appearstimes;
	}
	//word segmentation
	public static ArrayList<String> cutWords(String file) throws IOException{
		
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(file.split(" ")));
		return words;
	}
	//get list of polarity sentences
	public static ArrayList<PolaritySentence> extractAllPolarityFiles(String dirc) throws IOException{
		
		List<String> filelist = readDirs(dirc);
		ArrayList<PolaritySentence> polaritySenList = new ArrayList<PolaritySentence>();
		
		for(String file : filelist){
			ArrayList<String> sentence = readFile(file);
			for(String text : sentence){
				Polarity polarity = polaritycheck( Integer.valueOf(text.substring(0, 1)) );
				//Polarity polarity = polaritycheck(text.substring(0, 1));
				text = text.substring(4);
				ArrayList<String> wordlist = new ArrayList<String>();
				String[] extracted = text.split(":");
				ArrayList<String> featurelist = cutWords(extracted[0]);
				ArrayList<String> sentimentlist = cutWords(extracted[1]);
				String Tsentiment = new String();
				if(!sentimentlist.isEmpty() && sentimentlist.get(0).isEmpty())
					sentimentlist.remove(0);
				while(!featurelist.isEmpty() && !featurelist.get(0).isEmpty()){
					if(featurelist.get(0).length() == 4){
						wordlist.add(featurelist.get(0).substring(0, 1));
						wordlist.add(featurelist.get(0).substring(2, 3));
					} else if(featurelist.get(0).length() == 5){
						wordlist.add(featurelist.get(0).substring(0, 1));
						wordlist.add(featurelist.get(0).substring(2, 3));
						wordlist.add(featurelist.get(0).substring(4, 4));
					} else
						wordlist.add(featurelist.get(0));
					featurelist.remove(0);
					if(!sentimentlist.isEmpty()){
						Tsentiment = new String(sentimentlist.get(0));
						wordlist.add(Tsentiment);
						sentimentlist.remove(0);
					}
					else{
						if(Tsentiment.isEmpty())
							wordlist.add("空");
						else
							wordlist.add(Tsentiment);
					}
				}
				PolaritySentence polaritysentence = new PolaritySentence();
				polaritysentence.polarity = polarity;
				polaritysentence.sentence = wordlist;
				polaritySenList.add(polaritysentence);
			}
		}
		
		return polaritySenList;
	}
	//read utf-8 file
	public static ArrayList<String> readUFile(String file) throws FileNotFoundException, IOException{
		
		ArrayList<String> sentence = new ArrayList<String>();
		InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "utf-8"); //byte streams to character streams
		BufferedReader br = new BufferedReader(inStrR); 
		String line = br.readLine();
		while(line != null){
			sentence.add(line);
			line = br.readLine();    
		}
		return sentence;
	}
	//determine the polarity and the corresponding strength of a term(f,s) pair extracted from a review	
	public static float[] KC(SenFea subSenFea, ArrayList<PolaritySentence> polaritySenList){
		float[] result = new float[3];
		float positiveDegree, negativeDegree;
		int times = times_presence(subSenFea, polaritySenList);
		float pc = pos_count(subSenFea, polaritySenList);
		float nc = neg_count(subSenFea, polaritySenList);
		float POS = (float)0.3, NEG = (float)1;
		result[1] = positiveDegree = log((pc/ppos+1), 2)*pc/POS;
		result[2] = negativeDegree = log((nc/pneg+1), 2)*nc/NEG;
		double θ = (positiveDegree - negativeDegree);
		result[0] = (float)( (Math.pow(Math.E, θ) - Math.pow(Math.E, -θ))
						/(Math.pow(Math.E, θ) + Math.pow(Math.E, -θ)) );
		return result;
	}
	//compute the polarity and print result
	public static void feaSenPolarity(int n, SenFea[] subSenFea, ArrayList<PolaritySentence> polaritySenList){
		int times = 1;
		float[] KCArray = new float[n];
		float[] pcArray = new float[n];
		float[] ncArray = new float[n];
		float A = (float)0.5, B = (float)0.5, KCI = (float)0.5, ontKC = 0;
		
		for(PolaritySentence pSentence : polaritySenList){
			if(pSentence.polarity == Polarity.positive){
				ppos++;
			}
			else if(pSentence.polarity == Polarity.negative){
				pneg++;
			}
		}
		ppos/=polaritySenList.size();
		pneg/=polaritySenList.size();
		
		for(int j = 0; j < n; j++){
			float[] result = KC(subSenFea[j], polaritySenList);
			KCArray[j] = result[0];
			pcArray[j] = result[1];
			ncArray[j] = result[2];
		}
		for(int i = 0; i < times; i++){
			for(int j = 0; j < n; j++){
				if(KCArray[j] > KCI)
					ontKC = (KCArray[j] - KCI)/(1 - KCI);
				else if(KCArray[j] < -KCI)
					ontKC = -(Math.abs(KCArray[j]) - KCI)/(1 - KCI);
				else
					ontKC = 0;
				KCArray[j] = A*KCArray[j] + B*ontKC;
			}
		}
		for(int j = 0; j < n; j++){
			SenFeaKC.put(subSenFea[j].feature, KCArray[j]);
		}
		for(int j = 0; j < n; j++){
			System.out.println(subSenFea[j].feature + '|' + subSenFea[j].sentiment + "|" + KCArray[j] + "|" + pcArray[j] + "|" + ncArray[j]);
		}
	}
        //get list of file for the directory, including sub-directory of it
	public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException{
		
		ArrayList<String> FileList = new ArrayList<String>();
		try{
			File file = new File(filepath);
			if(!file.isDirectory()){
				System.out.println("�����[]");
				System.out.println("filepath:" + file.getAbsolutePath());
			}
			else{
				String[] flist = file.list();
				for(int i = 0; i < flist.length; i++){
					File newfile = new File(filepath + "\\" + flist[i]);
					if(!newfile.isDirectory()){
						FileList.add(newfile.getAbsolutePath());
					}
					else if(newfile.isDirectory()){//if file is a directory, call ReadDirs
						readDirs(filepath + "\\" + flist[i]);
					}
				}
			}
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
	return FileList;
	}
	//read file
	public static ArrayList<String> readFile(String file) throws FileNotFoundException, IOException{
		
		ArrayList<String> sentence = new ArrayList<String>();
		InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "gbk"); //byte streams to character streams
		BufferedReader br = new BufferedReader(inStrR); 
		String line = br.readLine();
		while(line != null){
			sentence.add(line);
			line = br.readLine();    
		}
		return sentence;
	}
	//compute the logarithm
	public static float log(float value, float base){
		return (float)( Math.log(value) / Math.log(base) );
	}
}
