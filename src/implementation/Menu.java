/**
 * 
 */
package implementation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * @author Schmidt Gaëtan
 *
 */
public class Menu {

	/**
	 * @param args
	 * @author Schmidt Gaëtan
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0)
		{
			String[] annotatorList = new  String[10];
			for (int i=0;i<10;i++)
			{
				annotatorList[i] = "";
			}
			Scanner sc = new Scanner(System.in);
			String str;
			
			
			System.out.println("Annotateurs disponible: ");
			System.out.println("- tokenize");
			System.out.println("- ssplit");
			System.out.println("- pos");
			System.out.println("- lemma");
			System.out.println("- ner");
			System.out.println("- parse");
			System.out.println("- deparse");
			System.out.println("- coref");
			System.out.println("- relation");
			System.out.println("- quote");
			System.out.println("Saisir les annotateurs :");
			str  = sc.nextLine();
			
			if (!"***".equals(str))
			{
			
				List<String> list = new ArrayList<String>(Arrays.asList(str.split(",")));
				
				if (list.contains("tokenize"))
					annotatorList[0]= "tokenize";
				
				if (list.contains("ssplit"))
					{
						annotatorList[0]= "tokenize";
						annotatorList[1]= "ssplit";
					}
				
				if (list.contains("pos"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[2]= "pos";
				}
				
				if (list.contains("lemma"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[2]= "pos";
					annotatorList[3]= "lemma";
				}
				
				if (list.contains("ner"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[2]= "pos";
					annotatorList[3]= "lemma";
					annotatorList[4]= "ner";
				}
				
				if (list.contains("parse"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[5]= "parse";
				}
				
				if (list.contains("deparse"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[2]= "pos";
					annotatorList[6]= "depparse";
				}
				
				if (list.contains("coref"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[2]= "pos";
					annotatorList[3]= "lemma";
					annotatorList[4]= "ner";
					annotatorList[5]= "parse";
					annotatorList[6]= "depparse";
					annotatorList[7]= "coref";
				}
				
				if (list.contains("relation"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[2]= "pos";
					annotatorList[3]= "lemma";
					annotatorList[4]= "ner";
					annotatorList[5]= "parse";
					annotatorList[6]= "depparse";
					annotatorList[8]= "relation";
				}
				
				if (list.contains("quote"))
				{
					annotatorList[0]= "tokenize";
					annotatorList[1]= "ssplit";
					annotatorList[2]= "pos";
					annotatorList[3]= "lemma";
					annotatorList[4]= "ner";
					annotatorList[6]= "depparse";
					annotatorList[7]= "coref";
					annotatorList[9]= "quote";
				}
				
				System.out.println("saisir chemin du fichier à traiter:");
				String path = sc.nextLine();
				String prop="";
				for (int i=0; i<10; i++)
				{
					if (!annotatorList[i].equals(""))
						prop+=annotatorList[i]+",";
				}
				prop = prop.substring(0, prop.length()-1);
				
				System.out.println("les annotateurs séléctioné sont: "+prop);
				
				Properties props = new Properties();
				props.setProperty("annotators",prop);
				StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				FileInputStream is = new FileInputStream(path);     
				String content = IOUtils.toString(is, "UTF-8");
				CoreDocument document = new CoreDocument(content);
				pipeline.annotate(document);
				 // OUTPUT
			    PrintWriter out = new PrintWriter("output.txt");
				///PrintWriter xmlOut = new PrintWriter("outpuc.xml");
				 // create annotation object for output
			    Annotation annotation = new Annotation(content);
			
			    // annotate the annotation
			    pipeline.annotate(annotation);
			    
			    // print result on a file
			    pipeline.prettyPrint(annotation, out );
			   // pipeline.prettyPrint(annotation, xmlOut);
				sc.close();
				
				Graph graph = new Graph();
				graph.setName("graphTestSEQ2");
				Windowing w = new Windowing(document,graph,true,"SENTENCE","SEQUENTIAL",10);
				w.MainWork();
				//w.testCorefByToken();
				Mention m = new Mention(document,graph,true,true);
				m.MainWork();
				
				graph.graphMLPrinter();
				
				//w.test2();
			}else // option de génération automatique
			{
				System.out.println("Start Auto gen . . .");
				
				Properties props = new Properties();
				props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref,quote");
				StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				
				ArrayList<String> fileList = new ArrayList<String>();
				fileList.add("corpus/bnw_page1.txt");
				fileList.add("corpus/bnw_page112.txt");
				fileList.add("corpus/Coraline.txt");
				//fileList.add("corpus/Coraline2.txt");
				fileList.add("corpus/dadoes_page18.txt");
				fileList.add("corpus/dadoes_page213.txt");
				fileList.add("corpus/dadoes_page82.txt");
				fileList.add("corpus/Hp.txt");
				fileList.add("corpus/Hp2.txt");
				fileList.add("corpus/hyperion_page203.txt");
				fileList.add("corpus/hyperion_page378.txt");
				fileList.add("corpus/hyperion_page9.txt");
				fileList.add("corpus/ial_page1.txt");
				fileList.add("corpus/ial_page56.txt");
				fileList.add("corpus/ial_page96.txt");
				fileList.add("corpus/Oz.txt");
				fileList.add("corpus/Oz2.txt");
				
				for (String path : fileList)
				{
					System.out.println("FILE : "+path);
					FileInputStream is = new FileInputStream(path);     
					String content = IOUtils.toString(is, "UTF-8");
					CoreDocument document = new CoreDocument(content);
					pipeline.annotate(document);
					
					// OUTPUT
				    PrintWriter out = new PrintWriter("resultats/"+path.substring(7)+"_output.txt");
				    Annotation annotation = new Annotation(content);
				    // annotate the annotation
				    pipeline.annotate(annotation);
				 // print result on a file
				    pipeline.prettyPrint(annotation, out );
				    Graph graph = new Graph();
					graph.setName("graph_sliding_1s_"+path.substring(7));
					Windowing w = new Windowing(document,graph,true,"SENTENCE","SLIDING",1);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_sliding_2s_"+path.substring(7));
					w = new Windowing(document,graph,true,"SENTENCE","SLIDING",2);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_sequential_1s_"+path.substring(7));
					w = new Windowing(document,graph,true,"SENTENCE","SEQUENTIAL",1);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_sequential_2s_"+path.substring(7));
					w = new Windowing(document,graph,true,"SENTENCE","SEQUENTIAL",2);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_sliding_1s_"+path.substring(7));
					w = new Windowing(document,graph,true,"WORD","SLIDING",10);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_sliding_2s_"+path.substring(7));
					w = new Windowing(document,graph,true,"WORD","SLIDING",50);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_sequential_1s_"+path.substring(7));
					w = new Windowing(document,graph,true,"WORD","SEQUENTIAL",10);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_sequential_2s_"+path.substring(7));
					w = new Windowing(document,graph,true,"WORD","SEQUENTIAL",50);
					w.MainWork();
					graph.graphMLPrinter("resultats");
					
					graph = new Graph();
					graph.setName("graph_mention_"+path.substring(7));
					Mention m = new Mention(document,graph,true,true);
					m.MainWork();
					graph.graphMLPrinter("resultats");
				}
				
			}
			
			
		}

	}

}
