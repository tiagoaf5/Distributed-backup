package Service;

import java.net.InetAddress;

public class Packet {

	private String ip;
	private byte[] data;
	
	public Packet(InetAddress ip, byte[] data) {
		this.ip = ip.toString();
		this.data = data;
	}
	
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
