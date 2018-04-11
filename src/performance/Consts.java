package performance;

import java.io.File;
import java.util.Properties;

public final class Consts 
{
	public static final String CORPUS_PATH = "corpus";
	private static final String STANFORD_PATH = "performance" + File.separator + "stanford";
	private static final String REFERENCE_PATH = "performance" + File.separator + "reference";
	public static final String RESULT_EXTENSION = "_stanford.txt";
	public static final String REFERENCE_EXTENSION = "_reference.txt";
	public static final String FORMAT = "UTF-8";
	
	public static final String STANFORD_SSPLIT_PATH = STANFORD_PATH + File.separator + "ssplit";
	public static final String REFERENCE_SSPLIT_PATH = REFERENCE_PATH + File.separator + "ssplit";
	public static final String SSPLIT_DEFAULT_PROPS = "tokenize, ssplit";
	
	public static final String STANFORD_COREF_PATH = STANFORD_PATH + File.separator + "coref";
	public static final String REFERENCE_REFERENCE_PATH = REFERENCE_PATH + File.separator + "coref";
	public static final String COREF_DEFAULT_PROPS = "tokenize,ssplit,pos,lemma,ner,parse,depparse, coref";
}
