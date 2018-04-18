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
import performance.BasicStats;
import performance.Consts;

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
		File[] referenceFolder = new File(Consts.REFERENCE_SSPLIT_PATH).listFiles();

		// Je n'ai pas encore annoté la main les autres fichiers
		for(int i = 0; i < 1/*referenceFolder.length*/; i++)
		{
			System.out.println("Je compare " + corpusFolder[i].getName() + " et " + referenceFolder[i].getName());
			List<CoreSentence> stanfordSentences = SsplitUtils.getStanfordSentences(corpusFolder[i], props);
			List<CoreSentence> referenceSentences = SsplitUtils.getCustomSentences(referenceFolder[i]);
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
				String stext = stanfordSentence.toString().replace("\r\n" , " ");
				String rtext = referenceSentence.toString();
				if(stext.equals(rtext))
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
