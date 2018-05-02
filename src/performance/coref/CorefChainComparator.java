package performance.coref;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import performance.Consts;
import performance.ssplit.SsplitUtils;
import performance.stats.BCubeStats;
import performance.stats.BasicStats;
import performance.stats.CEAFStats;
import performance.stats.Stats;

public class CorefChainComparator 
{
	private enum Stat
	{
		RECALL,
		PRECISION;
	}
	
	public enum Similarity
	{
		SIMPLE,
		ADVANCED;
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
			//printMentions(stanfordCorefChains, referenceCorefChains);
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
	
	public Stats compareFiles_CEAF(Similarity function) throws IOException, ClassNotFoundException
	{		
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		// Je n'ai pas encore annoté la main les autres fichiers
		for(int i = 0; i < 1/*referenceFolder.length*/; i++)
		{
			Map<Integer, CorefChain> stanfordCorefChains = CorefUtils.getStanfordCorefChains(corpusFolder[i], props);
			Map<Integer, CorefChain> referenceCorefChains = CorefUtils.getCustomCorefChains(corpusFolder[i]);
			compareFile_CEAF(stanfordCorefChains, referenceCorefChains, function);
		}
		return this.ceaf;
	}
	
	private void compareFile_CEAF(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains, Similarity function) 
	{
		// Map<Reference, Stanford>
		Map<Integer, Integer> mapping = getMapping(stanfordCorefChains, referenceCorefChains, function);
		float simSum = getSimilaritySum(mapping, function, stanfordCorefChains, referenceCorefChains);
		updateCEAF(simSum, referenceCorefChains, stanfordCorefChains, function);
	}

	private void updateCEAF(float simSum, Map<Integer, CorefChain> referenceCorefChains, Map<Integer, CorefChain> stanfordCorefChains, Similarity function) 
	{
		float p;
		float r;
		if(function == Similarity.SIMPLE)
		{
			int nbrReferenceMentions = countMentions(referenceCorefChains);
			int nbrStanfordMentions = countMentions(stanfordCorefChains);
			p = simSum /((float)nbrStanfordMentions);
			r = simSum /((float)nbrReferenceMentions);
		}
		else
		{
			int nbrReferenceChains = countChains(referenceCorefChains);
			int nbrStanfordChains = countChains(stanfordCorefChains);
			p = simSum /((float) nbrStanfordChains);
			r = simSum /((float) nbrReferenceChains);
		}
		ceaf.updatePrecision(p);
		ceaf.updateRecall(r);
	}

	private float getSimilaritySum(Map<Integer, Integer> mapping, Similarity function, Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains) 
	{
		float result = 0;
		for(Integer key : mapping.keySet())
		{
			CorefChain referenceChain = referenceCorefChains.get(key);
			CorefChain stanfordChain = stanfordCorefChains.get(mapping.get(key));
			if(function == Similarity.SIMPLE)
			{
				System.out.println("map => " + getSimpleSimilarityScore(referenceChain, stanfordChain) + " ref : " + referenceChain + " stan : " + stanfordChain);
				result += (float) getSimpleSimilarityScore(referenceChain, stanfordChain);
			}
			else
			{
				result += getAdvancedSimilarityScore(referenceChain, stanfordChain);
				System.out.println("map => " + getAdvancedSimilarityScore(referenceChain, stanfordChain) + " ref : " + referenceChain + " stan : " + stanfordChain);
			}

		}
		return result;
	}

	private float getAdvancedSimilarityScore(CorefChain referenceChain, CorefChain stanfordChain) 
	{
		float result = 0;
		if(stanfordChain != null)
		{
			float mentions = (float)(countMentions(referenceChain) + countMentions(stanfordChain));
			result = ((float)(2 * getSimpleSimilarityScore(referenceChain, stanfordChain))) / mentions;
		}
		return result;
	}

	private int getSimpleSimilarityScore(CorefChain referenceChain, CorefChain stanfordChain) 
	{
		int count = 0;
		if(stanfordChain != null)
		{
			List<CorefMention> referenceMentions = referenceChain.getMentionsInTextualOrder();
			List<CorefMention> stanfordMentions = stanfordChain.getMentionsInTextualOrder();
			for(CorefMention referenceMention : referenceMentions)
			{
				for(CorefMention stanfordMention : stanfordMentions)
				{
					if(compareMentions(stanfordMention, referenceMention))
					{
						count ++;
						break;
					}
				}
			}
		}
		return count;
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
			//printMentions(stanfordCorefChains, referenceCorefChains);
			compareFile_MUC(stanfordCorefChains, referenceCorefChains);
		}
		return this.muc;
	}
	

