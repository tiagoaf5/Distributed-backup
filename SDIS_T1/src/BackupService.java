import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import Messages.Message;
import Messages.MessageStored;
import Multicast.*;


public class BackupService {
	
	private InetAddress mcAddress;
	private int mcPort;
	private InetAddress mdbAddress;
	private int mdbPort;
	private InetAddress mdrAddress;
	private int mdrPort;

	private LocalFiles localFiles;
	private Multicast mdb;
	private Multicast mdr;
	MC mc;

	private static Map<String, Map<Integer, Integer>> filesStored=null;
	
	public static void main(String[] args) {

		try {
			BackupService a = new BackupService(args);
			a.initReceivingThreads();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void initReceivingThreads() {
		
		/*
		try {
			while(true) {
				mc.sendMessage(abc);
				System.out.println(Message.byteArrayToHexString(abc.getMessage()));
				Thread.sleep(2000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}

	public BackupService(String args[]) throws UnknownHostException {

		this.mcAddress =  InetAddress.getByName(args[0]);
		this.mcPort = Integer.parseInt(args[1]);
		this.mdbAddress = InetAddress.getByName(args[2]);
		this.mdbPort = Integer.parseInt(args[3]);
		this.mdrAddress = InetAddress.getByName(args[4]);
		this.mdrPort = Integer.parseInt(args[5]);

		//localFiles = new LocalFiles(); //get files to backup info

		try {
			openMulticastSessions();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void openMulticastSessions() throws IOException {
		mc = new MC(mcAddress, mcPort);
		mdb = new Multicast(mdbAddress, mdbPort);
		mdr = new Multicast(mdrAddress, mdrPort);
	}

	public void addFile(String fileId, int chunkNo, int size) {
		
		Map<Integer, Integer> temp = null;
		temp.put(chunkNo, size);
		filesStored.put(fileId, temp);
	}

	public boolean searchFile(String fileId, int chunkNo) {
		
		if(filesStored.containsKey(fileId)) {
			Map<Integer, Integer> res=filesStored.get(fileId);
			if(res.containsKey(chunkNo))
				return true;
		}
		return false;
	}
	

	

}
