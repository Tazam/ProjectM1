/**
 *
 */
package test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import performance.ner.NERUtils;

/**
 * @author Schmidt Gaëtan
 *
 */
public class TestNER {
	//NamedEntityTagAnnotation.class
	public static void main(String[] args) throws IOException {
		
		String text="Joe Smith was born in California. " +
			      "In 2017, he went to Paris, France in the summer. " +
			      "His flight left at 3:00pm on July 10th, 2017. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
			      "He sent a postcard to his sister Jane Smith. " +
			      "After hearing about Joe's trip, Jane decided she might go to France one day."+
			      "Marc said, 'I'm the bigger butterfly in this world !!!!!!!!!!!!' ";
		
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
	    // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(text);
	    // annnotate the document
	    pipeline.annotate(document);
	    
	    // examples
	    String res = document.annotation().get(NamedEntityTagAnnotation.class);
	    for (CoreSentence sentence1 : document.sentences())
		{
			for (CoreLabel t : sentence1.tokens())
			{
				System.out.println(t.word());
				System.out.println(t.ner());
			}
		}
	    
	    System.out.println("NORM : "+document.annotation().get(NormalizedNamedEntityTagAnnotation.class));
	    //document.annotation().get(NamedEntityTagAnnotation.class);
		
	    }
	
}
