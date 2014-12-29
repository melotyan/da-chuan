package compute;

import java.io.*;
import java.util.*;
import logic.HandleOntology;


public class RelevancyDegree {
	public static class SenFea{
            public String feature;
            public String sentiment;
	}
	public class Similar{
		String feature;
		ArrayList<String> similarfea;
		public Similar(){
			feature = new String();
			similarfea = new ArrayList<String>();
		}
	}
	//adjust the relative weight of positive and negative evidence respectively
	private final static float BMI = (float)0.7;//0.5~0.7
	private final static int TIMES = 3;
	private final static float EI = (float)0.8;
	public  String sentencefile = "";
	public static ArrayList<Similar> similarfealist = new ArrayList<Similar>();
	
	//record the relevance pair
	public static ArrayList<SenFea> comSenFea = new ArrayList<SenFea>();
	
	@SuppressWarnings({ "static-access", "rawtypes" })
	public static void main(String[] args) throws IOException{
		
		//initial a area tree
		HandleOntology hand = new HandleOntology();
		//hand.delimateDuplicateNode(hand.getRoot());
		
		RelevancyDegree test1 = new RelevancyDegree();
		
		for(SenFea senfea : test1.comSenFea){
			try {
				hand.basicTree.reset(senfea.feature, "realSentiment", senfea.sentiment);
			} catch (SecurityException | ClassNotFoundException
					| IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//compute the polarity
//		PolarityDegree test2 = new PolarityDegree(test1.comSenFea);
//		
//		Iterator iter = test2.SenFeaKC.entrySet().iterator();
//		while(iter.hasNext()){
//			Map.Entry entrys = (Map.Entry)iter.next();
//			try {
//				hand.basicTree.reset((String)entrys.getKey(), "polarity", (float)entrys.getValue());
//			} catch (SecurityException | ClassNotFoundException
//					| IllegalArgumentException | IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		hand.basicTree.display();

	}
        public RelevancyDegree(String sentencefile){
		
                this.sentencefile = sentencefile;
		String adjfile = "D:\\programs-about-dachuang\\11-22\\FinalGUI\\src\\compute\\adjDic.txt";
		String similarfile = "D:\\programs-about-dachuang\\11-22\\FinalGUI\\src\\compute\\特征词的同义词库.txt";
		try {
			ArrayList<ArrayList<String>> SentenceList = new ArrayList<ArrayList<String>>(extractAllFiles(sentencefile));
			//get review sentence list
			ArrayList<String> featureSentimrntList = new ArrayList<String>(readFile(adjfile));
			ArrayList<String> similarlist = new ArrayList<String>(readFile(similarfile));
			for (String wordlist : similarlist) {
				String[] wordarray = wordlist.split(" ");
				Similar similar = new Similar();
				similar.feature = wordarray[0];
				for (String word : wordarray) {
					similar.similarfea.add(word);
				}
				similarfealist.add(similar);
			}
			
			SenFea[] senFeaArray = new SenFea[1];
			for(String sentence : featureSentimrntList){
				String[] mainterm = sentence.split(":");
				
				if(mainterm.length != 1){
					String[] otherterm = mainterm[1].split(",");
					senFeaArray = new SenFea[otherterm.length];
					for (int i = 0; i < otherterm.length; i++) {
						if(!otherterm[i].isEmpty())
						{
							SenFea newSenFea = new SenFea();
							newSenFea.feature = mainterm[0];
							newSenFea.sentiment = otherterm[i];
							senFeaArray[i] = newSenFea;
						}
					}
					computeRelevancyDegree(senFeaArray.length, senFeaArray, SentenceList);
				}
			}
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	public RelevancyDegree(){
		
		String adjfile = "adjDic.txt";
		String similarfile = "特征词的同义词库.txt";
		try {
			ArrayList<ArrayList<String>> SentenceList = new ArrayList<ArrayList<String>>(extractAllFiles(sentencefile));
			//get review sentence list
			ArrayList<String> featureSentimrntList = new ArrayList<String>(readFile(adjfile));
			ArrayList<String> similarlist = new ArrayList<String>(readFile(similarfile));
			for (String wordlist : similarlist) {
				String[] wordarray = wordlist.split(" ");
				Similar similar = new Similar();
				similar.feature = wordarray[0];
				for (String word : wordarray) {
					similar.similarfea.add(word);
				}
				similarfealist.add(similar);
			}
			
			SenFea[] senFeaArray = new SenFea[1];
			for(String sentence : featureSentimrntList){
				String[] mainterm = sentence.split(":");
				
				if(mainterm.length != 1){
					String[] otherterm = mainterm[1].split(",");
					senFeaArray = new SenFea[otherterm.length];
					for (int i = 0; i < otherterm.length; i++) {
						if(!otherterm[i].isEmpty())
						{
							SenFea newSenFea = new SenFea();
							newSenFea.feature = mainterm[0];
							newSenFea.sentiment = otherterm[i];
							senFeaArray[i] = newSenFea;
						}
					}
					computeRelevancyDegree(senFeaArray.length, senFeaArray, SentenceList);
				}
			}
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	//
	public static ArrayList<String> getSimilsrFeature(String feature){
		for (Similar similar : similarfealist) {
			if(similar.feature.equals(feature)){
				return similar.similarfea;
			}
		}
		return null;
	}
	//estimate the degree of association between sentiment and product feature
	public static float[] tempBMI(SenFea subSenFea, ArrayList<ArrayList<String>> sentencelist){
		
		float[] result = new float[8];
		float both, neither, spresence, fpresence, 
			sabsence, fabsence, fscontray, sfcontray;
		Float bothT, neitherT, contrayT1, contrayT2;
		both = both_count(subSenFea, sentencelist);
		neither = neither_count(subSenFea, sentencelist);
		result[6] = spresence = presence_count(subSenFea.sentiment, sentencelist);
		result[5] = fpresence = presence_count(subSenFea.feature, sentencelist);
		sabsence = 1 - spresence;
		fabsence = 1 - fpresence;
		fscontray = contray_count(subSenFea.feature, subSenFea.sentiment, sentencelist);
		sfcontray = contray_count(subSenFea.sentiment, subSenFea.feature, sentencelist);
		
		result[7] = spresence;
		result[1] = bothT = log( (both + 1)/spresence/fpresence, 2 )*both;
		if(bothT.isNaN()){
			result[1] = bothT = (float)0;
		}
		result[2] = neitherT = neither*log( (neither + 1)/sabsence/fabsence, 2);//useless
		result[3] = contrayT1 = log( (fscontray + 1)/fpresence/sabsence, 2 )*fscontray;
		if(contrayT1.isNaN()){
			result[3] = contrayT1 = fscontray;
		}
		result[4] = contrayT2 = log( (sfcontray + 1)/spresence/fabsence, 2 )*sfcontray;
		if(contrayT2.isNaN()){
			result[4] = contrayT2 = sfcontray;
		}
		result[0] = BMI*(bothT) - (1 - BMI)*(contrayT1 + contrayT2);
		return result;
	}
	//linear normalization process
	public static void computeRelevancyDegree(int n, SenFea[] subSenFea, ArrayList<ArrayList<String>> sentencelist) throws IOException{
		class Relevancy{
			String feature, sentiment;
			public float BMI, bothT, neitherT, contrayT1, contrayT2, spresence;
			public int Pfea, Psen;
			public String toString(int type){
				if(type == 0)
					return String.format("%s %s %f %d %d", feature, sentiment, spresence, Pfea, Psen);
				else if(type == 1)
					return String.format("%s\t%s\t%-7.4f %-7.7f %-3.3f %-7.7f %-7.7f %6d %4d", feature, sentiment, BMI, bothT, neitherT, contrayT1, contrayT2, Pfea, Psen);
				else
					return null;
			}
		}
//		if(subSenFea[0].feature.equals("�ֻ�"))
//			System.out.println("");
		Relevancy[] reArray = new Relevancy[n];
		float[] result = new float[5];
		float A = (float)0.5, B = (float)0.5;
		float max = 0, min = 1;
		byte[] buff=new byte[]{};
		ArrayList<Relevancy> indiBMI= new ArrayList<Relevancy>();//record the relevance degree
		File deletef = new File("D:\\programs-about-dachuang\\11-22\\特别度计算库\\result.txt");
		if(deletef.exists())
			deletef.delete();
		FileOutputStream out = new FileOutputStream("D:\\programs-about-dachuang\\11-22\\特别度计算库\\result.txt", true);
		for(int j = 0; j < n; j++){
			reArray[j] = new Relevancy();
			result = tempBMI(subSenFea[j], sentencelist);
			reArray[j].feature = subSenFea[j].feature;
			reArray[j].sentiment = subSenFea[j].sentiment;
			reArray[j].bothT = result[1];
			reArray[j].neitherT = result[2];
			reArray[j].contrayT1 = result[3];
			reArray[j].contrayT2 = result[4];
			reArray[j].BMI = result[0];
			reArray[j].Pfea = (int)(result[5]*sentencelist.size());
			reArray[j].Psen = (int)(result[6]*sentencelist.size());
			reArray[j].spresence = result[7];
		}
		//check if both_count exist no-zero
		boolean allzero = true;
		for(int i = 0; i < n; i++){
			if(reArray[i].bothT!=0)
				allzero = false;
		}
		//update BMI to optimize result
		for(int i = 0; i < TIMES; i++){
			for(int j = 0; j < n; j++){
				if(reArray[j].BMI > max)
					max = reArray[j].BMI;
				if(reArray[j].BMI < min)
					min = reArray[j].BMI;
			}
			for(int j = 0; j < n; j++)
				reArray[j].BMI = A*reArray[j].BMI + B*(reArray[j].BMI - min)/(max - min);
			max = 0;
			min = 1;
		}
		//set zero BMI when both_count is zero
		int usefulBMI = 0;//record the number that is bigger than 0.8;
		if(!allzero)
			for(int i = 0; i < n; i++){
				if(reArray[i].bothT==0){
					reArray[i].BMI = 0;
				}
				if(reArray[i].BMI>EI)
					usefulBMI++;
			}
		//continue optimize
		if(allzero){
			//sort by contrayT2 when all bothT are zero
			for (int i = 0; i < n; i++) {
				int lowIndex = i;
				for (int j = n - 1; j > i; j--) {
					if (reArray[j].contrayT2 < reArray[lowIndex].contrayT2) {
						lowIndex = j;
					}
				}
				Relevancy temp = reArray[i];
				reArray[i] = reArray[lowIndex];
				reArray[lowIndex] = temp;
			}
			indiBMI.add(reArray[0]);
			System.out.println(reArray[0].toString(1));
		}else{
			//sort by BMI
			for (int i = 0; i < n; i++) {
				int lowIndex = i;
				for (int j = n - 1; j > i; j--) {
					if (reArray[j].BMI > reArray[lowIndex].BMI) {
						lowIndex = j;
					}
				}
				Relevancy temp = reArray[i];
				reArray[i] = reArray[lowIndex];
				reArray[lowIndex] = temp;
			}
			if(usefulBMI >= 3){
				int index = 0;
				if(reArray[0].BMI - reArray[2].BMI <= 0.025){
					for(int i=0;i<2;i++){
						if(reArray[i].bothT>reArray[index].bothT)
							index = i;
					}
					indiBMI.add(reArray[index]);
					System.out.println(reArray[index].toString(1));
				}
				else{
					indiBMI.add(reArray[0]);
					System.out.println(reArray[0].toString(1));
				}
			}else if(usefulBMI >= 1){
				indiBMI.add(reArray[0]);
					System.out.println(reArray[0].toString(1));
			}else{
				for(int i = 0; i < n; i++){
					int lowIndex = i;
					for (int j = n - 1; j > i; j--) {
						if (reArray[j].bothT > reArray[lowIndex].bothT) {
							lowIndex = j;
						}
					}
					Relevancy temp = reArray[i];
					reArray[i] = reArray[lowIndex];
					reArray[lowIndex] = temp;
				}
				if(reArray[0].BMI>0.5){
					indiBMI.add(reArray[0]);
					System.out.println(reArray[0].toString(1));
				}
			}
		}
		
		//record high relevance feature-sentiment pair
		for(Relevancy pair : indiBMI){
			SenFea tmpSenFea = new SenFea();
			tmpSenFea.feature = pair.feature;
			tmpSenFea.sentiment = pair.sentiment;
			comSenFea.add(tmpSenFea);
			buff = (pair.toString(0)+"\r\n").getBytes();
			out.write(buff, 0, buff.length);
		}
		
	}
	//compute the logarithm
	public static float log(float value, float base){
		return (float)( Math.log(value) / Math.log(base) );
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
	//get list of sentences
	public static ArrayList<ArrayList<String>> extractAllFiles(String dirc) throws IOException{
		
		//List<String> filelist = readDirs(dirc);
		ArrayList<ArrayList<String>> sentencelist = new ArrayList<ArrayList<String>>();
		//for(String file : filelist){
			//ArrayList<String> sentence = readFile(file);
                        ArrayList<String> sentence = readFile(dirc);
			for(String text : sentence){
				ArrayList<String> wordlist = new ArrayList<String>();
				text = text.substring(4);
				String[] extracted = text.split(":");
				ArrayList<String> featurelist = cutWords(extracted[0]);
				ArrayList<String> sentimentlist = cutWords(extracted[1]);
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
						wordlist.add(sentimentlist.get(0));
						sentimentlist.remove(0);
					}
					else
						break;
				}
				sentencelist.add(wordlist);
			}
		//}
		
		return sentencelist;
	}
	//word segmentation
	public static ArrayList<String> cutWords(String file) throws IOException{
		
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(file.split(" ")));
		return words;
	}
	//probability that t1 and t2 both appear in a text window
	public static float both_count(SenFea subSenFea, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		ArrayList<String> similarfeature = getSimilsrFeature(subSenFea.feature);
		if(similarfeature == null){
			for(ArrayList<String> wordlist: sentencelist)
				if(wordlist.contains(subSenFea.feature) & wordlist.contains(subSenFea.sentiment))
					appearstimes++;
		} else {
			for(ArrayList<String> wordlist: sentencelist){
				boolean flag = false;
				for(String similar :similarfeature){
					if(wordlist.contains(similar) & wordlist.contains(subSenFea.sentiment)){
						flag = true;
						break;
					}
				}
				if(flag)
					appearstimes++;
			}
		}
		return (float)appearstimes/alltimes;
	}
	//probability that t1 and t2 neither appear in a text window
	public static float neither_count(SenFea subSenFea, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		ArrayList<String> similarfeature = getSimilsrFeature(subSenFea.feature);
		if(similarfeature == null){
			for(ArrayList<String> wordlist: sentencelist)
				if(!wordlist.contains(subSenFea.feature) & !wordlist.contains(subSenFea.sentiment))
					appearstimes++;
		} else {
			for(ArrayList<String> wordlist: sentencelist){
				boolean flag = false;
				for(String similar :similarfeature){
					if(!wordlist.contains(similar) & !wordlist.contains(subSenFea.sentiment)){
						flag = true;
						break;
					}
				}
				if(flag)
					appearstimes++;
			}
		}
		return (float)appearstimes/alltimes;
	}
	//probability that a term appear in a text window
	public static float presence_count(String term, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		ArrayList<String> similarfeature = getSimilsrFeature(term);
		if(similarfeature == null){
			for(ArrayList<String> wordlist: sentencelist)
				if(wordlist.contains(term))
					appearstimes++;
		} else {
			for(ArrayList<String> wordlist: sentencelist){
				boolean flag = false;
				for(String similar :similarfeature){
					if(wordlist.contains(similar)){
						flag = true;
						break;
					}
				}
				if(flag)
					appearstimes++;
			}
		}
		return (float)appearstimes/alltimes;
	}
	//probability that t1 appear and t2 does not appear in a text window
	public static float contray_count(String term1, String term2, ArrayList<ArrayList<String>> sentencelist){
		
		int alltimes = sentencelist.size(), appearstimes = 0;
		ArrayList<String> similarfeature1 = getSimilsrFeature(term1);
		ArrayList<String> similarfeature2 = getSimilsrFeature(term2);
		if(similarfeature1 == null && similarfeature2 == null){
			for(ArrayList<String> wordlist: sentencelist)
				if(wordlist.contains(term1) & !wordlist.contains(term2))
					appearstimes++;
		} else {
			if(similarfeature2 == null)
				for(ArrayList<String> wordlist: sentencelist){
					boolean flag = false;
					for(String similar :similarfeature1){
						if(wordlist.contains(similar) & !wordlist.contains(term2)){
							flag = true;
							break;
						}
					}
					if(flag)
						appearstimes++;
				}
			else if(similarfeature1 == null)
				for(ArrayList<String> wordlist: sentencelist){
					boolean flag = false;
					for(String similar :similarfeature2){
						if(!wordlist.contains(similar) & wordlist.contains(term1)){
							flag = true;
							break;
						}
					}
					if(flag)
						appearstimes++;
				}
		}
		return (float)appearstimes/alltimes;
	}
	//simple amount
	public static int count(String term, ArrayList<ArrayList<String>> sentencelist){
		int appearstimes = 0;
		ArrayList<String> similarfeature = getSimilsrFeature(term);
		if(similarfeature == null){
			for(ArrayList<String> wordlist: sentencelist)
				if(wordlist.contains(term))
					appearstimes++;
		} else {
			for(ArrayList<String> wordlist: sentencelist){
				boolean flag = false;
				for(String similar :similarfeature){
					if(wordlist.contains(similar)){
						flag = true;
						break;
					}
				}
				if(flag)
					appearstimes++;
			}
		}
		return appearstimes;
	}
}
