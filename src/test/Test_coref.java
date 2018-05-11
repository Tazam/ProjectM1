package test;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntTuple;
import performance.coref.CorefUtils;
import performance.coref.customannotators.CorefAnnotatorCustom;

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
		//TestDivers();
		//testBlob();
		//TestCorefUtils();
		//TestCorefStandAlone();
		testCompareCorefDcoref();
	}
	
	public static void TestCorefUtils() throws IOException, ClassNotFoundException
	{
		String path = "corpus" + File.separator + "hyperion_page378.txt";
		File file = new File(path);
		Annotation annotation = CorefUtils.getInitAnnotation(file);
		Properties props = new Properties();
		CorefAnnotator corefAnnotator = new CorefAnnotator(props);
		corefAnnotator.annotate(annotation);
		
		CoreDocument document = new CoreDocument(annotation);
		List<CoreSentence> sentences = document.sentences();
		
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
		props.setProperty("coref.algorithm", "neural");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

		// Permet de r�cup�rer toutes les mentions d'une phrase
		CoreSentence sentence = document.sentences().get(1);
		List<CoreEntityMention> entityMentions = sentence.entityMentions();

		// Test de cor�f�rence entre deux mentions
		CoreEntityMention originalEntityMention = sentence.entityMentions().get(4);;
		CoreEntityMention test = null;
		//test.canonicalEntityMention();

		Map<Integer, CorefChain> corefChains = document.corefChains();
		
		CorefChain test2;
		CorefChain chain = corefChains.get(18);
		System.out.println(corefChains);

		
		/*List<CorefMention> test = chain.getMentionsInTextualOrder();
		for(int i = 0; i < test.size(); i++) {
			System.out.println("Mot : " + test.get(i) + " sentence : " + test.get(i).sentNum + " mentionnum : " + test.get(i).startIndex + " endindex : " + test.get(i).endIndex);
			System.out.println(test.get(i).position);
		}*/
		
		/*
		for(Integer key : corefChains.keySet())
		{
			CorefChain chain = corefChains.get(key);
			System.out.println(chain);
		}*/
	}
	
	public static void testBlob() throws ClassNotFoundException, IOException
	{
		String path = "corpus" + File.separator + "reference.txt";
		File file = new File(path);
		Annotation annotation = CorefUtils.getInitAnnotation(file);
		Properties props = new Properties();

		
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		List<List<Mention>> mentions = new ArrayList<>();
		CorefAnnotator corefAnnotator = new CorefAnnotator(props);
		corefAnnotator.annotate(annotation);
		CoreDocument document1 = new CoreDocument(annotation);
		Map<Integer, CorefChain> corefChains1 = document1.corefChains();
		System.out.println(corefChains1);
		
		
		for(CoreMap sentence : sentences)
		{
			mentions.add(sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
			for(Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class))
			{
				System.out.println("mention : " + m + " heaword " + m.headWord + " headindex " + m.headIndex);
			}
			//List<Mention> mentions = sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
			//SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
			/*for(Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class))
			{
				System.out.println("mention");
				System.out.println(m);
				System.out.println("mentionID");
				System.out.println(m.mentionID);
				/*System.out.println("mentionBasicDependency");
				System.out.println(m.basicDependency);
				System.out.println("mentionEnhancedDependency");
				System.out.println(m.enhancedDependency);
				System.out.println("startindex");
				System.out.println(m.startIndex);
				System.out.println("startindex");
				System.out.println(m.endIndex);
				System.out.println("sentencesWords");
				System.out.println(m.sentenceWords);
			}*/
		}
		System.out.println("SEPARATION");
		/*
		SemanticGraph dependencies = sentences.get(0).get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
		List<List<Mention>> test = new ArrayList<>();
		SemanticGraph dependencies2 = sentences.get(0).get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
		List<CoreLabel> oui = new ArrayList<>();
		// pour dcoref
		//Mention exampleMention = new Mention(0, 0, 2, dependencies);
		//pour coref
		Mention exampleMention = new Mention(0,0, 2, oui, dependencies, dependencies2);*/
		
		Annotation annotation2 = CorefUtils.getInitAnnotation(file);
		CorefAnnotatorCustom corefAnnotatorCustom = new CorefAnnotatorCustom(props);
		corefAnnotatorCustom.annotateCustom(annotation2, mentions, corefChains1);
		
		List<CoreMap> sentences2 = annotation.get(SentencesAnnotation.class);
		for(CoreMap sentence : sentences2)
		{
			for(Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class))
			{
				System.out.println("mention : " + m.toString() + " startindex : " + m.startIndex + " endindex : " + m.endIndex);
			}
		}
		CoreDocument document = new CoreDocument(annotation2);
		Map<Integer, CorefChain> corefChains = document.corefChains();
		System.out.println(corefChains);
		/*
		for(Map.Entry entry : corefChains.entrySet())
		{
			System.out.println(entry.getKey() + " " + entry.getValue());
		}*/
		/*CorefChain chain = corefChains.get(18);
		System.out.println(chain);
		
		List<CorefMention> test = chain.getMentionsInTextualOrder();
		for(int i = 0; i < test.size(); i++)
			System.out.println("Mot : " + test.get(i) + " sentence : " + test.get(i).sentNum + " startindex : " + test.get(i).startIndex + " endindex : " + test.get(i).endIndex);*/
	}
	
	
	public static void testCompareCorefDcoref() throws ClassNotFoundException, IOException
	{
		String path = "corpus" + File.separator + "reference.txt";
		File file = new File(path);
		Properties props = new Properties();
		props.setProperty("coref.algorithm", "deterministic");
		Map<Integer, CorefChain> dcorefChains = CorefUtils.getStanfordCorefChains(file, props);
	}
}
