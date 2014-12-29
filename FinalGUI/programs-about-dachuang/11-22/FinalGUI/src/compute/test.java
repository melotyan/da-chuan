package compute;

import ICTCLAS.I3S.AC.ICTCLAS50;

import java.util.*;
import java.io.*;

public class test {
	public static class WordType{
		public String word;
		public String type;
		public WordType(String newWord, String newType){
			this.word = newWord;
			this.type = newType;
		}
	}
	public static class Sdegree{
		public String feature = new String();
		public float subsume;
		public float specadj;
		public float specvarg;
	}
	@SuppressWarnings("unused")
	private static String exampleInput = "屏幕很大很清晰\n" +
			"性价比比较高\n" +
			"价格还算便宜\n" +
			"系统反应速度快\n" +
			"音质一般\n" +
			"电池带电还可以\n" +
			"强大的配置\n" +
			"轻松地把握\n" +
			"运行得迅速\n" +
			"操作方便\n" +
			"做工优良精致\n" +
			"价格还算优惠\n" +
			"触屏的反应也很快\n";
	private static String file1 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/testfiles1";

	public static void main(String[] args) throws IOException{
		ArrayList<ArrayList<String>> SentenceList = new ArrayList<ArrayList<String>>(extractAllFiles(file1));

//		for (ArrayList<WordType> sentence : SentenceList) {
//			for (WordType w : sentence) {
//				System.out.println(w.word+"|"+w.type+"-");
//			}
//			participlePhrase(sentence);
//		}
		
//		primaryPartition();
//		secondPartition();
		HashMap<String, Float> sdegree = new HashMap<String, Float>(specialDegree(SentenceList));
//		similarDegree(SentenceList);
		
		Iterator iter = sdegree.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entrys = (Map.Entry)iter.next();
			System.out.println(entrys.getKey() + "|" + entrys.getValue());
		}
	}
	//get feature partition by word number
	public static void similarDegree(ArrayList<ArrayList<String>> SentenceList) throws FileNotFoundException, IOException{
		String filedir = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/特别度计算库/";
		ArrayList<String> file = new ArrayList<String>(readFile(filedir + "nounDic.txt"));
		HashMap<String, ArrayList<String>> ff = new HashMap<String, ArrayList<String>>();
		//get similar feature pair
		for(String sentence : file){
			String[] mainterm = sentence.split(":");
			ArrayList<String> others = new ArrayList<String>();
			if(mainterm.length != 1)
				others = new ArrayList<String>(Arrays.asList(mainterm[1].split(",")));
			ff.put(mainterm[0], others);
		}
		//output result
		FileOutputStream out = new FileOutputStream("G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/特别度计算库/SimilarDegree.txt", true);
		byte[] buff=new byte[]{};
		//compute the degree of co-occurrence in file
		Iterator iter = ff.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entrys = (Map.Entry)iter.next();
			ArrayList<String> wordlist = (ArrayList<String>)entrys.getValue();
			if(wordlist.size() == 0){
				String str = entrys.getKey() + "：no similar word\r\n";
				System.out.print(str);
				buff = str.getBytes();
				out.write(buff, 0, buff.length);
				continue;
			}
			String t1 = (String)entrys.getKey();
			
			for (String t2 : wordlist) {
				double n1 = 0, n2 = 0, n3 = 0;
				double simcoldoc = 0, simasym = 0;
				double n = SentenceList.size();
				for(ArrayList<String> sentence : SentenceList){
					if (sentence.contains(t1)){
						n1++;
						if(sentence.contains(t2))
							n3++;
					}
					if (sentence.contains(t2))
						n2++;
				}
				simcoldoc = ((n1+n2)!=0) ? 2*n3/(n1+n2) : 0;
				simasym = (n3!=0) ? n3/n1*log(n/n3,n/n1)*log(n/n2, n) : 0;
				String str = t1+"|"+t2+"|"+simcoldoc+"|"+simasym+"\r\n";
				System.out.print(t1+"|"+t2+"|"+(simcoldoc+simasym)+"\r\n");
				buff = str.getBytes();
				out.write(buff, 0, buff.length);
			}
			
//			ArrayList<FeaPair> pair = new ArrayList<FeaPair>();
//			for (String t2 : wordlist) {
//				FeaPair feaPair = new FeaPair();
//				feaPair.feature = t1;
//				feaPair.nearfeature = t2;
//				pair.add(feaPair);
//			}
//			FeaPair[] feaArray = new FeaPair[pair.size()];
//			pair.toArray(feaArray);
//			CAssociation.CSimilarity(pair.size(), feaArray, SentenceList);
			
		}
		out.close();
	}
	//get feature partition in word combination
	public static void secondPartition() throws IOException{
		String testfile1 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/N-A对儿名词词频.txt";
		String testfile2 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/特征词.txt";
		ArrayList<String> sentence = new ArrayList<String>(readFile(testfile1));
		ArrayList<String> feature = new ArrayList<String>(readFile(testfile2));
		String testfile3 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/停用词.txt";
		FileOutputStream fis1 = new FileOutputStream(testfile3, true);
		OutputStreamWriter osw1 = new OutputStreamWriter(fis1);
		String testfile4 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/组合词.txt";
		FileOutputStream fis2 = new FileOutputStream(testfile4, true);
		OutputStreamWriter osw2 = new OutputStreamWriter(fis2);
		
		for (String word : sentence) {
			String[] term = word.split(" ");
			if(term[0].length() == 1){
				osw1.write(term[0] + "\r\n");
			} else if(term[0].length() == 2){
				if(!feature.contains(term[0]))
					osw1.write(term[0] + "\r\n");
			} else if(term[0].length() == 3){
				if(!feature.contains(term[0]))
					osw1.write(term[0] + "\r\n");
			} else if(term[0].length() == 4){
				if(feature.contains(term[0].subSequence(0, 2))&&
					feature.contains(term[0].subSequence(2, 4)))
					osw2.write(term[0] + "\r\n");
			} else if(term[0].length() == 5){
				if(feature.contains(term[0].subSequence(0, 2))||
					feature.contains(term[0].subSequence(2, 5))||
					feature.contains(term[0].subSequence(0, 3))||
					feature.contains(term[0].subSequence(3, 5)))
					osw2.write(term[0] + "\r\n");
			} else if(term[0].length() >= 6){
				for (String f : feature) {
					if(term[0].contains(f))
						osw2.write(term[0] + "\r\n");
				}
			}
		}
		osw1.close();
		osw2.close();
		fis1.close();
		fis2.close();
		System.out.println("Over!");
	}
	//get feature partition by word number
	public static void primaryPartition() throws IOException{
		String testfile = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/N-A对儿名词词频.txt";
		ArrayList<String> sentence = new ArrayList<String>(readFile(testfile));
		
		String testfile1 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/单字.txt";
		String testfile2 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/双字.txt";
		String testfile3 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/三字.txt";
		String testfile4 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/四字.txt";
		String testfile5 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/五字.txt";
		String testfile6 = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/五字以上.txt";
		FileOutputStream fis1 = new FileOutputStream(testfile1, true);
		FileOutputStream fis2 = new FileOutputStream(testfile2, true);
		FileOutputStream fis3 = new FileOutputStream(testfile3, true);
		FileOutputStream fis4 = new FileOutputStream(testfile4, true);
		FileOutputStream fis5 = new FileOutputStream(testfile5, true);
		FileOutputStream fis6 = new FileOutputStream(testfile6, true);
		OutputStreamWriter osw1 = new OutputStreamWriter(fis1);
		OutputStreamWriter osw2 = new OutputStreamWriter(fis2);
		OutputStreamWriter osw3 = new OutputStreamWriter(fis3);
		OutputStreamWriter osw4 = new OutputStreamWriter(fis4);
		OutputStreamWriter osw5 = new OutputStreamWriter(fis5);
		OutputStreamWriter osw6 = new OutputStreamWriter(fis6);
		try{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false) {
				System.out.println("Init Fail!");
				System.exit(0);
			}
			else { 
				System.out.println("Init Succeed!"); 
			}	
			for (String word : sentence) {
				String[] term = word.split(" ");
				byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(term[0].getBytes("GB2312"), 0, 1);
				String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
				if(term[0].length() == 1){
					osw1.write(nativeStr + term[1] + "\r\n");
				} else if(term[0].length() == 2){
					osw2.write(nativeStr + term[1] + "\r\n");
				} else if(term[0].length() == 3){
					osw3.write(nativeStr + term[1] + "\r\n");
				} else if(term[0].length() == 4){
					osw4.write(nativeStr + term[1] + "\r\n");
				} else if(term[0].length() == 5){
					osw5.write(nativeStr + term[1] + "\r\n");
				} else if(term[0].length() >= 6){
					osw6.write(nativeStr + term[1] + "\r\n");
				}
			}
			testICTCLAS50.ICTCLAS_Exit();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		osw1.close();
		osw2.close();
		osw3.close();
		osw4.close();
		osw5.close();
		osw6.close();
		fis1.close();
		fis2.close();
		fis3.close();
		fis4.close();
		fis5.close();
		fis6.close();
		System.out.println("Over!");
	}
	//get special degree
	public static class Similar{
		String feature;
		ArrayList<String> similarfea;
		public Similar(){
			feature = new String();
			similarfea = new ArrayList<String>();
		}
	}
	private static ArrayList<Similar> similarfealist = new ArrayList<Similar>();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap<String, Float> specialDegree(ArrayList<ArrayList<String>> SentenceList) throws FileNotFoundException, IOException{
		float A = (float) 0.4, B = (float) 0.3, C = (float) 0.3;
		
		String similarfile = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/testfiles2/特征词的同义词库.txt";
		//get similar 
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
		
		String filedir = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/特别度计算库/";
		FileOutputStream fis = new FileOutputStream(filedir + "SpecialDegree.txt", true);
		OutputStreamWriter osw = new OutputStreamWriter(fis);
		ArrayList<String> testfile1 = new ArrayList<String>(readFile(filedir + "nounDic3.0.txt"));
		ArrayList<String> testfile2 = new ArrayList<String>(readFile(filedir + "adjDic3.0.txt"));
		ArrayList<String> testfile3 = new ArrayList<String>(readFile(filedir + "verbDic3.0.txt"));
		HashMap<String, ArrayList<String>> ff = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> fa = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> fv = new HashMap<String, ArrayList<String>>();
		for(String sentence : testfile1){
			String[] mainterm = sentence.split(":");
			ArrayList<String> others = new ArrayList<String>();
			if(mainterm.length != 1)
				others = new ArrayList<String>(Arrays.asList(mainterm[1].split(",")));
			ff.put(mainterm[0], others);
		}
		for(String sentence : testfile2){
			String[] mainterm = sentence.split(":");
			ArrayList<String> others = new ArrayList<String>();
			if(mainterm.length != 1){
				String[] otherterm = mainterm[1].split(",");
				
				for (int i = 0; i < otherterm.length; i++) {
					if(!otherterm[i].isEmpty()&&otherterm[i] != "1")
						others.add(otherterm[i]);
				}
			}
			fa.put(mainterm[0], others);
		}
		for(String sentence : testfile3){
			String[] mainterm = sentence.split(":");
			ArrayList<String> others = new ArrayList<String>();
			if(mainterm.length != 1){
				String[] otherterm = mainterm[1].split(",");
				for (int i = 0; i < otherterm.length; i++) {
					if(!otherterm[i].isEmpty()&&otherterm[i]!="、"&&otherterm[i]!="6")
						others.add(otherterm[i]);
				}
			}
			fv.put(mainterm[0], others);
		}
		Sdegree[] sdegree = new Sdegree[testfile1.size()];
		HashMap<String, Float> spec = new HashMap<String, Float>();
		//co-occurrence-term
		Iterator iter1 = ff.entrySet().iterator();
		for(int n = 0; iter1.hasNext(); n++){
			Map.Entry entrys = (Map.Entry)iter1.next();
			ArrayList<String> wordlist = (ArrayList<String>)entrys.getValue();
			String t1 = (String)entrys.getKey();
			int sum = wordlist.size(), i = 0;
			int[] subsume = new int[sum];
			sdegree[n] = new Sdegree();
			sdegree[n].feature = t1;
			ArrayList<String> similarfeature = getSimilsrFeature(t1);
			
			for (String t2 : wordlist) {
				int n1 = 0, n2 = 0;
				if(similarfeature == null){
					for(ArrayList<String> sentence : SentenceList){
						if (sentence.contains(t1))
							n1++;
						if (sentence.contains(t2))
							n2++;
					}
				} else {
					for(ArrayList<String> sentence : SentenceList){
						for(String similar :similarfeature){
							if (sentence.contains(similar)){
								n1++;
								break;
							}
						}
						if (sentence.contains(t2))
							n2++;
					}
				}
				if (n1>n2)
					subsume[i] = 1;
				else 
					subsume[i] = 0;
				i++;
			}
			for (i = 0; i < sum; i++){
				sdegree[n].subsume += subsume[i];
			}
			if(sum != 0)
				sdegree[n].subsume /= sum;
		}
		//adj-term
		Iterator iter2 = fa.entrySet().iterator();
		while(iter2.hasNext()){
			Map.Entry entrys = (Map.Entry)iter2.next();
			ArrayList<String> wordlist = (ArrayList<String>)entrys.getValue();
			String t1 = (String)entrys.getKey();
			float specadj = 0;
			ArrayList<String> similarfeature = getSimilsrFeature(t1);
			
			for (String t2 : wordlist) {
				double adj = 0, feature = 0;
				if(similarfeature == null){
					for(ArrayList<String> sentence : SentenceList){
						if (sentence.contains(t1)){
							feature++;
							if (sentence.contains(t2)){
								adj++;
							}
						}
					}
				} else {
					for(ArrayList<String> sentence : SentenceList){
						for(String similar :similarfeature){
							if (sentence.contains(similar)){
								feature++;
								if (sentence.contains(t2)){
									adj++;
								}
								break;
							}
						}
					}
				}
				if(feature == 0 || adj == 0)
					specadj += 0;
				else
					specadj += -Math.log(adj/feature)*adj/feature;
			}
			
			for(int i = 0; i < sdegree.length; i++){
				if(sdegree[i].feature.equals(t1)){
					sdegree[i].specadj = specadj;
					break;
				}
			}
		}
		//verb-term
		Iterator iter3 = fv.entrySet().iterator();
		while(iter3.hasNext()){
			Map.Entry entrys = (Map.Entry)iter3.next();
			ArrayList<String> wordlist = (ArrayList<String>)entrys.getValue();
			String t1 = (String)entrys.getKey();
			float specvarg = 0;
			ArrayList<String> similarfeature = getSimilsrFeature(t1);
			
			for (String t2 : wordlist) {
				double verb = 0, term = 0;
				if(similarfeature == null){
					for(ArrayList<String> sentence : SentenceList){
						if (sentence.contains(t2)){
							verb++;
							if (sentence.contains(t1)){
								term++;
							}
						}
					}
				} else {
					for(ArrayList<String> sentence : SentenceList){
						if (sentence.contains(t2)){
							verb++;
							for(String similar :similarfeature){
								if (sentence.contains(similar)){
									term++;
									break;
								}
							}
						}
					}
				}
				
				if(verb == 0 || term == 0)
					specvarg += 0;
				else
					specvarg += -term/verb*Math.log(term/verb);
			}
			for(int i = 0; i < sdegree.length; i++){
				if(sdegree[i].feature.equals(t1)){
					sdegree[i].specvarg = specvarg;
					break;
				}
			}
		}
		//total
		for (int i = 0; i < testfile1.size(); i++){
			spec.put(sdegree[i].feature, A*sdegree[i].subsume + B*sdegree[i].specadj + C*sdegree[i].specvarg); 
			osw.write(sdegree[i].feature + "|" + sdegree[i].subsume + "|" + sdegree[i].specadj + "|" + sdegree[i].specvarg + "\r\n");
		}
		osw.close();
		fis.close();
		return spec;
	}
	public static ArrayList<String> getSimilsrFeature(String feature){
		for (Similar similar : similarfealist) {
			if(similar.feature.equals(feature)){
				return similar.similarfea;
			}
		}
		return null;
	}
	//get list of sentences, cut the word by ICTCLAS50 in the same time.
	public static ArrayList<ArrayList<String>> extractAllFiles(String dirc){
		ArrayList<ArrayList<String>> sentencelist = new ArrayList<ArrayList<String>>();
		try{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false) {
				System.out.println("Init Fail!");
				System.exit(0);
			}
			else { 
				System.out.println("Init Succeed!"); 
			}
			
			//导入用户字典
			int nCount = 0;
			String usrdir = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/pattern/特别度计算库/usrdir.txt"; //用户字典路径
			byte[] usrdirb = usrdir.getBytes();
			//第一个参数为用户字典路径，第二个参数为用户字典的编码类型(0:type unknown;1:ASCII码;2:GB2312,GBK,GB10380;3:UTF-8;4:BIG5)
			nCount = testICTCLAS50.ICTCLAS_ImportUserDictFile(usrdirb, 3);
			System.out.println("导入用户词个数"+ nCount);
			nCount = 0;
			
			List<String> filelist = test.readDirs(dirc);
			for(String file : filelist){
				ArrayList<String> sentence = test.readFile(file);
				for(String text : sentence){
					byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(text.getBytes("GB2312"), 0, 1);
					//System.out.println(nativeBytes.length);
					String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
					String[] str = nativeStr.split(" ");
					ArrayList<String> phrase = new ArrayList<String>();
					for (int i=0;i<str.length;i++) {
						if(!str[i].isEmpty()&&str[i].contains("/")) {
							String[] t = str[i].split("/");
							if(t[1].startsWith("n")||
								t[1].startsWith("v")||
								t[1].startsWith("a")){
								//WordType a = new WordType(t[0],t[1]);
								String a = new String(t[0]);
								phrase.add(a);
							}
						}
					}
					sentencelist.add(phrase);
				}
			}
			testICTCLAS50.ICTCLAS_Exit();
			
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		return sentencelist;
	}
	//get list of file for the directory, including sub-directory of it
	public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException{
		ArrayList<String> FileList = new ArrayList<String>();
		try{
			File file = new File(filepath);
			if(!file.isDirectory()){
				System.out.println("输入的[]");
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
		inStrR.close();
		br.close();
		return sentence;
	}
	//get phrases
	public static void participlePhrase(ArrayList<WordType> wordlist) throws FileNotFoundException, IOException{
		/*
		 * nvna
		 * na
		 * nva
		 * aude1n
		 * aude2vn
		 * vude3a
		 * va
		 * nza
		 * ndv (adv)
		 * vude1na
		 */
		String filedir = "G:/大学生创新知识竞赛/吴国仕团队/项目组1/tfidf_分词包/testfiles2/";
		FileOutputStream fis = new FileOutputStream(filedir+"nvna.txt", true);
		OutputStreamWriter osw = new OutputStreamWriter(fis);
		WordType[] sentence = new WordType[wordlist.size()]; 
		wordlist.toArray(sentence);
		for (int i=0;i<sentence.length;i++) {
			if(i+3<sentence.length&&sentence[i].type.startsWith("n")&&
								sentence[i+1].type.startsWith("v")&&
								sentence[i+2].type.startsWith("n")&&
								sentence[i+3].type.startsWith("a")){
				String phrase = sentence[i].word + sentence[++i].word + sentence[++i].word + sentence[++i].word + "\r\n";
				System.out.println(phrase);
				osw.write(phrase);
			}
		}
		osw.close();
	}
	//compute the logarithm
	public static double log(double d, double e){
		return (double)( Math.log(d) / Math.log(e) );
	}
}