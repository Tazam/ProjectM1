package performance;

import java.io.File;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class AnnotatorFilesBuilder 
{
	protected File corpusFolder;
	protected StanfordCoreNLP pipeline;
	protected List<String> properties;
	
	// Créer les fichiers avec des propriétés différentes
	public AnnotatorFilesBuilder(Properties props)
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
		this.pipeline = new StanfordCoreNLP(props);
	}
}
