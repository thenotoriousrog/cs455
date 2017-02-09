package overlay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import Graph.Dijkstra;
import wireformat.NodeDeregistrationRequestMessage;
import wireformat.RegistrationRequestMessage;

// creates the structure of the messaging nodes.
public class MessagingNode implements Node, Runnable {
	
	private static ServerSocket serverSocket; // allows each messaging node to open a port and allow connections.
	private static Socket clientSocket; // the node will retrieve data from this socket from clients.
	private static int portNum = 0; // this will hold a random port number. 
	private static String hostname = ""; // holds the hostname created from within the console.
	private static MessagingNode messagingNode; // an instance that will be sent to TCPNodeReceiver to help receive messages.
	private static ArrayList<Pair<String, Integer>> otherNodes = null; // holds the list of other nodes.
	ArrayList<Trio<Pair<String, Integer>, Pair<String, Integer>, Integer>> linkWeights = 
			new ArrayList<Trio<Pair<String, Integer>, Pair<String, Integer>, Integer>>(); // this will hold the link weights between this node and it's connected node.
	private static Socket registrySocket = null; // socket that we will use to communicate with the registry.
	ArrayList<Pair<String, Integer>> shortestPathList = null; // holds the shortest path list sent from the registry.
	
	private static int sendTracker = 0; // keeps track of all the messages that this node sends. This is held in main memory, not cached.
	private static int receiveTracker = 0; // keeps track of all the messages that this node receives. This is held in main memory, not cached.
	private static int relayTracker = 0; // keeps track of all the messages that this node relays to another node
	private static long sendSummation = 0; // sums all the payloads that this message sends.
	private static long receiveSummation = 0; // sums all the payloads that this message receives.
	private volatile int Payload = 0; // Payload to send to the other nodes.
	
	
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
	
