package performance.ssplit;

import edu.stanford.nlp.pipeline.*;
import performance.AnnotatorFilesBuilder;
import performance.Consts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

// Le but de cette classe est de construire les fichiers textes
// contenant les phrases déterminées par StanfordNLP
public class SentencesFilesBuilder extends AnnotatorFilesBuilder
{	
	// Créer les fichiers avec des propriétés différentes
	public SentencesFilesBuilder(Properties props)
	{
		super(props);
	}
	
	// Créer les fichiers avec les propriétés de base de Stanford
	public SentencesFilesBuilder()
	{
		super(getDefaultProps());
	}
		
	public void buildSentencesFiles() throws IOException
	{
		for (final File fileEntry : corpusFolder.listFiles()) 
		{
			buildSentenceFile(fileEntry);
		}
	}
	
	private void buildSentenceFile(File file) throws IOException
	{
		// On lit le contenu du fichier texte d'origine
		System.out.println("Je traite " + file.getName());
		FileInputStream is = new FileInputStream(file);
		String content = IOUtils.toString(is, Consts.FORMAT);
		
		// On annote le fichier
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);
		List<CoreSentence> sentences = document.sentences();
		List<String> sentencesText = new ArrayList<>();
		// on remplace les retours à la ligne par des espaces dans les phrases
		// car on veut un fichier où une ligne représente une phrase
		for(CoreSentence sentence : sentences) {
			sentencesText.add(sentence.text().replace("\r\n", " "));
		}
	
		// On écrit le résultat dans un autre fichier
		String resultName = FilenameUtils.removeExtension(file.getName());
		String resultPath = Consts.STANFORD_SSPLIT_PATH + File.separator + resultName + Consts.RESULT_EXTENSION;
		System.out.println("J'écris le résultat dans : " + resultPath);
		Path result = Paths.get(resultPath);
		Files.write(result, sentencesText, Charset.forName(Consts.FORMAT));
	}
	
	private static Properties getDefaultProps()
	{
		Properties props = new Properties();
		props.setProperty("annotator", Consts.SSPLIT_DEFAULT_PROPS);
		return props;
	}
}
