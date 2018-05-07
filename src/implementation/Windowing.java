/**
 * 
 */
package implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * @author Schmidt Gaëtan
 *
 */
public class Windowing implements RelationshipExtractionMethod {
	
	 CoreDocument document;
	 boolean ponderation;
	 int windowSize;
	 Graph graph;
	 /*
	  *  characters
	  *  word
	  *  sentence
	  */
	 String optionSize;
	 /*
	  * sequential
	  * sliding
	  */
	 String type;
	 
	 public Windowing()
	 {
		 
	 }
	 
	 public Windowing(CoreDocument document,Graph graph, boolean ponderation, String optionSize,String type, int windowSize)
	 {
		 this.document = document;
		 this.ponderation = ponderation;
		 this.windowSize = windowSize;
		 this.optionSize = optionSize;
		 this.type = type;
		 this.graph = graph;
	 }

	/* (non-Javadoc)
	 * @see implementation.RelationshipExtractionMethod#MainWork()
	 */
	@Override
	public void MainWork() {
		windowing1();
		System.out.println("GRAPH :");
		graph.toString();
		System.out.println("fin");
		

	}
	
	/**
	 * fenetre glissante (echelle: phrase) avec ponderation.
	 * 
	 * @author Schmidt Gaëtan
	 */
	private void windowing1()
	{
		List<CoreSentence> sentences = document.sentences();
		Map<String,Node> charMap = new HashMap<String, Node>();		
		List<Edge> linkList = new ArrayList<Edge>();
		// glissage de la fenêtre pour toute les phrases du document
		for (int i=0; i<sentences.size(); i++)
		{
			// intervalle de la fenêtre.
			for (int j=i; j<i+this.windowSize; j++)
			{
				// débordement de fenetre à la fin.
				if (j>=sentences.size())
					continue;
				
				// Pour tous les token de la phrase
				for (CoreLabel token : sentences.get(j).tokens())
				{
					// Si s'est une personne
					if (token.ner().equals("PERSON"))
					{ 
						String corefEntity = corefByToken(document.corefChains(),token);
						
						// si il n'a pas de coréférence on ajoute un neud.
						if (corefEntity == null)
						{
							Node n = new Node(token.word(),token.word(),1);
							charMap.put(n.id, n);
						}else // sinon, on vérifie s'il éxiste pour augmenter le poid de l'objet éxistant
						{
							
							if (charMap.containsKey(corefEntity))
							{
								charMap.get(corefEntity).addWeight(1);
							}else
							{
								Node n = new Node(corefEntity,corefEntity,1);
								charMap.put(n.id, n);
							}
						}
						
					}else if (token.ner().equals("O"))// si ce n'est pas une entité nommé
					{
						String corefEntity = corefByToken(document.corefChains(),token);
						// si il a une coréférence
						if (corefEntity!=null)
						{
							if (charMap.containsKey(corefEntity))
							{
								charMap.get(corefEntity).addWeight(1);
							}else
							{
								Node n = new Node(corefEntity,corefEntity,1);
								charMap.put(n.id, n);
							}
						}
					}
				}
			}
			System.out.println("Phrase: "+i);
			System.out.println(sentences.get(i).text());
			System.out.println("--------------");
			
			System.out.println(charMap.toString());
			
			// une fois les noeuds de la fenêtre ajouté, on crée les arcs entres eux.
			for (Node nodeL : charMap.values())
			{
				for (Node nodeR : charMap.values())
				{
					if (nodeL.equals(nodeR))
						continue;
					Edge edge = new Edge(nodeL.id,nodeL,nodeR,false,1);
					if (!linkList.contains(edge)&&!containInverseLink(linkList,edge))
					{
						linkList.add(edge);
					}
				}
			}
			
			// on ajoute les noeuds au graph.
			for (Node n : charMap.values())
				graph.addNode(n);
			
			// on ajoute les arcs.
			for (Edge e : linkList)
				graph.addEdgeWithPonderation(e);
			
			System.out.println("EDGE:");
			System.out.println(linkList.toString());
			
			// on éfface les objet pour la prochaine fenêtre.
			charMap.clear();
			linkList.clear();
		}
		
		System.out.println("COREFCHAINS:");
		System.out.println((document.corefChains().toString()));
		
	}
	
	private boolean containInverseLink(List<Edge> l, Edge e)
	{
		for (Edge el : l)
		{
			if (el.nodeLeft.equals(e.nodeRight)&&el.nodeRight.equals(e.nodeLeft))
				return true;
		}
		return false;
	}
	
