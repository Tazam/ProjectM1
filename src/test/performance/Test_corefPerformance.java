package test.performance;

import java.io.IOException;

import performance.coref.CorefFilesBuilder;
import performance.coref.CorefUtils;

public class Test_corefPerformance 
{
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		CorefFilesBuilder builder = new CorefFilesBuilder();
		builder.buildFiles();
	}
}
