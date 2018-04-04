package performance;

import java.io.File;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class AnnotatorBuilder 
{
	protected File corpusFolder;
	protected StanfordCoreNLP pipeline;
	protected List<String> properties;
	
	// Cr�er les fichiers avec des propri�t�s diff�rentes
	public AnnotatorBuilder(Properties props)
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
		this.pipeline = new StanfordCoreNLP(props);
	}
	// Cr�er les fichiers avec les propri�t�s de base de Stanford	
}
