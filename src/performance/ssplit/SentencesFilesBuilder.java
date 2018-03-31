package performance.ssplit;

import edu.stanford.nlp.pipeline.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

// Le but de cette classe est de construire les fichiers textes
// contenant les phrases d�termin�es par StanfordNLP
public class SentencesFilesBuilder 
{
	private File corpusFolder;
	private StanfordCoreNLP pipeline;
	private List<String> properties;
	
	// Cr�er les fichiers avec des propri�t�s diff�rentes
	public SentencesFilesBuilder(List<String> properties)
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		this.pipeline = new StanfordCoreNLP(props);
	}
	
	// Cr�er les fichiers avec les propri�t�s de base de Stanford
	public SentencesFilesBuilder()
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		this.pipeline = new StanfordCoreNLP(props);
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
		for(CoreSentence sentence : sentences) {
			sentencesText.add(sentence.text());
		}
		
		// On �crit le r�sultat dans un autre fichier
		String resultName = FilenameUtils.removeExtension(file.getName());
		String resultPath = Consts.SSPLIT_PATH + File.separator + resultName + Consts.RESULT_EXTENSION;
		System.out.println("J'�cris le r�sultat dans : " + resultPath);
		Path result = Paths.get(resultPath);
		Files.write(result, sentencesText, Charset.forName(Consts.FORMAT));
	}
}
