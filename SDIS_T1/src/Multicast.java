import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class Multicast {

	private final int SIZE = 1024;
	
	private InetAddress mAddress;
	private int mPortNumber;
	private MulticastSocket mClient;
	
	
	public Multicast() {}

	public Multicast(String address, String port) throws IOException {
		
		mAddress=InetAddress.getByName(address);
		mPortNumber = Integer.parseInt(port);
		mClient = new MulticastSocket(mPortNumber);
		mClient.joinGroup(mAddress);
	}
	
	public void leaveGroup() {
		
		try {
			mClient.leaveGroup(mAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mClient.close();
	}
	
	private void send(String string) throws IOException {

		byte[] buf = new byte[SIZE];
		DatagramPacket p = new DatagramPacket(string.getBytes(), string.getBytes().length, mAddress, mPortNumber);
		mClient.send(p);
		//System.out.println("Sent " + new String(p.getData()));
	}
	
	private String receive() throws IOException {
		
		byte[] buf = new byte[SIZE];
		DatagramPacket sp = new DatagramPacket(buf, buf.length); 
		mClient.receive(sp);
		//System.out.println("Received " + new String(sp.getData()));
		
		return new String(sp.getData());
	}
}
