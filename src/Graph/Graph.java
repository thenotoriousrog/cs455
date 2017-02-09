package Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import overlay.Trio;

public class Graph {
    
    private HashMap<String, Vertex> vertices;
    private HashMap<Integer, Edge> edges;
    private ArrayList<Trio<Vertex, Vertex, Integer>> edgeList = new ArrayList<Trio<Vertex, Vertex, Integer>>(); // simple list to be used to generate link weights.
    
    public Graph()
    {
        this.vertices = new HashMap<String, Vertex>(); // String holds the port number identified with each vertex.
        this.edges = new HashMap<Integer, Edge>();
    }
    
    // converts an int to a string.
 	public String convertToString(int anInt)
 	{
 		return Integer.toString(anInt); // return String version of the portnumber.
 	}
    
 	public ArrayList<Trio<Vertex, Vertex, Integer>> getEdgeList()
 	{
 		return edgeList; // return the list of edges.
 	}
 	
    // populates arraylist with vertices.
    public Graph(ArrayList<Vertex> vertices){
        
    	this.vertices = new HashMap<String, Vertex>();
        this.edges = new HashMap<Integer, Edge>();
        
        for(Vertex v: vertices)
        {
            this.vertices.put(v.convertToString(v.getVertexPortNum()), v); // converts portnum to string. This is the vertex ID if you will.
        }    
    }
    
    // adds an edge between a vertex and an edge.
    public boolean addEdge(Vertex one, Vertex two)
    {
        return addEdge(one, two, 1);
    }
    
    // checks two vertices and a weight, then will return true if the edge does not exist.
    public boolean addEdge(Vertex one, Vertex two, int weight)
    {
    	System.out.println("trying to add edge now.");
        if(one.equals(two))
        {
            return false;   
        }
       
        // ensures the Edge is not in the Graph
        Edge e = new Edge(one, two, weight); // weight is being set here.
        if(edges.containsKey(e.hashCode()))
        {
            return false;
        }
       
        //and that the Edge isn't already incident to one of the vertices
        else if(one.containsSquadMember(e) || two.containsSquadMember(e))
        {
            return false;
        }
            
        edges.put(e.hashCode(), e); // put the edge in.
        Trio<Vertex, Vertex, Integer> newEdge = new Trio<Vertex, Vertex, Integer>(one, two, weight); // create a new Trio.
        edgeList.add(newEdge); // add the newly created edge into the trio.
        one.addSquadMember(e);
        two.addSquadMember(e);
        
        return true; // it been added.
    }
    
    // true if graph contains the edge
    public boolean containsEdge(Edge e)
    {
        if(e.getOne() == null || e.getTwo() == null) // check if the edge exists or not.
        {
            return false;
        }
        
        return this.edges.containsKey(e.hashCode());
    }
    
    // remove the vertex from the graph.
    public Edge removeEdge(Edge e){
       e.getOne().removeSquadMember(e);
       e.getTwo().removeSquadMember(e);
       return this.edges.remove(e.hashCode());
    }
    
    // check if the vertex exists in the graph.
    public boolean containsVertex(Vertex vertex)
    {
        return this.vertices.get(vertex.getVertexPortNum()) != null;
    }
    
    // gets vertex from the graph.
    public Vertex getVertex(String vertexPortnum)
    {
        return vertices.get(vertexPortnum);
    }
    
    // adds a vertex to the graph.
    public void addVertex(Vertex vertex)
    {
    	// ** Registry will make sure node does not exist before we add it to the graph ** no need to check for this **
    	vertices.put(vertex.convertToString(vertex.getVertexPortNum()), vertex); // add vertex to the graph.
    	
    	System.out.println("vertex was added! ");
    }
    
    // remove vertex from graph along with its corresponding Edge as well.
    public Vertex removeVertex(String vertexPortnum){
        Vertex v = vertices.remove(vertexPortnum);
        
        while(v.getSquadCount() > 0){
            this.removeEdge(v.getSquadMember((0)));
        }
        
        return v;
    }
    
    // get the names of all vertices in the graph. 
    public Set<String> vertexKeys(){
        return this.vertices.keySet();
    }
    
    // gets all edges of the graph.
    public Set<Edge> getEdges()
    {
        return new HashSet<Edge>(this.edges.values());
    }
}
