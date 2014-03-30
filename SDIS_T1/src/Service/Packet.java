package Service;

import java.net.InetAddress;

public class Packet {
	private InetAddress ip2;
	private String ip;
	private byte[] data;
	
	public Packet(InetAddress ip, byte[] data) {
		ip2 = ip;
		this.ip = ip.toString();
		this.data = data;
	}
	
	public InetAddress getIpInet() {
		return ip2;
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
