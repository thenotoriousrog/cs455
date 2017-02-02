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
import java.util.Random;
import java.util.StringTokenizer;

import Graph.Dijkstra;
import Graph.Edge;
import Graph.Graph;
import Graph.Vertex;
import wireformat.MessagingNodesListMessage;
import wireformat.RegistryRegistrationResponseMessage;



public class Registry implements Runnable {
	
	private static ServerSocket registryServer; // serversocket that the registry will be using.
	private static Socket nodeSocket; // socket that the registry will use to communicate between the nodes.
	private static Socket sendSocket; // socket that will be used to send messages back to MessagingNodes.
	private static Registry theRegistry; // a copy of the registry we are using.
	private static Graph Overlay; // the Graph that holds the overlay.  
	private static Vertex newVertex; // Vertex that we will add to the overlay.
	private static ArrayList<Vertex> vertices = new ArrayList<Vertex>(); // holds list of vertices that are being registered.
	
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
			registeredNodes.add(newMessagingNode); // add the newMessagingNode into list of registered nodes.
			
			// add the new node into the vertices
			
			//Overlay = new Graph();
			newVertex = new Vertex(newMessagingNode); // create a new vertex.
			vertices.add(newVertex); // add vertex into the vertices list.
			//Overlay = new Graph(vertices); // create the new graph with the vertices list, which automatically populates the Graph!
			//Overlay.addVertex(newVertex); // add vertex to the overlay.
			
