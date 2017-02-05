package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import Graph.Vertex;

public class MessagingNodesListMessage {

	// takes in list of registered nodes.
	// also tells how many connections the messaging nodes must make.
	// also gives which node we are working with so that we may be able to make a connection with said node.
	public byte[] getNodeListBytes(ArrayList<Vertex> vertices, int nodeNum, int numOfConnections) throws IOException
	{
		
		/*
		 * IMPORTANT NOTE
		 * My counter could be off causing mismatches in getting the correct node info. If so, check the nodeNum that is being sent in and make sure it is 
		 * accurate. 
		 * 
		 * 
		 */
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("MESSAGING_NODES_LIST\n"); // send the header of the messaging node list that is incoming.
		String convertedInt = Integer.toString(numOfConnections); // convert the portnum into a string to send to TCPReceiver.
		dataOutput.writeBytes("Number of peer messaging nodes: " + convertedInt + "\n"); // tell how many connections the messaging node must make.
		String convertedNodeNum = Integer.toString(nodeNum);
		
		nodeNum++; // increase the counter of nodeNum just to see if it fixes out of bound errors or not.
		
		// port numbers of nodes to connect to.
		String node1PortNum = "";
		String node2PortNum = "";
		String node3PortNum = "";
		String node4PortNum = "";
		
		// IP addresses of nodes to connect to
		String node1IPAddr = "";
		String node2IPAddr = "";
		String node3IPAddr = "";
		String node4IPAddr = "";
		
		if( (nodeNum > 2) && (nodeNum < vertices.size() - 2)) // the beginning and ending vertices need a special message formation. Working with the middle of these nodes.
		{
			// portNum to connect to.
			node1PortNum = Integer.toString(vertices.get(nodeNum-1).getVertexPortNum());
			node2PortNum = Integer.toString(vertices.get(nodeNum-2).getVertexPortNum());
			node3PortNum = Integer.toString(vertices.get(nodeNum+1).getVertexPortNum());
			node4PortNum = Integer.toString(vertices.get(nodeNum+2).getVertexPortNum());
			
			// IP addresses of nodes to connect to
			node1IPAddr = vertices.get(nodeNum-1).getVertexIPaddr();
			node2IPAddr = vertices.get(nodeNum-2).getVertexIPaddr();
			node3IPAddr = vertices.get(nodeNum+1).getVertexIPaddr();
			node4IPAddr = vertices.get(nodeNum+2).getVertexIPaddr();
			
			
			dataOutput.writeBytes("Messaging node" + convertedNodeNum + " " + node1PortNum + " " + node1IPAddr + "\n"); // node 1 to connect to.
			dataOutput.writeBytes("Messaging node" + convertedNodeNum + " " + node2PortNum + " " + node2IPAddr + "\n"); // node 2 to connect to.
			dataOutput.writeBytes("Messaging node" + convertedNodeNum + " " + node3PortNum + " " + node3IPAddr + "\n"); // node 3 to connect to.
			dataOutput.writeBytes("Messaging node" + convertedNodeNum + " " + node4PortNum + " " + node4IPAddr + "\n"); // node 4 to connect to.
		}
		else if(nodeNum == 2) // connects to first node, and last node then node+1 and node+2
		{
			// string formation
			String firstNodePortNum = Integer.toString(vertices.get(nodeNum-1).getVertexPortNum());
			String lastNodePortNum = Integer.toString(vertices.get(vertices.size()-1).getVertexPortNum());
			String firstNodeIPAddr = vertices.get(nodeNum-1).getVertexIPaddr(); 
			String lastNodeIPAddr = vertices.get(vertices.size()-1).getVertexIPaddr();
			
			node3PortNum = Integer.toString(vertices.get(nodeNum+1).getVertexPortNum());
			node4PortNum = Integer.toString(vertices.get(nodeNum+2).getVertexPortNum());
			node3IPAddr = vertices.get(nodeNum+1).getVertexIPaddr();
			node4IPAddr = vertices.get(nodeNum+2).getVertexIPaddr();
			
			// writing node info
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + firstNodePortNum + " " + firstNodeIPAddr + "\n"); // connect to first node.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + lastNodePortNum + " " + lastNodeIPAddr + "\n"); // connect to last node.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node3PortNum + " " + node3IPAddr + "\n"); // connect to node+1
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node4PortNum + " " + node4IPAddr + "\n"); // connect to node+2
		}
		else if(nodeNum == 1) // connects to last node, second to last node, then node+1 and node+2
		{
			// string formation
			String lastNodePortNum = Integer.toString(vertices.get(vertices.size()-1).getVertexPortNum());
			String secToLastNodePortNum = Integer.toString(vertices.get(vertices.size()-2).getVertexPortNum());
			String lastNodeIPAddr = vertices.get(vertices.size()-1).getVertexIPaddr();
			String secToLastNodeIPAddr = vertices.get(vertices.size()-2).getVertexIPaddr();
			
			node3PortNum = Integer.toString(vertices.get(nodeNum+1).getVertexPortNum());
			node4PortNum = Integer.toString(vertices.get(nodeNum+2).getVertexPortNum());
			node3IPAddr = vertices.get(nodeNum+1).getVertexIPaddr();
			node4IPAddr = vertices.get(nodeNum+2).getVertexIPaddr();
			
			// writing node info
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + lastNodePortNum + " " + lastNodeIPAddr + "\n"); // node 1 to connect to.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + secToLastNodePortNum + " " + secToLastNodeIPAddr + "\n"); // node 2 to connect to.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node3PortNum + " " + node3IPAddr + "\n"); // node 3 to connect to.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node4PortNum + " " + node4IPAddr + "\n"); // node 4 to connect to.
		}
		else if(nodeNum == vertices.size()) // connects to lastnode-1, lastnode-2, but connects to first node, and second node.
		{
			// string formation
			String firstNodePortNum = Integer.toString(vertices.get(0).getVertexPortNum());
			String secondNodePortNum = Integer.toString(vertices.get(1).getVertexPortNum());
			String firstNodeIPAddr = vertices.get(0).getVertexIPaddr();
			String secondNodeIPAddr = vertices.get(1).getVertexIPaddr();
			
			node1PortNum = Integer.toString(vertices.get(nodeNum-1).getVertexPortNum());
			node2PortNum = Integer.toString(vertices.get(nodeNum-2).getVertexPortNum());
			node1IPAddr = vertices.get(nodeNum-1).getVertexIPaddr();
			node2IPAddr = vertices.get(nodeNum-2).getVertexIPaddr();
			
			// writing node info
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node1PortNum + " " + node1IPAddr + "\n"); // connect to first node.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node2PortNum + " " + node2IPAddr + "\n"); // connect to second node.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + firstNodePortNum + " " + firstNodeIPAddr + "\n"); // connect to node+1
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + secondNodePortNum + " " + secondNodeIPAddr + "\n"); // connect to node+2
		}
		else // nodeNum == vertices.size()-1, connects to lastnode, connects to node1, connects to node-1, connects to node-2
		{
			// string formation
			String firstNodePortNum = Integer.toString(vertices.get(0).getVertexPortNum());
			String firstNodeIPAddr = vertices.get(0).getVertexIPaddr();
			
			
			String lastNodePortNum = Integer.toString(vertices.get(vertices.size()-1).getVertexPortNum());
			node3PortNum = Integer.toString(vertices.get(nodeNum-1).getVertexPortNum());
			node4PortNum = Integer.toString(vertices.get(nodeNum-2).getVertexPortNum());
			
			String lastNodeIPAddr = vertices.get(vertices.size()-1).getVertexIPaddr();
			node3IPAddr = vertices.get(nodeNum-1).getVertexIPaddr();
			node4IPAddr = vertices.get(nodeNum-2).getVertexIPaddr();
			
			// writing node info
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + firstNodePortNum + " " + firstNodeIPAddr + "\n"); // connect to first node.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + lastNodePortNum + " " + lastNodeIPAddr + "\n"); // connect to last node.
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node3PortNum + " " + node3IPAddr + "\n"); // connect to node-1
			dataOutput.writeBytes("Messaging Node" + convertedNodeNum + " " + node4PortNum + " " + node4IPAddr + "\n"); // connect to node-2
		}
		
		// clean up
		dataOutput.flush(); // flush the stream.
		marshalledBytes = baOutStream.toByteArray(); // get the marshalled byte[]
		baOutStream.close(); // close the byte array stream.
		dataOutput.close(); // close the data output stream.
		
		return marshalledBytes; // return the marshalled byte[]. Send to registry.
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
