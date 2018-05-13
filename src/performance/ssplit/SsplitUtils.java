package performance.ssplit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import performance.Consts;
//import performance.ner.TokenNerCustom;

// @author Axel Clerici
// Cette classe contient les différentes méthodes nécessaires à la création des phrases,
// que ce soit à partir de l'annotateur de Stanford ou à partir d'un fichier de référence.
// De plus, elle permet de renvoyer l'annotation "nettoyée" pour les prochaines étapes
// d'évaluation des performances.


public class SsplitUtils 
{

	// Construit l'annotation requise en entrée pour l'annotateur de Stanford
	// ou l'annotateur de référence à partir d'un texte du Corpus.
	// Ssplit n'a besoin que de Tokenize pour fonctionner; celui n'étant pas évalué,
	// on utilise celui par défaut de Stanford.
	public static Annotation getInitAnnotation(File file) throws IOException
	{
		FileInputStream is = new FileInputStream(file);     
		String content = IOUtils.toString(is, "UTF-8");
		Annotation annotation = new Annotation(content);
		TokenizerAnnotator tokenizer = new TokenizerAnnotator();
		tokenizer.annotate(annotation);
		return annotation;
	}
	
	// Renvoie l'annotation propre annotée par WordsToSentencesAnnotatorCustom
	// File file est un fichier du corpus.
	public static Annotation getCleanAnnotation(File file) throws IOException
	{
		Annotation annotation = getInitAnnotation(file);
		
		// On retrouve le fichier de référence à partir du nom du fichier du corpus.
		// Corpus : corpus.txt => Référence : corpus_reference.txt
		String fileName = FilenameUtils.removeExtension(file.getName());
		String referencePath = Consts.SSPLIT_PATH + File.separator + fileName + Consts.REFERENCE_EXTENSION;
		File referenceFile = new File(referencePath);
		
		// On récupère le nombre de tokens par lignes dans le fichier de référence,
		List<Integer> tokensPerLine = countTokensPerLine(referenceFile);
		// On regroupe les tokens du texte du corpus en liste de listes représentant les phrases
		List<List<CoreLabel>> coreLabelsDoc = splitCoreLabels(tokensPerLine, annotation.get(TokensAnnotation.class));
		// On transmet cette liste de listes à l'annotateur personnalisée qui annotera l'annotation correctement
		WordsToSentencesAnnotatorCustom custom = new WordsToSentencesAnnotatorCustom();
		custom.annotateCustom(annotation, coreLabelsDoc);
		return annotation;
	}
	
	// Retourne une liste de phrases qui correspondent aux phrases détectées par l'annotateur WordsToSentencesAnnotator
	// de Stanford dans un fichier texte du corpus.
	public static List<CoreSentence> getStanfordSentences(File file, Properties props) throws IOException
	{
		Annotation annotation = getInitAnnotation(file);
		WordsToSentencesAnnotator ssplit;
		if(props == null)
			ssplit = new WordsToSentencesAnnotator();
		else
			ssplit = new WordsToSentencesAnnotator(props);
		ssplit.annotate(annotation);
		return (new CoreDocument(annotation).sentences());
	}
	
	// Retourne une liste de phrases qui correspondent aux phrases détectées manuellement
	// File file est un texte du corpus
	public static List<CoreSentence> getCustomSentences(File file) throws IOException
	{
		Annotation annotation = getCleanAnnotation(file);
		return (new CoreDocument(annotation).sentences());
	}
	
	// Retourne une liste d'entiers qui indiquent la taille des phrases de référence en nombre de tokens.
	// File file est un fichier de référence.
	private static List<Integer> countTokensPerLine(File file)
	{
		List<Integer> tokensPerLine = new ArrayList<>();
		// ce Tokenizer devrait utilisé les même propriétés que celui de utilisé dans getInitAnnotation()
		TokenizerAnnotator tokenizer = new TokenizerAnnotator();
	    try 
	    {
	        BufferedReader b = new BufferedReader(new FileReader(file));
	        String readLine = "";
	        // On tokenize chacune des lignes du fichier de référence, et on compte le nombre de tokens créés
	        while ((readLine = b.readLine()) != null) 
	        {
	        	Annotation annotation = new Annotation(readLine);
	        	tokenizer.annotate(annotation);
	        	// puis on ajoute ce nombre à la liste du résultat
	        	tokensPerLine.add(annotation.get(TokensAnnotation.class).size());
	        }
	        b.close();
	     } catch (IOException e) {e.printStackTrace();}
	    return tokensPerLine;
	}
	
	// Cette fonction reçoit en entrée la liste entière des tokens du corpus, ainsi qu'une liste d'entier
	// indiquant la taille des phrases dans le fichier de référence. Il va alors séparer la liste de tokens
	// en liste de listes de tokens représentant les phrases telles qu'elles sont le fichier de référence
	public static List<List<CoreLabel>> splitCoreLabels(List<Integer> tokensPerLine, List<CoreLabel> labels)
	{
		Iterator<CoreLabel> itr = labels.iterator();
		List<List<CoreLabel>> coreLabelsDoc = new ArrayList<>();
		for(Integer tokens : tokensPerLine)
		{
			List<CoreLabel> coreLabelsSent = new ArrayList<>();
			for(int i = 0; i < tokens; i ++)
			{
				coreLabelsSent.add(itr.next());
			}
			coreLabelsDoc.add(coreLabelsSent);
		}
		return coreLabelsDoc;
	}
	
	// Retourne la position de la coupure d'une phrase
	public static int getEndCharOffsets(CoreSentence sentence)
	{
		return sentence.charOffsets().second;
	}
	
}
