package test;

import edu.stanford.nlp.pipeline.*;

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
		// Lecture du contenu d'un texte du corpus. Il faut télécharger et
		// ajouter au path org.apache.commons.io.IOUtils;
		String path = "corpus" + File.separator + "bnw_page1.txt";
		FileInputStream is = new FileInputStream(path);     
		String content = IOUtils.toString(is, "UTF-8");

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
}

