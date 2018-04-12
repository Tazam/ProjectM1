package test;

import edu.stanford.nlp.ie.machinereading.structure.AnnotationUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import performance.ssplit.SsplitUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;

/* @author="Axel Clerici" */

public class Test_ssplit
{
	public static void main(String[] args) throws IOException 
	{
		//TestPipeLine();
		//TestWordsToSentencesAnnotator();
		//TestSsplitUtils();
		//testCustomSsplit();
		//test();
		//TestCoreLabels();
		TestCleanAnnotation();
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
	
	// Tests divers
	public static void testCustomSsplit() throws IOException
	{
		System.out.println("Ceci est un test de ssplit � l'envers");
		String content = lireExemple();
		
		Annotation annotation = new Annotation(content);
		TokenizerAnnotator required = new TokenizerAnnotator();
		required.annotate(annotation);
		
		//SsplitUtils.test(annotation);
		
		
		WordsToSentencesAnnotator sentenceSplitter = new WordsToSentencesAnnotator();
		sentenceSplitter.annotate(annotation);
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);

		Annotation annotation2 = new Annotation(content);
		required.annotate(annotation);
		annotation2.set(CoreAnnotations.SentencesAnnotation.class, sentences);
		List<CoreMap> sentences2 = annotation.get(SentencesAnnotation.class);
		
		for(int i = 0; i < sentences2.size(); i++)
				System.out.println("Phrase" + sentences2.get(i));
	}
	
	public static String lireExemple() throws IOException
	{
		String path2 = "performance" + File.separator + "reference" + File.separator + "ssplit" + File.separator + "bnw_page1_reference.txt";
		String path3 = "performance" + File.separator + "stanford" + File.separator + "ssplit" + File.separator + "bnw_page1_stanford.txt";
		String path = "corpus" + File.separator + "bnw_page1.txt";
		FileInputStream is = new FileInputStream(path2);     
		String content = IOUtils.toString(is, "UTF-8");
		return content;
	}
	
	public static void test() throws IOException
	{
		String content = lireExemple();
		Annotation annotation = new Annotation(content);
		
		TokenizerAnnotator tokenizer = new TokenizerAnnotator();
		tokenizer.annotate(annotation);
		WordsToSentencesAnnotator sentenceSplitter = new WordsToSentencesAnnotator();
		sentenceSplitter.annotate(annotation);
		
		List<List<CoreLabel>> coreLabelsDoc = new ArrayList<>();
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) 
		{
			coreLabelsDoc.add(sentence.get(TokensAnnotation.class));
		}
		for(int i = 0; i < coreLabelsDoc.size(); i++)
		{
			List<CoreLabel> coreLabelsPhrase = coreLabelsDoc.get(i);
			System.out.println("PHRASE " + i + ": " + coreLabelsPhrase.size() + "   " + coreLabelsPhrase.toString());
		}
		String path2 = "performance" + File.separator + "reference" + File.separator + "ssplit" + File.separator + "bnw_page1_reference.txt";
		File f = new File(path2);
        BufferedReader b = new BufferedReader(new FileReader(f));
        String readLine = "";
        readLine = b.readLine();
        System.out.println(readLine);
        Annotation annotation2 = new Annotation(readLine);
        tokenizer.annotate(annotation2);
        List<CoreLabel> test = annotation2.get(TokensAnnotation.class);
        System.out.println("PHRASE : " + test.size() + "   " + test.toString());
	}
	
	public static void TestCleanAnnotation() throws IOException
	{
		String path2 = "performance" + File.separator + "reference" + File.separator + "ssplit" + File.separator + "bnw_page1_reference.txt";
		String path3 = "performance" + File.separator + "stanford" + File.separator + "ssplit" + File.separator + "bnw_page1_stanford.txt";
		String path = "corpus" + File.separator + "bnw_page1.txt";
		File file = new File(path);
		Annotation annotation = SsplitUtils.getCleanAnnotation(file);
		CoreDocument doc = new CoreDocument(annotation);
		List<CoreSentence> sentences = doc.sentences();
		for(int i = 0; i < sentences.size(); i ++)
			System.out.println("Phrase : " + i + " " + sentences.get(i).toString());
	}
}

