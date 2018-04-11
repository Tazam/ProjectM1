package performance.coref;

import java.io.File;
import java.util.Properties;

import performance.Consts;

public class CorefFilesBuilder
{

	public CorefFilesBuilder(Properties props) 
	{
		
	}
	
	public CorefFilesBuilder()
	{
		
	}

	private static Properties getDefaultProps()
	{
		Properties props = new Properties();
		props.setProperty("annotator", Consts.COREF_DEFAULT_PROPS);
		return props;
	}
	
	public void buildCorefFiles()
	{

	}

	private void buildCorefFiles(File fileEntry)
	{
		// TODO Auto-generated method stub
	}

}
