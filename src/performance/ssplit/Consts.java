package performance.ssplit;

import java.io.File;

public final class Consts 
{
	static final String CORPUS_PATH = "corpus";
	private static final String STANFORD_PATH = "performance" + File.separator + "stanford";
	private static final String REFERENCE_PATH = "performance" + File.separator + "reference";
	static final String RESULT_EXTENSION = "_stanford.txt";
	static final String FORMAT = "UTF-8";
	
	static final String STANFORD_SSPLIT_PATH = STANFORD_PATH + File.separator + "ssplit";
	static final String REFERENCE_SSPLIT_PATH = REFERENCE_PATH + File.separator + "ssplit";
}
