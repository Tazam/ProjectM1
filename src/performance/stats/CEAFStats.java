package performance.stats;

// @author Axel Clerici

// Classe représente les statistiques nécessaires au calcul de la mesure CEAF
// pour Coref. Concrètement, cette mesure est calculée en associant une entité
// de référence à une entité de Stanford en fonction de leur similarité.

public class CEAFStats implements Stats
{
	private float precision;
	private float recall;
	
	public CEAFStats()
	{
		this.precision = 0;
		this.recall = 0;
	}
	
	@Override
	public float getRecall() 
	{
		return this.recall;
	}

	@Override
	public float getPrecision() 
	{
		return this.precision;
	}

	@Override
	public float getFMeasure() 
	{
		float p = getPrecision();
		float r = getRecall();
		return (2 * ((p * r)/(p + r)));
	}

	public void updatePrecision(float p)
	{
		this.precision = p;
	}
	
	public void updateRecall(float r)
	{
		this.recall = r;
	}
}
