/*
 * Classe que representa um ficheiro local que entrará 
 * no sistema de backup
 */
public class MyFile {

	private int replication;
	private String path;
	
	public MyFile() {
		setReplication(0);
	}
	
	public MyFile(String path, int replication) {
		setPath(path);
		setReplication(replication);
	}
	
	public MyFile(String path, String replication) {
		setPath(path);
		setReplication(Integer.parseInt(replication));
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getId() {
		//TODO: calcular devidamente os ids
		
		//>http://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
		//>http://stackoverflow.com/questions/4793387/utf-16-encoding-in-java-versus-c-sharp
		//>http://beginnersbook.com/2013/12/java-string-getbytes-method-example/
	
		return null;
	}

	
}
