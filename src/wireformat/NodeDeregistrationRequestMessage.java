package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// creates the deregistration request to the Registry.
public class NodeDeregistrationRequestMessage {

	public byte[] getDeregistrationBytes(int portnum, Socket nodeSocket) throws IOException
	{
		byte[] marshalledBytes = null; // message to send to the registry.
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("DEREGISTER_REQUEST\n"); // ask registry to deregister this node.
		dataOutput.writeBytes(nodeSocket.getInetAddress().getHostAddress() + "\n");
		String convertedPortnum = Integer.toString(portnum); // convert the portnum to String to send to the registry.
		dataOutput.writeBytes(convertedPortnum + "\n");
		
		// clean up
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray(); // get our message to send to the registry.
		baOutStream.close();
		dataOutput.close();
		
		return marshalledBytes; // send this message to the Registry.
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
