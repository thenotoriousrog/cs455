package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PayloadMessage {
	
	// converts the payload into a byte[] and creates a message that we can use to send to other messaging nodes.
	public byte[] getPayloadMessage(int Payload) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("Payload\n");
		String convertedPayload = Integer.toString(Payload);
		
		dataOutput.writeBytes(convertedPayload + "\n"); // write payload.
		
		// clean up
		dataOutput.flush(); // flush the stream.
		marshalledBytes = baOutStream.toByteArray(); // get the marshalled byte[]
		baOutStream.close(); // close the byte array stream.
		dataOutput.close(); // close the data output stream.
		
		return marshalledBytes; // return message that will be sent to the messaging nodes.
	}

}
