package Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Graph {
    
    private HashMap<String, Vertex> vertices;
    private HashMap<Integer, Edge> edges;
    
    public Graph(){
        this.vertices = new HashMap<String, Vertex>();
        this.edges = new HashMap<Integer, Edge>();
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
        if(one.equals(two))
        {
            return false;   
        }
       
        //ensures the Edge is not in the Graph
        Edge e = new Edge(one, two, weight);
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
        one.addSquadMember(e);
        two.addSquadMember(e);
        
        return true;
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
    public Vertex getVertex(String vertexPortnum){
        return vertices.get(vertexPortnum);
    }
    

    /*
    // adds vertex to the graph. If two vertices(nodes) have the same portnum we overwrite the node. Returns true if successfully added to the graph.
    public boolean addVertex(Vertex vertex, boolean overwriteExisting)
    {
        Vertex current = this.vertices.get(vertex.getVertexPortNum());
        if(current != null)
        {
            if(!overwriteExisting)
            {
                return false;
            }
            
            while(current.getSquadCount() > 0){
                this.removeEdge(current.getSquadMember(0));
            }
        }
        
        vertices.put(vertex.convertToString(vertex.getVertexPortNum()), vertex); // add vertex of the 
        return true;
    }
    */
    
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
    public Set<Edge> getEdges(){
        return new HashSet<Edge>(this.edges.values());
    }
    
}
