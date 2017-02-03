package Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {
    
    private Graph graph;
    private int initialVertexPortNum;
    private HashMap<String, String> predecessors;
    private HashMap<String, Integer> distances; 
    private PriorityQueue<Vertex> availableVertices;
    private HashSet<Vertex> visitedVertices; 
    
    
    // converts an int to a string.
 	public String convertToString(int anInt)
 	{
 		return Integer.toString(anInt); // return String version of the portnumber.
 	}
    
    // Finds the shortest path between vertices using Dijkstra's algorithm.
    public Dijkstra(Graph graph, int firstVertexPortNum)
    {
        this.graph = graph;
        Set<String> vertexKeys = this.graph.vertexKeys();
        
        /* remove this later.
        if(!vertexKeys.contains(initialVertexPortNum)){
            throw new IllegalArgumentException("The graph must contain the initial vertex.");
        }
        */
        
        this.initialVertexPortNum = firstVertexPortNum;
        this.predecessors = new HashMap<String, String>();
        this.distances = new HashMap<String, Integer>();
        this.availableVertices = new PriorityQueue<Vertex>(vertexKeys.size(), new Comparator<Vertex>()
        {
            // compare weights of these two vertices.
            public int compare(Vertex v1, Vertex v2)
            {
            	// errors are here.
                int weightOne = Dijkstra.this.distances.get(v1.convertToString(v1.getVertexPortNum())); // distances stored by portnum in string form.
                int weightTwo = Dijkstra.this.distances.get(v2.convertToString(v2.getVertexPortNum())); // distances stored by portnum in string form.
                return weightOne - weightTwo; // return the modified weight of the two vertices.
            }
        });
        
        this.visitedVertices = new HashSet<Vertex>();
        
        // for each Vertex in the graph
        for(String key: vertexKeys)
        {
            this.predecessors.put(key, null);
            this.distances.put(key, Integer.MAX_VALUE); // assuming that the distance of this is infinity.
        }
        
        
        //the distance from the initial vertex to itself is 0
        this.distances.put(convertToString(initialVertexPortNum), 0); 
        
        //and seed initialVertex's neighbors
        Vertex initialVertex = this.graph.getVertex(convertToString(initialVertexPortNum)); // converted portnum to String to make this work.
        ArrayList<Edge> initialVertexNeighbors = initialVertex.getSquad();
        for(Edge e : initialVertexNeighbors)
        {
            Vertex other = e.getNeighbor(initialVertex);
            this.predecessors.put(convertToString(other.getVertexPortNum()), convertToString(initialVertexPortNum));
            this.distances.put(convertToString(other.getVertexPortNum()), e.getWeight()); // converted portnum to String to make this work.
            this.availableVertices.add(other); // add to available vertices.
        }
        
        this.visitedVertices.add(initialVertex); // add to list of visited vertices.
        
        // apply Dijkstra's algorithm to the overlay.
        processOverlay();
        
    }
    
    // applies Dijkstra's algorithm to the overlay using the first vertex that was sent in.
    private void processOverlay(){
        
        // go for as long as edges still exist.
        while(this.availableVertices.size() > 0)
        {
            
            // pick the least weight vertex.
            Vertex next = this.availableVertices.poll(); // finding the least weight vertex.
            int distanceToNext = this.distances.get(convertToString(next.getVertexPortNum())); // must use the string version of the portnum.
            
            // and for each available squad member of the chosen vertex
            List<Edge> nextSquad = next.getSquad();     
            for(Edge e: nextSquad)
            {
                Vertex other = e.getNeighbor(next);
                if(this.visitedVertices.contains(other))
                {
                    continue; // continue the while loop in this case.
                }
                
                // check for a shorter path, update if a new path is found within the overlay.
                int currentWeight = this.distances.get(convertToString(other.getVertexPortNum())); // using string version of the port number.
                int newWeight = distanceToNext + e.getWeight();
                
                if(newWeight < currentWeight){
                    this.predecessors.put(convertToString(other.getVertexPortNum()), convertToString(next.getVertexPortNum())); // use the string versions of the port numbers.
                    this.distances.put(convertToString(other.getVertexPortNum()), newWeight); // use the string version of the port numbers.
                    // updating here.
                    this.availableVertices.remove(other);
                    this.availableVertices.add(other);
                }
                
            }
            
            this.visitedVertices.add(next); // add this vertex as "visited" so we do not visit it again.
        }
    }
    
    // gets the path to the vertex that ending in the one with the portnum that we pass into it.
    public List<Vertex> getPathTo(int targetVertexPortnum){
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        path.add(graph.getVertex(convertToString(targetVertexPortnum))); // use string version of the port num.
        
        while(targetVertexPortnum != this.initialVertexPortNum)
        {
            Vertex predecessor = graph.getVertex(this.predecessors.get(convertToString(targetVertexPortnum))); // use string version of the portnum.
            targetVertexPortnum = predecessor.getVertexPortNum(); // get the int representation of the vertex portnum.
            path.add(0, predecessor); // add the vertex to our shortest path.
        }
        
        return path; // return the shortest path to the target vertex.
    }
    
    
    // get distance from initial vertex. is this the weight?
    public int getDistanceTo(int targetVertexPortNum)
    {
        return this.distances.get(convertToString(targetVertexPortNum)); // have to use the string version of the integer.
    }
    
    
    public static void main(String[] args){
       
    }
}
