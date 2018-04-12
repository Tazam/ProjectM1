package performance.ssplit;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;
import performance.Consts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FilenameUtils;

// Le but de cette classe est de construire les fichiers textes
// contenant les phrases déterminées par StanfordNLP
public class SentencesFilesBuilder
{	
	protected File corpusFolder;
	protected WordsToSentencesAnnotator sentencesSplitter;
	
	// Créer les fichiers avec des propriétés différentes
	public SentencesFilesBuilder(Properties props)
	{
		this.sentencesSplitter = new WordsToSentencesAnnotator(props);
		this.corpusFolder = new File(Consts.CORPUS_PATH);
	}
	
	// Créer les fichiers avec les propriétés de base de Stanford
	public SentencesFilesBuilder()
	{
		this.sentencesSplitter = new WordsToSentencesAnnotator();
		this.corpusFolder = new File(Consts.CORPUS_PATH);
	}
	
	// Construit tous les fichiers
	public void buildFiles() throws IOException
	{
		for (final File fileEntry : corpusFolder.listFiles()) 
		{
			buildFile(fileEntry);
		}
	}
		
	// Ecrit les phrases dans le bon fichier
	public void buildFile(File file) throws IOException
	{
		System.out.println("Je traite " + file.getName());
		
		// On annote le fichier
		List<CoreMap> sentences = getAnnotation(file).get(SentencesAnnotation.class);
		List<String> sentencesText = new ArrayList<>();
		// on remplace les retours à la ligne par des espaces dans les phrases
		// car on veut un fichier où une ligne représente une phrase
		for(CoreMap sentence : sentences) {
			sentencesText.add(sentence.toString().replace("\r\n", " "));
		}
	
		// On écrit le résultat dans un autre fichier
		String resultName = FilenameUtils.removeExtension(file.getName());
		String resultPath = Consts.STANFORD_SSPLIT_PATH + File.separator + resultName + Consts.RESULT_EXTENSION;
		System.out.println("J'écris les résultats dans " + resultPath);
		Path result = Paths.get(resultPath);
		Files.write(result, sentencesText, Charset.forName(Consts.FORMAT));
	}
		
	// Récupère une annotation annotée
	private Annotation getAnnotation(File file) throws IOException
	{
		Annotation annotation = SsplitUtils.getInitAnnotation(file);
		sentencesSplitter.annotate(annotation);
		return annotation;
	}
}
