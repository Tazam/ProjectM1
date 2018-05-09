/**
 * 
 */
package test;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.IntPair;

/**
 * @author Schmidt Gaëtan
 *
 */
public class TestVrack {

	/**
	 * @param args
	 * @author Schmidt Gaëtan
	 */
	public static void main(String[] args) {
		String text;
		
		/*
		text="In 2017, he went to Paris, France in the summer. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" ";
		
		text="Joe Smith was born in California. " +
			      "In 2017, he went to Paris, France in the summer. " +
			      "His flight left at 3:00pm on July 10th, 2017. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
			      "He sent a postcard to his sister Jane Smith. " +
			      "After hearing about Joe's trip, Jane decided she might go to France one day. "+
			      "Marc said, \"I'm the bigger butterfly in this world !\" "+
			      "Who is he? "+
			      "he is my brother Yohann. "+
			      "My brother is blue. ";
		
		 Properties props = new Properties();
		    // set the list of annotators to run
		    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse, coref");
		    //props.setProperty("coref.algorithm", "neural");
		    
		 // build pipeline
		    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		    // create a document object
		    CoreDocument document = new CoreDocument(text);
		    // annnotate the document
		    pipeline.annotate(document);
		    
		 // Permet de r�cup�rer toutes les mentions d'une phrase
			CoreSentence sentence = document.sentences().get(1);
			List<CoreEntityMention> entityMentions = sentence.entityMentions();
			System.out.println("Example: entity mentions");
			System.out.println(entityMentions);
			System.out.println();
			// Test de cor�f�rence entre deux mentions
			CoreEntityMention originalEntityMention = sentence.entityMentions().get(4);
			System.out.println("Example: original entity mention");
			System.out.println(originalEntityMention.text());
			// celle d'origine : la premi�re rencontr�e
			System.out.println("Example: canonical entity mention");
			//System.out.println(originalEntityMention.canonicalEntityMention().get());
			// R�cup�rer les cha�nes de cor�f�rence de l'int�gralit� du document
			Map<Integer, CorefChain> corefChains = document.corefChains();
			System.out.println("Example: coref chains for document");
			System.out.println(corefChains);
			System.out.println();
			
			
			for (CoreSentence sentence1 : document.sentences())
			{
				for (CoreLabel t : sentence1.tokens())
				{
					System.out.println(t.word());
					System.out.println(t.ner());
					if (t.equals(originalEntityMention.tokens().get(0)))
					{
						System.out.println("enfirrrrrrrrrrrrrrrrrrrrrrn");
						System.out.println(originalEntityMention.entity());
					}
				}
			}
			
			for (Integer key : corefChains.keySet())
			{
				System.out.println("ooooo");
				
				System.out.println((document.sentences().get(0).tokens().get(0).value()));
				System.out.println((document.sentences().get(0).tokens().get(0).sentIndex()));
				System.out.println(corefChains.get(key).getMentionsInTextualOrder().get(0).toString());
				System.out.println(corefChains.get(key).getMentionsInTextualOrder().get(0).sentNum);
				System.out.println(corefChains.get(key).getMentionsInTextualOrder().get(0).equals(document.sentences().get(0).tokens().get(0)));
				for (int k=0;k< corefChains.get(key).getMentionsInTextualOrder().size();k++)
				{
					System.out.println("p");
					System.out.println(corefChains.get(key).getMentionsInTextualOrder().get(k).mentionSpan);
					System.out.println(corefChains.get(key).getMentionsInTextualOrder().get(k).sentNum);
					
				}
			}*/
		
		
	}

}
