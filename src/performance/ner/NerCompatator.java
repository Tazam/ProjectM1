/**
 * 
 */
package performance.ner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.Pair;
import performance.stats.NerStats;

/**
 * @author Schmidt Gaëtan
 *
 */
public class NerCompatator {
	
	

	/**
	 * @author Schmidt Gaëtan
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static NerStats compare(String filePath) throws ClassNotFoundException, IOException 
	{
		
		File file = new File(filePath);
		NerStats stats = new NerStats();
		
		Annotation annotation = NERUtils.getCleanAnnotation(file);
		Annotation annotationStanford = NERUtils.getOriginalAnnotation(file);
		
		CoreDocument document = new CoreDocument(annotation);
		CoreDocument documentStanford = new CoreDocument(annotationStanford);
		
		int pos = 0;
		for (CoreLabel token : document.tokens())
		{
			if ("PERSON".equals(token.ner()))
			{
				if ("PERSON".equals(documentStanford.tokens().get(pos).ner()))
				{
					stats.updateStats(1, 0, 0); // une personne est détécté comme une persone = vraix positif
				}else
				{
					stats.updateStats(0, 0, 1); // une personne n'est pas détécté = faux négatif
				}
			}else
			{
				if ("PERSON".equals(documentStanford.tokens().get(pos).ner()))
				{
					stats.updateStats(0, 1, 0); // un objet détécté comme personne = faux positif
				}
			}
			pos++;
		}
		return stats;

	}
	
	public void test(File file) throws IOException
	{
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    FileInputStream is = new FileInputStream(file);     
		String content = IOUtils.toString(is, "UTF-8");
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);
		int p=0;
		for (CoreSentence token : document.sentences())
		{
			System.out.println(p+" : "+token.text());
			
			p++;
		}
		
	}
	
	public static void main(String[] args) throws IOException
	{
		ArrayList<String> fileList = new ArrayList<String>();
		fileList.add("corpus/bnw_page1.txt");
		fileList.add("corpus/bnw_page112.txt");
		//fileList.add("corpus/bvn_page63.txt");
		fileList.add("corpus/Coraline.txt");
		fileList.add("corpus/Coraline2.txt");
		fileList.add("corpus/dadoes_page18.txt");
		fileList.add("corpus/dadoes_page213.txt");
		fileList.add("corpus/dadoes_page82.txt");
		fileList.add("corpus/Hp.txt");
		fileList.add("corpus/Hp2.txt");
		fileList.add("corpus/hyperion_page203.txt");
		fileList.add("corpus/hyperion_page378.txt");
		fileList.add("corpus/hyperion_page9.txt");
		fileList.add("corpus/ial_page1.txt");
		fileList.add("corpus/ial_page56.txt");
		fileList.add("corpus/ial_page96.txt");
		fileList.add("corpus/Oz.txt");
		fileList.add("corpus/Oz2.txt");
		
		ArrayList<Pair<String,NerStats>> statsList = new ArrayList<Pair<String,NerStats>>();
		
		for (String path : fileList)
		{
			System.out.println("compare ... "+path);
			try {
				statsList.add(new Pair<String, NerStats>(path,NerCompatator.compare(path)));
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
				
			}

			
			
			
		}
		
		FileWriter fw = new FileWriter("performance/ner/nerPerf.csv");
		BufferedWriter buffer = new BufferedWriter(fw);
			
			buffer.write("File;Vrais positifs;Faux positifs;Faux négatif;Precision;Rappel;F-Mesure");
			buffer.newLine();
			
			for (Pair<String,NerStats> p : statsList)
			{
				System.out.println("ecrit ... "+p.first());
				buffer.write(p.first()+";"+p.second().getTP()+";"+p.second().getFP()+";"+p.second().getFN()+";"+p.second().getPrecision()+";"+p.second().getRecall()+";"+p.second().getFMeasure(1));
				buffer.newLine();
			}
			buffer.flush();
			buffer.close();
		
		
	}

}
