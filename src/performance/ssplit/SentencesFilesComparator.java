package performance.ssplit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import performance.BasicStats;

// Cette classe permet de comparer les résultats obtenus par l'annotateur ssplit de StanfordNLP
// avec une référence qui correspond aux même textes annotés manuellement
public class SentencesFilesComparator 
{
	private BasicStats stats;
	
	public SentencesFilesComparator()
	{
		stats = new BasicStats();
	}
	
	public BasicStats compareFiles() throws IOException
	{		
		File[] stanfordFolder = new File(Consts.STANFORD_SSPLIT_PATH).listFiles();
		File[] referenceFolder = new File(Consts.REFERENCE_SSPLIT_PATH).listFiles();

		for(int i = 0; i < 2/*referenceFolder.length*/; i++)
		{
			compareFile(stanfordFolder[i], referenceFolder[i]);
		}
		
		return this.stats;
	}
	
	private void compareFile(File stanfordFile, File referenceFile) throws IOException
	{
		System.out.println("Je compare " + stanfordFile.getName() + " et " + referenceFile.getName());
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int ss = 0;
		
		BufferedReader rfr = null;
		BufferedReader sfr = null;
		String line_rfr = null;
		String line_sfr = null;
		
		rfr = new BufferedReader(new FileReader(referenceFile));
		boolean countss = true;
		while((line_rfr = rfr.readLine()) != null)
		{
			boolean fncheck = true;
			sfr = new BufferedReader(new FileReader(stanfordFile));
			while((line_sfr = sfr.readLine()) != null)
			{
				if(countss == true)
					ss ++;
				if(line_sfr.equals(line_rfr))
				{
					tp ++;
					fncheck = false;
					if(countss == false)
						break;
				}
			}
			countss = false;
			if(fncheck == true) {
				fn ++;
			}
		}
		sfr.close();
		rfr.close();
		fp = ss - tp;
		
		stats.updateStats(tp, fp, fn);
	}
}
