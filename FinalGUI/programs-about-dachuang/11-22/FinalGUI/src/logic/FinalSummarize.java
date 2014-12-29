package logic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import logic.AreaTree;
public class FinalSummarize {
	public String[][] professionInfo;
	static int ROW_SIZE = 60;
	public String getSummarize(String filename){
		String s = null;
		BufferedReader br;
	    s += professionInfo[0][1]+ professionInfo[1][1] + ":\n";
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream("D:/programs-about-dachuang/11-22/�ر�ȼ����/ƻ��iPhone5s.txt"), "GBK"));
			while(br.readLine() != null){
				String[] featureInfo = br.readLine().split(" ");
				s+=getInfo(featureInfo[0],featureInfo[1],featureInfo[2]);
				s+= "\n";
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		return s;
	}
        public String getSummarize(AreaTree tree,String[][] review){
		int i;
		String sum = "";
		Node p1 = tree.findNode("品牌");
		Node p2 = tree.findNode("型号");
		if(p1!=null && p2!=null){
			sum+= p1.proParameter + p2.proParameter +": \n";
		}
		for(i = 0; i < review.length;i++){
			String feature = review[i][0];
			System.out.println("现在的评论特征是" + review[i][0]);
			if("end".equals(feature)) break;
			Node node =tree.findNode(feature);//要改也应该是在findnode这个函数改，应该还可以查找近义词集合的
			if(node != null){
				System.out.println("找到了这个评论特征结点");//参数可能在他的孩子身上
				if(Integer.valueOf(review[i][3]) >= 100 || Integer.valueOf(review[i][4]) >=100){
					//这里如果只考虑二级特征的话。。。。//这里默认取特征和情感出现次数小的
					System.out.println("开始得到这个节点的摘要");
					sum += getInfo(node,review[i][1],min(review[i][3],review[i][4]));
					
				}
				else {
					System.out.println("但这个节点不是所关注的");
				}
			}
			System.out.println("没有在本体树中找到这个节点");
		}
		
		
		return sum;
		
	}

	private String min(String s1,String s2){
		if(Integer.valueOf(s1) <= Integer.valueOf(s2)){
			return s1;
		}
		else return s2;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FinalSummarize hand = new FinalSummarize();
		String summarize = hand.getSummarize("file.txt");	
		System.out.println(summarize);
		
	}
	public FinalSummarize(){
		professionInfo = new String[ROW_SIZE][2];
		 //getArgumentsList();
	}
        public String getInfo(Node node,String sentiment,String ratio){
		String s = "";//这个函数修改逻辑可以改变往上找多少层。其实也可以直接把参数名放在这个节点上，这样就不用遍历了
		/*if(feature.father!=null){
	       s += "本手机" +feature.father.feature +feature.feature + "是"+feature.proParameter + ",";
		   s += ratio +"条评论认为" + feature + sentiment+"\n";//这里还需要加上feature的情感和
		}
		else{
			 s += "本手机" +feature.feature + "是"+feature.proParameter + ",";
			s += ratio +"条评论认为" + feature + sentiment+"\n";//这里还需要加上feature的情感和
		}*/
		//子节点有参数，父节点没有，返回把子节点的参数也加到父节点上
		String par = "";
		String name = "";
		System.out.println("当前节点"+node.feature+":专业名称"+node.proName+":专业参数"+node.proParameter);
		if(node.proParameter == null && node.children!= null){
			   name += node.feature;//name更应该是一个同义词集合
			   par = getAllParAboutNode(node,name);
			}
		else if(node.proParameter == null){
				par += "不知道";//???/为什么不知道跑不出来
		}else {
				par +=node.proParameter;
		}
		//if(polarity.equals("正面")){
		if(!par.equals("")){
			s += "本手机" + par + ",";
			s += ratio +"条评论认为" + node.feature + sentiment+"\n";//这里还需要加上feature的情感和
		
		}
		else {
			//s += "本手机" +node.feature + "是"+par + ",";
			s += ratio +"条评论认为" + node.feature + sentiment+"\n";//这里还需要加上feature的情感和	
		}
		//}
		//else if(polarity.equals("负面")){
			//s += "本手机" +node.feature + "是"+par + ",";
			//s += "但是"+ratio +"条评论认为" + node.feature + sentiment+"\n";//这里还需要加上feature的情感和
			
		
		System.out.println("这个节点的摘要是"+s);
		return s;
	}
	
	public String getAllParAboutNode(Node node,String name){
		Iterator iter = node.children.iterator();
		String par = "";//name����
		//����Ӧ����node.feature����node.feature�Ľ���ʼ�����
		/*if(node.proParameter!= null && isContains(name,node)){
			par+=" "+node.proName+":";
			par+= node.proParameter+" ";
			//name+= node.proName;//
			return par;
		}*/
		if(iter == null){
			return par;
		}
		else {
			while(iter.hasNext()){
				Node child = (Node)iter.next();
				String na = "";
				//par+= getAllParAboutNode(child,na);
				if(child.proParameter!= null && isContains(name,child)){
					par+=" "+child.proName+":";
					par+= child.proParameter+" ";
					//name+= node.proName;//
					return par;
				}
			}
		}
		return par;
	}
	public String getInfo(String feature,String sentiment,String ratio){
		String s = null;
		for(int i = 0; i < ROW_SIZE ;i++ ){
			if(professionInfo[i][0].equals("end")) return s;
			if(new String(professionInfo[i][0]+professionInfo[i][1]).contains(feature)){
				s += "���ֻ�" + professionInfo[i][0] +"��" + professionInfo[i][1] + ",";
				s += ratio +"%" +"���û���Ϊ" + feature + sentiment;//���ﻹ��Ҫ����feature����к�
				break;
			}
	      
		}
		return s;
	}
	//������ȱ���������רҵ����
	public void addProParameterToAreaTree(AreaTree m){
		boolean flag = false;
		 if(m.root==null){
	            System.out.println("empty tree");
	            return;
	      }
		 
		 for(int i = 0; i < ROW_SIZE ;i++ ){
			 if(professionInfo[i][0].equals("end")) return;
			 ArrayDeque<Node> queue=new ArrayDeque<Node>();
			 queue.add(m.root);
			 while(queue.isEmpty()==false){
				 Node node=queue.remove();
				 if(matchParameter(node,i)){
					 flag = true;
					 break;
				 }
				 if(node.children!=null){
					 Iterator children = node.children.iterator();
					 while(children.hasNext()){
						 Node child = (Node)children.next();
						 queue.add(child);
					 }
				 }
	        }
			 if(flag == false){
				 Node add = new Node(professionInfo[i][0]);
				 add.setProName(m.root.feature+professionInfo[i][0]);
				 add.setProParameter(professionInfo[i][1]);
				 m.insertNode(add,m.root.feature); 
				 System.out.println("Add a new node"+ professionInfo[i][0]);
			 }
			 flag = false;
		 }
	        System.out.print("\n");
	}
	
     //����д��������,m�����������������Ӳ����������
	//�°汾������Ҳֻ���������������
	public boolean matchParameter(Node m, int i){
		if(!isContains(i,m)){//�����Ϊ�ǲ��ǰ�m������ͬ�����
		    //professionInfo[i][0].replace(oldChar, m.feature);�Ѿ����滻
			//System.out.println("isContains return false");
			return false;
		}//��ô���Ʒ�ƣ��ͺ��������Ϣ��������
		//�����ڵ�����ӽڵ�Ĳ���Ͳ�����,����ᵼ�¡���������ӽڵ㡣������
		  //m.setProParameter(professionInfo[i][1]);
          //m.setProName(professionInfo[i][0]);
		//���ڵ�������ƥ���ϸ��ڵ㣬���������ӽڵ���
	    if(m.feature.length() < professionInfo[i][0].length()){
	    	//�����е��߼�����....
	    	//professionInfo[i][0].replaceAll(m.feature, "");
	    	System.out.println("���������Ϣ�Ȳ�����Ϣ�������٣��������ӽڵ�");
	    	System.out.println("���ڵ���"+m.feature);
	    	System.out.println("���ڵĲ�����"+professionInfo[i][0]);
	    	ListIterator j =m.children.listIterator();
			if( j == null) {
				System.out.println("û���ӽڵ���");
				Node add = new Node(professionInfo[i][0]);
				add.setProName(professionInfo[i][0]);//���ӽڵ����רҵ�����������
				add.setProParameter(professionInfo[i][1]);
				m.children.add(add);
				System.out.println("��"+m.feature+"�����Ӻ���"+add.feature);
				return true;//����ҵ��˸��ף�û���ҵ��ӣ�����͸��׼���һ��
			}
			else{
				System.out.println("���ӽڵ㣬��ʼ����");
				boolean childContains = false;
				for(; j.hasNext();){
					 Node current = (Node)j.next();
					 if(isContains(i,current)){
						 current.setProName(professionInfo[i][0]);
						 current.setProParameter(professionInfo[i][1]);
						 System.out.println(current.feature+"����������"+ professionInfo[i][0]);
						 childContains  = true;
						 return true;
					 }
			    }
				//֪��ƥ���������
				if(childContains == false){
					System.out.println("���ӽڵ㣬��û���������Ľڵ�");
					Node add = new Node(professionInfo[i][0]);
					add.setProName(professionInfo[i][0]);
					add.setProParameter(professionInfo[i][1]);
					m.children.add(add);
					System.out.println("��"+m.feature+"�����Ӻ���"+add.feature);
					return true;//����ҵ��˸��ף�û���ҵ��ӣ�����͸��׼���һ��
				}
				 //matchParameter(current,i);	 
			}
	    	
	    }else if(m.feature.length() == professionInfo[i][0].length()){
	    	m.setProParameter(professionInfo[i][1]);
	          m.setProName(professionInfo[i][0]);
	          System.out.println("�����������������ˣ��Ѿ�ƥ��");
	    }
	    return true;
	}
	private boolean isContains(String reviewFeature,Node m){
		ArrayList<String> similars = m.similarity;//��û����m�����ͬ����
		//�����Ȳ����ͬ���㣬ֱ�Ӳ�����������û�а����
		if(reviewFeature.contains(m.feature)||m.feature.contains(reviewFeature)){
			//professionInfo[i][0].replace(current, m.feature);
			System.out.println(reviewFeature +" include" +"Node "+m.feature);
			return true;
		}
		Iterator it = similars.iterator();
		while(it.hasNext()){
			String current = (String)it.next();
			if(reviewFeature.contains(current)||m.feature.contains(current)){
				//professionInfo[i][0].replace(current, m.feature);
				return true;
			}
		}
		return false;
	}
	
	//�жϽڵ�m�ǲ��ǰ���i���רҵ������Ϣ������
	private boolean isContains(int i,Node m){
		ArrayList<String> similars = m.similarity;//��û����m�����ͬ����
		//�����Ȳ����ͬ���㣬ֱ�Ӳ�����������û�а����
		if(professionInfo[i][0].contains(m.feature)){
			//professionInfo[i][0].replace(current, m.feature);
			System.out.println(professionInfo[i][0]+" include" +"Node "+m.feature);
			return true;
		}
		Iterator it = similars.iterator();
		while(it.hasNext()){
			String current = (String)it.next();
			if(professionInfo[i][0].contains(current)){
				professionInfo[i][0].replace(current, m.feature);
				return true;
			}
		}
		return false;
	}
	public String[][] getActualReviewParameter(String filename){
		String line;
		String str = null;
		int i;
		String[][] reviewResult = new String[100][5];
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename),"UTF-8"));
			for( i = 0; i < ROW_SIZE ;i++ ){
				if((line = br.readLine()) != null){
					//line = new String(line.getBytes(), "GBK"); 
					String[] args = line.split(" ");
					System.out.print(line);
					reviewResult[i][0] = args[0];
					//System.out.print(args[0]);
					reviewResult[i][1] = args[1];
					//System.out.print(args[1]);
					reviewResult[i][2] = args[2];	
					reviewResult[i][3] = args[3];
					reviewResult[i][4] = args[4];	
					//reviewResult[i][5] = args[5];
					System.out.println(reviewResult[i][0]+ " "+reviewResult[i][1]);
				}else break;
			}
			reviewResult[i][0] = "end";	
			reviewResult[i][1] = "end";
			br.close();
			
	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reviewResult;
	}
			
	public void getArgumentsList(String filename) {
		String line;
		String str = null;	
		int i;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename), "GBK"));
			for( i = 0; i < ROW_SIZE ;i++ ){
				if((line = br.readLine()) != null){
					String[] args = line.split("&&");
					professionInfo[i][0] = args[0];	
					professionInfo[i][1] = args[1];
					System.out.println(professionInfo[i][0] + "  "+professionInfo[i][1]);
				}else break;
			}
			professionInfo[i][0] = "end";	
			professionInfo[i][1] = "end";
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
