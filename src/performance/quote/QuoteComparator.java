package performance.quote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import performance.Consts;
import performance.ssplit.SsplitUtils;
import performance.stats.BasicStats;
import test.performance.AnnotationHelper;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.util.Pair;

public class QuoteComparator {

	List<Pair<Integer, Integer>> quoteStanford = new ArrayList();
	List<Pair<Integer, Integer>> quoteFile = new ArrayList();
	
	private BasicStats stats;
	private Properties props;
	
	public QuoteComparator()
	{
		stats = new BasicStats();
		this.props = null;
	}
	
	public BasicStats getStatsQuote(){
		return this.stats;
	}
	
	public QuoteComparator(Properties props)
	{
		stats = new BasicStats();
		this.props = props;
	}
	
	public BasicStats compareFiles() throws IOException
	{			
		//List<Pair<Integer, Integer>> stanfordQuotes = QuoteUtils.getStanfordQuotes(new File("corpus/Coraline.txt"));
		//List<Pair<Integer, Integer>> referenceQuotes = QuoteUtils.annotationLoader(new File("performance/quote/TextBaseCoraline.xml"));
		List<Pair<Integer, Integer>> stanfordQuotes = QuoteUtils.getStanfordQuotes(new File("corpus/Hp.txt"));
		List<Pair<Integer, Integer>> referenceQuotes = QuoteUtils.annotationLoader(new File("performance/quote/TextBaseHp.xml"));
		//List<Pair<Integer, Integer>> stanfordQuotes = QuoteUtils.getStanfordQuotes(new File("corpus/Coraline2.txt"));
		//List<Pair<Integer, Integer>> referenceQuotes = QuoteUtils.annotationLoader(new File("performance/quote/TextBaseCoraline2.xml"));

		//List<Pair<Integer, Integer>> stanfordQuotes = QuoteUtils.getStanfordQuotes(new File("corpus/Oz.txt"));
		//List<Pair<Integer, Integer>> referenceQuotes = QuoteUtils.annotationLoader(new File("performance/quote/TextBaseOz.xml"));

				
		compareFile(stanfordQuotes, referenceQuotes);
		displayQuotes(stanfordQuotes);
		System.out.println("-----");
		displayQuotes(referenceQuotes);
		return this.stats;
	}
	
	public void compareFile(List<Pair<Integer, Integer>> stanfordQuotes, List<Pair<Integer, Integer>> referenceQuotes) throws IOException
	{
		int tp = 0;
		int fp = 0;
		int fn = 0;
		
		for(Pair<Integer, Integer> stanfordQuote : stanfordQuotes)
		{
			for(Pair<Integer, Integer> referenceSentence : referenceQuotes)
			{
				if(stanfordQuote.equals(referenceSentence))
				{
					tp ++;
					break;
				}
			}
		}

		// On retrouve faux positif et faux négatif à partir du nombre final de vrai positif
		fp = stanfordQuotes.size() - tp;
		fn = referenceQuotes.size() - tp;
		// on met à jour le compte
		stats.updateStats(tp, fp, fn);
	}
	
	private void displayQuotes(List<Pair<Integer, Integer>> quotes)
	{
		for(Pair<Integer, Integer> quote : quotes)
			System.out.println(quote.toString());
	}
}
