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

import wireformat.RegistrationMessage;
import wireformat.WireFormat;

// creates the structure of the messaging nodes.
public class MessagingNode implements Node {
	
	private static ServerSocket serverSocket; // allows each messaging node to open a port and allow connections.
	private static Socket clientSocket; // the node will retrieve data from this socket from clients.
	private static int portNum = 0; // this will hold a random port number. 
	private static String hostname = ""; // holds the hostname created from within the console.

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
	public static void registerNode(Socket registrySocket, int portNum)
	{
		try 
		{
			TCPSender registerMe = new TCPSender(registrySocket); // send registry socket to be used to send data to the Registry.
			RegistrationMessage registerMsg = new RegistrationMessage();
			byte[] msgBytes = registerMsg.getRegistrationBytes(portNum, registrySocket); // get the marshalled registration msg.
			registerMe.sendData(msgBytes); // send this registration msg to the registry.
			
		} catch (IOException e) {
			System.err.println("registerNode got error " + e.getMessage());
		} 
	}
	
	
	public static void main(String[] args) throws IOException {
		
		Socket registrySocket = new Socket("localhost", 9999);
		try 
		{
			serverSocket = new ServerSocket(0, 10); // give random portnum, accept 10 connections.
			InetAddress ip; 
			ip = InetAddress.getLocalHost(); // get hostname
			hostname = ip.getHostName();
			portNum = serverSocket.getLocalPort(); // assign the port number that was assigned to serverSocket.
			
			// register within the Registry
			
			System.out.println("test 1");
			
			// using registerNode to do all of the registration work.
			registerNode(registrySocket, portNum); // have messaging node get registered with the Registry.
			
			/*
			 * This works below to send a message. Commented out to get TCPSender to work.
			PrintWriter writeOut = new PrintWriter(registrySocket.getOutputStream(), true);
			writeOut.println(portNum); // send the registry the port number we are working with.
			writeOut.close(); // close the printWriter.
			*/
			System.out.println("test 2");
			System.out.println("Messaging node started on port " + portNum + " with hostname " + hostname); // alert that a node was started to the console.
		
		} catch(Exception e) {
			System.err.println("port " + portNum + " is already in use");
			System.exit(1);
		}
		
		while(true) // accept connections and communications.
		{
			try
			{
				clientSocket = serverSocket.accept(); // accept incoming connections.
				System.out.println("Connection established: " + clientSocket);
				
				// should be using TCPReceiver here.
				InputStreamReader inputstreamreader = new InputStreamReader(clientSocket.getInputStream());
			    BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			    String RegistryMsg = "";
			    
			    while((RegistryMsg = bufferedreader.readLine()) != null)
			    {
			    	System.out.println("Regsitry says: " + RegistryMsg);
			    }
				
				// THIS BELOW IS CAUSING A CONNECTION REFUSED EXCEPTION.
				//Thread connectionThread = new Thread(new TCPReceiver(clientSocket)); // start a new Thread on TCPReceiver to receive messages through clientSocket.
				//connectionThread.start(); // start receiving messages.
				
			} catch(Exception e) {
				System.err.println("Messaging node caught error: " + e.getMessage());
				//System.err.println("Error while attempting connection to port " + portNum); // tell which port has the error (should be the node name actually)
			}
		}
	}
}
