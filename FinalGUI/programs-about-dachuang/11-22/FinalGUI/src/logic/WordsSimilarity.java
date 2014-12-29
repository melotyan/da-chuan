package logic;

public class WordsSimilarity implements Comparable<WordsSimilarity>{
	private String s1;
	private String s2;
	private double similarity;
	public WordsSimilarity(String wordSentence1, String  wordSentence2,
			Double valueOf) {
		// TODO Auto-generated constructor stub
		s1 =  wordSentence1;
		s2 =  wordSentence2;
		similarity = valueOf;
	}
	public String getS1() {
		return s1;
	}
	public void setS1(String s1) {
		this.s1 = s1;
	}
	public String getS2() {
		return s2;
	}
	public void setS2(String s2) {
		this.s2 = s2;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	@Override
	public int compareTo(WordsSimilarity o){
		if(this.similarity > o.getSimilarity()){
			return -1;
		}
		else if (this.similarity < o.getSimilarity()){
			return 1;
		}
		return 0;
	}
}
