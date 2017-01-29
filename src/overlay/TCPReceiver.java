package overlay;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

// will receive data from connecting nodes.
// this will be controlled via threads.
public class TCPReceiver extends Thread {

	private Socket socket; // socket sent to TCPReceiver
	private DataInputStream dataIn;
	private String message; // holds the message that was received.
	
	public TCPReceiver(Socket socket) throws IOException
	{
		this.socket = socket; // use the socket in which the node is sending the data.
		System.out.println("socket port number = " + socket.getLocalPort());
		dataIn = new DataInputStream(socket.getInputStream()); // get data being sent.
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
				//System.out.println("In TCPReceiver: portnumber = " + socket.getPort());
				dataLength = dataIn.readInt();
				
				byte[] data = new byte[dataLength];
				dataIn.readFully(data, 0, dataLength);
				
				String msg = new String(data);
				System.out.println("TCPReceiver got message " + msg); // print the received message
					
				running = false; // stop the loop
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
