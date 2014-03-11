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
		
		//>http://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
		//>http://stackoverflow.com/questions/4793387/utf-16-encoding-in-java-versus-c-sharp
		//>http://beginnersbook.com/2013/12/java-string-getbytes-method-example/
	
		return null;
	}
}
