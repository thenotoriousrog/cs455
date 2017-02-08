package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// creates the TASK_INITIATE message which tell nodes to start sending messages to each other.
public class TaskInitiateMessage {
	
	public byte[] getTaskInitiateMessage(int numOfRounds) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("TASK_INITIATE\n");
		String converted = Integer.toString(numOfRounds); // convert the number of rounds into a String.
		dataOutput.writeBytes("Rounds: " + converted); // write the number of rounds.
		
		// clean up
		dataOutput.flush(); // flush the stream.
		marshalledBytes = baOutStream.toByteArray(); // get the marshalled byte[]
		baOutStream.close(); // close the byte array stream.
		dataOutput.close(); // close the data output stream.
		
		return marshalledBytes; // return the marshalled byte[]. Send to registry.
	}

}
