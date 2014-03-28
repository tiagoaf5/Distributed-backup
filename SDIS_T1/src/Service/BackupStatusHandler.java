package Service;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import GUI.Window;
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
	

	public void run() { //TODO localFiles if data not on disk get it
		localFiles = BackupService.getLocalFiles();
		mdb = BackupService.getMdb();
		try {
			int deviationCounter = 0;
			while (true) {
				
				System.out.println(MESSAGE + "checking chunks replication degree");
				Window.log(MESSAGE + "checking chunks replication degree");

				sleep(ThreadLocalRandom.current().nextInt(WAIT_TIME_LOWER,
						WAIT_TIME_HIGHER + deviationCounter * WAIT_TIME_DEVIATION));

				for (int i = 0; i < localFiles.size(); i++) {
				
					ArrayList<Integer> chunks = localFiles.get(i).getChunksLowReplication();
					
					for(int j = 0; j < chunks.size(); j++) {
						mdb.backupChunk(localFiles.get(i), chunks.get(j));
					}
					sleep(ThreadLocalRandom.current().nextInt(WAIT_TIME_FILES_LOWER,WAIT_TIME_FILES_HIGHER));
				}
				deviationCounter++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
