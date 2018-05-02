package performance.ssplit;

import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;
import performance.Consts;
import performance.stats.BasicStats;

// Cette classe permet de comparer les r�sultats obtenus par l'annotateur ssplit de StanfordNLP
// avec une r�f�rence qui correspond aux m�me textes annot�s manuellement
public class CoreSentencesComparator //implements AnnotationComparator<CoreSentence>
{
	private BasicStats stats;
	private Properties props;
	
	public CoreSentencesComparator()
	{
		stats = new BasicStats();
		this.props = null;
	}
	
	public CoreSentencesComparator(Properties props)
	{
		stats = new BasicStats();
		this.props = props;
	}
	
	public BasicStats compareFiles() throws IOException
	{			
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		// Je n'ai pas encore annoté la main les autres fichiers
		for(int i = 0; i < corpusFolder.length; i++)
		{
			List<CoreSentence> stanfordSentences = SsplitUtils.getStanfordSentences(corpusFolder[i], props);
			List<CoreSentence> referenceSentences = SsplitUtils.getCustomSentences(corpusFolder[i]);
			compareFile(stanfordSentences, referenceSentences);
		}
		return this.stats;
	}
	
	public void compareFile(List<CoreSentence> stanfordSentences, List<CoreSentence> referenceSentences) throws IOException
	{
		int tp = 0;
		int fp = 0;
		int fn = 0;
		
		for(CoreSentence stanfordSentence : stanfordSentences)
		{
			for(CoreSentence referenceSentence : referenceSentences)
			{
				int sEnd = SsplitUtils.getEndCharOffsets(stanfordSentence);
				int rEnd =  SsplitUtils.getEndCharOffsets(referenceSentence);
				if(sEnd == rEnd)
				{
					tp ++;
					break;
				}
			}
		}
		fp = stanfordSentences.size() - tp;
		fn = referenceSentences.size() - tp;
		
		stats.updateStats(tp, fp, fn);
	}
}
