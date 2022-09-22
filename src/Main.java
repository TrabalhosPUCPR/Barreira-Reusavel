import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        final int threadsAmount = 4;

        // numero negativo para rodar infinitamente
        final int timesToRun = 1;

        Trabalhadora[] trabalhadoras = new Trabalhadora[threadsAmount];
        ArrayList<Semaphore> semaphores = new ArrayList<>(threadsAmount);
        Semaphore combinatorSemaphore = new Semaphore(0);

        ArrayList<String> filesReady = new ArrayList<>();

        for(int i = 0; i < threadsAmount; i++)
            semaphores.add(new Semaphore(0));

        for(int i = 0; i < threadsAmount; i++)
            trabalhadoras[i] = new Trabalhadora(semaphores.get(i), semaphores, combinatorSemaphore, "src/output/", filesReady, 1_000_000, 10_000_000, timesToRun);

        Combinadora combinadora = new Combinadora(combinatorSemaphore, filesReady, "src/output/results", semaphores, timesToRun);

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