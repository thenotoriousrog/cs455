package overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

// this will take in messages sent to the MessagingNodes
public class TCPNodeReceiver extends Thread {

	private Socket socket; // socket sent to TCPReceiver
	private DataInputStream dataIn;
	private MessagingNode node; // provides a copy of the registry so that we may use it to take in messages.
	
	public TCPNodeReceiver(Socket socket, MessagingNode NODE) throws IOException
	{
		this.socket = socket; // use the socket in which the node is sending the data.
		dataIn = new DataInputStream(socket.getInputStream()); // get data being sent.
		node = NODE; // set the messaging node instance.
	}
	
	
	// runs when we create a thread for the messaging node.
	public void run() 
	{
		int dataLength;
		boolean running = true;
		while(running)
		{
			try
			{
				dataLength = dataIn.readInt();
				
				byte[] data = new byte[dataLength];
				dataIn.readFully(data, 0, dataLength);
				
				String msg = new String(data);
				String threadname = Thread.currentThread().getName();
				System.out.println("TCPNodeReceiver got message " + msg + " threadname: " + threadname); // print the received message
				System.out.println(); // empty line for readability.
				
				node.TCPNodeMessage(msg);
				
			} catch(SocketException se) {
				System.out.println(se.getMessage());
				break; // if socket fails, we must break the loop.
			} catch(IOException ioe) {
				System.out.println(ioe.getMessage());
				break; // if something fails, we must break the loop.
			}
		}
	}
	
	public static void main(String[] args) {

	}

}