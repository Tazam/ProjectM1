/**
 * 
 */
package test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.BinarizedTreeAnnotation;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author Schmidt Ga�tan
 *
 */
public class testParse {
	public static void main(String[] args) throws IOException {
		
		
			String text = "Joe Smith was born in California. " +
			      "In 2017, he went to Paris, France in the summer. " +
			      "His flight left at 3:00pm on July 10th, 2017. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
			      "He sent a postcard to his sister Jane Smith. " +
			      "After hearing about Joe's trip, Jane decided she might go to France one day.";
		 // set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,parse");
	    props.setProperty("parse.binaryTrees", "true");
	    
	    
	    
	 // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(text);
	 // annnotate the document
	    pipeline.annotate(document);
	    // examples
	    
	    // first sentence
	    CoreSentence sentence1 = document.sentences().get(0);
	    // constituency parse for the first sentence
	    Tree constituencyParse = sentence1.constituencyParse();
	    System.out.println("Example: constituency parse");
	    System.out.println(constituencyParse);
	    System.out.println();
	    // dependency parse for the first sentence
	    SemanticGraph dependencyParse = sentence1.dependencyParse();
	    System.out.println("Example: dependency parse");
	    System.out.println(dependencyParse);
	    System.out.println();
	    
	    List<CoreMap> sentences = document.annotation().get(SentencesAnnotation.class);
	    for(CoreMap sentence: sentences) {
	    	
	    	System.out.println("sentence : "+sentence.toString());
	    	System.out.println("TreeAnnotation :");
	    	Tree tree = sentence.get(TreeAnnotation.class);
	    	System.out.println(tree.toString());
	    	
	    	System.out.println("sentence : "+sentence.toString());
	    	System.out.println("BasicDependenciesAnnotation :");
	    	SemanticGraph basicGraph = sentence.get(BasicDependenciesAnnotation.class);
	    	System.out.println(basicGraph.toString());
	    	
	    	System.out.println("sentence : "+sentence.toString());
	    	System.out.println("EnhancedDependenciesAnnotation :");
	    	SemanticGraph enhanced = sentence.get(EnhancedDependenciesAnnotation.class);
	    	System.out.println(enhanced.toString());
	    	
	    	System.out.println("sentence : "+sentence.toString());
	    	System.out.println("EnhancedPlusPlusDependenciesAnnotation :");
	    	SemanticGraph truc = sentence.get(EnhancedPlusPlusDependenciesAnnotation.class);
	    	System.out.println(truc.toString());
	    	
	    	System.out.println("sentence : "+sentence.toString());
	    	System.out.println("BinarizedTreeAnnotation :");
	    	Tree binarized = sentence.get(BinarizedTreeAnnotation.class);
	    	System.out.println(binarized.toString());
	    	/*
	    	System.out.println("sentence : "+sentence.toString());
	    	System.out.println("KBestTreesAnnotation :");
	    	List<Tree> kb = sentence.get(KBestTreesAnnotation.class);
	    	System.out.println(kb.toString());
	    	*/
	    }

	    
	    
	}
}
