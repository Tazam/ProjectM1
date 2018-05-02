package performance;

// @author Axel Clerici

// Une classe qui contient quelques constantes permettant de simplifier
// la recherche des différents fichiers dans l'arborescence du projet

import java.io.File;

public final class Consts 
{
	public static final String CORPUS_PATH = "corpus";
	private static final String PERFORMANCE_PATH = "performance";
	public static final String REFERENCE_EXTENSION = "_reference.txt";
	public static final String XML_REFERENCE_EXTENSION = "_reference.xml";
	public static final String FORMAT = "UTF-8";
	
	public static final String SSPLIT_PATH = PERFORMANCE_PATH + File.separator + "ssplit";
	public static final String COREF_PATH = PERFORMANCE_PATH + File.separator + "coref";
}
