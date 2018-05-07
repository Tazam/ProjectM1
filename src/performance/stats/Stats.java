package performance.stats;

//@author Axel Clerici

// Interface basique qui sera implémentée par les différentes mesures implémentées

public interface Stats 
{
	public float getRecall();
	public float getPrecision();
	public float getFMeasure();
}
