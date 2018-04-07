package test;

import edu.stanford.nlp.ie.machinereading.structure.AnnotationUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;

/* @author="Axel Clerici" */

public class Test_ssplit
{
	public static void main(String[] args) throws IOException 
	{
		//TestPipeLine();
		TestWordsToSentencesAnnotator();
	}
	
	public static void TestPipeLine() throws IOException
	{
		System.out.println("Ceci est un test de ssplit via le pipeline");
		String content = lireExemple();
		
		// Construction du CoreDocument
		Properties props = new Properties();
		// pour tester ssplit, on a besoin de tokenize
		props.setProperty("annotators", "tokenize, ssplit");
		props.setProperty("ssplit.eolonly", "true");
		//props.setProperty("ssplit.boundaryTokenRegex", "...");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

		// Exemple pour r�cup�rer une phrase (sans utiliser CoreMap)
		List<CoreSentence> sentences = document.sentences();
		// CoreSentence firstSentence = sentences.get(0);
		
		int i = 0;
		for(CoreSentence sentence : sentences)
		{
			i ++;
			String sentenceText = sentence.text();
			System.out.println("Phrase " + i + " : " + sentenceText);
		}
	}
	
	public static void TestWordsToSentencesAnnotator() throws IOException
	{
		System.out.println("Ceci est un test de ssplit via le WordsToSentencesAnnotator");
		String content = lireExemple();
		
		Annotation annotation = new Annotation(content);
		
		Properties test = new Properties();
		//Obligatoire pour pouvoir utiliser newlineIsSentenceBreak
		test.setProperty("tokenize.keepeol", "true");
		test.setProperty("tokenize.whitespace", "true");
		TokenizerAnnotator required = new TokenizerAnnotator(test);
		required.annotate(annotation);
		
		Properties props = new Properties();
		props.setProperty("ssplit.boundaryTokenRegex", "null");
		props.setProperty("ssplit.newlineIsSentenceBreak", "always");
		// Ne fonctionne pas pour un raison inconnue
		//props.setProperty("ssplit.eolonly", "true");
		
		WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
		annotator.annotate(annotation);
		
		CoreDocument doc = new CoreDocument(annotation);
		List<CoreSentence> phrases = doc.sentences();
		int j = 0;
		for(CoreSentence sentence : phrases)
		{
			j ++;
			String sentenceText = sentence.text();
			System.out.println("Phrase " + j + " : " + sentenceText);
			//System.out.println("CoreMap : " + sentence.coreMap().toString());
		}
		
	//	
	}
	
	// Prise en main/découverte de stanford NLP
	public static void TestCustomSentences() throws IOException
	{
		/*
		System.out.println("Ceci est un test de ssplit � l'envers");
		String content = lireExemple();
		
		Annotation annotation = new Annotation(content);
		TokenizerAnnotator required = new TokenizerAnnotator();
		required.annotate(annotation);
		List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
		//System.out.println(annotation);
		CoreMap coreMapSentence = new CoreLabel();
		coreMapSentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
		//System.out.println(coreMapSentence.toShorterString());
		List<CoreMap> sentences = new ArrayList<>();
		sentences.add(coreMapSentence);
	
		AnnotationUtils.addSentence(annotation, sentences.get(0));
		System.out.println(AnnotationUtils.sentenceToString(sentences.get(0)));
		CoreDocument test = new CoreDocument(annotation);
		List<CoreSentence> phrases = test.sentences();
		for(CoreSentence phrase : phrases)
			System.out.println(phrase);
		*/
	}
	
	public static String lireExemple() throws IOException
	{
		String path2 = "performance" + File.separator + "reference" + File.separator + "ssplit" + File.separator + "bnw_page1_reference.txt";
		String path3 = "performance" + File.separator + "stanford" + File.separator + "ssplit" + File.separator + "bnw_page1_stanford.txt";
		String path = "corpus" + File.separator + "bnw_page1.txt";
		FileInputStream is = new FileInputStream(path3);     
		String content = IOUtils.toString(is, "UTF-8");
		return content;
	}
}

