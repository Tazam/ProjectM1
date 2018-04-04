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
	
	// Créer les fichiers avec des propriétés différentes
	public AnnotatorBuilder(Properties props)
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
		this.pipeline = new StanfordCoreNLP(props);
	}
	// Créer les fichiers avec les propriétés de base de Stanford	
}
