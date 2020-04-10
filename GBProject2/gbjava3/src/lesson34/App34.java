package lesson34;

public class App34 {

    private final Object lock = new Object();
    private volatile char currentLetter;

    // Задание 1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз
    // (порядок – ABСABСABС). Используйте wait/notify/notifyAll.
    public static void main(String[] args) {

        App34 a = new App34();

        // начать с А
        a.currentLetter = 'A';

        // печатать последовательно
        new Thread(() -> a.printLetter('A')).start();
        new Thread(() -> a.printLetter('B')).start();
        new Thread(() -> a.printLetter('C')).start();

    }

    public void printLetter(char c) {

        synchronized (lock) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != c) {
                        //System.out.printf("{%s ждет}", c);
                        lock.wait();
                    }
                    System.out.print(c);
                    currentLetter = switch(c) { case 'A'->'B'; case 'B'->'C'; case 'C'->'A';
                        default -> throw new IllegalStateException("Unexpected value: " + c);
                    };
                    // c lock.notify(); зависает
                    lock.notifyAll();
                }
            } catch (InterruptedException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

}
