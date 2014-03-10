import java.io.File;

public class MyFile {

	private int replication;
	private String path;
	
	public MyFile() {
		setReplication(0);
	}
	
	public MyFile(int replication) {
		setReplication(replication);
	}

	public int getReplication() {
		return replication;
	}

	public void setReplication(int replication) {
		
		if(replication>9)
			this.replication=9;
		else
			this.replication = replication;
	}

	public String getId() {
		//TODO: calcular devidamente os ids
		return null;
	}
}
