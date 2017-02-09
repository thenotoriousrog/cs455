package overlay;

import java.util.Scanner;

// this class will take in input from a user and pass the corresponding command back to the MessagingNode.
public class NodeUserInput extends Thread {

	private MessagingNode node; // instance of a messaging node which will be used to pass back the user inputed commands.
	
	public NodeUserInput(MessagingNode currentNode)
	{
		node = currentNode; // set the current node.
	}
	
	// runs when the Thread is starting taking in commands from the user.
	public void run()
	{
		Scanner input = new Scanner(System.in);
		String command = ""; // holds command from user.
		while(true) // continue while MessagingNode is connected.
		{
			command = input.nextLine(); // get a command from user.
			String[] splitMessage = command.split(" "); // split this by spaces.
			node.userNodeCommand(splitMessage); // send the command off to be handled by the Messaging Node.	
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
