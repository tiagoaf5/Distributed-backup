Especificar endereços e portas:
-através da linha de comandos foi adotado o seguinte formato:
 <ip mc> <port number mc> <ip mdb> <port number mdb> <ip mdr> <port number mdr>
-interface antes de iniciar o serviço.


A versão do protocolo é configurável na interface estando disponíveis duas opções: 
-versão 1.0  que corresponde à versão “normal” do protocolo 
-versão 2.2 que corresponde a uma adição de uma valorização ao protocolo inicial. 
 Outras versões para além destas duas não serão aceites e o protocolo irá ignorar as mensagens.


Para adicionar ficheiros para backup, existem duas opções: 
-criar um ficheiro “files.txt” em que a primeira linha corresponde ao espaço disponível para backups e cada 
 linha seguinte corresponde a um ficheiro para backup seguindo o formato <grau de replicação> - <ficheiro>. 
-em tempo de execução, adicionar os ficheiros pretendidos tirando partido da interface fornecida.


Os ficheiros cujo restore foi pedido e concluído com sucesso são guardados na pasta “RestoredFiles”. Os chunks
guardados estão na pasta “RemoteFiles” bem como o ficheiro “remotes.txt” que indica para cada par FileId e 
ChunkNo guardado o grau de replicação corrente.