package wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


// creates the messages that need to be sent to other nodes.
public class WireFormat {

	private int type;
	private long timestamp;
	private String identifier;
	private int tracker;
	
	// returns the marshaled data.
	public byte[] getMarshaledBytes() throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(baOutStream));
		
		dataOutput.writeInt(type);
		dataOutput.writeLong(timestamp);
		
		byte[] identifierBytes = identifier.getBytes();
		int elementLength = identifierBytes.length; 
		dataOutput.writeInt(elementLength);
		dataOutput.write(identifierBytes);
		
		dataOutput.writeInt(tracker);
		
		dataOutput.flush();
		marshalledBytes = baOutStream.toByteArray();
		
		baOutStream.close();
		dataOutput.close();
		
		return marshalledBytes; // send back our marshalled data.
	}

	// constructor to get all data
	// just may also be unmarhaling the bytes.
	public WireFormat(byte[] marshalingBytes) throws IOException
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalingBytes);
		DataInputStream dataInput = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = dataInput.readInt();
		timestamp = dataInput.readLong();
		
		int identifierLength = dataInput.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		dataInput.readFully(identifierBytes);
		
		identifier = new String(identifierBytes);
		tracker = dataInput.readInt();
		
		System.out.println("wireFormat string: " + dataInput.toString());
		baInputStream.close();
		dataInput.close();
	}
	
	
	public static void main(String[] args) {

	}

}
