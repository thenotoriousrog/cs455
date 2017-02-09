package overlay;
/*
 * This is the registry class. Here the registry will be created which keeps track of all messaging transmissions.
 */

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Graph.Dijkstra;
import Graph.Graph;
import Graph.Vertex;
import wireformat.LinkWeightsMessage;
import wireformat.MessagingNodesListMessage;
import wireformat.RegistryRegistrationResponseMessage;
import wireformat.RegistryShortestPathListResponse;
import wireformat.TaskInitiateMessage;



public class Registry implements Runnable {
	
	private static ServerSocket registryServer; // serversocket that the registry will be using.
	private static Socket nodeSocket; // socket that the registry will use to communicate between the nodes.
	private static Socket sendSocket; // socket that will be used to send messages back to MessagingNodes.
	private static Registry theRegistry; // a copy of the registry we are using.
	private static Graph Overlay; // the Graph that holds the overlay.  
	private static Vertex newVertex; // Vertex that we will add to the overlay.
	private static ArrayList<Vertex> vertices = new ArrayList<Vertex>(); // holds list of vertices that are being registered.
	private static ArrayList<Socket> nodeSockets = new ArrayList<Socket>(); // holds all nodes that are registered in the registry.
	private static Dijkstra dijkstra = null; // this will be set later in the code. Important to find the shortest path.

	
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
			newVertex = new Vertex(newMessagingNode); // create a new vertex.
			vertices.add(newVertex); // add vertex into the vertices list.
			