	private void compareFile_MUC(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains) throws IOException
	{
		int tp = 0;
		int fp = 0;
		int fn = 0;
		
		int nbrStanfordLinks = countLinks(stanfordCorefChains);
		int nbrReferenceLinks = countLinks(referenceCorefChains);
		
		for(Integer key : referenceCorefChains.keySet())
		{
			CorefChain referenceChain = referenceCorefChains.get(key);
			List<CorefMention> referenceMentions = referenceChain.getMentionsInTextualOrder();
			for(int i = 0; i < referenceMentions.size() - 1; i ++)
			{
				// on récupère la mention actuelle et la suivante
				CorefMention currentReferenceMention = referenceMentions.get(i);
				CorefMention nextReferenceMention = referenceMentions.get(i + 1);
				
				CorefMention currentStanfordEquivalent = findMentionInChains(currentReferenceMention, stanfordCorefChains);
				CorefMention nextStanfordEquivalent = findMentionInChains(nextReferenceMention, stanfordCorefChains);
				if(currentStanfordEquivalent != null && nextStanfordEquivalent != null)
				{
					if(currentStanfordEquivalent.corefClusterID == nextStanfordEquivalent.corefClusterID)
					{
						tp ++;
					}
				}
			}
		}
		
		fp = nbrStanfordLinks - tp;
		fn = nbrReferenceLinks - tp;
		
		muc.updateStats(tp, fp, fn);
	}
	
	private int countChains(Map<Integer, CorefChain> corefChains)
	{
		int count = 0;
		for(Integer key : corefChains.keySet())
		{
			count += 1;
		}
		return count;
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
	// Dans la pratique, elle sert à retrouver une mention de stanford dans les chaînes de références ou vice versa
	private CorefMention findMentionInChains(CorefMention mention, Map<Integer, CorefChain> corefChains) 
	{
		CorefMention result = null;
		
		for(Integer key : corefChains.keySet())
		{
			CorefChain stanfordChain = corefChains.get(key);
			List<CorefMention> stanfordMentions = stanfordChain.getMentionsInTextualOrder();
			for(int i = 0; i < stanfordMentions.size(); i ++)
			{
				CorefMention currentStanfordMention = stanfordMentions.get(i);
				if(compareMentions(currentStanfordMention, mention))
					result = currentStanfordMention;
			}
		}	
		return result;
	}

	private void updateBCUBE(CorefChain chain, Map<Integer, CorefChain> corefChains, Stat stat)
	{
		List<CorefMention> stanfordMentions = chain.getMentionsInTextualOrder();
		int refEqChains[] = new int[countMentions(chain)];
		for(int i = 0; i < stanfordMentions.size(); i ++)
		{
			CorefMention eqMention = findMentionInChains(stanfordMentions.get(i), corefChains);
			if(eqMention != null)
				refEqChains[i] = eqMention.corefClusterID;
			else
			{
				refEqChains[i] = -1;
			}
		}
		for(int i = 0; i < refEqChains.length; i ++)
		{
			int count = 0;
			int refEqChain = refEqChains[i];
			if(refEqChain == -1)
			{
				float x = ((float)0)/((float)refEqChains.length);
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
	
	private boolean compareMentions(CorefMention mention1, CorefMention mention2)
	{
		// Dans ce cas, les "intersections" de mentions sont considérées comme erreurs en totalité
		if(mention1.startIndex == mention2.startIndex && mention1.sentNum == mention2.sentNum && mention1.endIndex == mention2.endIndex)
			return true;
		else
			return false;
	}
	
	private Map<Integer, Integer> getMapping(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains, Similarity function)
	{
		Map<Integer, Integer> mapping = new HashMap<>();
		for(Integer key : stanfordCorefChains.keySet())
		{
			CorefChain stanfordChain = stanfordCorefChains.get(key);
			float maxSimilarityScore = 0;
			Integer bestKey = null;
			
			for(Integer key2 : referenceCorefChains.keySet())
			{
				float similarityScore = 0;
				CorefChain referenceChain = referenceCorefChains.get(key2);
				if(!mapping.containsValue(key2))
				{
					if(function == Similarity.SIMPLE)
						similarityScore = getSimpleSimilarityScore(referenceChain, stanfordChain);
					else
						similarityScore = getAdvancedSimilarityScore(referenceChain, stanfordChain);
					
					if(similarityScore > maxSimilarityScore)
					{
						maxSimilarityScore = similarityScore;
						bestKey = key2;
					}
				}
			}
			mapping.put(bestKey, key);
		}
		return mapping;
	}
	
	private int countLinks(Map<Integer, CorefChain> corefChains)
	{
		int count = 0;
		for(Integer key : corefChains.keySet())
		{
			CorefChain corefChain = corefChains.get(key);
			count += countMentions(corefChain) -1;
		}
		return count;
	}
	
	private void printMentions(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains)
	{
		for(Integer key : stanfordCorefChains.keySet())
		{
			CorefChain stanfordChain = stanfordCorefChains.get(key);
			List<CorefMention> stanfordMentions = stanfordChain.getMentionsInTextualOrder();
			for(CorefMention stanfordMention : stanfordMentions)
				System.out.println(stanfordMention + " " + stanfordMention.startIndex + " " +stanfordMention.endIndex);
		}
		System.out.print("reference");
		for(Integer key : referenceCorefChains.keySet())
		{
			CorefChain referenceChain = referenceCorefChains.get(key);
			List<CorefMention> referenceMentions = referenceChain.getMentionsInTextualOrder();
			for(CorefMention referenceMention : referenceMentions)
				System.out.println(referenceMention + " " + referenceMention.startIndex + " " +referenceMention.endIndex);
		}
	}
	
	private int[] countChains()
	{
		return null;
	}
}
