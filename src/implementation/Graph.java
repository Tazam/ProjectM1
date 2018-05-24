/**
 * 
 */
package implementation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Schmidt Gaëtan
 *
 */
public class Graph {
	
	protected Map<String,Node> nodeMap;
	protected Map<String, Edge> edgeMap;
	protected boolean oriented;
	String name;
	
	public Graph()
	{
		this.nodeMap = new HashMap<String,Node>();
		this.edgeMap = new HashMap<String, Edge>();
	}
	
	public Graph(HashMap<String,Node> nodeMap, HashMap<String, Edge> edgeMap, String name)
	{
		this.nodeMap = nodeMap;
		this.edgeMap = edgeMap;
		this.name = name;
	}
	
	public Node getNodeById(String id)
	{
		return nodeMap.get(id);
	}
	
	public boolean addNode(Node node)
	{
		if (this.nodeMap.containsKey(node.id))
			return false;
		this.nodeMap.put(node.id, node);
		return true;
	}
	
	public void addNodeWithWeight(Node node)
	{
		if (this.nodeMap.containsKey(node.id))
		{
			this.nodeMap.get(node.id).addWeight(node.weight);
		}else
		{
			this.nodeMap.put(node.id, node);
		}
	}
	
	public Edge getEdgeById(String id)
	{
		return this.edgeMap.get(id);
	}
	
	public boolean addEdge(Edge edge)
	{
		if (this.edgeMap.containsKey(edge.id))
			return false;
		this.edgeMap.put(edge.id, edge);
		return true;
	}
	
	public void addEdgeWithPonderation(Edge edge)
	{
		if (this.edgeMap.containsKey(edge.id))
			{
				this.edgeMap.get(edge.id).addPonderation(edge.ponderation);
			}else
			{
				this.edgeMap.put(edge.id, edge);
			}
		
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void graphMLPrinter() throws IOException
	{
		graphMLPrinter("");
	}
	
	public void graphMLPrinter(String path) throws IOException
	{
		

		if (this.nodeMap.isEmpty())
		{
			System.out.println("Erreur nodeMapeMpty !");
			return;
		}
		
		if (this.edgeMap.isEmpty())
		{
			System.out.println("Erreur edgeMapeMpty !");
			return;
		}
		
		String pathDest = this.name+".graphml";
		
		if (!"".equals(path))
			pathDest = path+"/"+this.name+".graphml";
		
		
		FileWriter fw = new FileWriter(pathDest);
		BufferedWriter buffer = new BufferedWriter(fw);
		
		buffer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.newLine();
		buffer.write("<graphml>");
		buffer.newLine();
		//buffer.write("<graph edgedefault=\"undirected\">");
		buffer.newLine();
		buffer.write("<key id=\"keyNode\" for=\"node\" attr.name=\"characterName\" attr.type=\"string\">");
		buffer.newLine();
		buffer.write("</key>");
		buffer.write("<key id=\"keyEdge\" for=\"edge\" attr.name=\"weight\" attr.type=\"float\"/>");
		buffer.newLine();
		
		for (Edge etpm : this.edgeMap.values())
		{
			this.oriented = etpm.oriented;
			break;
		}
		
		if (this.oriented)
		{
			buffer.write("<graph id=\""+this.name+"\" edgedefault=\"directed\">");
		}else
		{
			buffer.write("<graph id=\""+this.name+"\" edgedefault=\"undirected\">");
		}
		buffer.newLine();
		
		for (Node node : this.nodeMap.values())
		{
			buffer.write("<node id=\""+node.id+"\">");
			buffer.newLine();
			buffer.write("<data key=\"keyNode\">"+node.name+"</data>");
			buffer.newLine();
			buffer.write("</node>");
			buffer.newLine();
		}
		float pond = 1;
		for (Edge edge : this.edgeMap.values())
		{
			if (edge.ponderation<0)
				pond = edge.ponderation;
			buffer.write("<edge id=\""+edge.id+"\" source=\""+edge.nodeLeft.id+"\" target=\""+edge.nodeRight.id+"\">");
			buffer.newLine();
			buffer.write("<data key=\"keyEdge\">"+pond+"</data>");
			buffer.newLine();
			buffer.write("</edge>");
			buffer.newLine();
		}
		
		buffer.write("</graph>");
		buffer.newLine();
		buffer.write("</graphml>");
		buffer.flush();
		buffer.close();
		
		System.out.println(pathDest+" printed !");
	}
	
	public String toString()
	{
		String ret="Graph name: "+this.name+"\n";
		//*
		for (Node node : this.nodeMap.values())
		{
			ret+="Node ID = "+node.id+" Name = "+node.name+"\n";
		}
		
		
		for (Edge edge : this.edgeMap.values())
		{
			ret+="Edge ID = "+edge.id+" Ponderation = "+edge.ponderation+" NodeLeft = "+edge.nodeLeft.name+" NodeRight = "+edge.nodeRight.name+"\n";
		}
		//*/
		
		//ret+=this.nodeMap.toString()+"\n";
		//ret+=this.edgeMap.toString()+"\n";
		
		
		
		
		return ret;
	}

}