			try 
			{
				sendSocket = new Socket(IPaddr, portnum); // establish connection with MessagingNode.
				nodeSockets.add(sendSocket); // add socket to the node socket list.
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
	public static void NodeRequest(String[] splitMessage)
	{
		if(splitMessage[0].equals("REGISTER_REQUEST")) // MessagingNode sends a registration command.
		{
			String IPaddr = splitMessage[1];
			int portnum = Integer.parseInt(splitMessage[2]);
			register(IPaddr, portnum); // register the node within the registry.
		}
		else if(splitMessage[0].equals("DEREGISTER_REQUEST"))
		{
			String IPaddr = splitMessage[1];
			int portnum = Integer.parseInt(splitMessage[2]);
			register(IPaddr, portnum); // register the node within the registry.
			deregister(IPaddr, portnum); // remove the node from the registry. Node should send final report to registry as well before full disconnection.
		}
		else if(splitMessage[0].equals("SHORTEST_PATH_REQUEST"))
		{
			int sendingNodesPortnum = Integer.parseInt(splitMessage[1]); // get the portnum of the node that sent the message.
			int shortestPathToNode = Integer.parseInt(splitMessage[2]); // get the portnum of the node we want the shortest path to.
			
			List<Vertex> shortestPath = dijkstra.getPathTo(shortestPathToNode); // have this list get 
			RegistryShortestPathListResponse rsplr = new RegistryShortestPathListResponse();
		
			try 
			{
				byte[] msgToSend = rsplr.getShortestPathListMessage(shortestPath);
				
				// search through list of nodeSockets and find the matching portnum
				for(int i = 0; i < nodeSockets.size(); i++)
				{
					if(nodeSockets.get(i).getPort() == sendingNodesPortnum) // find the node that requested the shorted path list.
					{
						send(nodeSockets.get(i), msgToSend); // send the shortest path list back to the node that requested it.
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Registry caught error while making shortest path list response: " + e.getMessage());
			} 
			
		}
	}
	
	// message received from the TCPReceiver will be passed here for processing and sent to NodeRequest.
	public void TCPmessage(String tcpMessage)
	{
		String[] splitMessage = tcpMessage.split("\n");
		NodeRequest(splitMessage); // send the request along with the relative information to handled.
	}
	
	// This will create edges between the created vertices and will assign weights in the process of doing that.
	public void buildEdgesandWeights(ArrayList<Vertex> vertices)
	{
		Random rn = new Random();
		int weight = 0; // holds weight of an edge.
		// for each vertex assign an edge along with a randomized weight.
		for(int i = 0; i < vertices.size() - 1; i++)
		{
			for(int j = i + 1; j < vertices.size(); j++)
			{
				// assign an edge to each vertex to make it truly undirected. 
				weight = rn.nextInt(10) + 1; // new weight
				Overlay.addEdge(vertices.get(i), vertices.get(j) , weight); // fill left side of vertex i
				//System.out.println("Vertex(portnum): " + vertices.get(i).getVertexPortNum() + " and Vertex(portnum): " 
				//		+ vertices.get(j).getVertexPortNum() + " has weight " + weight); // for testing purposes
				
				weight = rn.nextInt(10) + 1; // new weight
				Overlay.addEdge(vertices.get(i), vertices.get(j) , weight); // fill right side of vertex i
				//System.out.println("Vertex(portnum): " + vertices.get(i).getVertexPortNum() + " and Vertex(portnum): " 
				//		+ vertices.get(j).getVertexPortNum() + " has weight " + weight); // for testing purposes
				
				weight = rn.nextInt(10) + 1; // new weight
				Overlay.addEdge(vertices.get(j), vertices.get(i) , weight); // fill vertex j.
				//System.out.println("Vertex(portnum): " + vertices.get(j).getVertexPortNum() + " and Vertex(portnum): " 
				//		+ vertices.get(i).getVertexPortNum() + " has weight " + weight); // for testing purposes
			}
		}
	}

	
	// takes in a split string array for the command. Important to note what is going on here.
	public void userCommand(String[] command)
	{
		if(command[0].equals("setup-overlay")) // this should trigger dijkstra's algorithm
		{	
			int numOfConnections = Integer.parseInt(command[1]); // get the number of connections user is requesting.
			
			MessagingNodesListMessage setupMsg = new MessagingNodesListMessage(); // this class must be edited!
			
			// since we received the setup-overlay command we must build the graph along with the edges.
			Overlay = new Graph(vertices); // now that we have edges and vertices we can now build the overlay.
			buildEdgesandWeights(vertices); // build the edges with weights to all the vertices.
				
			//System.out.println("There are " + sortedVertices.size() + " vertices in the overlay.");
			dijkstra = new Dijkstra(Overlay, vertices.get(0).getVertexPortNum()); // takes in the overlay and the first graph in the list.
			
			int nodeCounter = 0; // tells the messaging node list which node it is working with.
			for(int i = 0; i < vertices.size(); i++)
			{
				//System.out.println("distance to vertex " + (i+1) + " is " + d.getDistanceTo(vertices.get(i).getVertexPortNum())); // just for testing. Will not need this yet.
				
				// its time to generate the Messaging_Node_List
				try 
				{
					// generate the messaging_nodes_list for every node in the registry.
					byte[] msgToSend = setupMsg.getNodeListBytes(vertices, nodeCounter, numOfConnections); // get the full messaging nodes list message.
					send(nodeSockets.get(nodeCounter), msgToSend); // send this message to the MessagingNodes.
					nodeCounter++; // keeps track of which node we are on as well which socket to use to send the Messaging_Nodes_List.
				} 
				catch (IOException e) {
					System.err.println("Registry caught error: " + e.getMessage()); // print the error.
					e.printStackTrace();
				}	
			}
		} 
		else if(command[0].equals("send-overlay-link-weights")) // this command will initiate the "Link_Weight" message to be sent to all other messaging nodes.
		{
			LinkWeightsMessage lwm = new LinkWeightsMessage();
			try 
			{
				byte[] msgToSend = lwm.getLinkWeightBytes(vertices, Overlay); // generates Link_Weight message 
				
				// send message to each Messaging Node.
				for(int i = 0; i < nodeSockets.size(); i++)
				{
					send(nodeSockets.get(i), msgToSend); // send the message to each messaging node, each node will process the message for themselves.
				}
			} 
			catch (IOException e){
				System.err.println(e.getMessage());
			} 
		}
		else if(command[0].equals("start")) // tells all messaging nodes to begin sending messages to each other.
		{
			int numberOfRounds = Integer.parseInt(command[1]); // get the number of rounds that the nodes should be going for.
			TaskInitiateMessage tim = new TaskInitiateMessage();
			try 
			{
				byte[] msgToSend = tim.getTaskInitiateMessage(numberOfRounds);
				
				// send message to each Messaging Node.
				for(int i = 0; i < nodeSockets.size(); i++)
				{
					send(nodeSockets.get(i), msgToSend); // send the message to this node.
				}
			} 
			catch (IOException e) {
				System.err.println("Registry got error while doing start command: " + e.getMessage());
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
