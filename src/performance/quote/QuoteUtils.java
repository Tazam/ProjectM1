package performance.quote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import performance.Consts;
import performance.ner.TokenNerCustom;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.util.Pair;

public class QuoteUtils {
	
	public static Annotation getInitAnnotation(File file) throws IOException
	{
		FileInputStream is = new FileInputStream(file);     
		String content = IOUtils.toString(is, "UTF-8");
		Annotation annotation = new Annotation(content);
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,coref,quote");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		pipeline.annotate(annotation);
		//QuoteAnnotatorCustom quoteA = new QuoteAnnotatorCustom(props);
		//quoteA.annotate(annotation);
		return annotation;
	}
	
	public static Annotation getCleanAnnotation(File f) throws IOException{	
		return null;	
	}
	
	public static  List<Pair<Integer, Integer>> annotationLoader(File referenceFile)
	{
		List<Pair<Integer, Integer>> ret = new  ArrayList<Pair<Integer, Integer>>();
		
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
		    			 
		    		NodeList quotes = entity.getElementsByTagName("quote");
		    
		    		
		    		
		    		for(int j = 0; j < quotes.getLength(); j ++)
		    		{
		    			if(quotes.item(j).getNodeType() == Node.ELEMENT_NODE)
		    			{
		    				Element quote = (Element) quotes.item(j);
		    				int start = Integer.parseInt(quote.getElementsByTagName("open").item(0).getTextContent())-1;
		    				int end = Integer.parseInt(quote.getElementsByTagName("close").item(0).getTextContent())-1;
		    				ret.add(new Pair<Integer, Integer>(start, end));
		    			}
		    		}
		    		
		    	}
		    }
		}	catch (final ParserConfigurationException e) {e.printStackTrace();}
		    catch (final SAXException e) {e.printStackTrace();}
	        catch (final IOException e) {e.printStackTrace();}	

		return ret;
	}
	
	
	public static List<Pair<Integer, Integer>> getStanfordQuotes(File f) throws IOException
	{
		List<Pair<Integer, Integer>> quoteStanford = new ArrayList<Pair<Integer, Integer>>();
		Annotation a = getInitAnnotation(f);
		CoreDocument cd = new CoreDocument(a); 
		List<CoreQuote> quotes = cd.quotes();
		for(CoreQuote cQuote : quotes){
			quoteStanford.add(cQuote.quoteCharOffsets());
		}	
		return quoteStanford;
	}

}
