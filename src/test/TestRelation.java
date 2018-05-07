/**
 * 
 */
package test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author Schmidt Gaëtan
 *
 */
public class TestRelation {
	public static void main(String[] args) throws IOException {
		String text="Joe Smith was born in California. " +
			      "In 2017, he went to Paris, France in the summer. " +
			      "His flight left at 3:00pm on July 10th, 2017. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
			      "He sent a postcard to his sister Jane Smith. " +
			      "After hearing about Joe's trip, Jane decided she might go to France one day."+
			      "Marc said, 'I'm the bigger butterfly in this world !!!!!!!!!!!!' ";
		
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse, relation");
	    // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(text);
	    // annnotate the document
	    pipeline.annotate(document);
	    
	    // examples
	    List<CoreMap> sentences = document.annotation().get(SentencesAnnotation.class);
	    for(CoreMap sentence: sentences) {
	    	System.out.println(sentence.toString());
	    	System.out.println("Relations :");
	    	List<RelationMention> tree = sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
	    	for (RelationMention relation: tree)
	    	{	System.out.println("Id --->"+relation.getObjectId()+"  : "+relation.getValue()+" : type ---> "+relation.getType()+ "|"+ "Sous-type ---> "+ relation.getSubType()+ "Signature ---> "+relation.getSignature());
	    		//System.out.println(relation.getSignature());
	    	}

	    }
	}
}
