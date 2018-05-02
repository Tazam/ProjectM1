package performance.coref;

import performance.Stats;
import performance.coref.CorefChainComparator.Similarity;

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