	// this method will get the current list of node connections so that the registry can generate the link weights to each node for this messaging node.
	private void setNodeConnectionList(ArrayList<Pair<String, Integer>> nodeConnectionList)
	{
		otherNodes = nodeConnectionList; // set this field.
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
	
	// this method is in charge of making connections with the other Messaging Nodes.
	public ArrayList<Pair<String, Integer>> parseNodeInfo(String[] splitMessage)
	{
		ArrayList<Pair<String, Integer>> otherNodes = new ArrayList<Pair<String, Integer>>(); // this will hold all of the messaging nodes that we must connect to.
		int portNum = 0; // port number that we will have for the node.
		String IPAddr = ""; // holds the IP address to use other nodes with.
		
		// starts at place 2 as this is when we start getting messaging node lines.
		for(int i = 2; i < splitMessage.length; i++)
		{
			// need to parse each of these lines to get the actual data.
			String[] nodeInfo = splitMessage[i].split(" "); 
			
			// nodeInfo[0] = "messaging", nodeInfo[1] = "node#", nodeInfo[2] = portnum(String), nodeInfo[3] = IPaddr
			portNum = Integer.parseInt(nodeInfo[2]); // get the portnum of the string.
			IPAddr = nodeInfo[3]; // get the IP address of the node.
			
			// create a node Pair
			Pair<String, Integer> otherNode = new Pair<String, Integer>(IPAddr, portNum); // this should automatically set the First and Second fields.
			otherNodes.add(otherNode); // add the other node into the list of other nodes.
		}
		
		return otherNodes; // return the parsed list of other nodes.
	}
	
	// start making connections with the other nodes and displays message to console upon success.
	public void makeConnections(ArrayList<Pair<String, Integer>> nodesToConnectTo, int numOfConnections)
	{
		ArrayList<Socket> nodeSockets = new ArrayList<Socket>(); // holds lists of Sockets to make connections to.
				
		// loop through list of nodes.
		for(int i = 0; i < nodesToConnectTo.size(); i++)
		{
			try 
			{
				Socket socket = new Socket(nodesToConnectTo.get(i).getFirst(), nodesToConnectTo.get(i).getSecond());
				nodeSockets.add(socket); // add the new socket into the list of sockets (nodes connected to)
							
			} catch (IOException e) {
				
				System.out.println(e.getMessage()); // print the message.
			}
		}
		
		System.out.println("All connections are established. Number of connections: " + numOfConnections); // message to console for testing purposes.
	}
	
	// takes messages line by line and trying to find the relevant information. And find link weights of the nodes connected to.
	private void processLinkWeights(String[] splitMessage)
	{
		String[] splitLinebySpace = null; // holds the lines split by space.
		String[] node1Info = null; // holds the lines split by ':' of first node getting IP:portnum
		String[] node2Info = null; // holds the lines split by ':' of second node getting IP:portnum
		
		int currentPortnum = 0; // portnum of this node.
		int otherNodePortnum = 0; // portnum of node we are connected to.
		int weight = 0; // holds the weight of the node we are connected to.
		
		// start at spot 2 as that where the link weights begin.
		for(int i = 2; i < splitMessage.length; i++)
		{
			// split the line up by space, then by ':'.
			splitLinebySpace = splitMessage[i].split(" "); // [0] = IP:portnum of node1, [1] = IP:portnum of node2, [2] = weight between these nodes.
			node1Info = splitLinebySpace[0].split(":"); // gets IP:portnum of node1
			node2Info = splitLinebySpace[1].split(":"); // gets IP:portnum of node2
			
			currentPortnum = Integer.parseInt(node1Info[1]); // gets the portnum and converts it to an int.
			weight = Integer.parseInt(splitLinebySpace[2]); // gets the weight associated with the two vertices.
			
			// check to see if we found the info related to this node.
			if(portNum == currentPortnum) // we found info related to this node.
			{
				otherNodePortnum = Integer.parseInt(node2Info[1]); // get portnum of another node.
				
				// search through list of nodes storing the match into the new list of link weights.
				System.out.println("otherNodes size is: " + otherNodes.size()); // for testing.
				for(int j = 0; j < otherNodes.size(); j++)
				{
					if(otherNodePortnum == otherNodes.get(j).getSecond()) // we have found a match.
					{
						Pair<String, Integer> currentNodeInfo = new Pair<String, Integer>(node1Info[0], portNum); // node1Info[0] == IPaddr.
						Pair<String, Integer> connectedNodeInfo = new Pair<String, Integer>(node2Info[0], otherNodePortnum); // node2Info[0] == IPaddr.
						Trio<Pair<String, Integer>, Pair<String, Integer>, Integer> link = new Trio<Pair<String, Integer>, Pair<String, Integer>, Integer>();
						
						// store the information associated with these nodes.
						link.setFirst(currentNodeInfo); // this node.
						link.setSecond(connectedNodeInfo); // node we are connected to.
						link.setThird(weight); // the weight that is between these two nodes.
						
						linkWeights.add(link); // add this link into the list of link weights.
					}
				}
			}
		}
		
		System.out.println("Link weights are received and processed. Ready to send messages."); // needed by HW documents.
		
		// run through link weights and print out the details.
		System.out.println("linkWeights size is: " + linkWeights.size());
		for(int i = 0; i < linkWeights.size(); i++)
		{
			System.out.println(linkWeights.get(i).getFirst().getSecond() + " " 
					+ linkWeights.get(i).getSecond().getSecond() + " " + linkWeights.get(i).getThird());
			
			// the above will print node1Portnum node2Portnum weight for each. This will tell me if it is done correctly or not.
		}
		
	}
	
	public synchronized void updateSendTracker(int newSendTracker)
	{
		sendTracker = newSendTracker; // updated information.
	}
	
	public synchronized void updateReceiveTracker(int newReceiveTracker)
	{
		receiveTracker = newReceiveTracker;
	}
	
	public synchronized void updateSendSummation(int newSendSummation)
	{
		sendSummation = newSendSummation;
	}
	
	public synchronized void updateReceiveSummation(int newReceiveSummation)
	{
		receiveSummation = newReceiveSummation;
	}
	
	private static int getPayload()
	{
		// may not need these max/min values.
		int max = Integer.MAX_VALUE; // largest int we can get 2147483647
		int min = Integer.MIN_VALUE; // smallest int we can get -2147483648
		
		Random rn = new Random();
		int randomInt = rn.nextInt(); // 
		return randomInt; // hoping this will create the large number that we want to use.
	}
	
	// starts sending messages to random nodes based on the number of rounds (one node selected per round)
	private void startSendingMessages(int numOfRounds) throws IOException
	{
		
		Random rn = new Random();
		int randomNode = 0; // holds the random position of the node we are going to start sending messages to.
		
		// continue selecting random nodes and sending a randomized payload until we finish our rounds.
		while(numOfRounds != 0) 
		{
			randomNode = rn.nextInt(otherNodes.size()); // picks a random node to select in otherNodes.
			Payload = getPayload(); // get the payload to send to another node. This is stored into main memory.
			
			// request the registry to generate the shortest path to this node
			
			
			numOfRounds--; // decrement 1 from number of rounds.
			
			// after I am confident that the shortest path works correctly, I must start sending the data correctly to the other nodes.
		}
				
		// once numOfRounds == 0 we must send the TASK_COMPLETE message to the registry.	
	}
	
	// nodes start at nodes[1] and remember that it is IPaddr:portnum.
	// may need to make this method synchronized.
	public void processShortestPath(String[] nodes)
	{
		shortestPathList = new ArrayList<Pair<String, Integer>>();
		
		for(int i = 1; i < nodes.length; i++)
		{
			String[] splitLine = nodes[i].split(":"); // splitLine[0] == IPaddr, splitLine[1] == portnum
			String IPaddr = splitLine[0];
			int portnum = Integer.parseInt(splitLine[1]);
			Pair<String, Integer> newNode = new Pair<String, Integer>(IPaddr, portnum); // create a new pair.
			
			shortestPathList.add(newNode); // add the node into the shortest path list. 
			// NOTE: I may want to also make this synchronized.
		}
	}
	
	// this will take in the messages that TCPNodeReceiver receives.
	public void TCPNodeMessage(String message) throws IOException
	{
		//System.out.println("MessagingNode got message: " + message + " ~from Registry.");
		
		String[] splitMessage = message.split("\n"); // split each line up.
		
		if(splitMessage[0].equals("MESSAGING_NODES_LIST"))
		{
			String[] splitLine = splitMessage[1].split(" "); // split the line containing the number of messaging nodes.
			int numConnections = Integer.parseInt(splitLine[splitLine.length-1]); // holds the number of connections the messaging node should make.
			otherNodes = parseNodeInfo(splitMessage); // get that info.
			setNodeConnectionList(otherNodes); // set this list of node connections to be used by the registry.
			// System.out.println("first node has IP address: " + otherNodes.get(0).getFirst() + " and has portnum: " + otherNodes.get(0).getSecond()); // for testing.
			makeConnections(otherNodes, numConnections); // start making connections with the other nodes.
		}
		else if(splitMessage[0].equals("Link_Weights"))
		{
			String[] splitLine = splitMessage[1].split(" "); // split the line containing the number of messaging nodes.
			int numLinks = Integer.parseInt(splitLine[splitLine.length - 1]); // get the number of links sent from the Registry.
			
			processLinkWeights(splitMessage); // have the link weights get processed.
		}
		else if(splitMessage[0].equals("TASK_INITIATE")) // registry is telling the nodes to begin sending data and payload.
		{
			String[] splitLine = splitMessage[1].split(" ");
			int numOfRounds = Integer.parseInt(splitLine[1]); // get the number of rounds that the messaging nodes must go for.
			startSendingMessages(numOfRounds); // start sending messages for the number of rounds.
		}
		else if(splitMessage[0].equals("SHORTEST_PATH_LIST"))
		{
			// make a method that may or may not need to be synchronized, then go through it storing all the portnumbers.
			processShortestPath(splitMessage); // takes in the string[] and constructs the shortest path that will be sent to the other nodes.
		}
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
	
	// help me sort my ArrayList of Trios
	public static Comparator<Trio<Pair<String, Integer>, Pair<String,Integer>, Integer>> sortByWeight()
	{
		Comparator<Trio<Pair<String, Integer>, Pair<String, Integer>, Integer>> comp = new Comparator<Trio<Pair<String, Integer>, Pair<String, Integer>, Integer>>() {
		
			public int compare(Trio<Pair<String, Integer>, Pair<String, Integer>, Integer> weight1,
					Trio<Pair<String, Integer>, Pair<String, Integer>, Integer> weight2) 
			{
				
				return weight1.getThird() - weight2.getThird();
			}
		};
		
		return comp;
	}
	
	// this method will handle the commands being sent by the user.
	public void userNodeCommand(String[] command)
	{
		
		if(command[0].equals("print-shortest-path"))
		{
			linkWeights.sort(sortByWeight()); // have the linkWeights get sorted by their weights.
			Collections.reverse(linkWeights); // simply reverse this list since the list was being sorted in terms of smallest to highest.
			
			// loop through the link weights and print them according to size.
			for(int i = 0; i < linkWeights.size(); i++)
			{
				System.out.print(linkWeights.get(i).getSecond().getFirst() + ":" + linkWeights.get(i).getSecond().getSecond()
						+ "--" + linkWeights.get(i).getThird() + "--"); // print this to console to get the list of the linkWeights in order by the nodes.
			}
			
		}
		else if(command[0].equals("exit-overlay"))
		{
			try 
			{
				TCPSender deregisterMe = new TCPSender(registrySocket);
				NodeDeregistrationRequestMessage ndrm = new NodeDeregistrationRequestMessage();
				byte[] msgToSend = ndrm.getDeregistrationBytes(portNum, registrySocket);
				deregisterMe.sendData(msgToSend); // send this command to the registry to begin the deregistration process.
			} 
			catch (IOException e) {
				System.err.println("Messaging node caught exception while trying to deregister: " + e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		registrySocket = new Socket("localhost", 9999);
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
