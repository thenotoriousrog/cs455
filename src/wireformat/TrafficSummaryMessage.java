package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrafficSummaryMessage {
	
	public byte[] getTrafficSummaryMessage(String IPaddr, int portnum, int sentMessages, long sendSum, 
			int receivedMessages, long receiveSum, int relayedMessages) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("TRAFFIC_SUMMARY\n"); // tells the registry that this is the traffic summary message.
		
		// write the traffic summary converting all ints and longs into strings
		dataOutput.writeBytes("Node IP Address:" + IPaddr + "\n");
		dataOutput.writeBytes("Node Port number:" + Integer.toString(portnum) + "\n"); 
		dataOutput.writeBytes("Number of messages sent:" + Integer.toString(sentMessages) + "\n");
		dataOutput.writeBytes("Summation of sent messages:" + Long.toString(sendSum) + "\n");
		dataOutput.writeBytes("Number of messages received:" + Integer.toString(receivedMessages) + "\n");
		dataOutput.writeBytes("Summation of received messages:" + Long.toString(receiveSum) + "\n");
		dataOutput.writeBytes("Number of relayedMessages:" + Integer.toString(relayedMessages) + "\n");
		
		// clean up
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray();
		baOutStream.close();
		dataOutput.flush();
		
		return marshalledBytes;
	}

}
