package performance.stats;

// @author Axel Clerici

// Classe représente les statistiques nécessaires au calcul de la mesure B^3
// pour Coref.

public class BCubeStats implements Stats
{
	private float precision;
	private float recall;
	private float nbrStanfordMentions;
	private float nbrReferenceMentions;
	
	public BCubeStats()
	{
		this.nbrStanfordMentions = 0;
		this.nbrReferenceMentions = 0;
		this.precision = 0;
		this.recall = 0;
	}
	
	public void updateMentions(int nbrStanfordMentions, int nbrReferenceMentions)
	{
		this.nbrReferenceMentions += nbrReferenceMentions;
		this.nbrStanfordMentions += nbrStanfordMentions;
	}
	
	public void updateRecall(float n)
	{
		recall += n;
	}
	
	public void updatePrecision(float n)
	{
		precision += n;
	}
	
	public float getPrecision()
	{
		return (1/nbrStanfordMentions) * precision;
	}
	
	public float getRecall()
	{
		return (1/nbrReferenceMentions) * recall;
	}

	@Override
	public float getFMeasure() 
	{
		float p = getPrecision();
		float r = getRecall();
		return (2 * ((p * r)/(p + r)));
	}
}
