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
	  * SEQUENTIAL
	  * SLIDING
	  */
	 String type;
	 
	 public Windowing()
	 {
		 
	 }
	 /**
	  * 
	  * @param document
	  * @param graph
	  * @param ponderation
	  * @param optionSize
	  * @param type
	  * @param windowSize
	  */
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
		System.out.println("MAIN WORK");

		if ("SENTENCE".equals(optionSize))
			corefSent();
		if ("WORD".equals(optionSize))
			corefWord();
		
		
		System.out.println("GRAPH :");
		graph.toString();
		System.out.println("fin");
		

	}
	
	/**
	 * fenetrage (echelle: phrase) avec ponderation selon option.
	 * 
	 * @author Schmidt Gaëtan
	 */
	private void corefSent()
	{
		List<CoreSentence> sentences = document.sentences();
		Map<String,Node> charMap = new HashMap<String, Node>();		
		List<Edge> linkList = new ArrayList<Edge>();
		// glissage de la fenêtre pour toute les phrases du document
		int i=0;
		do
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
						CorefChain corefEntity = impUtils.corefByToken(document.corefChains(),token);
						
						// si il n'a pas de coréférence on ajoute un neud.
						if (corefEntity == null)
						{
							Node n = new Node(token.word(),token.word(),0);
							charMap.put(n.id, n);
						}else // sinon,
						{
							// on récupère une mention valide de la chaine (prévien de certaines erreurs possible de coref
							CorefMention mentionV = impUtils.valideRepresentativeMention(corefEntity,document);
							
							// on vérifie s'il éxiste pour augmenter le poid de l'objet éxistant
							if (mentionV!=null)
							{
								if (charMap.containsKey(mentionV.mentionSpan))
								{
									if (this.ponderation)
										charMap.get(mentionV.mentionSpan).addWeight(1);
								}else
								{
									Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,0);
									charMap.put(n.id, n);
								}
							}
							
						}
						
					}else if (token.ner().equals("O"))// si ce n'est pas une entité nommé
					{
						CorefChain corefEntity = impUtils.corefByToken(document.corefChains(),token);
						// si il a une coréférence
						if (corefEntity!=null)
						{
							CorefMention mentionV = impUtils.valideRepresentativeMention(corefEntity,document);
							if (mentionV!=null)
							{
								if (charMap.containsKey(mentionV.mentionSpan))
								{
									if (this.ponderation)
										charMap.get(mentionV.mentionSpan).addWeight(1);
								}else
								{
								Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,0);
								charMap.put(n.id, n);
								}
							}
							
						}
					}
				}
			}
			//System.out.println("Phrase: "+i);
		//	System.out.println(sentences.get(i).text());
		//	System.out.println("--------------");
			
	//		System.out.println(charMap.toString());
			
			// une fois les noeuds de la fenêtre ajouté, on crée les arcs entres eux.
			for (Node nodeL : charMap.values())
			{
				for (Node nodeR : charMap.values())
				{
					if (nodeL.equals(nodeR))
						continue;
					Edge edge = new Edge(nodeL.id,nodeL,nodeR,false,nodeL.weight+nodeR.weight);
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
			
			//System.out.println("EDGE:");
			//System.out.println(linkList.toString());
			
			// on éfface les objet pour la prochaine fenêtre.
			charMap.clear();
			linkList.clear();
		
	//	System.out.println("COREFCHAINS:");
	//	System.out.println((document.corefChains().toString()));
		
			if ("SEQUENTIAL".equals(this.optionSize))
			{
				i+=this.windowSize;
			}else
			{
				i++;
			}
		}while (i<sentences.size());
		
	}
	
	/**
	 * Fenetrage (echelle: mot) avec ponderation selon option.
	 * 
	 * @author Schmidt Gaëtan
	 */
	private void corefWord()
	{
		List<CoreLabel> tokens = document.tokens();
		//List<CoreSentence> sentences = document.sentences();
		Map<String,Node> charMap = new HashMap<String, Node>();		
		List<Edge> linkList = new ArrayList<Edge>();
		// glissage de la fenêtre pour toute les phrases du document
		int i=0;
		do
		{
			// intervalle de la fenêtre.
			for (int j=i; j<i+this.windowSize; j++)
			{
				// débordement de fenetre à la fin.
				if (j>=tokens.size())
					continue;
				CoreLabel token = tokens.get(j);
				// Si s'est une personne
				
				if (token.ner().equals("PERSON"))
				{
					CorefChain corefEntity = impUtils.corefByToken(document.corefChains(),token);
					
					// si il n'a pas de coréférence on ajoute un neud.
					if (corefEntity == null)
					{
						Node n = new Node(token.word(),token.word(),1);
						charMap.put(n.id, n);
					}else // sinon,
					{
						// on récupère une mention valide de la chaine (prévien de certaines erreurs possible de coref
						CorefMention mentionV = impUtils.valideRepresentativeMention(corefEntity,document);
						
						// on vérifie s'il éxiste pour augmenter le poid de l'objet éxistant
						if (mentionV!=null)
						{
							if (charMap.containsKey(mentionV.mentionSpan))
							{
								if (this.ponderation)
									charMap.get(mentionV.mentionSpan).addWeight(1);
							}else
							{
								Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,1);
								charMap.put(n.id, n);
							}
						}
						
					}
					
				}else if (token.ner().equals("O"))// si ce n'est pas une entité nommé
				{
					CorefChain corefEntity = impUtils.corefByToken(document.corefChains(),token);
					// si il a une coréférence
					if (corefEntity!=null)
					{
						CorefMention mentionV = impUtils.valideRepresentativeMention(corefEntity,document);
						if (mentionV!=null)
						{
							if (charMap.containsKey(mentionV.mentionSpan))
							{
								if (this.ponderation)
									charMap.get(mentionV.mentionSpan).addWeight(1);
							}else
							{
							Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,1);
							charMap.put(n.id, n);
							}
						}
						
					}
				}
			}
			//System.out.println("Phrase: "+i);
		//	System.out.println(sentences.get(i).text());
		//	System.out.println("--------------");
			
	//		System.out.println(charMap.toString());
			
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
			
			//System.out.println("EDGE:");
			//System.out.println(linkList.toString());
			
			// on éfface les objet pour la prochaine fenêtre.
			charMap.clear();
			linkList.clear();
		
	//	System.out.println("COREFCHAINS:");
	//	System.out.println((document.corefChains().toString()));
		
			if ("SEQUENTIAL".equals(this.optionSize))
			{
				i+=this.windowSize;
			}else
			{
				i++;
			}
		}while (i<tokens.size());
		
	}
	/*
	private void windowing3()
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
						CorefChain corefEntity = corefByToken(document.corefChains(),token);
						
						// si il n'a pas de coréférence on ajoute un neud.
						if (corefEntity == null)
						{
							Node n = new Node(token.word(),token.word(),1);
							charMap.put(n.id, n);
						}else // sinon,
						{
							// on récupère une mention valide de la chaine (prévien de certaines erreurs possible de coref
							CorefMention mentionV = valideRepresentativeMention(corefEntity);
							
							// on vérifie s'il éxiste pour augmenter le poid de l'objet éxistant
							if (mentionV!=null)
							{
								if (charMap.containsKey(mentionV.mentionSpan))
								{
									if (this.ponderation)
										charMap.get(mentionV.mentionSpan).addWeight(1);
								}else
								{
									Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,1);
									charMap.put(n.id, n);
								}
							}
							
						}
						
					}else if (token.ner().equals("O"))// si ce n'est pas une entité nommé
					{
						CorefChain corefEntity = corefByToken(document.corefChains(),token);
						// si il a une coréférence
						if (corefEntity!=null)
						{
							CorefMention mentionV = valideRepresentativeMention(corefEntity);
							if (mentionV!=null)
							{
								if (charMap.containsKey(mentionV.mentionSpan))
								{
									if (this.ponderation)
										charMap.get(mentionV.mentionSpan).addWeight(1);
								}else
								{
								Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,1);
								charMap.put(n.id, n);
								}
							}
							
						}
					}
				}
			}
			//System.out.println("Phrase: "+i);
		//	System.out.println(sentences.get(i).text());
		//	System.out.println("--------------");
			
	//		System.out.println(charMap.toString());
			
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
			
			//System.out.println("EDGE:");
			//System.out.println(linkList.toString());
			
			// on éfface les objet pour la prochaine fenêtre.
			charMap.clear();
			linkList.clear();
		}
		
	//	System.out.println("COREFCHAINS:");
	//	System.out.println((document.corefChains().toString()));
		
	}
	*/
	private boolean containInverseLink(List<Edge> l, Edge e)
	{
		for (Edge el : l)
		{
			if (el.nodeLeft.equals(e.nodeRight)&&el.nodeRight.equals(e.nodeLeft))
				return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	

}
