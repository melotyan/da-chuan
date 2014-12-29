package test;





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
	static final String inputFileName = "D:/programs-about-dachuang/11-22/特别度计算库/phone.owl";
	private static final int MAX_SIZE = 500;
	static String phNS = "http://www.xfront.com/owl/ontology/phone/#";
	public static void main(String[] args) throws IOException{
		//String phoneURI = "http://Phone/";
		//String relationshipURI = "http://pur1.org/vocab/relationship/";
		String filedir = "D:/programs-about-dachuang/11-22/特别度计算库/";
		
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
		for(String sentence : testfile1){//没有测试是否读到文件尾
			
			String[] terms = sentence.split(":");  
			specialPerWord.add(new Words(terms[0],Float.valueOf(terms[1])));
			System.out.println(terms[0] + ":" + terms[1]);
			
		}
		Words[][] templateWord = new Words[MAX_SIZE][2];//初始化
		int i = 0;
		for(String sentence : testfile2){//没有测试是否读到文件尾
			
			String[] terms = sentence.split(" ");  
			templateWord[i][0] = new Words(terms[0]);
			templateWord[i][1] = new Words(terms[1]);//做一个特别度计算并设置
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
	    OntClass phone = m1.getOntClass(phNS+ new String("手机".getBytes(),"GBK"));
	    for( ExtendedIterator j = phone.listSubClasses();j.hasNext();){
	    	OntClass c = (OntClass)j.next();
	    	System.out.println(c.getLocalName() + "subclass of Phone");
	    }
	    DatatypeProperty p1 = m1.createDatatypeProperty(phNS+"sentiment");
	    OntProperty p = m1.createOntProperty(phNS+"sentiment");
		p.addDomain(phone);
		p1.addDomain(phone);
	    phone.addProperty(p, "流畅");
		phone.addProperty(p, "便宜");
		phone.getPropertyValue(p);
		
		//phone.addProperty(arg0, arg1, arg2)
		//RDFNode 
		//phone.get
	    String info = null;
	    for(Iterator prys=phone.listDeclaredProperties();prys.hasNext();){
	        OntProperty pry=(OntProperty)prys.next();
	        //获取属性名称
	        String pryname=pry.toString();   //属性uri
	        int index=pryname.indexOf("#");
	        String str=pryname.substring(index+1);  //获取属性名称
	        //判断该属性是对象属性还是数值数值属性，rang获取的是属性的数据类型
	        //从rang中可以看出那些是对象属性那些是数据属性。
	        //对象属性的命名空间是自定义的，值属性的命名空间是http://www.w3.org/2001/XMLSchema#
	           String rang =pry.getRDFType().toString();
	           index=rang.indexOf("#");
	           String nstype=rang.substring(index+1);
	           if("ObjectProperty".equals(nstype)){//对象属性
	                  //获取这个实例的属性值
	        
	                  info+="对象属性:"+str+": ";
	                  
	           //index=pryvalue.indexOf("#");
	            //pryvalue=pryvalue.substring(index+1);  //获取对象属性值
	            //info+="值为："+pryvalue+"\n\n";
	           }
	           else if("DatatypeProperty".equals(nstype)){
	               info+="数据属性:"+str+": ";
	               /*try {
	                        String dataValue=instance.getPropertyValue(pry).toString();
	                        if(-1!=dataValue.indexOf("^^")){
	                            info+="值为："+dataValue.substring(0,dataValue.indexOf("^^"))+"\n\n";//去除数据类型
	                        }
	                        else if(-1!=dataValue.indexOf("@")){//可能这样写不是很好，有可能字符串中本来就包含这个字符
	                            info+="值为："+dataValue.substring(0,dataValue.indexOf("@"))+"\n\n";//去除语言
	                        }
	                        else{
	                            info+="值为："+dataValue+"\n\n";
	                        }
	                    } catch (Exception e) {
	                        info+="值为：空"+"\n\n";
	                 
	                    }*/
	           //}
	       //}
	}
	public static void createOntologyByTemplate(OntModel m,Words[][] te) throws UnsupportedEncodingException{
		//这里的te应该是已经有了特别度了，根据特别度来判断谁在上次，而且需要根据文本中的关键字分别出关系
		ObjectProperty proper = m.getObjectProperty(phNS+"HasA");
		OntClass phone = null;
		try {
			 phone = m.getOntClass(phNS+ new String("手机".getBytes(),"GBK"));
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
				//有父无子，直接简单加在这个父节点之下
				parentClass = m.getOntClass(phNS+ new String(te[i][0].getWordSentence().getBytes(),"GBK"));
				childClass = m.createClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				parentClass.addSubClass(childClass);
			}
			else if(!hasOWLNode(m,te[i][0].getWordSentence())&&hasOWLNode(m,te[i][1].getWordSentence())){
				//有子无父，从子节点依次向上查找
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
					parent2 = new Words(temp2.getLocalName());//得到本地名字，不知道会不会出错
					//需要一个函数。来看当前是否可能的父节点在两个父节点之间
					//假设是从最接近的父节点往上走
					
					   if(mayParent.getWordSpecialCore()<parent2.getWordSpecialCore()&&
								mayParent.getWordSpecialCore()>parent1.getWordSpecialCore()){
						   //parentClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
							temp2.addSubClass(parentClass);
							//parent.setSuperClass(parentClass);//这里不一定保证逻辑关系正确
							temp1.setSuperClass(parentClass);
							break;
					   }
					 temp1 = temp2;		
				}
				//这里肯定是能追溯到根节点吗？
			}
			else if(hasOWLNode(m,te[i][0].getWordSentence())&&hasOWLNode(m,te[i][1].getWordSentence())){
				//两者都有，检查是否符合逻辑
				try {
					parentClass = m.getOntClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
					 childClass = m.getOntClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		//这里是不是要判断不能为空的类
				if(!parentClass.hasSubClass(childClass)){//这里目前不清楚能不能判断爷孙
					//如果不符合逻辑
					childClass.setSuperClass(parentClass);//这里就不管是不是直接还是间接啦？
					System.out.println("have");
				}
			}
			else{//都没有
				try {
					parentClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
					 childClass = m.createClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		//这里是不是要判断不能为空的类
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
				}		//这里是不是要判断不能为空的类
								
			}else{
				try {
					parentClass = m.createClass(phNS+ new String(te[i][0].getWordSentence().getBytes(), "GBK"));
					 childClass = m.createClass(phNS+ new String(te[i][1].getWordSentence().getBytes(), "GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}*/
			
			
			//在这里，是否有必要对已有的这颗本体树进行搜索遍历
			//如果有当前的父节点，没有子节点，直接添加子节点
			//如果有子节点，没有父节点，让子节点的父节点变为当前节点，父节点的父节点是变为手机的子节点还是子的父节点
			//如果两者都有，则检查顺序，顺序对，不做改动
			//如果两者都没有，则直接向根节点添加
			
			
			proper.addRange(childClass);//？？直接放在外面，不检验是否重复了？
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
	public static void createOntology(OntModel m,List<Words> l){
		List<Words> hasPut = new ArrayList<Words>();
		OntClass Phone = null;
		try {
			Phone = m.createClass(phNS+ new String("手机".getBytes(),"GBK"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//OntClass Body = m.createClass(phNS+"机身");
		hasPut.add(new Words("手机",(float) 20));
		//Phone.addSubClass(Body);
		//ObjectProperty body = m.createObjectProperty(phNS+"有机身");
		//body.addDomain(Phone);
		//body.addRange(Body);
		ObjectProperty proper = m.createObjectProperty(phNS+"HasA");
		proper.addDomain(Phone);
		
		Iterator li = l.iterator();
		int scope = 3;
		while(li.hasNext()){
			Words currentNode = (Words)li.next();
			//if(currentNode.getWordSentence().equals("屏幕")) currentNode.setWordSpecialCore((float)4.21);
			ArrayList<WordsSimilarity> re = new ArrayList<WordsSimilarity>();
			Iterator has = hasPut.iterator();
			while(has.hasNext()){
				RequestSimilarity reSimilar = new RequestSimilarity();
				
				Words now = (Words)has.next();
				if(now.getWordSentence().equals(currentNode.getWordSentence())) break;
				try {
					String result = reSimilar.request(currentNode.getWordSentence(), now.getWordSentence());
					if(!result.equals("NotConsistThisString'sAssociationValue")){
						re.add(new WordsSimilarity(currentNode.getWordSentence(), now.getWordSentence(),Double.valueOf(result)));
						System.out.println(currentNode.getWordSentence()+"is related to "+now.getWordSentence()+"at"+result);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//re是当前节点与其他已经在书中
			Collections.sort(re);//re是对当前单词和树中的单词进行的一个关联度的排序，hasPut是已经在树中的结点
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
		return null;//需要处理null的情况
		
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
