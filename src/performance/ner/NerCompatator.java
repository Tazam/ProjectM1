/**
 * 
 */
package performance.ner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import performance.stats.NerStats;

/**
 * @author Schmidt Gaëtan
 *
 */
public class NerCompatator {
	
	private NerStats stats;
	
	public NerCompatator()
	{
		this.stats =  new NerStats();
	}
	

	/**
	 * @author Schmidt Gaëtan
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void compare() throws ClassNotFoundException, IOException 
	{
		
		File file = new File("");
		
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
					this.stats.updateStats(1, 0, 0); // une personne est détécté comme une persone = vraix positif
				}else
				{
					this.stats.updateStats(0, 0, 1); // une personne n'est pas détécté = faux négatif
				}
			}else
			{
				if ("PERSON".equals(documentStanford.tokens().get(pos).ner()))
				{
					this.stats.updateStats(0, 1, 0); // un objet détécté comme personne = faux positif
				}
			}
			pos++;
		}

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
		NerCompatator comp = new NerCompatator();
		File file = new File("corpus/dadoes_page18.txt");    
		comp.test(file);
	}

}
