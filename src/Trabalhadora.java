import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Trabalhadora extends Thread {

    private static int nTrabalhadores = 1;
    private int nOutputs = 0;
    private final int nTrabalhador;
    private final Semaphore semaphore, combinatorSemaphore;
    private final ArrayList<Semaphore> rendezvousSemaphores;
    private String outputFileDirectory;
    private final ArrayList<String> filesPaths;
    private final int NAmount, MaxNumber;

    public Trabalhadora(Semaphore semaphore, ArrayList<Semaphore> rendezvousSemaphores, Semaphore combinatorSemaphore, String fileOutputDirectory, ArrayList<String> files, int NAmount, int MaxNumber) {
        this.nTrabalhador = nTrabalhadores;
        nTrabalhadores++;
        this.semaphore = semaphore;
        this.combinatorSemaphore = combinatorSemaphore;
        this.rendezvousSemaphores = rendezvousSemaphores;
        this.outputFileDirectory = fileOutputDirectory;
        this.filesPaths = files;
        this.NAmount = NAmount;
        this.MaxNumber = MaxNumber;
    }

    @Override
    public void run() {
        while (true) {
            this.nOutputs++;
            System.out.println("Trabalhador " + this.nTrabalhador + ": criando arquivo temporario: " + this.nOutputs);
            File tempFile = new File(outputFileDirectory + "/tmp/temp_" + this.nTrabalhador + "_" + this.nOutputs + ".txt");
            try {
                tempFile.createNewFile();
                FileWriter writer = new FileWriter(tempFile);
                for (int i = 0; i < NAmount; i++) {
                    Random r = new Random();
                    writer.write(r.nextInt(this.MaxNumber) + "\n");
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Erro ao criar arquivo temporario: " + this.nOutputs + ": " + e);
            }

            System.out.println("Trabalhador " + this.nTrabalhador + ": lendo os numeros gerados no arquivo: " + this.nOutputs);
            int[] numbers = new int[this.NAmount];
            try {
                BufferedReader reader = new BufferedReader(new FileReader(tempFile));
                for (int i = 0; reader.ready(); i++) {
                    numbers[i] = Integer.parseInt(reader.readLine());
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler os arquivos temporarios: "  + this.nOutputs + ": " + e);
            }

            System.out.println("Trabalhador " + this.nTrabalhador + ": ordenando numeros dentro do arquivo gerado:" + this.nOutputs);
            heapsort(numbers);

            System.out.println("Trabalhador " + this.nTrabalhador + ": inserindo numeros ordenados dentro de novo arquivo: " + this.nOutputs);
            File outputFile = new File(this.outputFileDirectory + "/output_" + this.nTrabalhador + "_" + this.nOutputs + ".txt");
            try {
                outputFile.createNewFile();
                FileWriter writer = new FileWriter(outputFile);
                for (int i : numbers) {
                    writer.write(i + "\n");
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Erro ao criar novo arquivo: " + this.nOutputs + ": " + e);
            }

            System.out.println("Trabalhador " + this.nTrabalhador + ": iniciando processo de rendezvous: " + this.nOutputs);
            this.rendezvous();

            System.out.println("Trabalhador " + this.nTrabalhador + ": inserindo diretorio do arquivo na lista de arquivos: " + this.nOutputs);
            this.filesPaths.add(outputFile.getPath());

            System.out.println("Trabalhador " + this.nTrabalhador + ": sinalizando semaphoro da combinadora: " + this.nOutputs);
            this.combinatorSemaphore.release();
            try {
                this.semaphore.acquire(rendezvousSemaphores.size());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void rendezvous() {
        this.semaphore.release(this.rendezvousSemaphores.size());
        try {
            for (Semaphore s : rendezvousSemaphores) {
                s.acquire();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Erro durante o rendezvous: " + this.nOutputs + ": " + e);
        }
    }

    private static int[] heapsort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i);
        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            heapify(arr, i, 0);
        }
        return arr;
    }

    private static void heapify(int arr[], int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < n && arr[l] > arr[largest]) largest = l;
        if (r < n && arr[r] > arr[largest]) largest = r;
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            heapify(arr, n, largest);
        }
    }
}
