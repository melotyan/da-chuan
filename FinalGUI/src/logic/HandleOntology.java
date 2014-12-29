package logic;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class HandleOntology {
	static String inputFileName = "Phonebeforeutf-8.owl";
	static String phNS = "http://www.xfront.com/owl/ontology/phone/#";
	private OntModel m = null;
	public AreaTree basicTree = null;
	private OntClass root = null;
	private String area = "手机";// root string
	public static String filestart = "D:\\programs-about-dachuang\\11-22\\特别度计算库\\";

	/**
	 * @param args
	 */
	public OntClass getRoot() {
		return root;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HandleOntology hand = new HandleOntology();//���ر����ļ�
		//hand.delimateDuplicateNode(hand.getRoot());
		try {
			hand.save();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ��ת����������֮����Ҫ����ͬ��ʼ��ϣ����⣬����Ҫ����רҵ�������
		//������ͬ��ʼ��ϣ���Ϊ����רҵ������Ҫ�ο�ͬ��ʣ��Զ�ժҪ��Ч����
		//hand.addSimilarConcept(hand.basicTree.root);

		FinalSummarize sum = new FinalSummarize();
		sum.getArgumentsList(filestart + "ƻ��iPhone5s.txt");//���ز����ļ�
		sum.addProParameterToAreaTree(hand.basicTree);//���ز���������
		String[][] review = sum.getActualReviewParameter(filestart
				+ "result(3).txt");//�����û����չ�ע�������ļ�
		String summarize = sum.getSummarize(hand.basicTree, review);//���ժҪ
        System.out.print(summarize);//��ӡժҪ
		FileOutputStream fis;
		try {
			fis = new FileOutputStream(filestart + "Iphone5SSummarizeR.txt",
					true);
			OutputStreamWriter osw = new OutputStreamWriter(fis);
			osw.write(summarize);
			osw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public HandleOntology(String fileName, String phNS, String area) {
		this.inputFileName = fileName;
		this.phNS = phNS;
		this.area = area;
		loadOntology();
		basicTree = new AreaTree();
		basicTree.insertNode(new Node(area));
		transferToAreaTree(root);// Ŀǰû������
		basicTree.display();
	}

	// �������ת����
	private void transferToAreaTree(OntClass parent) {
		if (!parent.hasSubClass())
			return;
		for (ExtendedIterator j = parent.listSubClasses(); j.hasNext();) {
			OntClass c = (OntClass) j.next();
			basicTree.insertNode(new Node(c.getLocalName()),
					parent.getLocalName());// -
			transferToAreaTree(c);
		}
	}

	public void addSimilarConcept(Node m) {// ������....��ôʵ�ֹ����
		Synonym syn = new Synonym();
	    //����ʵ�ֺ�����һ������
		if(m.similarity == null){
			List<String> features = syn.getSimilarityWord(m.feature);
			m.similarity = (ArrayList<String>) features;
		}
		ListIterator j = m.children.listIterator();
		if (j == null)
			return;
		for (; j.hasNext();) {
			Node current = (Node) j.next();
			List<String> features = syn.getSimilarityWord(current.feature);
			current.similarity = (ArrayList<String>) features;
			addSimilarConcept(current);
		}
	}

	// �������ҵ�ͬ�����ڵ㣬�ڱ������н����޼������Ǳ��Ϊ�ȼ���?
	void delimateDuplicateNode(OntClass parent) {// ����Ҳ�����⣬����ֻ�����˸�ڵ�ĺ����ǲ����еȼ���
		// д��������������ҵ�ͬ��Ľڵ㣬�õ����ּ��ϣ��ñ����model����Щ�ڵ���ӵȼ���
		Synonym syn = new Synonym();
		if(parent == null) return;
		System.out.println("Parent is" + parent.getLocalName());
		ExtendedIterator j = parent.listSubClasses();
		if(j == null) return;
		for (;j.hasNext();) {
			OntClass c = (OntClass) j.next();
			System.out.print("Child is " + c.getLocalName() + " : ");
			List<String> features = syn.getSimilarityWord(c.getLocalName());
			Iterator iter = features.iterator();
			while (iter.hasNext()) {
				String temp = (String) iter.next();
				System.out.print(temp + " ");
				if (!temp.equals(c.getLocalName())) {
					try {
						OntClass o = m.getOntClass(phNS
								+ new String(temp.getBytes(), "utf-8"));
						if (o != null) {
							c.addEquivalentClass(o);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// �ǲ���Ӧ��ɾȥ����
				}
			}
			System.out.println();
			delimateDuplicateNode(c);
		}
	}

	public void save() throws FileNotFoundException {
		FileOutputStream fissd = new FileOutputStream(inputFileName
				+ "PhoneDlie11.owl", true);
		OutputStreamWriter oswsd = new OutputStreamWriter(fissd);
		m.write(oswsd);
		try {
			oswsd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void transferToOntology(Node rootNode) {
		// Node rootNode =basicTree.findNode(area);
		if (null == rootNode.children)
			return;
		Iterator iterchildren = rootNode.children.iterator();
		OntClass parent = m.getOntClass(phNS + rootNode.feature);
		while (iterchildren.hasNext()) {
			Node childreNode = (Node) iterchildren.next();
			OntClass child = m.createClass(phNS + childreNode.feature);
			parent.addSubClass(child);
			transferToOntology(childreNode);
		}
	}

	private void loadOntology() {
		OntDocumentManager mgr = new OntDocumentManager();
		OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
		s.setDocumentManager(mgr);
		m = ModelFactory.createOntologyModel(s, null);
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName
					+ " not found");
		}

		m.read(in, "");
		// OntProperty p = m.createOntProperty(phNS+"sentiment");
		try {// ���ﻹ�д�Ľ���û��ֱ�Ӳ��Ҹ�ڵ�ģ�û�еĻ����Դ�����һ������ʼ��
			root = m.getOntClass(phNS + new String(area.getBytes(), "utf-8"));
                        if(root == null) System.out.println("Root is null");
			// root.addProperty(p, "����");
			// root.addProperty(p, "����");
			// p.addDomain(root);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * for( ExtendedIterator j = phone.listSubClasses();j.hasNext();){
		 * OntClass c = (OntClass)j.next(); System.out.println(c.getLocalName()
		 * + "subclass of Phone"); }
		 */
	}

	public HandleOntology() {
		loadOntology();
		basicTree = new AreaTree();
		basicTree.insertNode(new Node(area));
		transferToAreaTree(root);
		basicTree.display();
	}

	// ������ϼӽڵ�
	public void addSentiment() {

	}
}
