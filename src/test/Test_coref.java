package test;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.*;
import performance.coref.CorefUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;

// Il faut ajouter -Xmx6g dans VM args dans Run Configuration
// pour ex�cuter ce test
public class Test_coref
{
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{

		TestCorefUtils();
		//TestCorefStandAlone();
	}
	
	public static void TestCorefUtils() throws IOException, ClassNotFoundException
	{
		String path = "corpus" + File.separator + "reference.txt";
		File file = new File(path);
		Annotation annotation = CorefUtils.getInitAnnotation(file);
		Properties props = new Properties();
		CorefAnnotator corefAnnotator = new CorefAnnotator(props);
		corefAnnotator.annotate(annotation);
	}
	
	public static void TestCorefStandAlone() throws IOException
	{
		String path = "corpus" + File.separator + "bnw_page1.txt";
		FileInputStream is = new FileInputStream(path);     
		String content = IOUtils.toString(is, "UTF-8");
		Annotation annotation = new Annotation(content);
		Properties propsbase = new Properties();
		propsbase.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(propsbase);
		CoreDocument document = new CoreDocument(annotation);
		pipeline.annotate(document);
		
		Properties props = new Properties();
		//props.setProperty("coref.language", "en");
		//props.setProperty("coref.algorithm", "statistical");
		CorefAnnotator annotator = new CorefAnnotator(new Properties());
		annotator.annotate(annotation);
		
		CoreDocument document2 = new CoreDocument(annotation);
		Map<Integer, CorefChain> corefChains = document2.corefChains();	
		for(Integer key : corefChains.keySet())
		{
			CorefChain chain = corefChains.get(key);
			System.out.println(chain);
		}
	}
	
	public static void TestDivers() throws IOException
	{
		// Lecture du contenu d'un texte du corpus. Il faut t�l�charger et
		// ajouter au path org.apache.commons.io.IOUtils;
		String path = "corpus" + File.separator + "reference.txt";
		FileInputStream is = new FileInputStream(path);     
		String content = IOUtils.toString(is, "UTF-8");
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse, coref");
		// Si on veut utiliser le mod�le "r�seau de neurones"
		//props.setProperty("coref.algorithm", "neural");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
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
		System.out.println(originalEntityMention);
		// celle d'origine : la premi�re rencontr�e
		System.out.println("Example: canonical entity mention");
		System.out.println(originalEntityMention.canonicalEntityMention().get());

		// R�cup�rer les cha�nes de cor�f�rence de l'int�gralit� du document
		Map<Integer, CorefChain> corefChains = document.corefChains();
		System.out.println("Example: coref chains for document");
		System.out.println(corefChains);
		System.out.println();
		
		/*CorefChain chain = corefChains.get(18);
		System.out.println(chain);
		
		List<CorefMention> test = chain.getMentionsInTextualOrder();
		for(int i = 0; i < test.size(); i++)
			System.out.println("Mot : " + test.get(i) + "index : " + test.get(i).startIndex);*/
		
		for(Integer key : corefChains.keySet())
		{
			CorefChain chain = corefChains.get(key);
			System.out.println(chain);
		}
	}
}
