package lesson35;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Race {

    private final CountDownLatch startRace;
    private final CountDownLatch finishRace;
    private final Lock lock = new ReentrantLock();

    private final ArrayList<Car> finishingSequence;
    private final ArrayList<Stage> stages;

    public ArrayList<Stage> getStages() { return stages; }

    public Race(int carCount, Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));
        this.finishingSequence = new ArrayList<>();
        this.startRace = new CountDownLatch(carCount);
        this.finishRace = new CountDownLatch(carCount);
    }

    public void ready(Car car) {
        startRace.countDown();
    }

    public void waitStart() throws InterruptedException {
        startRace.await();
    }
    public void waitFinish() throws InterruptedException {
        finishRace.await();
    }

    public void finish(Car car) {
        try {
            lock.lock();
            finishRace.countDown();
            finishingSequence.add(car);
            if (finishingSequence.size() == 1) {
                System.out.println(car.getName() + " - WIN");
            }
        } finally {
            lock.unlock();
        }
    }

    public void printWinners() {
        for (int i = 0; i < 3; i++) {
            System.out.printf("%s место занял %s%n", i+1, finishingSequence.get(i).getName());
        }
    }
}
