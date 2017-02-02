package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import overlay.Pair;

public class MessagingNodesListMessage {

	// takes in the registered nodes and generates a list of the information that is being sent to the other messaging nodes.
	// also tells how many connections the messaging nodes must make.
	public byte[] getNodeListBytes(ArrayList<Pair<String, Integer>> registeredNodes, int numOfConnections) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("MESSAGING_NODES_LIST\n"); // send the header of the messaging node list that is incoming.
		String convertedInt = Integer.toString(numOfConnections); // convert the portnum into a string to send to TCPReceiver.
		dataOutput.writeBytes("Number of peer messaging nodes: " + convertedInt + "\n"); // tell how many connections the messaging node must make.
		
		// loop through all registered nodes and write their information.
		for(int i = 0; i < registeredNodes.size(); i++)
		{
			String ipaddress = registeredNodes.get(i).getFirst(); // get the IP address of the messaging Node.
			String convertedPortNum = Integer.toString(registeredNodes.get(i).getSecond()); // convert the portnum of the node into a string to send to the other nodes.
			String convertedNodeNum = Integer.toString(i); // convert the counter i to string to use to count the number of nodes in the system.
			dataOutput.writeBytes("Messaging node" + convertedNodeNum + " " + ipaddress + " " + convertedPortNum + "\n"); // writing messaging node info.
		
		}// done writing messaging node info.
		
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
