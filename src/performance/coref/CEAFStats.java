package performance.coref;

import performance.Stats;

public class CEAFStats implements Stats
{

	@Override
	public float getRecall() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getPrecision() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFMeasure() 
	{
		float p = getPrecision();
		float r = getRecall();
		return (2 * ((p * r)/(p + r)));
	}

}
