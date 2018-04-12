package performance.coref;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CorefAnnotator;
import performance.Consts;
import performance.ssplit.SsplitUtils;

public class CorefFilesBuilder
{
	private File corpusFolder;
	private CorefAnnotator corefAnnotator;

	public CorefFilesBuilder(Properties props) 
	{
		this.corpusFolder = new File(Consts.CORPUS_PATH);
		this.corefAnnotator = new CorefAnnotator(props);
	}
	
	public CorefFilesBuilder()
	{
		this.corefAnnotator = new CorefAnnotator(new Properties());
		this.corpusFolder = new File(Consts.CORPUS_PATH);
	}
	
	public void buildFiles() throws ClassNotFoundException, IOException
	{
		// Pour le moment, il n'y a que 2 fichiers de références pour ssplit.
		int i = 0;
		for (final File fileEntry : corpusFolder.listFiles()) 
		{
			if(i < 2)
			buildFile(fileEntry);
			i ++;
		}
	}

	private void buildFile(File file) throws ClassNotFoundException, IOException
	{
		System.out.println("Je traite " + file.getName());
		
		try 
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document= builder.newDocument();
		    final Element root = document.createElement("document");
		    document.appendChild(root);
			
		    int chainID = 1;
		    Map<Integer, CorefChain> corefChains = getCorefChains(file);
			for(Integer key : corefChains.keySet())
			{
				CorefChain corefChain = corefChains.get(key);
				final Element chainNode = document.createElement("chain");
				chainNode.setAttribute("chainID", String.valueOf(chainID));
				root.appendChild(chainNode);
				List<CorefMention> mentions = corefChain.getMentionsInTextualOrder();
				for(CorefMention mention : mentions)
				{
					final Element mentionNode = document.createElement("mention");
					mentionNode.setAttribute("mentionID", String.valueOf(mention.sentNum) + "_" + String.valueOf(mention.startIndex));
					chainNode.appendChild(mentionNode);
					mentionNode.appendChild(document.createTextNode(mention.toString()));
				}
				
				chainID ++;
			}
			
			String resultName = FilenameUtils.removeExtension(file.getName());
			String resultPath = Consts.STANFORD_COREF_PATH + File.separator + resultName + Consts.RESULT_EXTENSION;
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    final Transformer transformer = transformerFactory.newTransformer();
		    final DOMSource source = new DOMSource(document);
		    final StreamResult output = new StreamResult(new File(resultPath));
		    //final StreamResult result = new StreamResult(System.out);
				

		    //prologue
		    transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			
		    		
		    //formatage
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				
		    //sortie
			System.out.println("J'écris les résultats dans " + resultPath);
		    transformer.transform(source, output);	
		    
		    
			
		} catch (ParserConfigurationException e) {e.printStackTrace();}
		catch (TransformerConfigurationException e) {
		    e.printStackTrace();
		}
		catch (TransformerException e) {
		    e.printStackTrace();
		}


	}
	
	private Map<Integer, CorefChain> getCorefChains(File file) throws ClassNotFoundException, IOException
	{
		Annotation annotation = CorefUtils.getInitAnnotation(file);
		corefAnnotator.annotate(annotation);
		CoreDocument document = new CoreDocument(annotation);
		return document.corefChains();
	}

}
