package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// this will marshal and unmarshal the registration message being sent to the Registry.
public class RegistrationRequestMessage {

	// marshals the registration message. Receives both portnum, and hostname from node creating the message.
	public byte[] getRegistrationBytes(int portnum, Socket nodeSocket) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("REGISTER_REQUEST "); // ask the registry to register the node.
		//dataOutput.writeBytes(hostname + " "); // add the hostname as well for the sake of connecting back to the messaging node.
		//dataOutput.writeBytes(nodeSocket.getInetAddress().toString() + " "); // write the ip address of the MessagingNode.
		dataOutput.writeBytes(nodeSocket.getInetAddress().getHostAddress() + " ");
		
		String convertedPortnum = Integer.toString(portnum); // convert the portnum into a string to send to TCPReceiver.
		dataOutput.writeBytes(convertedPortnum);
		
		// clean up
		dataOutput.flush(); // flush the stream.
		marshalledBytes = baOutStream.toByteArray(); // get the marshalled byte[]
		baOutStream.close(); // close the byte array stream.
		dataOutput.close(); // close the data output stream.
		
		return marshalledBytes; // return the marshalled byte[]. Send to registry.
	}
	
	
	public static void main(String[] args) {

	}

}
