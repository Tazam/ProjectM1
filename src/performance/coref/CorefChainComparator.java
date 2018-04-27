package performance.coref;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import performance.BasicStats;
import performance.Consts;
import performance.Stats;
import performance.ssplit.SsplitUtils;

public class CorefChainComparator 
{
	private enum Stat
	{
		RECALL,
		PRECISION;
	}
	
	private BCubeStats bcube;
	private CEAFStats ceaf;
	private BasicStats muc;
	private Properties props;
	
	public CorefChainComparator(Properties props)
	{
		this.bcube = new BCubeStats();
		this.ceaf = new CEAFStats();
		this.muc = new BasicStats();
		this.props = props;
	}
	
	public Stats compareFiles_BCUBE() throws IOException, ClassNotFoundException
	{		
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		// Je n'ai pas encore annoté la main les autres fichiers
		for(int i = 0; i < 1/*referenceFolder.length*/; i++)
		{
			Map<Integer, CorefChain> stanfordCorefChains = CorefUtils.getStanfordCorefChains(corpusFolder[i], props);
			Map<Integer, CorefChain> referenceCorefChains = CorefUtils.getCustomCorefChains(corpusFolder[i]);
			compareFile_BCUBE(stanfordCorefChains, referenceCorefChains);
		}
		return this.bcube;
	}
	
	private void compareFile_BCUBE(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains) throws IOException
	{
		this.bcube.updateMentions(countMentions(stanfordCorefChains), countMentions(referenceCorefChains));
		
		// Calcul de la précision
		for(Integer key : stanfordCorefChains.keySet())
		{
			CorefChain stanfordChain = stanfordCorefChains.get(key);
			updateBCUBE(stanfordChain, referenceCorefChains, Stat.PRECISION);
		}
		
		// Calcul du Rappel
		for(Integer key : referenceCorefChains.keySet())
		{
			CorefChain referenceChain = referenceCorefChains.get(key);
			updateBCUBE(referenceChain, stanfordCorefChains, Stat.RECALL);
		}
		
	}
	
	public Stats compareFiles_CEAF() throws IOException
	{		
		return null;
	}
	
	private void compareFile_CEAF() throws IOException
	{
		
	}
	
	public Stats compareFiles_MUC() throws IOException, ClassNotFoundException
	{		
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		// Je n'ai pas encore annoté la main les autres fichiers
		for(int i = 0; i < 1/*referenceFolder.length*/; i++)
		{
			System.out.println("Evaluation sur : " + corpusFolder[i].getName());
			Map<Integer, CorefChain> stanfordCorefChains = CorefUtils.getStanfordCorefChains(corpusFolder[i], props);
			Map<Integer, CorefChain> referenceCorefChains = CorefUtils.getCustomCorefChains(corpusFolder[i]);
			compareFile_MUC(stanfordCorefChains, referenceCorefChains);
		}
		return this.muc;
	}
	
	//TODO a modifier la détection des TP :
	// pour l'instant deux mentions se suivent chez Stanford et chez réf (algo ref mais problèmatique)
	// objectif : deux mentions se suivent dans ref, on vérifie que chez stanford elles soient dans la même chaine
	// idée => findMentionsInChains ne renvoie plus un tableau, on prend l'équivalent de i et i+1 ref, ces équivalents doivent 
	// appartenir à la même chaîne.
	private void compareFile_MUC(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains) throws IOException
	{
		int tp = 0;
		int fp = 0;
		int fn = 0;
		
		int nbrStanfordMentions = countMentions(stanfordCorefChains);
		int nbrReferenceMentions = countMentions(referenceCorefChains);
		
		for(Integer key : referenceCorefChains.keySet())
		{
			CorefChain referenceChain = referenceCorefChains.get(key);
			List<CorefMention> referenceMentions = referenceChain.getMentionsInTextualOrder();
			for(int i = 0; i < referenceMentions.size() - 1; i ++)
			{
				// on récupère la mention actuelle et la suivante
				CorefMention currentReferenceMention = referenceMentions.get(i);
				CorefMention nextReferenceMention = referenceMentions.get(i + 1);
				
				CorefMention[] stanfordEquivalent = findMentionInChains(currentReferenceMention, stanfordCorefChains);
				
				if(stanfordEquivalent[1].headIndex == nextReferenceMention.headIndex &&
						stanfordEquivalent[1].sentNum == nextReferenceMention.sentNum)
					tp ++;
			}
		}
		
		fp = nbrStanfordMentions - tp;
		fn = nbrReferenceMentions - tp;
		
		muc.updateStats(tp, fp, fn);
	}
	
	private int countMentions(Map<Integer, CorefChain> corefChains) 
	{
		int count = 0;
		for(Integer key : corefChains.keySet())
		{
			CorefChain corefChain = corefChains.get(key);
			count += countMentions(corefChain);
		}
		return count;
	}
	
	private int countMentions(CorefChain corefChain)
	{
		int count = 0;
		
		List<CorefMention> referenceMentions = corefChain.getMentionsInTextualOrder();
		for(int i = 0; i < referenceMentions.size(); i ++)
			count += 1;
		
		return count;
	}

	// Parcours une map de chaînes de coref pour retrouver une mention
	// Renvoie la mention trouvée et la suivante
	// Dans la pratique, elle sert à retrouver une mention de stanford dans les chaînes de références ou vice versa
	private CorefMention[] findMentionInChains(CorefMention mention, Map<Integer, CorefChain> corefChains) 
	{
		CorefMention[] result = new CorefMention[2];
		result[0] = null;
		result[1] = null;
		
		for(Integer key : corefChains.keySet())
		{
			CorefChain stanfordChain = corefChains.get(key);
			List<CorefMention> stanfordMentions = stanfordChain.getMentionsInTextualOrder();
			for(int i = 0; i < stanfordMentions.size(); i ++)
			{
				CorefMention currentStanfordMention = stanfordMentions.get(i);
				if(mention.headIndex == currentStanfordMention.headIndex 
						&& mention.sentNum == currentStanfordMention.sentNum)
				{
					result[0] = currentStanfordMention;
					if(i + 1 < stanfordMentions.size())
						result[1] = stanfordMentions.get(i + 1);
					else
						result[1] = null;
				}
			}
		}
		
		return result;
	}

	public int getAvgFMeasure()
	{
		return 0;
	}
	
	private void updateBCUBE(CorefChain chain, Map<Integer, CorefChain> corefChains, Stat stat)
	{
		List<CorefMention> stanfordMentions = chain.getMentionsInTextualOrder();
		int refEqChains[] = new int[countMentions(chain)];
		for(int i = 0; i < stanfordMentions.size(); i ++)
		{
			CorefMention eqMention = findMentionInChains(stanfordMentions.get(i), corefChains)[0];
			if(eqMention != null)
				refEqChains[i] = eqMention.corefClusterID;
			else
				refEqChains[i] = -1;
		}
		for(int i = 0; i < refEqChains.length; i ++)
		{
			int count = 0;
			int refEqChain = refEqChains[i];
			if(refEqChain == -1)
			{
				float x = ((float)1)/((float)refEqChains.length);
				if(stat == Stat.PRECISION)
					this.bcube.updatePrecision(x);
				else
					this.bcube.updateRecall(x);
			}
			else
			{
				for(int j = 0; j < refEqChains.length; j ++)
				{
					if(refEqChains[j] == refEqChain)
						count ++;
				}
				float x = ((float)count)/((float)refEqChains.length);
				if(stat == Stat.PRECISION)
					this.bcube.updatePrecision(x);
				else
					this.bcube.updateRecall(x);
			}
		}
	}
	
}
