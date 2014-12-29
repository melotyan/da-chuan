package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyStore.Entry;
import java.util.HashMap;
import java.util.Scanner;

public class RequestSimilarity {
	public HashMap<String,String> map=new HashMap<String,String>();
	public HashMap<String,String> fremap = new HashMap<String,String>();
	public void loadMap() throws FileNotFoundException{
		//����ļ���ʽ�� ��1 ��2 ����ֵ
		Scanner fin=new Scanner(new File("D:/programs-about-dachuang/11-22/�ر�ȼ����/SimilarDegree222.txt"));
		while(fin.hasNextLine()){
			String s,s1,s2,value,frequency;
			s = fin.nextLine();
			String[] tokens = s.split("\\|");
			//String[] mainterm = sentence.split(":");
			s1 = tokens[0];
			s2 = tokens[1];
			//s1=fin.next();
			//s2=fin.next();
			s1=s1+s2;
			//value=fin.next();
			value = tokens[2];
			map.put(s1, value);
			fremap.put(s1, tokens[3]);
			//System.out.println(map.containsKey(s1));
		}
		
		/*while(fin.hasNext()){
			String s1,s2,value;
			
		}*/
	}
	/*���s1��s2�������ļ���s1��s2�Ĺ����ȣ�����ļ���û������Զ�Ĺ�����û�У�����"NotConsistThisString'sAssociationValue"
	 * */
	public String request(String s1,String s2) throws FileNotFoundException{
		if(map.isEmpty()){
			loadMap();
		}
		String s=s1+s2;
		if(map.containsKey(s)){
			return map.get(s);
		}else {
			return "NotConsistThisString'sAssociationValue";
		}
	}
	
	public String requestFrequency(String s1,String s2) throws FileNotFoundException{
		if(map.isEmpty()){
			loadMap();
		}
		String s=s1+s2;
		if(fremap.containsKey(s)){
			return fremap.get(s);
		}else {
			return "NotConsistThisString'sAssociationValue";
		}
	}
	/*�������
	 * */
	public static void main(String args[]) throws FileNotFoundException{
		RequestSimilarity rs=new RequestSimilarity();
		
		Scanner cin=new Scanner(System.in);
		String s1=cin.next();
		String s2=cin.next();
		while(s1!=","&&s2!=","){
			System.out.println(rs.request(s1, s2));
			s1=cin.next();
		    s2=cin.next();
		}
		
	}
	
}
