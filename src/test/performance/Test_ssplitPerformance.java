package test.performance;

import java.io.IOException;

import performance.BasicStats;
import performance.ssplit.CoreSentencesComparator;

public class Test_ssplitPerformance 
{
	public static void main(String[] args) throws IOException
	{
		CoreSentencesComparator comparator = new CoreSentencesComparator();
		BasicStats stats = comparator.compareFiles();
		
		System.out.println(stats.toString());
		System.out.println("Precision : " + stats.getPrecision());
		System.out.println("Recall : " + stats.getRecall());
		System.out.println("f-mesure : " + stats.getFMesure());
	}
}
