package performance.coref;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import performance.Consts;
import performance.stats.BCubeStats;
import performance.stats.BasicStats;
import performance.stats.CEAFStats;
import performance.stats.Stats;

// @author Axel Clerici

// Cette classe permet de comparer des chaînes de coréférence issues de Stanford NLP
// avec des chaînes de coréférence de références déterminées manuellement

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
	private CEAFStats ceafS;
	private CEAFStats ceafA;
	private BasicStats muc;
	private Properties props;
	
	private List<Map<Integer, CorefChain>> stanfordChains;
	private List<Map<Integer, CorefChain>> referenceChains;
	
	public CorefChainComparator(Properties props)
	{
		this.bcube = new BCubeStats();
		this.ceafS = new CEAFStats();
		this.ceafA = new CEAFStats();
		this.muc = new BasicStats();
		this.props = props;
		
		this.stanfordChains = new ArrayList<>();
		this.referenceChains = new ArrayList<>();
	}
	
	// Permet de réaliser une comparaison MUC sur l'ensemble du corpus
	public Stats compareFiles_MUC() throws IOException, ClassNotFoundException
	{		
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		for(int i = 0; i < corpusFolder.length; i++)
		{
			System.out.println("Evaluation  MUC sur : " + corpusFolder[i].getName());
			Map<Integer, CorefChain> stanfordCorefChain = getStanfordChains(corpusFolder, i);
			Map<Integer, CorefChain> referenceCorefChain = getReferenceChains(corpusFolder, i);
			
			compareFile_MUC(stanfordCorefChain, referenceCorefChain);
		}
		return this.muc;
	}

	// Permet de réaliser une comparaison MUC sur un texte du corpus
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
				// On s'intéresse ici aux liens entre mentions :
				// on récupère donc une mention et sa suivante dans les références,
				// et on cherche à les retrouver côté stanford
				CorefMention currentReferenceMention = referenceMentions.get(i);
				CorefMention nextReferenceMention = referenceMentions.get(i + 1);
				
				CorefMention currentStanfordEquivalent = findMentionInChains(currentReferenceMention, stanfordCorefChains);
				CorefMention nextStanfordEquivalent = findMentionInChains(nextReferenceMention, stanfordCorefChains);
				
				// Si on les retrouve côté Stanford, on vérifie qu'elles appartiennent à la même chaîne de coréférence
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
	
	// Permet de réaliser une comparaison BCUBE sur l'ensemble du corpus
	public Stats compareFiles_BCUBE() throws IOException, ClassNotFoundException
	{		
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		for(int i = 0; i < corpusFolder.length; i++)
		{
			System.out.println("Evaluation BCUBE sur : " + corpusFolder[i].getName());
			Map<Integer, CorefChain> stanfordCorefChains = getStanfordChains(corpusFolder, i);
			Map<Integer, CorefChain> referenceCorefChains = getReferenceChains(corpusFolder, i);
			compareFile_BCUBE(stanfordCorefChains, referenceCorefChains);
		}
		return this.bcube;
	}
	
	// Permet de réaliser une comparaison BCUBE sur un texte du corpus
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
	
	// Fonction qui met à jour la précision ou le rappel pour les stats de bcube
	// chain est une chaîne de coréférence et corefChains est l'ensemble des chaînes du côté opposé de celui de chain
	// par exemple, chain est une chaîne de Stanford, alors corefChains est l'ensemble des chaînes de Référence
	private void updateBCUBE(CorefChain chain, Map<Integer, CorefChain> corefChains, Stat stat)
	{
		// Dans un premier temps, on cherche l'équivalent des mentions de chain dans corefChains
		// et on consigne les ID des clusters dans un tableau. Si une mention n'existe pas dans corefChains
		// on consigne -1 dans le tableau.
		List<CorefMention> mentions = chain.getMentionsInTextualOrder();
		int refEqChains[] = new int[countMentions(chain)];
		for(int i = 0; i < mentions.size(); i ++)
		{
			CorefMention eqMention = findMentionInChains(mentions.get(i), corefChains);
			if(eqMention != null)
				refEqChains[i] = eqMention.corefClusterID;
			else
			{
				refEqChains[i] = -1;
			}
		}
		
		// Ensuite, on fait la moyenne pour chaque mentions du nombre de mentions appartenant à la même chaîne
		// (ID contenus dans le tableau).
		for(int i = 0; i < refEqChains.length; i ++)
		{
			int count = 0;
			int refEqChain = refEqChains[i];
			// cas où la mention n'existe pas de l'autre côté
			if(refEqChain == -1)
			{
				float x = ((float)0);
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
	
	// Fonctions permettant de réaliser une comparaison CEAF sur l'ensemble du corpus
	public Stats compareFiles_CEAF(Similarity function) throws IOException, ClassNotFoundException
	{		
		File[] corpusFolder = new File(Consts.CORPUS_PATH).listFiles();

		for(int i = 0; i < corpusFolder.length; i++)
		{
			System.out.println("Evaluation CEAF " + function + " sur : " + corpusFolder[i].getName());
			Map<Integer, CorefChain> stanfordCorefChains = getStanfordChains(corpusFolder, i);
			Map<Integer, CorefChain> referenceCorefChains = getReferenceChains(corpusFolder, i);
			compareFile_CEAF(stanfordCorefChains, referenceCorefChains, function);
		}
		if(function == Similarity.SIMPLE)
			return this.ceafS;
		else
			return this.ceafA;
	}
	
	// Permet de réaliser une comparaison CEAF sur un fichier du corpus
	private void compareFile_CEAF(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains, Similarity function) 
	{
		// Map<Reference, Stanford>
		Map<Integer, Integer> mapping = getMapping(stanfordCorefChains, referenceCorefChains, function);
		float simSum = getSimilaritySum(mapping, function, stanfordCorefChains, referenceCorefChains);
		updateCEAF(simSum, referenceCorefChains, stanfordCorefChains, function);
	}

	private void updateCEAF(float simSum, Map<Integer, CorefChain> referenceCorefChains, Map<Integer, CorefChain> stanfordCorefChains, Similarity function) 
	{
		if(function == Similarity.SIMPLE)
		{
			int nbrReferenceMentions = countMentions(referenceCorefChains);
			int nbrStanfordMentions = countMentions(stanfordCorefChains);
			ceafS.updatePrecision((float)nbrStanfordMentions);
			ceafS.updateRecall((float)nbrReferenceMentions);
			ceafS.updateSimilarity(simSum);
		}
		else
		{
			int nbrReferenceChains = referenceCorefChains.size();
			int nbrStanfordChains = stanfordCorefChains.size();
			ceafA.updatePrecision((float)nbrStanfordChains);
			ceafA.updateRecall((float)nbrReferenceChains);
			ceafA.updateSimilarity(simSum);
		}
	}

	// Retourne la somme des similarités pour l'ensemble des chaînes mappées dans mapping
	private float getSimilaritySum(Map<Integer, Integer> mapping, Similarity function, Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains) 
	{
		float result = 0;
		for(Integer key : mapping.keySet())
		{
			CorefChain referenceChain = referenceCorefChains.get(key);
			CorefChain stanfordChain = stanfordCorefChains.get(mapping.get(key));
			if(function == Similarity.SIMPLE)
			{
				//System.out.println("map => " + getSimpleSimilarityScore(referenceChain, stanfordChain) + " ref : " + referenceChain + " stan : " + stanfordChain);
				result += (float) getSimpleSimilarityScore(referenceChain, stanfordChain);
			}
			else
			{
				result += getAdvancedSimilarityScore(referenceChain, stanfordChain);
				//System.out.println("map => " + getAdvancedSimilarityScore(referenceChain, stanfordChain) + " ref : " + referenceChain + " stan : " + stanfordChain);
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
		
	// compte le nombre de mentions dans un ensemble de chaînes de corédérence
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
	
	// compte le nombre de mentions dans une chaîne de coréférence
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

	
	private boolean compareMentions(CorefMention mention1, CorefMention mention2)
	{
		// Dans ce cas, les "intersections" de mentions sont considérées comme erreurs en totalité
		if(mention1.startIndex == mention2.startIndex && mention1.sentNum == mention2.sentNum && mention1.endIndex == mention2.endIndex)
			return true;
		else
			return false;
	}
	
	// Permet de créer une map dont les entrées sont : une clé d'une chaîne de référence et une clé d'une chaîne de Stanford
	// Le regroupement est fait en maximisant leur score de similarité. Une chaîne ne peut être associée qu'à une seule autre chaîne
	private Map<Integer, Integer> getMapping(Map<Integer, CorefChain> stanfordCorefChains, Map<Integer, CorefChain> referenceCorefChains, Similarity function)
	{
		Map<Integer, Integer> mapping = new HashMap<>();
		Map<Integer, Map<Integer, Float>> allSimilarities = getAllSimilarities(stanfordCorefChains, referenceCorefChains, function);

		for(Integer key : allSimilarities.keySet())
		{
			Map<Integer, Float> entry = MapUtils.sortByValue(allSimilarities.get(key));
			allSimilarities.put(key, entry);
		}
		
		for(Integer key : referenceCorefChains.keySet())
			mapping.put(key, getBestMatch(key, allSimilarities, mapping));
		
		return mapping;
	}

	private Integer getBestMatch(Integer key, Map<Integer, Map<Integer, Float>> allSimilarities, Map<Integer, Integer> mapping) 	
	{
		Integer bestMatch = null;
		Map<Integer, Float> similarities = allSimilarities.get(key);
		for(Integer key2 : similarities.keySet())
		{
			if(similarities.get(key2) == 0)
				break;
			
			if(!mapping.containsValue(key2) && !betterMatch(key2, similarities.get(key2), allSimilarities))
			{
				bestMatch = key2;
				break;
			}
			
		}
		return bestMatch;
	}

	private boolean betterMatch(Integer key2, Float score, Map<Integer, Map<Integer, Float>> allSimilarities) 
	{
		for(Integer i : allSimilarities.keySet())
		{
			for(Integer j : allSimilarities.get(i).keySet())
			{
				if(j == key2 && allSimilarities.get(i).get(j) > score)
					return true;
			}
		}
		return false;
	}

	private Map<Integer, Map<Integer, Float>> getAllSimilarities(Map<Integer, CorefChain> stanfordCorefChains,
			Map<Integer, CorefChain> referenceCorefChains, Similarity function) 
	{
		Map<Integer, Map<Integer, Float>> allSimilarities = new HashMap<>();
		for(Integer key : referenceCorefChains.keySet())
		{
			Map<Integer, Float> similarities = new HashMap<>();
			for(Integer key2 : stanfordCorefChains.keySet())
			{

				float similarity;
				if(function == Similarity.SIMPLE)
					similarity = getSimpleSimilarityScore(referenceCorefChains.get(key), stanfordCorefChains.get(key2));
				else
					similarity = getAdvancedSimilarityScore(referenceCorefChains.get(key), stanfordCorefChains.get(key2));
				
				similarities.put(key2, similarity);
			}
			allSimilarities.put(key, similarities);
		}
		return allSimilarities;
	}

	// Permet de compter le nombre de liens entre les mentions.
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
	
	// Fonction de test, permet d'afficher les chaînes de coréférence
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
	
	private Map<Integer, CorefChain> getStanfordChains(File[] corpusFolder, int i) throws ClassNotFoundException, IOException 
	{
		if(this.stanfordChains.size() != corpusFolder.length)
		{
			this.stanfordChains.add(CorefUtils.getStanfordCorefChains(corpusFolder[i], props));
		}
		return stanfordChains.get(i);
	}
	
	private Map<Integer, CorefChain> getReferenceChains(File[] corpusFolder, int i) throws ClassNotFoundException, IOException 
	{
		if(this.referenceChains.size() != corpusFolder.length)
		{
			this.referenceChains.add(CorefUtils.getCustomCorefChains(corpusFolder[i]));
		}
		return referenceChains.get(i);
	}
}
