package overlay;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// sends messages between threads. The messages are created in WireFormat.
public class TCPSender {

	private Socket socket;
	private DataOutputStream dataOut; 
	
	public TCPSender(Socket socket) throws IOException
	{
		this.socket = socket; // socket that a messaging node is sending data through.
		dataOut = new DataOutputStream(socket.getOutputStream()); // get the current data.
	}
	
	// we should marshall the data before using sendData to another node!
	public void sendData(byte[] dataToSend) throws IOException
	{
		int dataLength = dataToSend.length;
		dataOut.writeInt(dataLength);
		dataOut.write(dataToSend, 0, dataLength);
		dataOut.flush(); // flush out the stream.
		
		String msg = new String(dataToSend);
		//System.out.println("sending data " + msg);
	}
	
	public static void main(String[] args) {

	}

}