	public void testCorefByToken()
	{
		List<CoreSentence> sentences = document.sentences();
		Map<String,Node> charMap = new HashMap<String, Node>();		
		Map<String,Edge> linkMap = new HashMap<String, Edge>();
		// glissage de la fenêtre pour toute les phrases du document
		for (int i=0; i<sentences.size(); i++)
		{
			// intervalle de la fenêtre.
			for (int j=i; j<i+this.windowSize; j++)
			{
				// débordement de fenetre à la fin.
				if (j>=sentences.size())
					continue;
				
				for (CoreLabel token : sentences.get(j).tokens())
				{
					if (token.ner().equals("PERSON"))
						{
							String test = corefByToken(document.corefChains(),token);
							if (test!=null)
							{
								System.out.println("TOKEN ---> "+token.word());
								System.out.println("Coref ---> "+test);
							}else
							{
								System.out.println("TOKEN SEUL ---> "+token.word());
							}
						}
				}
			}
		}
		
	}
	
	private void test()
	{
		// parcour des chaine de coréférence
		for (CorefChain corefchain : document.corefChains().values())
		{
			// parcour des mentions de la chaine de coréférence courante
			for (CorefMention mention : corefchain.getMentionsInTextualOrder())
			{
				// position de la phrase qui contien la mention
				int posSentence = mention.position.get(0); // ou 1 si ça commence par 1
				// position de la mention dans la phrase
				int posWord = mention.position.get(1); // ou 2 si ça commence par 1
				if (document.sentences().get(posSentence).tokens().get(posWord).ner().equals("PERSON"))
				{
					// La mention est une personne.
				}
			}
		}
	}
	
	private String corefByToken(Map<Integer,CorefChain> corefChains, CoreLabel token)
	{
		String ret = null;
		
		for (CorefChain corefchain : corefChains.values())
		{
			for (CorefMention mention : corefchain.getMentionsInTextualOrder())
			{
				if (mention.mentionSpan.equals(token.word())||mention.mentionSpan.contains(token.word()))
					return corefchain.getRepresentativeMention().mentionSpan;
			}
		}
		
		return ret;
	}
	
	private void coref()
	{
		Map<Integer,CorefChain> corefChains = document.corefChains();
		List<CoreSentence> sentences = document.sentences();
		Map<String,Node> charMap = new HashMap<String, Node>();		
		Map<String,Edge> linkMap = new HashMap<String, Edge>();
		// glissage de la fenêtre pour toute les phrases du document;
		
		System.out.println("SIZE :"+sentences.size());
		
		for (int i=0; i<sentences.size(); i++)
		{
			// intervalle de la fenêtre.
			for (int j=i; j<i+this.windowSize; j++)
			{
				if (j>=sentences.size())
					continue;
				System.out.println("phrase: "+j);
				for (Integer key : corefChains.keySet())
				{
					System.out.println("corefChain ----> "+corefChains.get(key));
					for (int k=0;k< corefChains.get(key).getMentionsInTextualOrder().size();k++)
					{
						System.out.println("Mention textOrder ----> "+corefChains.get(key).getMentionsInTextualOrder().get(k));
						// si la mention est dans la fennêtre
						if (corefChains.get(key).getMentionsInTextualOrder().get(k).sentNum-1 == j)
						{
							System.out.println("sentNum: "+corefChains.get(key).getMentionsInTextualOrder().get(k).sentNum+" SentCurrent: "+j);
							//corefChains.get(0).getRepresentativeMention().
							Node n = new Node(String.valueOf(corefChains.get(key).getMentionsInTextualOrder().get(k).corefClusterID),corefChains.get(key).getMentionsInTextualOrder().get(k).mentionSpan,0);
							if (charMap.containsKey(n.id))
							{
								charMap.get(n.id).addWeight(1);
							}else
							{
								charMap.put(n.id, n);
							}
						}
					}
				}
				
				
				
			}
			
			for (Node nLeft: charMap.values())
			{
				for (Node nRight : charMap.values())
				{
					if (!nLeft.equals(nRight))
					{
						Edge e = new Edge(nLeft.id,nLeft,nRight,false,0);
						if (linkMap.containsKey(nRight.id))
							continue;
						if (linkMap.containsKey(nLeft.id))
						{
							if (this.ponderation)
							{
								e.setPonderation(e.ponderation+e.nodeLeft.weight+e.nodeRight.weight);
							}else
							{
								continue;
							}
						}
						linkMap.put(e.id, e);
					}
				}
			}
			
			for (Node n : charMap.values())
			{
				n.setWeight(0);
				this.graph.addNode(n);
			}
			
			for (Edge e : linkMap.values())
			{
				this.graph.addEdge(e);
			}
			
			charMap.clear();
			linkMap.clear();
			
		}
	}
	
	
	

}
