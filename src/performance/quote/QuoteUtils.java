package performance.quote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class QuoteUtils {

	public static Annotation getCleanAnnotation(File f){
		
		
		return null;	
	}
	
	public static void testQuotes() throws FileNotFoundException{
		//texte a tester
		String text="Joe Smith was born in California. " +
			      "In 2017, he went to Paris, France in the summer. " +
			      "His flight left at 3:00pm on July 10th, 2017. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
			      "He sent a postcard to his sister Jane Smith. " +
			      "After hearing about Joe's trip, Jane decided she might go to France one day."+
			      "Marc said, 'I'm the bigger butterfly in this world !!!!!!!!!!!!' ";
		
		 Properties props = new Properties();
		 // set the list of annotators to run
		 props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref");
		    
		 // build pipeline
		 StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		 // create a document object
		 CoreDocument document = new CoreDocument(text);
		 // annnotate the document
		 pipeline.annotate(document);
		    
		 // OUTPUT
		 PrintWriter out = new PrintWriter("output.txt");
		 PrintWriter xmlOut = new PrintWriter("outpuc.xml");
		 // create annotation object for output
		 Annotation annotation = new Annotation(text);
		   
		 props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref,quote");
		    
		 QuoteAnnotatorCustom quoteC = new QuoteAnnotatorCustom(props);
		 quoteC.annotateCustom(annotation);
		    
		 // annotate the annotation
		 pipeline.annotate(annotation);
		    
		 // print result on a file
		 pipeline.prettyPrint(annotation, out );
		 pipeline.prettyPrint(annotation, xmlOut);
					
		    
	}
}
