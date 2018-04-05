package performance.coref;

import java.io.IOException;

import performance.BasicStats;

public class CorefFilesComparator 
{
	private BCubeStats bcube;
	private CEAFStats ceaf;
	private BasicStats muc;
	
	public CorefFilesComparator()
	{
		this.bcube = new BCubeStats();
		this.ceaf = new CEAFStats();
		this.muc = new BasicStats();
	}
	
	public BasicStats compareFiles_BCUBE() throws IOException
	{		
		return null;
	}
	
	private void compareFile_BCUBE() throws IOException
	{
		
	}
	
	public BasicStats compareFiles_CEAF() throws IOException
	{		
		return null;
	}
	
	private void compareFile_CEAF() throws IOException
	{
		
	}
	
	public BasicStats compareFiles_MUC() throws IOException
	{		
		return null;
	}
	
	private void compareFile_MUC() throws IOException
	{
		
	}
}
