package performance.ssplit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TokenEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokenBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
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
	

	public static List<Integer> countTokensPerLine(File file)
	{
		List<Integer> tokensPerLine = new ArrayList<>();
		TokenizerAnnotator tokenizer = new TokenizerAnnotator();
	    try 
	    {
	        BufferedReader b = new BufferedReader(new FileReader(file));
	        String readLine = "";
	        while ((readLine = b.readLine()) != null) 
	        {
	        	Annotation annotation = new Annotation(readLine);
	        	tokenizer.annotate(annotation);
	        	tokensPerLine.add(annotation.get(TokensAnnotation.class).size());
	        }
	     } catch (IOException e) {e.printStackTrace();}
	    return tokensPerLine;
	}
	
	public static List<CoreSentence> getCustomSentences(File file) throws IOException
	{
		Annotation annotation = getCleanAnnotation(file);
		return (new CoreDocument(annotation).sentences());
	}
	
	public static List<List<CoreLabel>> splitCoreLabels(List<Integer> tokensPerLine, List<CoreLabel> labels)
	{
		Iterator<CoreLabel> itr = labels.iterator();
		List<List<CoreLabel>> coreLabelsDoc = new ArrayList<>();
		for(Integer tokens : tokensPerLine)
		{
			List<CoreLabel> coreLabelsSent = new ArrayList<>();
			for(int i = 0; i < tokens; i ++)
			{
				coreLabelsSent.add(itr.next());
			}
			coreLabelsDoc.add(coreLabelsSent);
		}
		return coreLabelsDoc;
	}
	
	// Le fichier est un fichier du corpus ! pas un Stanford ni un Référence !
	// Renvoie l'annotation propre pour les annotateurs suivants
	public static Annotation getCleanAnnotation(File file) throws IOException
	{
		Annotation annotation = getInitAnnotation(file);
		
		String fileName = FilenameUtils.removeExtension(file.getName());
		String referencePath = Consts.SSPLIT_PATH + File.separator + fileName + Consts.REFERENCE_EXTENSION;
		
		File referenceFile = new File(referencePath);
		
		List<Integer> tokensPerLine = countTokensPerLine(referenceFile);
		List<List<CoreLabel>> coreLabelsDoc = splitCoreLabels(tokensPerLine, annotation.get(TokensAnnotation.class));
		WordsToSentencesAnnotatorCustom custom = new WordsToSentencesAnnotatorCustom();
		custom.annotateCustom(annotation, coreLabelsDoc);
		return annotation;
	}
	
	public static List<CoreSentence> getStanfordSentences(File file, Properties props) throws IOException
	{
		Annotation annotation = getInitAnnotation(file);
		WordsToSentencesAnnotator ssplit;
		if(props == null)
			ssplit = new WordsToSentencesAnnotator();
		else
			ssplit = new WordsToSentencesAnnotator(props);
		ssplit.annotate(annotation);
		return (new CoreDocument(annotation).sentences());
	}
	
	public static int getEndCharOffsets(CoreSentence sentence)
	{
		return sentence.charOffsets().second;
	}
}
