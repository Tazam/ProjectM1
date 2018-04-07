package performance.ssplit;

import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import edu.stanford.nlp.pipeline.*;
import performance.BasicStats;
import performance.Consts;

// Cette classe permet de comparer les r�sultats obtenus par l'annotateur ssplit de StanfordNLP
// avec une r�f�rence qui correspond aux m�me textes annot�s manuellement
public class SentencesFilesComparator 
{
	private BasicStats stats;
	
	public SentencesFilesComparator()
	{
		stats = new BasicStats();
	}
	
	public BasicStats compareFiles() throws IOException
	{			
		File[] stanfordFolder = new File(Consts.STANFORD_SSPLIT_PATH).listFiles();
		File[] referenceFolder = new File(Consts.REFERENCE_SSPLIT_PATH).listFiles();

		// Je n'ai pas encore annot� � la main les autres fichiers
		for(int i = 0; i < 2/*referenceFolder.length*/; i++)
		{
			System.out.println("Je compare " + stanfordFolder[i].getName() + " et " + referenceFolder[i].getName());
			List<CoreSentence> stanfordSentences = getSentencesFromFile(stanfordFolder[i]);
			List<CoreSentence> referenceSentences = getSentencesFromFile(referenceFolder[i]);
			compareFile(stanfordSentences, referenceSentences);
		}
		return this.stats;
	}
	
	private void compareFile(List<CoreSentence> stanfordSentences, List<CoreSentence> referenceSentences) throws IOException
	{
		int tp = 0;
		int fp = 0;
		int fn = 0;
		
		for(CoreSentence stanfordSentence : stanfordSentences)
		{
			for(CoreSentence referenceSentence : referenceSentences)
			{
				String stext = stanfordSentence.toString();
				String rtext = referenceSentence.toString();
				if(stext.equals(rtext))
				{
					tp ++;
					System.out.println(tp + ":" +rtext);
					System.out.println(tp + ":" +stext);
					break;
				}
			}
		}
		System.out.println(stanfordSentences.size());
		System.out.println(referenceSentences.size());
		fp = stanfordSentences.size() - tp;
		fn = referenceSentences.size() - tp;
		
		stats.updateStats(tp, fp, fn);
	}
	
	private List<CoreSentence> getSentencesFromFile(File file) throws IOException
	{
		CoreDocument document = new CoreDocument(getSsplitAnnotation(file));
		return document.sentences();
	}
	
	// Permet de réaliser la séparation en phrases pour un format de fichier précis :
	// une ligne == une phrase.
	
	// TODO une méthode qui renvoie l'annotation nettoyé de ssplit et des annotateurs suivants non évalués
	// comme POS, pour les prochains annotateurs évalués
	
	private Annotation getSsplitAnnotation(File file) throws IOException
	{	
		Annotation annotation = getInitAnnotation(file);
		Properties props = new Properties();
		props.setProperty("ssplit.boundaryTokenRegex", "null");
		props.setProperty("ssplit.newlineIsSentenceBreak", "always");
		WordsToSentencesAnnotator sentenceSplitter = new WordsToSentencesAnnotator(props);
		sentenceSplitter.annotate(annotation);
		
		return annotation;
	}
	
	// Utile que si Tokenizer n'est pas évalué
	// Dans le cas contraire, il faut récupérer l'annotation générée par le Tokenizer nettoyé
	private Annotation getInitAnnotation(File file) throws IOException
	{
		FileInputStream is = new FileInputStream(file);     
		String content = IOUtils.toString(is, "UTF-8");
		Annotation annotation = new Annotation(content);
		Properties props = new Properties();
		//Obligatoire pour pouvoir utiliser newlineIsSentenceBreak
		props.setProperty("tokenize.keepeol", "true");
		props.setProperty("tokenize.whitespace", "true");
		TokenizerAnnotator annotator = new TokenizerAnnotator(props);
		annotator.annotate(annotation);
		return annotation;
	}
}
