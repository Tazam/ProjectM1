/**
 * 
 */
package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
/**
 * @author Schmidt Gaëtan
 *
 */
public class testQuote {
	public static void main(String[] args) throws IOException {
		/*
		String text ="In 2017, he went to Paris, France in the summer. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" "+
			      "But dips was better."+
			      "I said, \"I didn't like it.\"";
		
	    Properties props = new Properties();
	    // set the list of annotators to run
	   props.setProperty("annotators", "tokenize,ssplit,quote");
	    
	 // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    Annotation annotation = new Annotation(text);
	    // annnotate the document
	    pipeline.annotate(annotation);
	    
	    // examples
	    
	    // get sentences
	    System.out.println("Sentences :");
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    for (CoreMap sentence : sentences)
	    {
	    	System.out.println(sentence.toString());
	    }
	    
	 // get quotes in document
	    
	    System.out.println("Quotes :");
	    
	    // parcours des quotes
	    for (CoreMap quote : annotation.get(CoreAnnotations.QuotationsAnnotation.class)) {
	        System.out.println(quote);
	        System.out.println("Cléfs de l'objet quote :");
	        System.out.println(quote.keySet());
	        int index = quote.get(QuotationIndexAnnotation.class);
	        System.out.println("L'index de la quote est: "+index);
	        System.out.println("Affichage de la quote par son index: "+annotation.get(CoreAnnotations.QuotationsAnnotation.class).get(index));
	        System.out.println("numéro de caractère du début: "+quote.get(CharacterOffsetBeginAnnotation.class));
	        System.out.println("numéro de caractère de la fin : "+quote.get(CharacterOffsetEndAnnotation.class));
	      }
*/
	    
		
		String text;
		/*
		text="In 2017, he went to Paris, France in the summer. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" ";
		//*/
		text="Joe Smith was born in California. " +
			      "In 2017, he went to Paris, France in the summer. " +
			      "His flight left at 3:00pm on July 10th, 2017. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
			      "He sent a postcard to his sister Jane Smith. " +
			      "After hearing about Joe's trip, Jane decided she might go to France one day."+
			      "Marc said, 'I'm the bigger butterfly in this world !!!!!!!!!!!!' ";
		
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref,quote");
	    
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
	    
	    
	    // annotate the annotation
	    pipeline.annotate(annotation);
	    
	    // print result on a file
	    pipeline.prettyPrint(annotation, out );
	    pipeline.prettyPrint(annotation, xmlOut);
				
	    
	    // examples
	    
	 // get quotes in document
	    List<CoreQuote> quotes = document.quotes();
	    // number of quote :
	    int size = quotes.size();
	    System.out.println(size+" citations ont été traitée.");
	    
	    CoreQuote quote = quotes.get(0);
	    System.out.println("taille citation 1: "+quote.text().length());
	    System.out.println("Example: quote");
	    System.out.println(quote.text());
	    System.out.println();

	    // original speaker of quote
	    // note that quote.speaker() returns an Optional
	    System.out.println("Example: original speaker of quote");
	    System.out.println(quote.speaker().get());
	    System.out.println();
	    

	    // canonical speaker of quote
	    System.out.println("Example: canonical speaker of quote");
	    System.out.println(quote.canonicalSpeaker().get());
	    System.out.println();
	   // */
	}
}
