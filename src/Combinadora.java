import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Combinadora extends Thread{
    private static int combinatorNumber = 0;
    private final Semaphore semaphore;
    private final ArrayList<String> filesToMerge;
    private final String outputPath;
    private final ArrayList<Semaphore> threadsToWait;
    private int timesToRun;

    public Combinadora(Semaphore semaphore, ArrayList<String> filesToMerge, String outputPath, ArrayList<Semaphore> threadsToWait, int timesToRun) {
        this.semaphore = semaphore;
        this.filesToMerge = filesToMerge;
        this.outputPath = outputPath;
        this.threadsToWait = threadsToWait;
        this.timesToRun = timesToRun;
        File directoryCreator = new File(outputPath);
        directoryCreator.mkdir();
    }

    protected int pickLowestNumber(int[] numbers){
        int chosenLowestIndex = 0;
        for(int i = 1; i < numbers.length; i++){
            if(numbers[chosenLowestIndex] > numbers[i]){
                chosenLowestIndex = i;
            }
        }
        return chosenLowestIndex;
    }

    @Override
    public void run() {
        try {
            while (this.timesToRun != 0){
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

                int[] numbers = new int[bufferedReaders.length];

                for(int i = 0; i < bufferedReaders.length; i++){
                    numbers[i] = Integer.parseInt(bufferedReaders[i].readLine());
                }

                boolean loop = true;
                while (true){
                    int chosenLowest = pickLowestNumber(numbers);
                    writer.write(numbers[chosenLowest] + "\n");
                    if(bufferedReaders[chosenLowest].ready()){
                        numbers[chosenLowest] = Integer.parseInt(bufferedReaders[chosenLowest].readLine());
                    }else{
                        numbers[chosenLowest] = Integer.MAX_VALUE;
                    }
                    checkContinue : {
                        for(Integer n : numbers){
                            if(n != Integer.MAX_VALUE){
                                break checkContinue;
                            }
                        }
                        break;
                    }
                }
                writer.close();
                System.out.println("\n\n\n\n\nCOMBINADORA " + combinatorNumber + " FINALIZADO!!!\n\n\n");
                Thread.sleep(1000);

                System.out.println("\nCombinadora: sinalizando trabalhadoras para voltarem...\n\n");
                for(Semaphore s : threadsToWait){
                    s.release(threadsToWait.size());
                }
                this.timesToRun--;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Combinadora: Arquivo de saida nao encontrado!" + e);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
