package overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class nodeConnectionThread extends Thread {

	Socket nodeSocket; // socket used to communicate with connected node.
	int nodeID = -1; // node we are working on with current thread. 
	boolean running = true; // check if this node is still active on its thread.
	
	// constructor to do some work.
	nodeConnectionThread(Socket currentSocket, int ID)
	{
		nodeSocket = currentSocket;
		nodeID = ID;
	}
	
	// here is where we will do our work with the MessagingNode
	public void run()
	{
		System.out.println("Accepted connection from Messaging Node: " + nodeSocket + " with ID: " + nodeID); // alert console of successful connection.
		/*
		 * Steps:
		 * 	- We will use TCPReceiver to read in the data from the MessagingNode
		 * 		- This will involve creating a new Thread within this thread that will receive messages. We can use that thread.start() snippet that I have it will work.
		 *  - We will first print the message to console to make sure it is the message that we have indeed sent to the registry from the MessagingNode.
		 *  - Once we have TCPReceiver taking in messages from the node, we will construct our TCPSender.
		 *  - Not sure yet, but TCPSender may have to be on another thread, but first let's build it not that way.
		 *  	- Construct TCPSender the same way that was done in MessaginNode.java. May have to create a new class.
		 *  
		 *  - Once we get messages from the MessagingNode using TCPSender we know that TCPSender is working. 
		 *  - Use TCPReceiver within the MessagingNode and make it a threaded server to handle incoming connection from other nodes as well as the Registry.
		 *  - Look back in notebook as well as documentation about what to do next.
		 */
		
		Thread messagingNodeThread; // start a new Thread on TCPReceiver to receive messages through clientSocket.
		try 
		{
			messagingNodeThread = new Thread(new TCPReceiver(nodeSocket));
			messagingNodeThread.start(); // start receiving messages.				
			
		} 
		catch (IOException e) {
			System.err.println("messagingNodeThread got error: " + e.getMessage());
		} 
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
