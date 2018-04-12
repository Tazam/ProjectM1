package performance.coref;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.DependencyParseAnnotator;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.NERCombinerAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.TokensRegexNERAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.util.CoreMap;
import performance.ssplit.SsplitUtils;
import java.util.Properties;

public class CorefUtils 
{
	private static ParserAnnotator parser = new ParserAnnotator(false, -1);
	private static DependencyParseAnnotator deparser = new DependencyParseAnnotator();
	private static POSTaggerAnnotator pos = new POSTaggerAnnotator();
	private static MorphaAnnotator lemma = new MorphaAnnotator();
	private static NERCombinerAnnotator ner = null;
	
	public static Annotation getInitAnnotation(File file) throws IOException, ClassNotFoundException
	{
		// En attendant :
		if(ner == null)
		{
			ner = new NERCombinerAnnotator(false);
		}
		Annotation annotation = SsplitUtils.getCleanAnnotation(file);
		pos.annotate(annotation);
		lemma.annotate(annotation);
		ner.annotate(annotation);

		/* Vrai contenu de la fonction ! En partant du principe que parse et déparse
		 * sont pas évalués !
		*Annotation annotation = NerUtils.getAnnotationCleaned(file);*/
		parser.annotate(annotation);
		deparser.annotate(annotation);
		return annotation;
	}
}
