package test.performance;

import java.io.IOException;
import java.util.Properties;

import performance.Stats;
import performance.coref.CorefChainComparator;

public class Test_corefPerformance 
{
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		testMUC();
		testBCUBE();
	}
	
	public static void testMUC() throws ClassNotFoundException, IOException
	{
		CorefChainComparator comparator = new CorefChainComparator(new Properties());
		Stats stats = comparator.compareFiles_MUC();
		
		System.out.println(stats.toString());
		System.out.println("Precision : " + stats.getPrecision());
		System.out.println("Recall : " + stats.getRecall());
		System.out.println("f-mesure : " + stats.getFMeasure());
	}
	
	public static void testBCUBE() throws ClassNotFoundException, IOException
	{
		CorefChainComparator comparator = new CorefChainComparator(new Properties());
		Stats stats = comparator.compareFiles_BCUBE();
		
		System.out.println("Precision : " + stats.getPrecision());
		System.out.println("Recall : " + stats.getRecall());
		System.out.println("f-mesure : " + stats.getFMeasure());
		
	}
}
