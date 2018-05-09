/**
 * 
 */
package implementation;

/**
 * @author Schmidt Gaëtan
 *
 */
public class Edge {
	
	protected String id;
	protected Node nodeLeft;
	protected Node nodeRight;
	protected boolean oriented;
	protected float ponderation;
	
	public Edge(String id, Node nodeLeft, Node nodeRight)
	{
		this.id = id;
		this.nodeLeft = nodeLeft;
		this.nodeRight = nodeRight;
		this.oriented = false;
		this.ponderation = 1;
	}
	
	public Edge(String id, Node nodeLeft, Node nodeRight, boolean oriented, float ponderation)
	{
		this.id = id;
		this.nodeLeft = nodeLeft;
		this.nodeRight = nodeRight;
		this.oriented = oriented;
		this.ponderation = ponderation;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public Node getNodeLeft()
	{
		return this.nodeLeft;
	}
	
	public Node getNodeRight()
	{
		return this.nodeRight;
	}
	
	public boolean isOreinted()
	{
		return this.oriented;
	}
	
	public float getPonderation()
	{
		return this.ponderation;
	}
	
	public void setPonderation(float ponderation)
	{
		this.ponderation = ponderation;
	}
	
	public void addPonderation(float ponderation)
	{
		this.ponderation+=ponderation;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Edge))return false;
	    Edge otherMyClass = (Edge)other;
	    
	    return this.id.equals(otherMyClass.id);
	    
	}
	
	@Override
	public String toString()
	{
		String ret="";
		ret+="{ id: "+this.id+", NodeLeft: "+this.nodeLeft.toString()+", nodeRight: "+this.nodeRight.toString()+", Ponderation: "+this.ponderation+"} ";
		return ret;
	}

}
