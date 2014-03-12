import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class BackupService {
	
	private InetAddress mcAddress;
	private int mcPort;
	private InetAddress mdbAddress;
	private int mdbPort;
	private InetAddress mdrAddress;
	private int mdrPort;
	
	public static void main(String[] args) {
		
		/*if(args.length != 6) {
			System.out.println("<MC_ADDRESS> <MC_PORT> <MDB_ADDRESS> <MDB_PORT> <MDR_ADDRESS> <MDR_PORT>");
			return;
		}
		
		try {
			BackupService service = new BackupService(args);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}*/
		
		LocalFiles files=new LocalFiles();
	}
	
	public BackupService(String args[]) throws UnknownHostException {
		
		this.mcAddress =  InetAddress.getByName(args[0]);
		this.mcPort = Integer.parseInt(args[1]);
		this.mdbAddress = InetAddress.getByName(args[2]);
		this.mdbPort = Integer.parseInt(args[3]);
		this.mdrAddress = InetAddress.getByName(args[4]);
		this.mdrPort = Integer.parseInt(args[5]);
	}

}
