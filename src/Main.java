import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        final int THREADS_AMOUNT = 4;
        final int MAX_NUMBER = 10_000_000;
        final int NUMBERS_AMOUNT = 1_000_000;

        // numero negativo para rodar infinitamente
        final int timesToRun = 5;

        Trabalhadora[] trabalhadoras = new Trabalhadora[THREADS_AMOUNT];
        ArrayList<Semaphore> semaphores = new ArrayList<>(THREADS_AMOUNT);
        Semaphore combinatorSemaphore = new Semaphore(0);

        ArrayList<String> filesReady = new ArrayList<>();

        for(int i = 0; i < THREADS_AMOUNT; i++)
            semaphores.add(new Semaphore(0));

        for(int i = 0; i < THREADS_AMOUNT; i++)
            trabalhadoras[i] = new Trabalhadora(semaphores.get(i), semaphores, combinatorSemaphore, "src/output/", filesReady, NUMBERS_AMOUNT, MAX_NUMBER, timesToRun);

        Combinadora combinadora = new Combinadora(combinatorSemaphore, filesReady, "src/output/results", semaphores, timesToRun, true);

        combinadora.start();
        for(Thread t : trabalhadoras){
            t.start();
        }
        try {
            for(Thread t : trabalhadoras){
                t.join();
            }
            combinadora.join();
            System.out.println("Codigo finalizado!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}