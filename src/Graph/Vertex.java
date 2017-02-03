package Graph;

import java.util.ArrayList;

import overlay.Pair;

// Builds a vertex for our graph.
public class Vertex {

	private ArrayList<Edge> squad; // vertex neighbors.
	private Pair<String, Integer> vertex; // node that is being placed as a vertex.
	private ArrayList<Pair<String, Integer>> vertexList = new ArrayList<Pair<String, Integer>>(); // holds a list of vertices.
	
	public Vertex(Pair<String, Integer> newNode)
	{
		vertex = newNode; // create a new vertex.
		vertexList.add(vertex); // add a new vertex to the list.
		this.squad = new ArrayList<Edge>(); // holds all edges within the squad
	}
	
	public ArrayList<Pair<String, Integer>> getVertexList()
	{
		return vertexList; // return the current vertex list.
	}
	
	// adds a vertex to the graph.
	public void addSquadMember(Edge edge)
	{
		if(this.squad.contains(edge))
		{
			return; // do not do anything, this edge already exists.
		}
		
		this.squad.add(edge); // add an edge between two squad mates.
	}
	
	// the index of the edge to get. See if the edge exists at that index.
	public boolean containsSquadMember(Edge edge)
	{
		return squad.contains(edge); // return result.
	}
	
	// gets the squad mate indicated at index.
	public Edge getSquadMember(int index)
	{
		return this.squad.get(index); // return this squad mate.
	}
	
	// removes a squad member.
	public void removeSquadMember(Edge edge)
	{
		squad.remove(edge); // remove squad member
	}
	
	// gets the number of vertices in the graph.
	public int getSquadCount()
	{
		return squad.size(); // return squad size.
	}
	
	// gets the portnum of the vertex.
	public Integer getVertexPortNum()
	{
		return vertex.getSecond(); // return the portnumber of the vertex node.
	}
	
	public String getVertexIPaddr()
	{
		return vertex.getFirst(); // return the IP address of the vertex node.
	}
	
	// converts port number to a string.
	public String convertToString(int vertexPortnum)
	{
		return Integer.toString(vertexPortnum); // return String version of the portnumber.
	}
	
	// returns hashcode of the port number. This is important as the portnumber will be unique, but the IP address may not always be.
	public int hashcode()
	{
		return vertex.getSecond().hashCode();
	}
	
	// used to compare objects. True if it is an instance of Vertex, false if not.
	public boolean equals(Object object)
	{
		if(!(object instanceof Vertex)) // check if object is an instance of Vertex.
		{
			return false;
		}
		
		Vertex v = (Vertex)object;
		return vertex.getSecond().equals(v.getVertexPortNum()); // check the portnum with current vertex with the new vertex. 
	}
	
	// creates a copy of the current squad. The advantage is that this can be modified without harming the original.
	public ArrayList<Edge> getSquad() // essentially this is getting the neighbors of the vertex.
	{
		return new ArrayList<Edge>(this.squad); // create a copy of the ArrayList
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
