package test;

import edu.stanford.nlp.pipeline.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;

public class Test_pos
{
	public static void main(String[] args) throws IOException 
	{
		// Lecture du contenu d'un texte du corpus. Il faut télécharger et
		// ajouter au path org.apache.commons.io.IOUtils;
		String path = "corpus" + File.separator + "reference.txt";
		FileInputStream is = new FileInputStream(path);     
		String content = IOUtils.toString(is, "UTF-8");
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

		// On retrouve les natures grammaticales des tokens dans
		// la classe CoreSentence.
		CoreSentence firstSentence = document.sentences().get(0);
		List<String> posTags = firstSentence.posTags();
		System.out.println("Example: pos tags");
		System.out.println(posTags);
		System.out.println();
  }

}