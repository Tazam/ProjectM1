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
			PrintWriter xmlOut = new PrintWriter("outpuc.xml");
			 // create annotation object for output
		    Annotation annotation = new Annotation(content);
		
		    // annotate the annotation
		    pipeline.annotate(annotation);
		    
		    // print result on a file
		    pipeline.prettyPrint(annotation, out );
		    pipeline.prettyPrint(annotation, xmlOut);
			sc.close();
			
			Graph graph = new Graph();
			graph.setName("graphTest1");
			Windowing w = new Windowing(document,graph,true,"sentence","sliding",1);
			w.MainWork();
			//w.testCorefByToken();
			graph.graphMLPrinter();
			
			
		}

	}

}
