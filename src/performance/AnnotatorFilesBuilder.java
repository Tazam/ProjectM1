package performance;

import java.io.File;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class AnnotatorFilesBuilder 
{
	protected File corpusFolder;
	protected StanfordCoreNLP pipeline;
	
	// Cr�er les fichiers avec des propri�t�s diff�rentes
	public AnnotatorFilesBuilder(Properties props)
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
		this.pipeline = new StanfordCoreNLP(props);
	}
	
	public AnnotatorFilesBuilder() 
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
	}
}
