package performance.coref;

import java.io.File;
import java.util.Properties;

import performance.AnnotatorFilesBuilder;
import performance.Consts;

public class CorefFilesBuilder extends AnnotatorFilesBuilder
{

	public CorefFilesBuilder(Properties props) 
	{
		super(props);
	}
	
	public CorefFilesBuilder()
	{
		super(getDefaultProps());
	}

	private static Properties getDefaultProps()
	{
		Properties props = new Properties();
		props.setProperty("annotator", Consts.COREF_DEFAULT_PROPS);
		return props;
	}
	
	public void buildCorefFiles()
	{
		for (final File fileEntry : corpusFolder.listFiles()) 
		{
			buildCorefFiles(fileEntry);
		}
	}

	private void buildCorefFiles(File fileEntry)
	{
		// TODO Auto-generated method stub
	}

}
