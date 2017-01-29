package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegistryRegistrationResponseMessage {

	
	// marshals the response message. 1 = successful registration
	public byte[] getSuccessBytes(Socket nodeSocket) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		dataOutput.writeBytes("REGISTER_RESPONSE "); // ask the registry to register the node.
		String statusByte = Integer.toString(1); 
		dataOutput.writeBytes(statusByte + " "); // Success code is 1 when node is successfully registered.	May need to convert to string first.
		dataOutput.writeBytes("Messaging Node was added to the Registry."); // Additional info can edit later.
			
		// clean up
		dataOutput.flush(); // flush the stream.
		marshalledBytes = baOutStream.toByteArray(); // get the marshalled byte[]
		baOutStream.close(); // close the byte array stream.
		dataOutput.close(); // close the data output stream.
			
		return marshalledBytes; // return the marshalled byte[]. Send to registry.
	}
	
	// marshals the response message. -1 = unsuccessful registration
	public byte[] getFailureBytes(Socket nodeSocket) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		dataOutput.writeBytes("REGISTER_RESPONSE "); // ask the registry to register the node.
		String statusByte = Integer.toString(-1); 
		dataOutput.writeBytes(statusByte + " "); // Success code is -1 when node is successfully registered.	May need to convert to string first.
		dataOutput.writeBytes("Messaging Node was NOT added to the Registry."); // Additional info can edit later.
				
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
