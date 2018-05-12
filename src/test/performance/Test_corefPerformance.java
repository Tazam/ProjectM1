package test.performance;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import performance.coref.CorefChainComparator;
import performance.coref.CorefChainComparator.Similarity;
import performance.stats.Stats;
import performance.coref.CorefUtils;

public class Test_corefPerformance 
{
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		Properties props = new Properties();
		props.setProperty("coref.algorithm", "neural");
		props.setProperty("coref.maxMentionDistance", "100");
		//props.setProperty("coref.algorithm", "statistical");
		CorefChainComparator comparator = new CorefChainComparator(props);
		
		File file = new File("corpus\\dadoes_page82.txt");
		AnnotationHelper.textAnnotationHelper(file);
	
		/*Stats stats1 = comparator.compareFiles_MUC();
		Stats stats2 = comparator.compareFiles_BCUBE();
		Stats stats3 = comparator.compareFiles_CEAF(Similarity.SIMPLE);
		Stats stats4 = comparator.compareFiles_CEAF(Similarity.ADVANCED);
		float avg = stats1.getFMeasure() + stats2.getFMeasure() + stats3.getFMeasure() + stats4.getFMeasure();
		avg /= (float)4;
		
		System.out.println("MUC :");
		System.out.println("Precision : " + stats1.getPrecision());
		System.out.println("Recall : " + stats1.getRecall());
		System.out.println("f-mesure : " + stats1.getFMeasure());
		
		System.out.println("BCUBE :");
		System.out.println("Precision : " + stats2.getPrecision());
		System.out.println("Recall : " + stats2.getRecall());
		System.out.println("f-mesure : " + stats2.getFMeasure());
		
		System.out.println("CEAF Similarité Simple :");
		System.out.println("Precision : " + stats3.getPrecision());
		System.out.println("Recall : " + stats3.getRecall());
		System.out.println("f-mesure : " + stats3.getFMeasure());
		
		System.out.println("CEAF Similarité Avancée :");
		System.out.println("Precision : " + stats4.getPrecision());
		System.out.println("Recall : " + stats4.getRecall());
		System.out.println("f-mesure : " + stats4.getFMeasure());
				
		System.out.println("f-measure average : " + avg);*/
		
	}
}
