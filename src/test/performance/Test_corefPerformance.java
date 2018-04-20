package test.performance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.pipeline.Annotation;
import performance.coref.CorefUtils;

public class Test_corefPerformance 
{
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		//String path = "performance\\coref\\reference_reference.xml";
		//File file = new File(path);
		
		String path = "corpus\\reference.txt";
		File file = new File(path);
		
		Map<Integer, CorefChain> corefChains = CorefUtils.getCustomCorefChains(file);
		System.out.println(corefChains);
	}
}
