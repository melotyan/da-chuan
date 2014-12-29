package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



public class Words implements Comparable<Words>{
	private String wordSentence;
	private Float wordSpecialCore;
	
	public String getWordSentence(){
		return this.wordSentence;
	}
	public Float getWordSpecialCore() {
		if(wordSpecialCore == 0.0f){
			this.wordSpecialCore = findFromFile(this.wordSentence);
		}
		return this.wordSpecialCore;
	}
	public Words(String w,Float s){
		wordSentence = w;
		wordSpecialCore = s;
	}
	
	public Float findFromFile(String sword) {
		Scanner fin;
		try {
			fin = new Scanner(new File("D:/programs-about-dachuang/11-22/特别度计算库/SpecialDegreeResult.txt"));
			while(fin.hasNextLine()){
				String s,s1,s2,value,frequency;
				s = fin.nextLine();
				String[] tokens = s.split(":");
				//String[] mainterm = sentence.split(":");
				s1 = tokens[0];
				s2 = tokens[1];
				if(s1.equals(sword)){
					return Float.valueOf(s2);
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Float.valueOf(0);
		
	}
	public void setWordSentence(String wordSentence) {
		this.wordSentence = wordSentence;
	}
	public void setWordSpecialCore(Float wordSpecialCore) {
		this.wordSpecialCore = wordSpecialCore;
	}
	public Words(String w){
		wordSentence = w;
		wordSpecialCore = 0.0f;
	}
	@Override
	public int compareTo(Words o){
		if(this.wordSpecialCore > o.getWordSpecialCore()){
			return -1;
		}
		else if (this.wordSpecialCore < o.getWordSpecialCore()){
			return 1;
		}
		return 0;
	}
	
}