			// by having having a vertices list it will really help with creating the topological sort.
			// test the topological sort out by doing it each time a node registers.
			
			
			try 
			{
				sendSocket = new Socket(IPaddr, portnum); // establish connection with MessagingNode.
				RegistryRegistrationResponseMessage rrrm = new RegistryRegistrationResponseMessage(registeredNodes.size());
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
				RegistryRegistrationResponseMessage rrrm = new RegistryRegistrationResponseMessage(registeredNodes.size());
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
	
	// this method will send a message to all messaging nodes registered in the Registry. 
	public void sendToAllNodes(ArrayList<Pair<String, Integer>> nodes, byte[] msgToSend)
	{
		// loop through all registered nodes.
		for(int i = 0; i < nodes.size(); i++)
		{
			
		}
	}
	
	// Messaging nodes sending deregistration requests are sent here.
	public static void deregister(String IPaddr, int portnum)
	{
		boolean nodeExists = isRegistered(portnum, IPaddr); // check if node is within registry.
		if(nodeExists == true)
		{
			for(int i = 0; i < registeredNodes.size(); i++)
			{
				if( (registeredNodes.get(i).getFirst().equals(IPaddr)) && (registeredNodes.get(i).getSecond() == portnum))
				{
					String portnumber = newVertex.convertToString(registeredNodes.get(i).getSecond()); // convert portnum to string to find vertex.
					Overlay.removeVertex(portnumber); // finds the vertex with this portnumber and remove it from the Overlay. 
					registeredNodes.remove(i); // remove the node from the registry list.
				}
			}
		}
		else
		{
			System.out.println("NODE DOES NOT EXIST"); // remove this later.
		}
	}
	
	// handles messages sent to the Registry.
	public static void NodeRequest(String request, String IPaddr, int portnum)
	{
		if(request.equals("REGISTER_REQUEST")) // MessagingNode sends a registration command.
		{
			register(IPaddr, portnum); // register the node within the registry.
		}
		else if(request.equals("DEREGISTER_REQUEST"))
		{
			deregister(IPaddr, portnum); // remove the node from the registry. Node should send final report to registry as well before full disconnection.
		}
	}
	
	// message received from the TCPReceiver will be passed here for processing and sent to NodeRequest.
	public void TCPmessage(String tcpMessage)
	{
		String[] tokens = tcpMessage.split(" "); // split message by spaces.
		String request = tokens[0]; // token[0] holds the node request.
		String IPaddr = tokens[1]; // token[1] holds the node's IP address.
		int portnum = Integer.parseInt(tokens[2]); // token[2] holds the node's port number.
		NodeRequest(request, IPaddr, portnum); // send the request along with the relative information to handled.
	}
	
	// This will create edges between the created vertices and will assign weights in the process of doing that.
	public void buildEdgesandWeights(ArrayList<Vertex> vertices)
	{
		Random rn = new Random();
		int weight = 0; // holds weight of an edge.
		
		System.out.println("there are " + vertices.size() + " vertices currently in the registry"); // for testing.
		// for each vertex assign an edge along with a randomized weight.
		for(int i = 0; i < vertices.size() - 1; i++)
		{
			for(int j = i + 1; j < vertices.size(); j++)
			{
				System.out.println("test");
				// assign an edge to each vertex to make it truly undirected. 
				weight = rn.nextInt(10) + 1; // new weight
				System.out.println("vertex 1: " + vertices.get(i).getVertexPortNum());
				System.out.println("vertex 2: " + vertices.get(j).getVertexPortNum());
				
				Overlay.addEdge(vertices.get(i), vertices.get(j) , weight); // fill left side of vertex i
				System.out.println("Vertex(portnum): " + vertices.get(i).getVertexPortNum() + " and Vertex(portnum): " 
						+ vertices.get(j).getVertexPortNum() + " has weight " + weight); // for testing purposes
				
				weight = rn.nextInt(10) + 1; // new weight
				Overlay.addEdge(vertices.get(i), vertices.get(j) , weight); // fill right side of vertex i
				System.out.println("Vertex(portnum): " + vertices.get(i).getVertexPortNum() + " and Vertex(portnum): " 
						+ vertices.get(j).getVertexPortNum() + " has weight " + weight); // for testing purposes
				
				weight = rn.nextInt(10) + 1; // new weight
				Overlay.addEdge(vertices.get(j), vertices.get(i) , weight); // fill vertex j.
				System.out.println("Vertex(portnum): " + vertices.get(i).getVertexPortNum() + " and Vertex(portnum): " 
						+ vertices.get(j).getVertexPortNum() + " has weight " + weight); // for testing purposes
			}
		}
	}
	
	// takes in a split string array for the command. Important to note what is going on here.
	public void userCommand(String[] command)
	{
		if(command[0].equalsIgnoreCase("setup-overlay")) // this should trigger dijkstra's algorithm
		{	
			int numOfConnections = Integer.parseInt(command[1]); // get the number of connections user is requesting.
			
			// messaging nodes will connect to nodes: node+1, node+2, node-1, node-2.
			// first node will connect to node+1, node+2, and last node, and second to last node.
			// modify messaging node list class accordingly.
			
			MessagingNodesListMessage setupMsg = new MessagingNodesListMessage(); // this class must be edited!
			
			// since we received the setup-overlay command we must build the graph along with the edges.
			Overlay = new Graph(vertices); // now that we have edges and vertices we can now build the overlay.
			buildEdgesandWeights(vertices); // build the edges with weights to all the vertices.
			
			
			ArrayList<Vertex> sortedVertices = Overlay.topologicalSort(vertices, Overlay); // get the topological sort of all vertices. 
			
			System.out.println("There are " + sortedVertices.size() + " vertices in the overlay.");
			Dijkstra d = new Dijkstra(Overlay, sortedVertices.get(0).getVertexPortNum()); // takes in the overlay and the first graph in the list.
			
			for(int i = 0; i < sortedVertices.size(); i++)
			{
				// get distance to each vertex in the sorted list. 
				// print this distance out for testing.
				System.out.println("distance to vertex " + (i+1) + " is " + d.getDistanceTo(sortedVertices.get(i).getVertexPortNum())); 
			}
			
			
			/*
			// print the topological sort for testing to make sure it is correct.
			for(int i = 0; i < sortVertices.size(); i++)
			{
				System.out.println("Messaging Node (vertex) " + (i+1) + " is: " + sortVertices.get(i).getVertexPortNum()); // print out the vertex and it's port num.
			}
			*/
			
			
			
			
			try 
			{
				// need to modify this message here!!
				byte[] msgToSend = setupMsg.getNodeListBytes(registeredNodes, numOfConnections); // get the full messaging nodes list message.
				// WORKING RIGHT HERE!!!!
			} 
			catch (IOException e) {
				System.err.println("Registry caught error: " + e.getMessage()); // print the error.
			}
			
		}
	}
	// runs when the registry thread starts.
	public void run()
	{
		System.out.println("Accepted connection from Messaging Node: " + nodeSocket); // alert console of successful connection.
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
				
		try 
		{
			theRegistry = new Registry(); // instance of the registry.
			
			registryServer = new ServerSocket(9999); // create a server for the registry to communicate through.
			System.out.println("Registry has started."); 
			
			// start a thread for listening for user input.
			Thread userInputThread = new Thread(new RegistryUserInput(theRegistry), "Registry_User_Input_Thread"); // create thread for user input.
			userInputThread.start(); // start the thread.
			
			while(true) // continue checking for new connections.
			{
				nodeSocket = registryServer.accept(); // accept connections from nodes. Remember code pauses here until a connection has been made.
				Thread registryThread = new Thread(theRegistry, "Registry_Thread"); // create a registry thread. Should only be one however.
				registryThread.start(); // start thread to start receiving messages.
			}
			
		} catch (Exception e) {
			System.err.println("Registry caught error: " + e.getMessage());
			System.exit(-1);
		} 		
	}
}
