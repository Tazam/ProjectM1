package test;

import java.io.IOException;

import performance.ssplit.SentencesFilesBuilder;

public class Test_ssplitPerformance 
{
	public static void main(String[] args) throws IOException
	{
		SentencesFilesBuilder builder = new SentencesFilesBuilder();
		builder.buildSentencesFiles();
	}
}
