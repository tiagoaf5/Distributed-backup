package Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import GUI.Window;
import Messages.Message;
import Multicast.MC;
import Multicast.MDB;

public class BackupStatusHandler extends Thread {
	
	private final int WAIT_TIME_LOWER = 30000; //30s
	private final int WAIT_TIME_HIGHER = 60000; //60s
	private final int WAIT_TIME_DEVIATION = 10000; //60s
	
	private final int WAIT_TIME_FILES_LOWER = 5000; //5s
	private final int WAIT_TIME_FILES_HIGHER = 10000; //10s
	
	private final String MESSAGE = "Backup service: ";
	
	ArrayList<LocalFile> localFiles;
	MDB mdb;
	MC mc;
	
	static ArrayList<Message> resendMessages = new  ArrayList<Message>();
	static boolean stopped;
	
	
	public synchronized static void stopIt() {
		stopped = true;
	}
	
	public synchronized static void startIt() {
		stopped = false;
	}
	

	public void run() { 
		stopped = false;
		localFiles = BackupService.getLocalFiles();
		mdb = BackupService.getMdb();
		mc = BackupService.getMc();
		
		
		try {
			
			int deviationCounter = 0;
			while (true) {
				
				sleep(ThreadLocalRandom.current().nextInt(WAIT_TIME_LOWER,
						WAIT_TIME_HIGHER + deviationCounter * WAIT_TIME_DEVIATION));
				
				while(stopped)
					sleep(ThreadLocalRandom.current().nextInt(WAIT_TIME_FILES_LOWER,WAIT_TIME_FILES_HIGHER));
				
				System.out.println(MESSAGE + "checking chunks replication degree");
				Window.log(MESSAGE + "checking chunks replication degree");

				

				for (int i = 0; i < localFiles.size(); i++) {
				
					ArrayList<Integer> chunks = localFiles.get(i).getChunksLowReplication();
					
					for(int j = 0; j < chunks.size(); j++) {
						mdb.backupChunk(localFiles.get(i), chunks.get(j));
						
						while(stopped)
							sleep(ThreadLocalRandom.current().nextInt(WAIT_TIME_FILES_LOWER,WAIT_TIME_FILES_HIGHER));
					}
					
					sleep(ThreadLocalRandom.current().nextInt(WAIT_TIME_FILES_LOWER,WAIT_TIME_FILES_HIGHER));
					
					while(stopped)
						sleep(ThreadLocalRandom.current().nextInt(WAIT_TIME_FILES_LOWER,WAIT_TIME_FILES_HIGHER));
				}
				
				for(int i = 0; i < resendMessages.size(); i++)
					mc.sendMessage(resendMessages.get(i));
				
				deviationCounter++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addPendentMessage (Message x) {
		resendMessages.add(x);
	}

}
