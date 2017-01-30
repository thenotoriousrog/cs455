package overlay;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import wireformat.RegistrationRequestMessage;
import wireformat.WireFormat;

// creates the structure of the messaging nodes.
public class MessagingNode implements Node, Runnable {
	
	private static ServerSocket serverSocket; // allows each messaging node to open a port and allow connections.
	private static Socket clientSocket; // the node will retrieve data from this socket from clients.
	private static int portNum = 0; // this will hold a random port number. 
	private static String hostname = ""; // holds the hostname created from within the console.
	private static MessagingNode messagingNode; // an instance that will be sent to TCPNodeReceiver to help receive messages.

	// get the Messaging node's port number.
	public int getPortNum()
	{
		return portNum; // return the port number associated with the Messaging node.
	}
	
	// get the Messaging node's hostname.
	public static String getHostname()
	{
		return hostname; // send the hostname
	}
	
	// this method will be in control of generating the registration message and telling the registry to register the MessagingNode.
	public static void registerNode(Socket registrySocket, int portNum, String hostname)
	{
		try 
		{
			TCPSender registerMe = new TCPSender(registrySocket); // send registry socket to be used to send data to the Registry.
			RegistrationRequestMessage registerMsg = new RegistrationRequestMessage();
			byte[] msgBytes = registerMsg.getRegistrationBytes(portNum, registrySocket); // get the marshalled registration msg.
			registerMe.sendData(msgBytes); // send this registration msg to the registry.
			
		} catch (IOException e) {
			System.err.println("registerNode got error " + e.getMessage());
		} 
	}
	
	// this will take in the messages that TCPNodeReceiver receives.
	public void TCPmessage(String message)
	{
		System.out.println("MessagingNode got message: " + message + " ~from Registry.");
	}
	
	// this will run when we start threading the MessagingNode.
	public void run()
	{
		System.out.println("Connection established: " + clientSocket);
		Thread messageThread; // start a new Thread on TCPReceiver to receive messages through clientSocket.
		try 
		{
			messageThread = new Thread(new TCPNodeReceiver(clientSocket, messagingNode), "tcpReceiverThread"); // pass a copy of the MessagingNode to TCPNodeReceiver.
			messageThread.start(); // start receiving messages.					
		} 
		catch (IOException e) {
			System.err.println("messagingNodeThread got error: " + e.getMessage());
		} 
	}
	
	public static void main(String[] args) throws IOException {
		
		Socket registrySocket = new Socket("localhost", 9999);
		messagingNode = new MessagingNode(); // create new messaging node instance.
		
		try 
		{
			serverSocket = new ServerSocket(0, 10); // give random portnum, accept 10 connections max.
			InetAddress ip; 
			ip = InetAddress.getLocalHost(); // get hostname
			hostname = ip.getHostName();
			portNum = serverSocket.getLocalPort(); // assign the port number that was assigned to serverSocket.
			
			registerNode(registrySocket, portNum, hostname); // have messaging node get registered with the Registry.
			
			System.out.println("Messaging node started on port " + portNum + " with hostname " + hostname); // alert that a node was started to the console.
		
		} catch(Exception e) {
			System.err.println("port " + portNum + " is already in use");
			System.exit(1);
		}
		
		// start a thread for listening for user input.
		Thread userInputThread = new Thread(new NodeUserInput(messagingNode), "Node_User_Input_Thread"); // create thread for taking in user input.
		userInputThread.start(); // start listening for user input.
		
		while(true) // accept connections and communications.
		{
			try
			{
				clientSocket = serverSocket.accept(); // accept incoming connections.
				
				Thread msgNodeThread = new Thread(messagingNode, "Messaging_Node_Thread");
				msgNodeThread.start(); // start the thread to start receiving messages.
				
			} catch(Exception e) {
				System.err.println("Messaging node caught error: " + e.getMessage());
			}
		}
	}
}
