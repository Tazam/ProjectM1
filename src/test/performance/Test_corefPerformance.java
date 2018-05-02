package test.performance;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CorefAnnotator;
import performance.Stats;
import performance.coref.CorefChainComparator;
import performance.coref.CorefChainComparator.Similarity;
import performance.coref.CorefUtils;

public class Test_corefPerformance 
{
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		File file = new File("corpus\\reference.txt");
		CorefUtils.textAnnotationHelper(file);
		
		Properties props = new Properties();
		props.setProperty("coref.algorithm", "neural");
		//props.setProperty("coref.algorithm", "statistical");
		CorefChainComparator comparator = new CorefChainComparator(props);
	
		System.out.println("MUC :");
		Stats stats1 = comparator.compareFiles_MUC();
		System.out.println("Precision : " + stats1.getPrecision());
		System.out.println("Recall : " + stats1.getRecall());
		System.out.println("f-mesure : " + stats1.getFMeasure());
		
		System.out.println("BCUBE :");
		Stats stats2 = comparator.compareFiles_BCUBE();
		System.out.println("Precision : " + stats2.getPrecision());
		System.out.println("Recall : " + stats2.getRecall());
		System.out.println("f-mesure : " + stats2.getFMeasure());
		
		System.out.println("CEAF Similarité Simple :");
		Stats stats3 = comparator.compareFiles_CEAF(Similarity.SIMPLE);
		System.out.println("Precision : " + stats3.getPrecision());
		System.out.println("Recall : " + stats3.getRecall());
		System.out.println("f-mesure : " + stats3.getFMeasure());
		
		System.out.println("CEAF Similarité Avancée :");
		Stats stats4 = comparator.compareFiles_CEAF(Similarity.ADVANCED);
		System.out.println("Precision : " + stats4.getPrecision());
		System.out.println("Recall : " + stats4.getRecall());
		System.out.println("f-mesure : " + stats4.getFMeasure());
		
		float avg = stats1.getFMeasure() + stats2.getFMeasure() + stats3.getFMeasure() /*+ stats4.getFMeasure()*/;
		avg /= (float)3;
		
		System.out.println("f-measure average : " + avg);
		
	}
}
