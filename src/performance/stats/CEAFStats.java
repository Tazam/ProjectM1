package performance.stats;

// @author Axel Clerici

// Classe représente les statistiques nécessaires au calcul de la mesure CEAF
// pour Coref. Concrètement, cette mesure est calculée en associant une entité
// de référence à une entité de Stanford en fonction de leur similarité.

public class CEAFStats implements Stats
{
	private float precision;
	private float recall;
	
	private float similarity;
	private float pDivisor;
	private float rDivisor;
	
	public CEAFStats()
	{
		this.precision = 0;
		this.recall = 0;
		this.similarity = 0;
	}
	
	@Override
	public float getRecall() 
	{
		return similarity/rDivisor;
	}

	@Override
	public float getPrecision() 
	{
		return similarity/pDivisor;
	}

	@Override
	public float getFMeasure() 
	{
		float p = getPrecision();
		float r = getRecall();
		return (2 * ((p * r)/(p + r)));
	}

	public void updatePrecision(float d)
	{
		this.pDivisor += d;
	}
	
	public void updateRecall(float d)
	{
		this.rDivisor += d;
	}
	
	public void updateSimilarity(float s)
	{
		this.similarity += s;
	}
}
