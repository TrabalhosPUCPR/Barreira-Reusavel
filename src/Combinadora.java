import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Combinadora extends Thread{
    private static int combinatorNumber = 0;
    private final Semaphore semaphore;
    private ArrayList<String> filesToMerge;
    private String outputPath;
    private ArrayList<Semaphore> threadsToWait;

    public Combinadora(Semaphore semaphore, ArrayList<String> filesToMerge, String outputPath, ArrayList<Semaphore> threadsToWait) {
        this.semaphore = semaphore;
        this.filesToMerge = filesToMerge;
        this.outputPath = outputPath;
        this.threadsToWait = threadsToWait;
    }

    @Override
    public void run() {
        try {
            while (true){
                combinatorNumber++;
                System.out.println("Combinadora: aguardandado trabalhadoras");
                this.semaphore.acquire(threadsToWait.size());

                System.out.println("Combinadora: iniciando merge dos arquivos");
                BufferedReader[] bufferedReaders = new BufferedReader[filesToMerge.size()];
                for(int i = 0; i < filesToMerge.size(); i++){
                    bufferedReaders[i] = new BufferedReader(new FileReader(filesToMerge.get(i)));
                }

                filesToMerge.clear();

                FileWriter writer = new FileWriter(outputPath + "/ResultadoFinal_" + combinatorNumber + ".txt");
                while (bufferedReaders[0].ready()){
                    for(BufferedReader r : bufferedReaders){
                        writer.write(r.readLine() + "\n");
                    }
                }
                writer.close();
                System.out.println("COMBINADORA " + combinatorNumber + " FINALIZADO!!!");
                Thread.sleep(1000);

                System.out.println("Combinadora: sinalizando trabalhadoras para voltarem");
                for(Semaphore s : threadsToWait){
                    s.release(threadsToWait.size());
                }
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException("Combinadora: Arquivo de saida nao encontrado!" + e);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
