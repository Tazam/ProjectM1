/**
 * 
 */
package implementation;

/**
 * @author Schmidt Gaëtan
 *
 */
public class Node {
	
	protected String id;
	protected String name;
	protected float weight;
	
	public Node()
	{
		id ="";
		name = "";
		weight=0;
	}
	
	public Node(String id, String name, float weight)
	{
		this.id = id;
		this.name = name;
		this.weight = weight;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public float getWeight()
	{
		return this.weight;
	}
	
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public void addWeight(float weight)
	{
		this.weight+=weight;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Node))return false;
	    Node otherMyClass = (Node)other;
	    
	    return this.id.equals(otherMyClass.id);
	    
	}
	
	@Override
	public String toString()
	{
		String ret = "";
		ret+="{id: "+this.id+", name: "+this.name+", Weight: "+this.weight+"} ";
		return ret;
		
	}
	

}
