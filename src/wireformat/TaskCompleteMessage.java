package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// tells Registry that this node has completed its task.
public class TaskCompleteMessage {
	
	// generates the message combines with the messaging nodes IPaddress and portnum
	public byte[] getTaskCompleteMessage(String IPaddr, int nodePortnum) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("TASK_COMPLETE\n"); // tells the Registry that this node has completed its task.
		String convertedPortnum = Integer.toString(nodePortnum); // convert int to string.
		dataOutput.writeBytes(IPaddr + "\n");
		dataOutput.writeBytes(convertedPortnum + "\n");
		
		// clean up
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray();
		baOutStream.close();
		dataOutput.flush();
				
		return marshalledBytes; // return the message that we are trying to send.
	}

}
