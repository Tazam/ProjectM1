/**
 * 
 */
package implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * @author Schmidt Gaëtan
 *
 */
public class Mention implements RelationshipExtractionMethod {
	
	 CoreDocument document;
	 Graph graph;
	 Boolean oriented;
	 Boolean ponderation;
	 /**
	  * 
	  * @param document
	  * @param graph
	  * @param oriented
	  * @param ponderation
	  */
	 public Mention(CoreDocument document, Graph graph, Boolean oriented, Boolean ponderation)
	 {
		 this.document = document;
		 this.graph = graph;
		 this.oriented = oriented;
		 this.ponderation = ponderation;
	 }
	 
	 

	/* (non-Javadoc)
	 * @see implementation.RelationshipExtractionMethod#MainWork()
	 */
	@Override
	public void MainWork() {
		
		Map<String,Node> charMap = new HashMap<String, Node>();		
		List<Edge> linkList = new ArrayList<Edge>();
		List<CoreQuote> quotes = this.document.quotes();
		Optional<String> speaker;
		
		for (CoreQuote quote : quotes)
		{
			if (!quote.hasSpeaker)
				continue;
			for (CoreSentence sentence : quote.sentences())
			{
				for (CoreLabel token : sentence.tokens())
				{
					// Si le token courrant n'est pas un personage ou que c'est le locuteur on passe ce token
					if ((!"PERSON".equals(token.ner())) || (token.word().equals(quote.speaker())))
						continue;
					// si la position du token ne le place pas dans la quote on le passe
					if ((token.beginPosition()<quote.quoteCharOffsets().first)||(token.endPosition()>quote.quoteCharOffsets().second))
						continue;
					if (this.ponderation)
					{
						if (charMap.containsKey(token.word()))
							{
								charMap.get(token.word()).addWeight(1);
							}else
							{
								charMap.put(token.word(), new Node(token.word(),token.word(),0));
							}
						
					}else
					{
						charMap.put(token.word(), new Node(token.word(),token.word(),0));
					}
					
				}
			}

			for (Node nodeR : charMap.values())
			{
				
				if (quote.hasCanonicalSpeaker)
				{
					speaker = quote.canonicalSpeaker();
				}else
				{
					speaker = quote.speaker();
				}
				linkList.add(new Edge(speaker,new Node(speaker,speaker,0),nodeR,this.oriented,nodeR.weight));
				this.graph.addNode(nodeR);
			}
			
			for (Edge edge : linkList)
			{
				this.graph.addNode(edge.nodeLeft);
				if (this.ponderation)
				{
					this.graph.addEdgeWithPonderation(edge);
				}else
				{
					this.graph.addEdge(edge);
				}
			}
			charMap.clear();
			linkList.clear();
		}

	}

}
