package logic;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class TestMain {
	static final String inputFileName = "D:/programs-about-dachuang/11-22/�ر�ȼ����/phone.owl";
	private static final int MAX_SIZE = 500;
	static String phNS = "http://www.xfront.com/owl/ontology/phone/#";
	public static void main(String[] args) throws IOException{
		//String phoneURI = "http://Phone/";
		//String relationshipURI = "http://pur1.org/vocab/relationship/";
		String filedir = "D:/programs-about-dachuang/11-22/�ر�ȼ����/";
		
		//Model model = ModelFactory.createDefaultModel();
		
		
		//Resource adam = model.createResource(phoneURI+"adam");
		
		//Resource beth =  model.createResource(phoneURI+"beth");
		//Property childOf = model.createProperty(relationshipURI+"childOf");
		//adam.addProperty(childOf, beth);
		//Statement statement = model.createStatement(adam, childOf, "sss");
		//model.add(statement);
		ArrayList<String> testfile1 = null;
		
		try {
			testfile1 = readFile(filedir +"SpecialDegreeResult.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> testfile2 = null;
		try {
			testfile2 = readFile(filedir +"afterStandarlization2.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Words> specialPerWord = new ArrayList<Words>();
		for(String sentence : testfile1){//û�в����Ƿ�����ļ�β
			
			String[] terms = sentence.split(":");  
			specialPerWord.add(new Words(terms[0],Float.valueOf(terms[1])));
			System.out.println(terms[0] + ":" + terms[1]);
			
		}
		Words[][] templateWord = new Words[MAX_SIZE][2];//��ʼ��
		int i = 0;
		for(String sentence : testfile2){//û�в����Ƿ�����ļ�β
			
			String[] terms = sentence.split(" ");  
			templateWord[i][0] = new Words(terms[0]);
			templateWord[i][1] = new Words(terms[1]);//��һ���ر�ȼ��㲢����
			System.out.println(terms[0] + " " + terms[1]);
			i++;
		}
		Collections.sort(specialPerWord);
		for (Words o : specialPerWord) {
            System.out.println(o.getWordSentence() + "-->" + o.getWordSpecialCore());
        }

		
		OntDocumentManager mgr = new OntDocumentManager();
		OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
		s.setDocumentManager(mgr);
		OntModel m = ModelFactory.createOntologyModel(s, null);
		createOntology(m,specialPerWord); 
		//createOntologyByTemplate(m,templateWord);
		m.write(System.out);
		FileOutputStream fissd = new FileOutputStream(filedir + "newPhone.owl", true);
		OutputStreamWriter oswsd = new OutputStreamWriter(fissd);
		m.write(oswsd);
		oswsd.close();
		fissd.close();
		/*OntDocumentManager mgr = new OntDocumentManager();
		OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
		s.setDocumentManager(mgr);
	   OntModel m1 = ModelFactory.createOntologyModel(s,null);
	    InputStream in = FileManager.get().open(inputFileName);
	    if(in == null){
	    	throw new IllegalArgumentException("File: "+inputFileName+" not found");
	    }
	    
	    m1.read(in,"");
	    OntClass phone = m1.getOntClass(phNS+ new String("�ֻ�".getBytes(),"GBK"));
	    for( ExtendedIterator j = phone.listSubClasses();j.hasNext();){
	    	OntClass c = (OntClass)j.next();
	    	System.out.println(c.getLocalName() + "subclass of Phone");
	    }
	    DatatypeProperty p1 = m1.createDatatypeProperty(phNS+"sentiment");
	    OntProperty p = m1.createOntProperty(phNS+"sentiment");
		p.addDomain(phone);
		p1.addDomain(phone);
	    phone.addProperty(p, "����");
		phone.addProperty(p, "����");
		phone.getPropertyValue(p);
		
		//phone.addProperty(arg0, arg1, arg2)
		//RDFNode 
		//phone.get
	    String info = null;
	    for(Iterator prys=phone.listDeclaredProperties();prys.hasNext();){
	        OntProperty pry=(OntProperty)prys.next();
	        //��ȡ�������
	        String pryname=pry.toString();   //����uri
	        int index=pryname.indexOf("#");
	        String str=pryname.substring(index+1);  //��ȡ�������
	        //�жϸ������Ƕ������Ի�����ֵ��ֵ���ԣ�rang��ȡ�������Ե��������
	        //��rang�п��Կ�����Щ�Ƕ���������Щ��������ԡ�
	        //�������Ե�����ռ����Զ���ģ�ֵ���Ե�����ռ���http://www.w3.org/2001/XMLSchema#
	           String rang =pry.getRDFType().toString();
	           index=rang.indexOf("#");
	           String nstype=rang.substring(index+1);
	           if("ObjectProperty".equals(nstype)){//��������
	                  //��ȡ���ʵ�������ֵ
	        
	                  info+="��������:"+str+": ";
	                  
	           //index=pryvalue.indexOf("#");
	            //pryvalue=pryvalue.substring(index+1);  //��ȡ��������ֵ
	            //info+="ֵΪ��"+pryvalue+"\n\n";
	           }
	           else if("DatatypeProperty".equals(nstype)){
	               info+="�������:"+str+": ";
	               /*try {
	                        String dataValue=instance.getPropertyValue(pry).toString();
	                        if(-1!=dataValue.indexOf("^^")){
	                            info+="ֵΪ��"+dataValue.substring(0,dataValue.indexOf("^^"))+"\n\n";//ȥ���������
	                        }
	                        else if(-1!=dataValue.indexOf("@")){//��������д���Ǻܺã��п����ַ��б����Ͱ�����ַ�
	                            info+="ֵΪ��"+dataValue.substring(0,dataValue.indexOf("@"))+"\n\n";//ȥ������
	                        }
	                        else{
	                            info+="ֵΪ��"+dataValue+"\n\n";
	                        }
	                    } catch (Exception e) {
	                        info+="ֵΪ����"+"\n\n";
	                 
	                    }*/
	           //}
	       //}
	}
	public static void createOntologyByTemplate(OntModel m,Words[][] te) throws UnsupportedEncodingException{
		//�����teӦ�����Ѿ������ر���ˣ�����ر�����ж�˭���ϴΣ�������Ҫ����ı��еĹؼ��ֱַ����ϵ
		ObjectProperty proper = m.getObjectProperty(phNS+"HasA");
		OntClass phone = null;
		try {
			 phone = m.getOntClass(phNS+ new String("�ֻ�".getBytes(),"GBK"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i= 0; i< MAX_SIZE;i++){
			OntClass parentClass = null;
			OntClass  childClass = null;
			if(te[i][0]==null) break;
			System.out.print(te[i][0].getWordSentence()+" "+ te[i][1].getWordSentence());
			if(hasOWLNode(m,te[i][0].getWordSentence())&&!hasOWLNode(m,te[i][1].getWordSentence())){
				//�и����ӣ�ֱ�Ӽ򵥼���������ڵ�֮��
				parentClass = m.getOntClass(phNS+ new String(te[i][0].getWordSentence().getBytes(),"GBK"));
				childClass = m.createClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				parentClass.addSubClass(childClass);
			}
			else if(!hasOWLNode(m,te[i][0].getWordSentence())&&hasOWLNode(m,te[i][1].getWordSentence())){
				//�����޸������ӽڵ��������ϲ���
				try {
					parentClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
					 childClass = m.getOntClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				Words mayParent = new Words(new String(te[i][0].getWordSentence()));
				Iterator iter = childClass.listSuperClasses();
				OntClass temp1 = null,temp2;
				Words parent1 = null,parent2;
				if(iter.hasNext()){
					temp1 = (OntClass)iter.next();
					parent1 = new Words(temp1.getLocalName());
					if(mayParent.getWordSpecialCore()< parent1.getWordSpecialCore()&&
							mayParent.getWordSpecialCore()>te[i][1].getWordSpecialCore()){
						temp1.addSubClass(parentClass);
						childClass.setSuperClass(parentClass);
					}
				}
				for(;iter.hasNext();){
					temp2 = (OntClass) iter.next();
					parent2 = new Words(temp2.getLocalName());//�õ��������֣���֪���᲻�����
					//��Ҫһ������������ǰ�Ƿ���ܵĸ��ڵ����������ڵ�֮��
					//�����Ǵ���ӽ�ĸ��ڵ�������
					
					   if(mayParent.getWordSpecialCore()<parent2.getWordSpecialCore()&&
								mayParent.getWordSpecialCore()>parent1.getWordSpecialCore()){
						   //parentClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
							temp2.addSubClass(parentClass);
							//parent.setSuperClass(parentClass);//���ﲻһ����֤�߼���ϵ��ȷ
							temp1.setSuperClass(parentClass);
							break;
					   }
					 temp1 = temp2;		
				}
				//����϶�����׷�ݵ���ڵ���
			}
			else if(hasOWLNode(m,te[i][0].getWordSentence())&&hasOWLNode(m,te[i][1].getWordSentence())){
				//���߶��У�����Ƿ����߼�
				try {
					parentClass = m.getOntClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
					 childClass = m.getOntClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		//�����ǲ���Ҫ�жϲ���Ϊ�յ���
				if(!parentClass.hasSubClass(childClass)){//����Ŀǰ������ܲ����ж�ү��
					//������߼�
					childClass.setSuperClass(parentClass);//����Ͳ����ǲ���ֱ�ӻ��Ǽ������
					System.out.println("have");
				}
			}
			else{//��û��
				try {
					parentClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
					 childClass = m.createClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		//�����ǲ���Ҫ�жϲ���Ϊ�յ���
				parentClass.addSubClass(childClass);
				if(phone!=null){
					phone.addSubClass(parentClass);
				}
			}
			/*if(te[i][0].compareTo(te[i][1])>0){				
			try {
					parentClass = m.createClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
					 childClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		//�����ǲ���Ҫ�жϲ���Ϊ�յ���
								
			}else{
				try {
					parentClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
					 childClass = m.createClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}*/
			
			
			//������Ƿ��б�Ҫ�����е���ű�����������������
			//����е�ǰ�ĸ��ڵ㣬û���ӽڵ㣬ֱ������ӽڵ�
			//������ӽڵ㣬û�и��ڵ㣬���ӽڵ�ĸ��ڵ��Ϊ��ǰ�ڵ㣬���ڵ�ĸ��ڵ��Ǳ�Ϊ�ֻ���ӽڵ㻹���ӵĸ��ڵ�
			//������߶��У�����˳��˳��ԣ������Ķ�
			//������߶�û�У���ֱ�����ڵ����
			
			
			proper.addRange(childClass);//����ֱ�ӷ������棬�������Ƿ��ظ��ˣ�
			proper.addDomain(parentClass);
			System.out.println();
			
		}
	}
	
	public static boolean hasOWLNode(OntModel m,String s){
		try {
			OntClass temp = m.getOntClass(phNS+ new String(s.getBytes(),"GBK"));
			if(temp != null) return true;
			else return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public static void createOntology(OntModel m,List<Words> l) throws FileNotFoundException{
		List<Words> hasPut = new ArrayList<Words>();
		OntClass Phone = null;
		try {
			Phone = m.createClass(phNS+ new String("�ֻ�".getBytes(),"GBK"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//OntClass Body = m.createClass(phNS+"����");
		hasPut.add(new Words("�ֻ�",(float) 20));
		//Phone.addSubClass(Body);
		//ObjectProperty body = m.createObjectProperty(phNS+"�л���");
		//body.addDomain(Phone);
		//body.addRange(Body);
		ObjectProperty proper = m.createObjectProperty(phNS+"HasA");
		proper.addDomain(Phone);
		
		Iterator li = l.iterator();
		int scope = 3;
		while(li.hasNext()){
			Words currentNode = (Words)li.next();
			//if(currentNode.getWordSentence().equals("��Ļ")) currentNode.setWordSpecialCore((float)4.21);
			ArrayList<WordsSimilarity> re = new ArrayList<WordsSimilarity>();
			Iterator has = hasPut.iterator();
			while(has.hasNext()){
				RequestSimilarity reSimilar = new RequestSimilarity();
				
				Words now = (Words)has.next();
				if(now.getWordSentence().equals(currentNode.getWordSentence())) break;
                                String result = reSimilar.request(currentNode.getWordSentence(), now.getWordSentence());
                                if(!result.equals("NotConsistThisString'sAssociationValue")){
                                    re.add(new WordsSimilarity(currentNode.getWordSentence(), now.getWordSentence(),Double.valueOf(result)));
                                    System.out.println(currentNode.getWordSentence()+"is related to "+now.getWordSentence()+"at"+result);
                                }
			}//re�ǵ�ǰ�ڵ��������Ѿ�������
			Collections.sort(re);//re�ǶԵ�ǰ���ʺ����еĵ��ʽ��е�һ�������ȵ�����hasPut���Ѿ������еĽ��
			Words parent = findParent(re,hasPut,currentNode);
			if(parent!=null){
				OntClass parentClass;
				try {
					parentClass = m.getOntClass(phNS+ new String(parent.getWordSentence().getBytes(), "GBK"));
					OntClass temporary = m.createClass(phNS+new String(currentNode.getWordSentence().getBytes(),"GBK"));
					hasPut.add(currentNode);
					parentClass.addSubClass(temporary);
					proper.addRange(temporary);
					proper.addDomain(parentClass);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else {
				System.out.println(currentNode.getWordSentence()+" has no parent.");
			}
		}
		
	}
	
	private static Words findParent(ArrayList<WordsSimilarity> re,
			List<Words> hasPut, Words currentNode) {
		// TODO Auto-generated method stub
		int scope = 3;
		if(re.size()<3) scope = re.size();
		for(int i = 0;i<scope;i++){
			
			WordsSimilarity simi = re.get(i);
			String maybe = simi.getS2();
			Float special = findSpecialDegree(hasPut,maybe);
			if(special>currentNode.getWordSpecialCore()){
				return new Words(maybe,special);
			}
		}
		return null;//��Ҫ����null�����
		
	}
	private static Float findSpecialDegree(List<Words> hasPut, String maybe) {
		// TODO Auto-generated method stub
		Iterator li = hasPut.iterator();
		while(li.hasNext()){
			Words current = (Words)li.next();
			if(current.getWordSentence().equals(maybe)){
				return current.getWordSpecialCore();
			}
		}
		return (float) 0.0;
	}
	public static ArrayList<String> readFile(String file) throws FileNotFoundException, IOException{
		
		ArrayList<String> sentence = new ArrayList<String>();
		InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "utf-8"); //byte streams to character streams
		BufferedReader br = new BufferedReader(inStrR); 
		String line = br.readLine();
		while(line != null && !line.equals("")){
			sentence.add(line);
			line = br.readLine();    
		}
		inStrR.close();
		br.close();
		return sentence;
	}
}
