
import java.util.concurrent.CountDownLatch;

public class TokenCounterIfExperiment {

    private final int[] buffer = new int[5];
    private int count = 0;
    private int out = 0;

    private final CountDownLatch waitingConsumers =
            new CountDownLatch(2);

    public synchronized int take() throws InterruptedException {

        // Incorrect: using if instead of while
        if (count == 0) {
            System.out.println(
                    Thread.currentThread().getName()
                    + " is waiting because buffer is empty.");

            waitingConsumers.countDown();
            wait();
        }

        // No second condition check after waking up
        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.println(
                Thread.currentThread().getName()
                + " removed token: " + token
                + " | Buffer count: " + count);

        return token;
    }

    public synchronized void putOneToken() {

        buffer[0] = 99;
        count++;

        System.out.println("Producer added token: 99"
                + " | Buffer count: " + count);

        notifyAll();
    }

    public static void main(String[] args)
            throws InterruptedException {

        TokenCounterIfExperiment counter =
                new TokenCounterIfExperiment();

        Thread consumer1 = new Thread(() -> {
            try {
                counter.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-1");

        Thread consumer2 = new Thread(() -> {
            try {
                counter.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-2");

        consumer1.start();
        consumer2.start();

        // Wait until both consumers are waiting
        counter.waitingConsumers.await();

        // Add only one token and wake both consumers
        counter.putOneToken();

        consumer1.join();
        consumer2.join();

        System.out.println("Final buffer count: "
                + counter.count);
    }
}
