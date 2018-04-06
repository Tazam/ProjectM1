package test;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.Sentence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;

/* @author="Axel Clerici" */

public class Test_ssplit
{
	public static void main(String[] args) throws IOException 
	{
		TestPipeLine();
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
		//props.setProperty("ssplit.boundaryTokenRegex", "...");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

		// Exemple pour récupérer une phrase (sans utiliser CoreMap)
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
		
		TokenizerAnnotator required = new TokenizerAnnotator();
		required.annotate(annotation);
		
		WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
		annotator.annotate(annotation);
		
		CoreDocument doc = new CoreDocument(annotation);
		List<CoreSentence> phrases = doc.sentences();
		int j = 0;
		for(CoreSentence sentence : phrases)
		{
			j ++;
			String sentenceText = sentence.text();
			System.out.println("Phrase " + j + " : " + sentenceText);
		}
	}
	
	public static String lireExemple() throws IOException
	{
		String path = "corpus" + File.separator + "reference.txt";
		FileInputStream is = new FileInputStream(path);     
		String content = IOUtils.toString(is, "UTF-8");
		return content;
	}
}

