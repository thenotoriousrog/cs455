package overlay;

import java.util.Scanner;

// this class is responsible for listening for user input and handling user input. 
// commands are sent back to the registry.
public class RegistryUserInput extends Thread {

	private Registry registry; // copy of Registry instance to send commands back to caller class.
	
	public RegistryUserInput(Registry REGISTRY)
	{
		registry = REGISTRY; // initialize registry instance.
	}
	
	
	// thread starts running and is in control of listening to added user input.
	public void run()
	{
		Scanner input = new Scanner(System.in);
		String command = ""; // holds command from user.
		while(true) // continue while Registry is connected.
		{
			command = input.nextLine(); // get a command from user.
			String[] splitCommand = command.split(" "); // split message based on spaces.
			//System.out.println("user wrote: " + command); // display the command for now, this will eventually go back to the Registry!
			registry.userCommand(splitCommand); // send command off to the registry.
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
