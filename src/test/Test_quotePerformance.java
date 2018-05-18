package test;

import java.io.IOException;

import performance.quote.QuoteComparator;
import performance.quote.QuoteUtils;
import performance.stats.BasicStats;

public class Test_quotePerformance {
	public static void main(String[] args) throws IOException {
		QuoteComparator cp = new QuoteComparator();
		cp.compareFiles();
		BasicStats stats = new BasicStats();
		System.out.println(cp.getStatsQuote().toString());
	}
}
