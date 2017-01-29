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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;



public class Registry implements Runnable {
	
	private static ServerSocket registryServer; // serversocket that the registry will be using.
	private static Socket nodeSocket; // socket that the registry will use to communicate between the nodes.
	private static Socket sendSocket; // socket that will be used to send messages back to MessagingNodes.
	private static Registry theRegistry; // a copy of the registry we are using.
	
	// Pair<String, Integer> is in the form of hostname, portnum for the nodes.
	private static ArrayList<Pair<String, Integer>> registeredNodes = new ArrayList<Pair<String, Integer>>(); // an array list of the hostname/portnum node Pairs
	
	// check if any hostnames match the current node's
	public static boolean matchedIPAddresses(ArrayList<Pair<String, Integer>> registeredNodes, String IPaddr)
	{
		boolean matched = false; // if a hostname matches, set to true.
		
		// loop through and check all hostnames.
		for(int i = 0; i < registeredNodes.size(); i++)
		{
			if(registeredNodes.get(i).getFirst() == IPaddr) 
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
	public static boolean isRegistered(int portnum, String IPaddr)
	{
		if(registeredNodes.isEmpty())
		{
			return false; // register this node.
		}
		else
		{
			boolean hostnamesMatch = matchedIPAddresses(registeredNodes, IPaddr); // returns true if any hostnames match
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
	
	// this method will start a connection with a MessagingNode and send appropriate messages.
	public static void send(Socket socket, byte[] messageToSend)
	{
		try 
		{
			TCPSender message = new TCPSender(socket);
			message.sendData(messageToSend); // send message to the Messaging Node.
			
		} catch (IOException e) {
			System.err.println("Registry caught error: " + e.getMessage());
		}
	}
	
	// Messaging nodes sending a registration command are sent here.
	public static void register(String IPaddr, int portnum)
	{		
		boolean nodeRegistered = isRegistered(portnum, IPaddr); // check if MessagingNode is registered or not.
		
		if(nodeRegistered == false) // node does not exist, add to registry.
		{
			Pair<String, Integer> newMessagingNode = new Pair<String, Integer>();
			newMessagingNode.setFirst(IPaddr); // set the IP address of the first node.
			newMessagingNode.setSecond(portnum); // set the second of the second node.
			registeredNodes.add(newMessagingNode); // add the newMessagingNode into the Registry.
			
			try 
			{
				sendSocket = new Socket(IPaddr, portnum); // establish connection with MessagingNode.
				RegistryRegistrationResponseMessage rrrm = new RegistryRegistrationResponseMessage();
				byte[] responseMessage = rrrm.getSuccessBytes(sendSocket); // get success message.
				send(sendSocket, responseMessage); // send message to Messaging Node.
				
			} catch (UnknownHostException e) {
				System.err.println("Registry caught error (unknown host): " + e.getMessage());
			} catch (IOException e) {
				System.err.println("Registry caught error (IO exception): " + e.getMessage());
			}
		}
		else // the node already exists send the failure message to the MessagingNode.
		{
			try 
			{
				sendSocket = new Socket(IPaddr, portnum); // establish connection with MessagingNode.
				RegistryRegistrationResponseMessage rrrm = new RegistryRegistrationResponseMessage();
				byte[] responseMessage = rrrm.getFailureBytes(sendSocket); // get failure message.
				send(sendSocket, responseMessage); // send message to MessagingNode.
				
			} catch (UnknownHostException e) {
				System.err.println("Registry caught error (unknown host): " + e.getMessage());
			} catch (IOException e) {
				System.err.println("Registry caught error (IO exception): " + e.getMessage());
			}
		}
		
		// **Note: I need to also check the socket input stream from the messaging node to ensure that they match, if they don't we need to send error message again.
	}
	
	// Messaging nodes sending deregistration requests are sent here.
	public static int deregister(int portnum, String hostname)
	{
		return 0; // change this!
	}
	
	// handles messages sent to the Registry.
	public static void NodeRequest(String request, String IPaddr, int portnum)
	{
		if(request.equals("REGISTER_REQUEST")) // MessagingNode sends a registration command.
		{
			register(IPaddr, portnum); // register the node within the registry.
		}
	}
	
	// message received from the TCPReceiver will be passed here for processing and sent to NodeRequest.
	public void TCPmessage(String tcpMessage)
	{
		String[] tokens = tcpMessage.split(" "); // split message by spaces.
		String request = tokens[0]; // token[0] holds the node request.
		//String hostname = tokens[1]; // token[1] holds the hostname
		String IPaddr = tokens[1]; // token[1] holds the node's IP address.
		int portnum = Integer.parseInt(tokens[2]); // token[2] holds the node's port number.
		
		// **NOTE: I'm using hostname here instead of IP address, I should fix this b4 turning in project.
		NodeRequest(request, IPaddr, portnum); // send the request along with the relative information to handled.
	}
	
	// runs when the registry thread starts.
	public void run()
	{
		System.out.println("Accepted connection from Messaging Node: " + nodeSocket); // alert console of successful connection.
		System.out.println("test 2");
		Thread messagingNodeThread; // start a new Thread on TCPReceiver to receive messages through clientSocket.
		try 
		{
			messagingNodeThread = new Thread(new TCPReceiver(nodeSocket, theRegistry), "tcpReceiverThread"); // pass a copy of the registry to TCPReceiver.
			messagingNodeThread.start(); // start receiving messages.				
			
		} 
		catch (IOException e) {
			System.err.println("messagingNodeThread got error: " + e.getMessage());
		} 

	}
	
	
	public static void main(String[] args) {
		// note: may want to add a thread within the registry to listen for commands.
		
		try 
		{
			theRegistry = new Registry(); // instance of the registry.
			
			registryServer = new ServerSocket(9999); // create a server for the registry to communicate through.
			System.out.println("Registry has started."); 
			
			// continue checking for new connections.
			while(true)
			{
				nodeSocket = registryServer.accept(); // accept connections from nodes. Remember code pauses here until a connection has been made.
				//System.out.println("Accepted connection: " + nodeSocket); 
				System.out.println("test 1"); // first test to make sure things go smoothely.
				Thread registryThread = new Thread(theRegistry, "registryThread"); // create a registry thread. Should only be one however.
				registryThread.start();
				//Thread nodeConnectionThread = new nodeConnectionThread(nodeSocket, ++threadID); // create a new Thread class to handle the node connection.
				//nodeConnectionThread.start(); // start this thread to handle incoming Messaging Nodes.
				
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
