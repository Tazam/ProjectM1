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
// contenant les phrases d�termin�es par StanfordNLP
public class SentencesFilesBuilder extends AnnotatorFilesBuilder
{	
	// Cr�er les fichiers avec des propri�t�s diff�rentes
	public SentencesFilesBuilder(Properties props)
	{
		super(props);
	}
	
	// Cr�er les fichiers avec les propri�t�s de base de Stanford
	public SentencesFilesBuilder()
	{
		super();
		Properties props = new Properties();
		props.setProperty("annotators", Consts.SSPLIT_DEFAULT_PROPS);
		this.pipeline = new StanfordCoreNLP(props);
	}
		
	public void buildFiles() throws IOException
	{
		for (final File fileEntry : corpusFolder.listFiles()) 
		{
			buildFile(fileEntry);
		}
	}
	
	private void buildFile(File file) throws IOException
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
		// on remplace les retours � la ligne par des espaces dans les phrases
		// car on veut un fichier o� une ligne repr�sente une phrase
		for(CoreSentence sentence : sentences) {
			sentencesText.add(sentence.text().replace("\r\n", " "));
		}
	
		// On �crit le r�sultat dans un autre fichier
		String resultName = FilenameUtils.removeExtension(file.getName());
		String resultPath = Consts.STANFORD_SSPLIT_PATH + File.separator + resultName + Consts.RESULT_EXTENSION;
		System.out.println("J'écris les résultats dans " + resultPath);
		Path result = Paths.get(resultPath);
		Files.write(result, sentencesText, Charset.forName(Consts.FORMAT));
	}
}
