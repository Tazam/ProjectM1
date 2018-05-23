/**
 * 
 */
package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import implementation.Graph;
import implementation.Mention;
import implementation.Windowing;

/**
 * @author Schmidt Gaëtan
 *
 */
public class test_impl {
	
	
	
	public static void main() throws IOException
	{
		System.out.println("Start Test imp . . .");
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref,quote");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		ArrayList<String> fileList = new ArrayList<String>();
		fileList.add("corpus/bnw_page1.txt");
		fileList.add("corpus/bnw_page112.txt");
		fileList.add("corpus/Coraline.txt");
		fileList.add("corpus/Coraline2.txt");
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
