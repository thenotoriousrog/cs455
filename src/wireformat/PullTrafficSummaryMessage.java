package wireformat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PullTrafficSummaryMessage {
	
	// tells the messaging nodes to send their traffic reports to the Registry.
	public byte[] getTrafficSummaryMessage() throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeBytes("PULL_TRAFFIC_SUMMARY\n"); // very simple message, tells messaging nodes to send in their data yo.
		
		// clean up
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray();
		baOutStream.close();
		dataOutput.flush();
		
		return marshalledBytes;
	}

}
