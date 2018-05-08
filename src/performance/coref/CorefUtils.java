package performance.coref;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Mention;
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
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.IntTuple;
import performance.Consts;
import performance.coref.customannotators.CorefAnnotatorCustom;
import performance.ssplit.SsplitUtils;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//@author Axel Clerici
//Cette classe contient les différentes méthodes nécessaires à la création des chaînes de coréférence,
//que ce soit à partir de l'annotateur de Stanford ou à partir d'un fichier de référence.
//De plus, elle permet de renvoyer l'annotation "nettoyée" pour les prochaines étapes
//d'évaluation des performances.

public class CorefUtils 
{
	// Pour ne pas avoir à créer des annotateur pour chaque fichier du corpus
	private static boolean initAnnotators = true;
	
	// Ce paquet d'annotateur n'est ici que temporairement. Il sera enlevé lors
	// de l'intégration de l'annotateur ner propre.
	private static POSTaggerAnnotator pos = null;
	private static MorphaAnnotator lemma = null;
	private static NERCombinerAnnotator ner = null;
	
	// Ces annotateurs sont requis par coref, mais pas évalué par nous. On utilisera
	// donc ceux de base de Stanford.
	private static ParserAnnotator parser = null;
	private static DependencyParseAnnotator deparser = null;
	
	private static CorefAnnotator coref = null;
	private static CorefAnnotatorCustom corefCustom = null;
	
	// Construit l'annotation requise en entrée de coref sur un texte du corpus
	// Pour ce faire, on récupère l'annotation nettoyée
	// de NER, puis on ajoute Parser et Deparser de base de Stanford, car ces annotateurs là ne sont pas
	// évalués par nous
	public static Annotation getInitAnnotation(File file) throws IOException, ClassNotFoundException
	{
		// En attendant (pos, lemma et ner seront "inclus" dans la version nettoyée de ner):
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

		// Cette ligne devra être ajoutée lors de l'intégration du NER Propre
		//Annotation annotation = NerUtils.getAnnotationCleaned(file);
		parser.annotate(annotation);
		deparser.annotate(annotation);
		return annotation;
	}
	
	// Renvoie l'annotation propre d'un texte du corpus. Cette annotation est annotée par
	// les annotateurs customs pour coref.
	public static Annotation getCleanAnnotation(File file) throws ClassNotFoundException, IOException
	{
		Annotation annotation = getInitAnnotation(file);
		
		// On récupère les mentions pour un texte du corpus. Elles sont classées en listes de
		// mentions qui représentent les différentes entités
		List<List<Mention>> mentions = getMentionsFromFile(file, annotation);
		// On a également besoin d'avoir les même mentions mais organisées en listes de mentions
		// qui représentent les différentes phrases
		List<List<Mention>> mentionsSents = getMentionsSents(mentions, annotation);
		
		// Ajout des ID des mentions ( vérifier la nécessité de cette opération )
		for(List<Mention> mentionsSent : mentionsSents)
		{
			int num = 0;
			for(Mention mention : mentionsSent)
			{
				mention.mentionNum = num;
				num ++;
			}
		}
		
		// Transformation des Mentions en CorefMentions
		List<List<CorefMention>> corefMentions = buildCorefMentions(mentions);
		// Construction des chaînes de coréférence à partir des CorefMentions
		Map<Integer, CorefChain> corefChains = buildCorefChains(corefMentions);
		
		// On transmet les infos à notre annotateur custom qui annotera correctement l'annotation
		if(corefCustom == null)
			corefCustom = new CorefAnnotatorCustom(new Properties());
		corefCustom.annotateCustom(annotation, mentionsSents, corefChains);
		
		return annotation;
	}
	
	// Renvoie les chaînes de coréférence générées par l'annotateur de Stanford
	// mais filtre pour ne garder que les chaînes qui représentent des personnes
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
	
	// Renvoie les chaînes de coréférence générées par l'annotateur Custom
	// et donc qui contient les chaînes issues du fichier de référence annoté manuellement,
	// mais filtre pour ne garder que les chaînes qui représentent des personnes
	public static Map<Integer, CorefChain> getCustomCorefChains(File file) throws ClassNotFoundException, IOException
	{
		Annotation annotation = getCleanAnnotation(file);
		Map<Integer, CorefChain> corefChains = new CoreDocument(annotation).corefChains();
		// il n'est pas nécessaire de filtrer les personnes ici, on sait qu'on ne garde que
		// les personnes dans le fichier de référence.
		return (corefChains);
	}
	
	// Reçoit un texte du corpus et une annotation ( nécessaire pour récupérer les phrases) et
	// renvoie une liste de liste de mentions représentant les mentions pour chaque entités, à partir d'un fichier
	// de référence manuellement annoté
	private static List<List<Mention>> getMentionsFromFile(File file, Annotation annotation) throws ClassNotFoundException, IOException
	{
		// On récupère le fichier de référence à partir du texte du corpus file
	    String fileName = FilenameUtils.removeExtension(file.getName());
		String referencePath = Consts.COREF_PATH + File.separator + fileName + Consts.XML_REFERENCE_EXTENSION;
		File referenceFile = new File(referencePath);
			
		List<List<Mention>> result = new ArrayList<>();
		
		// On lit les différentes informations du fichier xml de référence
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
		    					 
		    				// On créer la mention à paritr des informations retrouvées
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
	
	// Construit et renvoie une mention à partir d'informations extraites d'un fichier de référence
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
		if(mentionSpan.size() == 0)
			System.out.println(startIndex +"-" +endIndex + "errrrrrrrrrrrrrrrrrrorrrrrrrrrrrrrrrrrrrr");
		mention.headWord = mentionSpan.get(mentionSpan.size()-1);
		mention.headIndex = sentencesWords.indexOf(mention.headWord);
		mention.sentNum = sent;
		return mention;
	}
	
	// Construit les chaînes de coréférence à partir des CorefMentions
	private static Map<Integer, CorefChain> buildCorefChains(List<List<CorefMention>> corefMentions)
	{
		Map<Integer, CorefChain> corefChains = new HashMap<>();
		for(int i = 0; i < corefMentions.size(); i ++)
		{
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
	
	// Cette fonction retire d'un ensemble de chaînes de coréférence celles qui ne représentent
	// pas un personnage
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
			int endIndex = representative.endIndex - 1;
			boolean person = false;
			for(int i = startIndex; i < endIndex; i++)
			{
				CoreLabel token = sentences.get(sent).get(TokensAnnotation.class).get(i);
				String nerTag = token.ner();
				if(nerTag.equals("PERSON") || nerTag.equals("TITLE"))
				{
					person = true;
					break;
				}
			}
			if(person == false)
				iter.remove();

		}
	}
	
	// Cette fonction renvoie une liste de liste de Mention qui regroupent les mentions en fonction des phrases
	// auxquelles elles appartiennent.
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

	// Construit des CorefMention à partir de Mention. Comme pour mentions, le résultat
	// est une liste de listes de CorefMentions qui représentent les différentes entités
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
}
