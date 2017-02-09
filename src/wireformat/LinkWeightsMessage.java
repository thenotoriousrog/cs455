package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import Graph.Graph;
import Graph.Vertex;
import overlay.Trio;

public class LinkWeightsMessage {

	/*
	 * vertices - holds all current vertices.
	 * Overlay - takes in our graph in which we will get all of the vertices to find the weights between them.
	 * 
	 * Creates a message of link weights for in order of each socket. i.e. socket1 will have all weights displayed in order
	 * then socket2 will have all link weights in a list form. 
	 * - This message is created once, the messaging node will be in charge of finding the node it's connected to and storing its weight accordingly.
	 */
	public byte[] getLinkWeightBytes(ArrayList<Vertex> vertices, Graph Overlay) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("Link_Weights\n"); // write the initial connection
		String convertedInt = Integer.toString(vertices.size());
		dataOutput.writeBytes("number of links: " + convertedInt + "\n"); // this is straight up incorrect.
		ArrayList<Trio<Vertex, Vertex, Integer>> edges = new ArrayList<Trio<Vertex, Vertex, Integer>>(); // holds list of edges.
		edges = Overlay.getEdgeList(); // get the list of edges and link weights.
		
		// Go through vertices one at a time finding edges and link weights.
		for(int i = 0; i < vertices.size()-1; i++)
		{
			// check every other vertex with current vertex.
			for(int j = i + 1; j < vertices.size(); j++)
			{
				// go through list of edges and find the ones that match with vertex i and j and then print the associated message with the weights.
				for(int k = 0; k < edges.size(); k++)
				{
					if( (vertices.get(i).getVertexPortNum() == edges.get(k).getFirst().getVertexPortNum()) // check if first vertex is equivalent.
							&& (vertices.get(j).getVertexPortNum() == edges.get(k).getSecond().getVertexPortNum())) // check if second vertex is equivalent
					{
						// Both vertices match meaning we have an edge between them. write the Message and get the weight between the two.
						
						// write the Linkinfo message i.e. IPaddr:portnum IPaddr:portnum weightbetweenLinks
						String V1_IPaddr = edges.get(k).getFirst().getVertexIPaddr(); // we can use edgesList here because we said it was equal to current vertex here.
						String V1_portnum = edges.get(k).getFirst().convertToString(edges.get(k).getFirst().getVertexPortNum()); // get string version of portnum of first vertex.
						
						String V2_IPaddr = edges.get(k).getSecond().getVertexIPaddr(); // we can use edgesList here because we said it was equal to current vertex here.
						String V2_portnum = edges.get(k).getSecond().convertToString(edges.get(k).getSecond().getVertexPortNum()); // get string version of portnum of first vertex
						
						int edgeWeight = edges.get(k).getThird(); // the edge weight between the two vertices.
						String converted = Integer.toString(edgeWeight);
						
						dataOutput.writeBytes(V1_IPaddr + ":" + V1_portnum + " " + V2_IPaddr + ":" + V2_portnum + " " + converted + "\n"); // write the link weight info. 
					}		
				}
			}
		}
		
		// clean up
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray();
		baOutStream.close();
		dataOutput.flush();
		
		return marshalledBytes; // return the message that we are trying to send.
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
