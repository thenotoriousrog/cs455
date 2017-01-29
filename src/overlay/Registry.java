package overlay;
/*
 * This is the registry class. Here the registry will be created which keeps track of all messaging transmissions.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class Registry {
	
	private static ServerSocket registryServer; // serversocket that the registry will be using.
	private static Socket nodeSocket; // socket that the registry will use to communicate between the nodes.
	
	// Pair<String, Integer> is in the form of hostname, portnum for the nodes.
	private static ArrayList<Pair<String, Integer>> registeredNodes = new ArrayList<Pair<String, Integer>>(); // an array list of the hostname/portnum node Pairs
	

	// check if any hostnames match the current node's
	public static boolean matchedHostnames(ArrayList<Pair<String, Integer>> registeredNodes, String hostname)
	{
		boolean matched = false; // if a hostname matches, set to true.
		
		// loop through and check all hostnames.
		for(int i = 0; i < registeredNodes.size(); i++)
		{
			if(registeredNodes.get(i).getFirst() == hostname) 
			{
				matched = true;  // hostnames match.
			}
		}
		
		return matched; // return whether a hostname matches or not.
	}
	
	// check if any portnumber matches the current node.
	public static boolean matchedPortnums(ArrayList<Pair<String, Integer>> registeredNodes, int portnum)
	{
		boolean matched = false; // if a port num matches, set to true.
		
		// loop through and check all portnumbers.
		for(int i = 0; i < registeredNodes.size(); i++)
		{
			if(registeredNodes.get(i).getSecond() == portnum) // if any port number matches do not register the node.
			{
				matched = true;  // portnums match.
			}
		}
		
		return matched; // return whether a port num matches or not.
	}
	
	// checks if a MessagingNode is registered or not.
	public static boolean isRegistered(int portnum, String hostname)
	{
		if(registeredNodes.isEmpty())
		{
			return false; // register this node.
		}
		else
		{
			boolean hostnamesMatch = matchedHostnames(registeredNodes, hostname); // returns true if any hostnames match
			boolean portnumsMatch = matchedPortnums(registeredNodes, portnum); // returns true if portnums match.
			
			if(hostnamesMatch && portnumsMatch == true) // node is already registered.
			{
				return true; // do not register this node again.
			}
			else
			{
				return false; // register this node.
			}
		}
	}
	
	// nodes sending a registration command are sent here.
	public static int register(int portnum, String hostname)
	{
		if(isRegistered(portnum, hostname) == false) // check if node is registered.
		{
			Pair<String, Integer> node = new Pair<String, Integer>(); // create a new node Pair.
			node.setFirst(hostname); // set the hostname field of the pair.
			node.setSecond(portnum); // set the portnum field of the pair.
			
			registeredNodes.add(node); // add new node to Registry
			return 1; // 1 == success code to the MessagingNode.
		}
		else
		{
			return 0; // 0 == failure code to the MessagingNode, was not registered.
		}	
	}
	
	// nodes sending deregistration command are sent here.
	public static int deregister(int portnum, String hostname)
	{
		return 0; // change this!
	}
	
	// action requests by Messaging Nodes are sent here in the form of ints.
	// a code is sent back to the node in the form of an integer detailing if its request was successful or not.
	public static int NodeRequest(int request, int portnum, String hostname)
	{
		if(request == 1) // registration request.
		{
			return register(portnum, hostname); // 1 == success, 0 == failure
		}
		else if(request == 0) // deregistration request.
		{
			return deregister(portnum, hostname); // 1 == success, 0 == failure
		}
		else
		{
			return -1; // -1 specifies that a node's request was not recognized, handle the error for that case!
		}
	}
	
	
	public static void main(String[] args) {
		// note: may want to add a thread within the registry to listen for commands.
		
		try 
		{
			registryServer = new ServerSocket(9999); // create a server for the registry to communicate through.
			System.out.println("Registry has started."); 
			
			int threadID = 0; // this will allow me to keep track of how many threads are being created.
			// continue checking for new connections.
			while(true)
			{
				nodeSocket = registryServer.accept(); // accept connections from nodes. Remember code pauses here until a connection has been made.
				//System.out.println("Accepted connection: " + nodeSocket); 
				System.out.println("test 1"); // first test to make sure things go smoothely.
				Thread nodeConnectionThread = new nodeConnectionThread(nodeSocket, ++threadID); // create a new Thread class to handle the node connection.
				nodeConnectionThread.start(); // start this thread to handle incoming Messaging Nodes.
				
			}
			
			/*
			// this is throwing an error.
			// just try to get some nodes communicating. This is very important!
			Thread messagingThread = new Thread(new TCPReceiver(nodeSocket));
			messagingThread.start(); // start the thread.
			System.out.println("test 3");
			
			TCPSender sendToNode = new TCPSender(nodeSocket); // sending a message to a node.
			byte[] msg = "hello node".getBytes(); // create a byte array to send to node.
			sendToNode.sendData(msg); // send message to the connected node.
			*/
			
			//InputStreamReader inputstreamreader = new InputStreamReader(nodeSocket.getInputStream());
		    //BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		    
		   // String nodeMsg = ""; // message read from node.
		   // Integer nodePortNum = 0; // holds the port number of the connected messaging node.
		    
		    // Using TCPReciever now!
		    // ** Note ** I must make the registry a threaded server to handle multiple connections from different MessagingNodes
		    
		    /*
		    // read from MessagingNode.
		    while((nodeMsg = bufferedreader.readLine()) != null)
		    {
		    	System.out.println("Node says: " + nodeMsg);
		    	nodePortNum = Integer.parseInt(nodeMsg);
		    }
		    */
		    
		    // will want to use TCPSender here.
		    // send message back to MessagingNode
		   // Socket sendSocket = new Socket("zatanna", nodePortNum); // use the portnum to establish a connection with the messaging node.
		    
		    // should be using TCPSender here.
		   // PrintWriter writeOut = new PrintWriter(sendSocket.getOutputStream(), true);
			//writeOut.println("Hello Messaging node we almost completed milestone!"); // send the registry the port number we are working with.
			
			
			//writeOut.close(); // close the printWriter.
		   // bufferedreader.close(); // close the bufferedreader.
		   // inputstreamreader.close(); // close the inputstreamreader
		   // nodeSocket.close(); // close the node socket.
			
		} catch (Exception e) {
			System.err.println("Registry caught error: " + e.getMessage());
			System.exit(-1);
		} 		
	}
}
