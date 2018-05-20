/**
 * 
 */
package performance.stats;

/**
 * @author Schmidt Gaëtan
 *
 */
public class NerStats {

	float truePositives;
	float falsePositives;
	float falseNegatives;
	float slotErrorRate;
	public NerStats()
	{
		this.truePositives = 0;
		this.falsePositives = 0;
		this.falseNegatives = 0;
		this.slotErrorRate = 0;
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
	
	public float getFMeasure(float b)
	{
		float p = getPrecision();
		float r = getRecall();
		return ((1+b*b) * (p * r))/((b*b*p) + r);
	}
	
	public float getSER()
	{
		return this.slotErrorRate; 
	}
	
	public String toString()
	{
		String result = "[True Positives : ";
		result += this.truePositives + "; False Positives : " + this.falsePositives + "; False Negatives :";
		result += this.falseNegatives + "]";
		return result;
	}

}
