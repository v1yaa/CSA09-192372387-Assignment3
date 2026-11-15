import java.util.concurrent.CountDownLatch;

public class DeadlockDemo {

    static class Wallet {
        final int id;
        int balance = 1000;

        Wallet(int id) {
            this.id = id;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Wallet w1 = new Wallet(1);
        Wallet w2 = new Wallet(2);

        CountDownLatch ready = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            synchronized (w1) {
                System.out.println("Thread 1 locked Wallet 1");
                ready.countDown();

                try {
                    ready.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                synchronized (w2) {
                    System.out.println("Thread 1 locked Wallet 2");
                }
            }
        }, "Transfer-Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (w2) {
                System.out.println("Thread 2 locked Wallet 2");
                ready.countDown();

                try {
                    ready.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                synchronized (w1) {
                    System.out.println("Thread 2 locked Wallet 1");
                }
            }
        }, "Transfer-Thread-2");

        t1.start();
        t2.start();

        System.out.println("Deadlock demonstration started.");
        System.out.println("Process ID: " + ProcessHandle.current().pid());

        t1.join();
        t2.join();
    }
}
