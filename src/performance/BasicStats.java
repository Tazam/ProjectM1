package performance;

public class BasicStats implements Stats
{
	float truePositives;
	float falsePositives;
	float falseNegatives;
	
	public BasicStats()
	{
		this.truePositives = 0;
		this.falsePositives = 0;
		this.falseNegatives = 0;
	}
	
	public void updateStats(float truePositives, float falsePositives, float falseNegatives)
	{
		this.truePositives += truePositives;
		this.falsePositives += falsePositives;
		this.falseNegatives += falseNegatives;
	}
	
	public float getRecall()
	{
		return (truePositives/(truePositives + falseNegatives));
	}
	
	public float getPrecision()
	{
		return (truePositives/(truePositives + falsePositives));
	}
	
	public float getFMeasure()
	{
		float p = getPrecision();
		float r = getRecall();
		return (2 * ((p * r)/(p + r)));
	}
	
	public String toString()
	{
		String result = "[True Positives : ";
		result += this.truePositives + "; False Positives : " + this.falsePositives + "; False Negatives :";
		result += this.falseNegatives + "]";
		return result;
	}
}
