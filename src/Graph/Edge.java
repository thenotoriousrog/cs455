package Graph;

// creates an edge between two vertices.
public class Edge implements Comparable<Edge> {

	private Vertex one, two; // vertices which an edge is created between.
	private int weight; // generates the weight of the node
	
	// compares port numbers of vertices.
	public int comparePortNum(int vertex1, int vertex2)
	{
		// 1 if greater than the object
		// -1 if less than the object
		// 0 if equal to the object
		if(vertex1 < vertex2)
		{
			return -1;
		}
		else if(vertex1 > vertex2)
		{
			return 1;
		}
		else
		{
			return 0; // vertices are equal.
		}
	}
	
	public Edge(Vertex one, Vertex two)
	{
		this(one, two, 1);
	}
	
	public Edge(Vertex one, Vertex two, int weight)
	{
		this.one = (comparePortNum(one.getVertexPortNum(), two.getVertexPortNum()) <= 0) ? one : two; // check if vertex already exists.
		this.two = (this.one == one) ? two : one;
		this.weight = weight;
	}
	
	// returns the vertex neighbor along with the edge
	public Vertex getNeighbor(Vertex current)
	{
		if(!(current.equals(one) || current.equals(two)))
		{
			return null; //  vertex is itself. 
		}
		
		return (current.equals(one)) ? two : one;
	}
	
	public Vertex getOne()
	{
		return this.one; // return this vertex.
	}
	
	public Vertex getTwo()
	{
		return this.two; // return this vertex.
	}
	
	// gets the weight of the edge
	public int getWeight()
	{
		return this.weight;
	}
	
	// sets the new weight of the edge.
	public void setWeight(int weight)
	{
		this.weight = weight; 
	}
	
	public int compareTo(Edge e)
	{
		return this.weight - e.weight; // compare the weight.
	}
	
	
	public String toString()
	{
		return "({" + one  + ", " + two + "}, " + weight + ")";
	}
	
	// gets the hashcode for this edge by converting portnums of both ints to an Integer object.
	public int hashCode()
	{
		Integer edgeInt = new Integer(one.getVertexPortNum() + two.getVertexPortNum()); // converts the combination to a new Integer object.
		return edgeInt.hashCode(); // generates the hashcode for this node.
	}
	
	// True if object is an Edge with the same vertices as this Edge.
	public boolean equals(Object object)
	{
		if(!(object instanceof Edge))
		{
			return false;
		}
		
		Edge e = (Edge)object;
		return e.one.equals(this.one) && e.two.equals(this.two);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
