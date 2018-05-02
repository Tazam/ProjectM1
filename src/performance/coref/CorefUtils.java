package performance.coref;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.CorefCluster;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CorefAnnotator;
import edu.stanford.nlp.pipeline.DependencyParseAnnotator;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.NERCombinerAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.TokensRegexNERAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.IntTuple;
import performance.Consts;
import performance.coref.customannotators.CorefAnnotatorCustom;
import performance.ssplit.SsplitUtils;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CorefUtils 
{
	private static boolean initAnnotators = true;
	
	private static POSTaggerAnnotator pos = null;
	private static MorphaAnnotator lemma = null;
	private static NERCombinerAnnotator ner = null;
	private static ParserAnnotator parser = null;
	private static DependencyParseAnnotator deparser = null;
	private static CorefAnnotator coref = null;
	private static CorefAnnotatorCustom corefCustom = null;
	
	public static Annotation getInitAnnotation(File file) throws IOException, ClassNotFoundException
	{
		// En attendant :
		if(initAnnotators == true)
		{
			initAnnotators = false;
			
			pos = new POSTaggerAnnotator();
			lemma = new MorphaAnnotator();
			ner = new NERCombinerAnnotator(false);
			parser = new ParserAnnotator(false, -1);
			deparser = new DependencyParseAnnotator();
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
	
	// Pour m'aider à annoter manuellement, sera supprimée plus tard
	public static void textAnnotationHelper(File file) throws IOException
	{
		Annotation annotation = SsplitUtils.getCleanAnnotation(file);
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		for(CoreMap sentence : sentences)
		{
			System.out.println(sentence.get(TokensAnnotation.class));
		}
	}
	
	// renvoie une liste de listes de mentions. une sous-liste == une entité
	private static List<List<Mention>> getMentionsFromFile(File file, Annotation annotation) throws ClassNotFoundException, IOException
	{
    	String fileName = FilenameUtils.removeExtension(file.getName());
		String referencePath = Consts.COREF_PATH + File.separator + fileName + Consts.XML_REFERENCE_EXTENSION;
		
		File referenceFile = new File(referencePath);
		
		List<List<Mention>> result = new ArrayList<>();
	    try
		{
			int id = 0;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document doc = builder.parse(referenceFile);
	    	 
	    	NodeList entities = doc.getElementsByTagName("entity");
	    	 for(int i = 0; i < entities.getLength(); i ++)
	    	 {
	    		 if (entities.item(i).getNodeType() == Node.ELEMENT_NODE) 
	    		 {
	    			 Element entity = (Element) entities.item(i);
	    			 
	    			 NodeList mentions = entity.getElementsByTagName("mention");
		    		 List<Mention> mentionList = new ArrayList<>();
	    			 for(int j = 0; j < mentions.getLength(); j ++)
	    			 {
	    				 if(mentions.item(j).getNodeType() == Node.ELEMENT_NODE)
	    				 {
	    					 Element mention = (Element) mentions.item(j);
	    					 int startIndex = Integer.parseInt(mention.getElementsByTagName("startIndex").item(0).getTextContent());
	    					 int endIndex = Integer.parseInt(mention.getElementsByTagName("endIndex").item(0).getTextContent());
	    					 int sent = Integer.parseInt(mention.getElementsByTagName("sent").item(0).getTextContent());
	    					 
	    					 Mention newMention = buildMention(id, startIndex, endIndex, sent, annotation, i);
	    					 
	    					 mentionList.add(newMention);
	    					 id ++;
	    				 }
	    			 }
	    			 result.add(mentionList);
	    		 }
	    	 }

		}	catch (final ParserConfigurationException e) {e.printStackTrace();}
	    	catch (final SAXException e) {e.printStackTrace();}
        	catch (final IOException e) {e.printStackTrace();}		
		return result;
	}
	
	private static Map<Integer, CorefChain> buildCorefChains(List<List<CorefMention>> corefMentions)
	{
		Map<Integer, CorefChain> corefChains = new HashMap<>();
		for(int i = 0; i < corefMentions.size(); i ++)
		{
			// on ne garde pas les singletons
			//TODO dans ce cas là, il n'est pas utile de les annoter manuellement, on gagne du temps
			// car leur présence n'influe pas sur la performance

			Map<IntPair, Set<CorefMention>> map = new HashMap<>();
			CorefMention representative = null;
			if(corefMentions.get(i).size() > 1)
			{
				for(int j = 0; j < corefMentions.get(i).size(); j ++) 
				{
					CorefMention m = corefMentions.get(i).get(j); 
					Set<CorefMention> set = new HashSet<>();
					set.add(m);
				
					IntPair pair = new IntPair(m.sentNum, m.startIndex);
					map.put(pair, set);
				}
				// representative devra être la première entité référencée dans le fichier xml
				representative = corefMentions.get(i).get(0);
				CorefChain chain = new CorefChain(representative.corefClusterID, map, representative);
				corefChains.put(representative.corefClusterID, chain);
			}
		}
		return corefChains;
	}
	
	
	public static Map<Integer, CorefChain> getCustomCorefChains(File file) throws ClassNotFoundException, IOException
	{
		Annotation annotation = getCleanAnnotation(file);
		Map<Integer, CorefChain> corefChains = new CoreDocument(annotation).corefChains();
		// il n'est pas nécessaire de filtrer les personnes ici, on sait qu'on ne garde que
		// les personnes dans le fichier de référence.
		return (corefChains);
	}
	
	public static Map<Integer, CorefChain> getStanfordCorefChains(File file, Properties props) throws IOException, ClassNotFoundException
	{
		Annotation annotation = getInitAnnotation(file);
		if(coref == null)
			coref = new CorefAnnotator(props);
		coref.annotate(annotation);
		Map<Integer, CorefChain> corefChains = new CoreDocument(annotation).corefChains();
		filterPerson(corefChains, annotation);
		return corefChains;
	}
	
	
	private static void filterPerson(Map<Integer, CorefChain> chains, Annotation annotation)
	{
		Iterator<Map.Entry<Integer, CorefChain>> iter = chains.entrySet().iterator();
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		while(iter.hasNext())
		{
			CorefChain chain = iter.next().getValue();
			
			CorefMention representative = chain.getRepresentativeMention();
			int sent = representative.sentNum -1;
			int startIndex = representative.startIndex - 1;
			CoreLabel token = sentences.get(sent).get(TokensAnnotation.class).get(startIndex);
			String nerTag = token.ner();
			if(!nerTag.equals("PERSON"))
				iter.remove();
		}
		// Récupérer les tokens du representative
		// check si ner == person
		// si oui on garde, sinon on retire
	}
	
	private static List<List<Mention>> getMentionsSents(List<List<Mention>> mentions, Annotation annotation) 
	{
		List<List<Mention>> mentionsSents = new ArrayList<>();
		int size = annotation.get(SentencesAnnotation.class).size();
		for(int i = 0; i < size; i ++)
		{
			mentionsSents.add(new ArrayList<>());
		}
		for(List<Mention> entitiesMentions : mentions)
		{
			for(Mention mention : entitiesMentions)
			{
				mentionsSents.get(mention.sentNum).add(mention);
			}
		}
		
		
		return mentionsSents;
	}

	private static List<List<CorefMention>> buildCorefMentions(List<List<Mention>> mentions) 
	{
		List<List<CorefMention>> result = new ArrayList<>();
		for(int i = 0; i < mentions.size(); i ++)
		{
			result.add(new ArrayList<>());
			for(int j = 0; j < mentions.get(i).size(); j ++)
			{
				//(sentence number, mention number in sentence):
				Mention m = mentions.get(i).get(j);
				int[] posArray = {m.sentNum, m.mentionNum};
				IntTuple position = new IntTuple(posArray);
				CorefMention corefMention = new CorefMention(m, position);
				result.get(i).add(corefMention);
			}
		}
		return result;
	}
	
	public static Annotation getCleanAnnotation(File file) throws ClassNotFoundException, IOException
	{
		//TODO a transférer dans getCleanAnnotation
		Annotation annotation = getInitAnnotation(file);
		
		List<List<Mention>> mentions = getMentionsFromFile(file, annotation);
		
		List<List<Mention>> mentionsSents = getMentionsSents(mentions, annotation);
		for(List<Mention> mentionsSent : mentionsSents)
		{
			int num = 0;
			for(Mention mention : mentionsSent)
			{
				mention.mentionNum = num;
				num ++;
			}
		}
		
		List<List<CorefMention>> corefMentions = buildCorefMentions(mentions);
		
		Map<Integer, CorefChain> corefChains = buildCorefChains(corefMentions);
		
		if(corefCustom == null)
			//TODO retirer les properties du constructeur, il n'y en a pas besoin
			corefCustom = new CorefAnnotatorCustom(new Properties());
		corefCustom.annotateCustom(annotation, mentionsSents, corefChains);
		
		return annotation;
	}
	
	private static Mention buildMention(int id, int startIndex, int endIndex, int sent, Annotation annotation, int clusterID)
	{
		CoreMap sentence = annotation.get(SentencesAnnotation.class).get(sent);
		List<CoreLabel> sentencesWords = sentence.get(TokensAnnotation.class);
		List<CoreLabel> mentionSpan = new ArrayList<>();
		for(int i = startIndex; i < endIndex; i ++)
			mentionSpan.add(sentencesWords.get(i));
		SemanticGraph basicDependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
		SemanticGraph enhancedDependencies = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);

		Mention mention = new Mention(id, startIndex, endIndex, sentencesWords, basicDependencies, enhancedDependencies, mentionSpan);

		mention.corefClusterID = clusterID;
		mention.headWord = mentionSpan.get(mentionSpan.size()-1);
		mention.headIndex = sentencesWords.indexOf(mention.headWord);
		mention.sentNum = sent;
		return mention;
	}
}
