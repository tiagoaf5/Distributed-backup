import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Classe responsável pela leitura e armazenamento da informação relativas aos
 * ficheiros locais que irão entrar no sistema de backup.
 */
public class LocalFiles {

	private static final String FILENAME = "files.txt";
	private static final String FILEBEGIN = "/*File format:";
	private static final int COMMENTSIZE = 13;
	private float diskSpace; //em kBytes
	private List<MyFile> files; 

	public LocalFiles() {
		
		files=new ArrayList<MyFile>();
		readFile();
	}

	public float getDiskSpace() {
		return diskSpace;
	}

	public void setDiskSpace(float diskSpace) {
		this.diskSpace = diskSpace;
	}

	/*
	 * Conteudo Ficheiro files.txt:
	 * espaçoDisco
	 * replicaçao1 - nomeFicheiro1 
	 * replicaçao2 - nomeFicheiro2
	 * ...
	 * replicaçaoN - nomeFicheiroN 
	 */
	private void readFile() {
	
		FileInputStream fileStream;
		
		try {
			fileStream = new FileInputStream(FILENAME);
			DataInputStream input = new DataInputStream(fileStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			
			if((line=reader.readLine())!=null) {
				if(line.startsWith(FILEBEGIN)) {
					for(int i=0; i<COMMENTSIZE; i++) {
						line = reader.readLine();
					}
				}
				try {
					setDiskSpace(Float.parseFloat(line));
					System.out.println(getDiskSpace());
				} catch(Exception e) {
					System.out.println("ERRO files.txt mal definido: falta espaço máximo de disco.");
					return;
				}
			} else {
				System.out.println("ERRO files.txt vazio.");
				return;
			}
			
			while ((line = reader.readLine()) != null)   {
				
				String[] splits=line.split(" - ", 2);
				if(splits.length!=2) {
					System.out.println("ERRO files.txt: linha " + line + " mal definida.");
					return;
				}
				
				MyFile newFile=new MyFile(splits[1], splits[0]);
				files.add(newFile);
				
				//System.out.println(line);
				//System.out.println(splits[0] + ", " + splits[1]);
			}
			System.out.println("Numero de ficheiros lidos: " + files.size());
			
			reader.close();
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: files.txt não encontrado");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
