package logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.*; 

class Node {
	public String feature;
	public Node father;
	public ArrayList<Node> children;
	public ArrayList<String> sentiment;
	public ArrayList<String> similarity;
	public String proParameter;
	public float frequency;
	public String realSentiment;
	public float polarity;
	public String proName;
	
	public Node(String feature){
		this.feature = feature;
		father = null;
		children = new ArrayList<Node>();
		sentiment = new ArrayList<String>();
		similarity = new ArrayList<String>();
		frequency = (float)0.0;
		polarity = (float)0.0;
	}
	
	public String toString(){
		return "this Node is "+ feature + "|" + realSentiment + " polarity is " + String.valueOf(polarity);
	}
	
	public void setProParameter(String parameter){
		this.proParameter = new String(parameter);
	}
	
	public String getFeature(){
		return this.feature;
	}
	
	public void setProName(String name){
		this.proName = new String(name);
	}
}

public class AreaTree {
	public Node root;
	
	public static void main(String[] args) {
		AreaTree tree = new AreaTree();
		tree.insertNode(new Node("�ֻ�"));
		tree.insertNode(new Node("��Ļ"), "�ֻ�");//-
		tree.insertNode(new Node("Ӳ��"), "�ֻ�");
		tree.insertNode(new Node("���"), "�ֻ�");
		tree.insertNode(new Node("�ʸ�"), "��Ļ");//-
		tree.insertNode(new Node("���"), "Ӳ��");
		tree.insertNode(new Node("����"), "���");
		tree.insertNode(new Node("����"), "��Ļ");//-
		tree.display();
		tree.deleteNode("��Ļ");
		try {
			tree.reset("Ӳ��", "realSentiment", "����");
			tree.reset("Ӳ��", "polarity", (float)0.89);
			tree.display();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	//reset attribution
	public boolean reset(String feature, String attribution, Object o)
			throws SecurityException, ClassNotFoundException, 
			IllegalArgumentException, IllegalAccessException{
		Node current = findNode(feature);
		if(current == null){
			System.out.println("reset error: no find node " + feature);
			return false;
		}
		else{
			try {
				Field[] fds = Class.forName("logic.Node").getDeclaredFields();
				if(attribution.equals("children")){
					((ArrayList<Node>)fds[2].get(current)).add((Node)o);
					return true;
				}else if(attribution.equals("realSentiment")){
					fds[6].set(current, o);
					return true;
				}else if(attribution.equals("similarity")){
					((ArrayList<String>)fds[4].get(current)).add((String)o);
					return true;
				}else if(attribution.equals("frequency")){
					fds[5].setFloat(current, (Float)o);
					return true;
				}else if(attribution.equals("polarity")){
					fds[7].setFloat(current, (Float)o);
					return true;
				}
			} catch (Throwable e) { 
				System.err.println(e); 
			}
		}
		return false;
	}
	//show tree
	public void display(){
		System.out.println("Tree:");
		display(root);
	}
	private void display(Node node){
		Node current = node;
		if(current == null) return;
		System.out.println(current.toString());
		for (Node child : current.children) {
			display(child);
		}
	}
	//find from root//���ܻ������
	public Node findNode(String feature){
		Node current = findNode(root, feature);
		return current;
	}
	//find by find
	private Node findNode(Node node, String feature){
		Node current = node;
		//дһ������ǰ���ĸ�����������������������
		//����ܿ���ֻ����һ������ģ��������ж�������
		if(current == null || node.feature.equals(feature)) 
			return current;
		for (Node child : current.children) {
			current = findNode(child, feature);
			if(current != null)
				return current;
		}
		return null;
	}
	
	private boolean isEquals(String reviewFeature,Node m){
		ArrayList<String> similars = m.similarity;//��û����m�����ͬ����
		//�����Ȳ����ͬ���㣬ֱ�Ӳ�����������û�а����
		if(reviewFeature.equals(m.feature)||m.feature.equals(reviewFeature)){
			//professionInfo[i][0].replace(current, m.feature);
			System.out.println(reviewFeature +" equals" +"Node "+m.feature);
			return true;
		}
		Iterator it = similars.iterator();
		while(it.hasNext()){
			String current = (String)it.next();
			if(reviewFeature.equals(current)||m.feature.equals(current)){
				//professionInfo[i][0].replace(current, m.feature);
				return true;
			}
		}
		return false;
	}
	//insert root
	public void insertNode(Node node){
		if(root == null)
			root = node;
		else
			System.out.println("insert error: already exist root");
	}
	//insert children
	public void insertNode(Node node, String father){
		Node current = findNode(father);
		if(current == null)
			System.out.println("insert error: no find father node " + father);
		else{
			current.children.add(node);
			node.father = current;
		}
	}
	//delete 
	public boolean deleteNode(String feature){
		Node current = findNode(feature);
		if(current == null){
			System.out.println("delete error: no find node " + feature);
			return false;
		}
		else if(feature == root.feature)
			root = null;
		else{
			current.father.children.remove(current);
			current = null;
		}
		return true;
	}
}