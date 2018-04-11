package performance.ssplit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.util.CoreMap;
import performance.Consts;

public class SsplitUtils 
{
	// Construit l'annotation telle quelle doit être en entrée de l'annotateur ssplit.
	// Tokenizer n'étant pas évalué, on utilise celui "de base" de Stanford.
	public static Annotation getInitAnnotation(File file) throws IOException
	{
		FileInputStream is = new FileInputStream(file);     
		String content = IOUtils.toString(is, "UTF-8");
		Annotation annotation = new Annotation(content);
		TokenizerAnnotator tokenizer = new TokenizerAnnotator();
		tokenizer.annotate(annotation);
		return annotation;
	}
	
	// Prend un document un document du corpus 
	public static Annotation getAnnotationCleaned(File file) throws IOException
	{	
		Annotation annotation = getInitAnnotation(file);
		String resultName = FilenameUtils.removeExtension(file.getName());
		String resultPath = Consts.REFERENCE_SSPLIT_PATH + File.separator + resultName + Consts.REFERENCE_EXTENSION;
		File referenceFile = new File(resultPath);
		List<CoreMap> sentences = getSentencesFromFile(referenceFile);
		annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
		
		return annotation;
	}
	
	// Prend un document annoté et retourne les phrases qui le constituent
	public static List<CoreMap> getSentencesFromFile(File file) throws IOException
	{
		FileInputStream is = new FileInputStream(file);     
		String content = IOUtils.toString(is, "UTF-8");
		Annotation annotation = new Annotation(content);
		
		Properties propsTokenizer = new Properties();
		//Obligatoire pour pouvoir utiliser newlineIsSentenceBreak
		propsTokenizer.setProperty("tokenize.keepeol", "true");
		propsTokenizer.setProperty("tokenize.whitespace", "true");
		TokenizerAnnotator tokenizer = new TokenizerAnnotator(propsTokenizer);
		tokenizer.annotate(annotation);
		
		Properties propsSsplit = new Properties();
		propsSsplit.setProperty("ssplit.boundaryTokenRegex", "null");
		propsSsplit.setProperty("ssplit.newlineIsSentenceBreak", "always");
		WordsToSentencesAnnotator sentenceSplitter = new WordsToSentencesAnnotator(propsSsplit);
		sentenceSplitter.annotate(annotation);
		
		return annotation.get(SentencesAnnotation.class);
	}
}
