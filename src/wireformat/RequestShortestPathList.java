package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestShortestPathList {
	
	// tells the registry to produce the shortest path with the given portnum
	public byte[] getShortestPathRequestMessage(int currentPortnum, int targetPortnum) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("SHORTEST_PATH_REQUEST\n");
		String convertedPortnum = Integer.toString(currentPortnum);
		String convertedTarget = Integer.toString(targetPortnum);
		
		dataOutput.writeBytes(convertedPortnum + "\n"); // write current nodes portnum
		dataOutput.writeBytes(convertedTarget + "\n"); // write target portnum.
		
		// clean up
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray();
		baOutStream.close();
		dataOutput.flush();
		
		return marshalledBytes; // return the message we wish to send.
	}

}
