package test.performance;

import java.io.IOException;
import java.util.Properties;

import performance.BasicStats;
import performance.ssplit.SentencesFilesBuilder;
import performance.ssplit.CoreSentencesComparator;

public class Test_ssplitPerformance 
{
	public static void main(String[] args) throws IOException
	{
		SentencesFilesBuilder builder = new SentencesFilesBuilder();
		builder.buildFiles();
		
		CoreSentencesComparator comparator = new CoreSentencesComparator();
		BasicStats stats = comparator.compareFiles();
		
		System.out.println(stats.toString());
		System.out.println("Precision : " + stats.getPrecision());
		System.out.println("Recall : " + stats.getRecall());
		System.out.println("f-mesure : " + stats.getFMesure());
	}
}
