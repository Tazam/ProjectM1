package performance.ssplit;

import java.util.List;
import java.util.Properties;

import java.io.File;
import java.io.IOException;

import edu.stanford.nlp.pipeline.*;
import performance.Consts;
import performance.ssplit.SsplitUtils;
import performance.stats.BasicStats;
import test.performance.AnnotationHelper;

//@author Axel Clerici

// Cette classe permet de comparer des phrases issues de Stanford NLP
// avec des phrases de références déterminées manuellement
public class CoreSentencesComparator
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
	
	// Fonction générale qui va comparer les phrases de référence à celles de Stanford
	// pour tous les textes du corpus. Elle met à jour des statistiques de base
	// (vrai positif, faux positif, faux négatif, précision, rappel et f-mesure)
	public BasicStats compareFiles() throws IOException
	{			
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		for(int i = 0; i < corpusFolder.length; i++)
		{
			// Pour chaque fichier, on récupère les phrases de références, celles de stanford, et on les compare
			List<CoreSentence> stanfordSentences = SsplitUtils.getStanfordSentences(corpusFolder[i], props);
			List<CoreSentence> referenceSentences = SsplitUtils.getCustomSentences(corpusFolder[i]);
			compareFile(stanfordSentences, referenceSentences);
		}
		return this.stats;
	}
	
	// Comparaison des phrases de référence et de stanford pour un texte du corpus.
	// On considère ici la positision des tokens à laquelle s'effectue la coupure qui sépare deux phrases.
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
				// Une coupure est présente à la même position dans Stanford et dans la référence :
				// on compte un vrai positif
				if(sEnd == rEnd)
				{
					tp ++;
					break;
				}
			}
		}
		// On retrouve faux positif et faux négatif à partir du nombre final de vrai positif
		fp = stanfordSentences.size() - tp;
		fn = referenceSentences.size() - tp;
		// on met à jour le compte
		stats.updateStats(tp, fp, fn);
	}
	
	private void displaySentences(List<CoreSentence> sentences)
	{
		for(int i = 0; i < sentences.size(); i ++)
			System.out.println(i + " " + sentences.get(i));
	}
}
