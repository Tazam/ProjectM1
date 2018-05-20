/**
 * 
 */
package performance.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import performance.Consts;
import performance.ssplit.SsplitUtils;


/**
 * @author Schmidt Gaëtan
 *
 */
public class NERUtils {
	
	public static Annotation getCleanAnnotation(File file) throws IOException, ClassNotFoundException
	{
		Annotation annotation = SsplitUtils.getCleanAnnotation(file);
		MorphaAnnotator lemma = new MorphaAnnotator();
		POSTaggerAnnotator pos = new POSTaggerAnnotator();
		pos.annotate(annotation);
		lemma.annotate(annotation);
		// On retrouve le fichier de référence à partir du nom du fichier du corpus.
		// Corpus : corpus.txt => Référence : corpus_reference.txt
		String fileName = FilenameUtils.removeExtension(file.getName());
		String referencePath = Consts.NER_PATH + File.separator + fileName + Consts.XML_REFERENCE_EXTENSION;
		File referenceFile = new File(referencePath);
		NERCombinerAnnotatorCustom custom = new NERCombinerAnnotatorCustom(true);
		NERCombinerAnnotatorCustom.init(getMapTokenNerCustom(referenceFile));
		custom.annotate(annotation);
		return annotation;
	}
	
	public static Annotation getOriginalAnnotation(File file) throws IOException
	{
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    FileInputStream is = new FileInputStream(file);     
		String content = IOUtils.toString(is, "UTF-8");
		Annotation annotation = new Annotation(content);
		pipeline.annotate(annotation);
		
		return annotation;
	}
	
	public static HashMap<Integer,HashMap<Integer,TokenNerCustom>> getMapTokenNerCustom(File referenceFile)
	{
		HashMap<Integer,HashMap<Integer,TokenNerCustom>> ret = new HashMap<Integer,HashMap<Integer,TokenNerCustom>>();
		
		
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
		    			 
		    		NodeList characters = entity.getElementsByTagName("character");
		    
		    		
		    		
		    		for(int j = 0; j < characters.getLength(); j ++)
		    		{
		    			if(characters.item(j).getNodeType() == Node.ELEMENT_NODE)
		    			{
		    				Element character = (Element) characters.item(j);
		    				String text = character.getElementsByTagName("text").item(0).getTextContent();
		    				int position = Integer.parseInt(character.getElementsByTagName("position").item(0).getTextContent())-1;
		    				int sent = Integer.parseInt(character.getElementsByTagName("sent").item(0).getTextContent())-1;
		    				
		    				
		    				if (ret.get(sent)==null)
		    				{
		    					ret.put(sent, new HashMap<Integer,TokenNerCustom>());
		    				}
		    				
		    				ret.get(sent).put(position, new TokenNerCustom(sent,position,text,"PERSON"));
		    				
		    				// si ont est au dernier personnage de la phrase courante
		    				
		    			}
		    		}
		    		
		    	}
		    }
		}	catch (final ParserConfigurationException e) {e.printStackTrace();}
		    catch (final SAXException e) {e.printStackTrace();}
	        catch (final IOException e) {e.printStackTrace();}	
	
		 System.out.println("----------------------------------");
		 
		 for(Integer key : ret.keySet())
		 {
			 System.out.println(ret.get(key).toString());
		 }
		 System.out.println("----------------------------------");
		return ret;
	}
	
	
	
	
	public static HashMap<Integer,HashMap<Integer,TokenNerCustom>> getMapTokenNerCustom2(File file)
	{
		HashMap<Integer,HashMap<Integer,TokenNerCustom>> ret = new HashMap<Integer,HashMap<Integer,TokenNerCustom>>();
		HashMap<Integer,TokenNerCustom> mapSentence = new HashMap<Integer,TokenNerCustom>();
		mapSentence.put(0, new TokenNerCustom(0,0,"Joe","PERSON"));
		mapSentence.put(1, new TokenNerCustom(0,1,"Smith","PERSON"));
		ret.put(0, mapSentence);
		HashMap<Integer,TokenNerCustom> mapSentence2 = new HashMap<Integer,TokenNerCustom>();
		mapSentence2.put(9, new TokenNerCustom(3,9,"Joe","PERSON"));
		ret.put(3, mapSentence2);
		HashMap<Integer,TokenNerCustom> mapSentence3 = new HashMap<Integer,TokenNerCustom>();
		mapSentence3.put(7, new TokenNerCustom(4,7,"Jane","PERSON"));
		mapSentence3.put(8, new TokenNerCustom(4,8,"Smith","PERSON"));
		ret.put(4, mapSentence3);
		HashMap<Integer,TokenNerCustom> mapSentence4 = new HashMap<Integer,TokenNerCustom>();
		mapSentence4.put(4, new TokenNerCustom(5,3,"Joe","PERSON"));
		mapSentence4.put(7, new TokenNerCustom(5,7,"Jane","PERSON"));
		ret.put(5, mapSentence4);
		HashMap<Integer,TokenNerCustom> mapSentence5 = new HashMap<Integer,TokenNerCustom>();
		mapSentence5.put(0, new TokenNerCustom(6,0,"Marc","PERSON"));
		ret.put(6, mapSentence5);
	/*
		 System.out.println("----------------------------------");
		 
		 for(Integer key : ret.keySet())
		 {
			 System.out.println(ret.get(key).toString());
		 }
		 System.out.println("----------------------------------");*/
		return ret;
	}
	
	public static void test() throws ClassNotFoundException, IOException
	{
		String text="Joe Smith was born in California. " +
			      "In 2017, he went to Paris, France in the summer. " +
			      "His flight left at 3:00pm on July 10th, 2017. " +
			      "After eating some escargot for the first time, Joe said, \"That was delicious!\" " +
			      "He sent a postcard to his sister Jane Smith. " +
			      "After hearing about Joe's trip, Jane decided she might go to France one day. "+
			      "Marc said, 'I'm the bigger butterfly in this world !!!!!!!!!!!!' ";
		
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
	    // build pipeline
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // create a document object
	    CoreDocument document = new CoreDocument(text);
	    // annnotate the document
	    pipeline.annotate(document);
	    Annotation annotation = document.annotation();
	    NERCombinerAnnotatorCustom custom = new NERCombinerAnnotatorCustom(true);
	    File file = new File("performance/ner/TextBase.xml");
	   // custom.annotate(annotation);
	//    custom.annotateCustom(annotation, getMapTokenNerCustom(file));
	    PrintWriter out = new PrintWriter("outputTest.txt");
	    pipeline.prettyPrint(annotation, out );
	}

}
