package extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ICTCLAS.I3S.AC.ICTCLAS50;
import constants.Constants;

public class DealComments {
	public static ICTCLAS50 ictlcas;


	static {
		try {
			ictlcas = new ICTCLAS50();
			String argu = ".";
			// initialize
			if (ictlcas.ICTCLAS_Init(argu.getBytes("GB2312")) == false) {
				System.out.println("Init Fail");
			}

			// import customized lexicon to perfect the ICTCLAS50
			int nCount = 0;
			String userDir = "userdict.txt";
			byte[] userDirByte = userDir.getBytes();
			nCount = ictlcas.ICTCLAS_ImportUserDictFile(userDirByte, 0);
			System.out.println("导入用户词数: " + nCount);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * part-of-speech tagging in source file, and write the result to middle
	 * file
	 * 
	 * @param sourceFile
	 * @param targetFile
	 */
	public static void posSourceText(String sourceFile, String targetFile) {
		try {
			byte[] sourceFileByte = sourceFile.getBytes();

			byte[] targetFileByte = targetFile.getBytes();
			ictlcas.ICTCLAS_FileProcess(sourceFileByte, 0, 1, targetFileByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * extract the noun and adjective from middle file, and write the result to
	 * final file
	 * 
	 * @param middleFile
	 * @param finalFile
	 *
	public void dealMiddleText(String middleFile, String finalFile) {
		try {
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
				nounMatch = nounPattern.matcher(str);
				adjMatch = adjPattern.matcher(str);
				while (nounMatch.find())
					nounSet.add(nounMatch.group(1));
				while (adjMatch.find())
					adjSet.add(adjMatch.group(1));

				// write the noun and adjective to new file
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

	*/
	
	
	/**
	 * get all the file in source folder
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> getFile(String path) {
		// get file list where the path has
		File file = new File(path);
		// get the folder list
		File[] array = file.listFiles();
		ArrayList<String> filename = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				// only take file name
				filename.add(array[i].getName());
				System.out.println(array[i].getName());
				// take file path and name
				// System.out.println("#####" + array[i]);
				// take file path and name
				// System.out.println("*****" + array[i].getPath());
			} else if (array[i].isDirectory()) {
				getFile(array[i].getPath());
			}
		}
		return filename;
	}	
	
	/**
	 * sorted the map by map.value in inverted order
	 * 
	 * @param map
	 * @return
	 */
	public static List sortMapByValue(Map<String, Integer> map) {
		List list = new ArrayList<Entry>(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				Entry obj1 = (Entry) o1;
				Entry obj2 = (Entry) o2;
				return ((Integer) obj2.getValue()).compareTo((Integer) obj1
						.getValue());
			}

		});
		return list;
	}

	
}
