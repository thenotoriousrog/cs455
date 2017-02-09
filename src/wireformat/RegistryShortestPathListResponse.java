package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import Graph.Vertex;

public class RegistryShortestPathListResponse {
	
	// sends the shortest path list back to the messaging node that requested it.
	public byte[] getShortestPathListMessage(List<Vertex> shortestPath) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("SHORTEST_PATH_LIST\n"); // tell messaging node to process this message.
		
		String IPaddr = ""; // holds IPaddr
		int portnum = 0; // holds portnum
		String convertedPortnum = ""; // holds the converted portnum.
		
		// go through shortest path list and write the path by IPaddr:portnum
		for(int i = 0; i < shortestPath.size(); i++)
		{
			IPaddr = shortestPath.get(i).getVertexIPaddr(); 
			portnum = shortestPath.get(i).getVertexPortNum();
			convertedPortnum = Integer.toString(portnum);
			
			dataOutput.writeBytes(IPaddr + ":" + convertedPortnum + "\n"); // write each shortest path as IPaddr:portnum
		}
		
		// clean up
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray();
		baOutStream.close();
		dataOutput.flush();
		
		return marshalledBytes; // return the shortest path response.
	}

}
