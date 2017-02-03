package Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import overlay.Pair;

public class Graph {
    
    private HashMap<String, Vertex> vertices;
    private HashMap<Integer, Edge> edges;
    
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
    
    /*
     * This whole block of code can go away right here. It is no longer needed.
    
    // creates a topological sort and sends back the sorted ArrayList to the caller of this method.
    public ArrayList<Vertex> topologicalSort(ArrayList<Vertex> vertices, Graph graph)
    {
    	ArrayList<Vertex> result = new ArrayList<Vertex>(); // result of our topological sort
    	HashMap<Vertex, Integer> map = new HashMap<Vertex, Integer>();
    	
    	// calculate in degree of all vertices.
    	
    	ArrayList<Edge> squad = new ArrayList<Edge>();
    	squad = vertices.get(0).getSquad(); // gets neighbors // getsquadmember() gets a vertex.
    	
    	Vertex neighbor = null; // will be changed later in code.
    	
    	// sort through all vertices and check indegree of each.
    	for(int i = 0; i < vertices.size(); i++)
    	//for(int i = 0; i < vertices.size() - 1; i++)
    	{
    		// It is important to sort through each vertex and find neighbors. 
    		// that is what we are trying to do below is grab each vertex and find it's corresponding neighbors.
    		
    		// sort through each edge looking for neighbors of the current vertex.
    		for(int j = 0; j < squad.size(); j++)
    		//for(int j = i+1; j < vertices.size(); j++)
    		{
    			// getting j edges that exist on vertex i, then finding neighbor vertices on vertex i.
    			
    			// problem lies right here. Need to figure out how to get the neighboring vertices.
    			//System.out.println("edges in graph: " + graph.edges.size()); 
    			// graph.edges.get() requires that we get a hashcode between two vertices! That way we can check if an edge exists!
    			// I should try to redo this loop so that I can be sure to get all vertices within the graph.
    			//Vertex a = vertices.get(i);
    			//Vertex b = vertices.get(j);
    			//edgeHashcode = a.getVertexPortNum() + b.getVertexPortNum(); // hashcode of current edge we are looking at.
    			//System.out.println("edgehashcode " + edgeHashcode); // print the hashcode.
    			//System.out.println("another way to get an edge: " + graph.edges.get(edges.get(j).hashCode())); // get the current edges hashcode.
    			//System.out.println("another way to get an edge 2: " + graph.edges.get(edgeHashcode.hashCode())); // another way to get an edge.
    			//System.out.println("testing usage of squad variable: " + graph.edges.get(squad.get(0).hashCode()).getNeighbor(vertices.get(i)).getVertexPortNum());
    			//System.out.println("Printing neighbor that we are getting: " + graph.edges.get(edgeHashcode.hashCode()).getNeighbor(vertices.get(i)).getVertexPortNum());
    			if(graph.edges.get(squad.get(0).hashCode()).getNeighbor(vertices.get(i)) != null)
    			{
    				System.out.println("topological sort test 1");
    				//System.out.println("we got vertex with portnum: " + graph.edges.get(squad.get(j).hashCode()).getNeighbor(vertices.get(i)).getVertexPortNum());
    				neighbor = graph.edges.get(squad.get(j).hashCode()).getNeighbor(vertices.get(i)); // neighbor vertex.
    				//System.out.println("neighbor vertex has portnum: " + neighbor.getVertexPortNum());
    				if(map.containsKey(neighbor))
        			{
    					System.out.println("topological sort test 2");
        				map.put(neighbor, map.get(neighbor) + 1); // assign into the map.
        			}
        			else
        			{
        				System.out.println("topological sort test 3");
        				map.put(neighbor, 1);
        			}
    			}
    			else
    			{
    				continue; // continue the loop
    			}
    		}// end loop 2
    	}// end loop 1
    	
    	Queue<Vertex> queue = new LinkedList<Vertex>(); // queue to hold our vertices in topological order.
		
		// now need to find nodes with indegree 0 i.e. meaning vertices with no incoming edges.
		for(int i = 0; i < vertices.size(); i++)
		{
			if(!map.containsKey(vertices.get(i))) // check each vertex in the map and see if it has any edges.
			{
				System.out.println("topological sort  4");
				queue.offer(vertices.get(i)); // add vertex into the queue.
				result.add(vertices.get(i)); // add vertex into our result first as these do not have any incoming edges.
			}
		}
		
		System.out.println("topological sort test 5");
		// now we search through the other nodes using BFS.
		while(!queue.isEmpty())
		{
			System.out.println("topological sort test 6");
			Vertex v = queue.poll(); // gets the next vertex from head of the queue.
			for(int i = 0; i < squad.size(); i++) // squad has list of all neighbors and can easily get the neighbor vertex, see loops above.
			{
				Vertex n = graph.edges.get(squad.get(i).hashCode()).getNeighbor(v); // gotta generate the hashcode of the edges in order to find them.
				map.put(n, map.get(n) - 1);
				if(map.get(n) == 0)
				{
					System.out.println("Topological sort test 7");
					queue.offer(n);
					result.add(n); // add result into our topological sort.
				}
			}
		}
    	
		return result; // send the result back to the caller.
    }  
    */
}
