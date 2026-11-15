
public class SafeTransferDemo {

    static class Wallet {
        final int id;
        int balance = 1000;

        Wallet(int id) {
            this.id = id;
        }
    }

    static void transfer(Wallet a, Wallet b, int amount) {

        Wallet first = a.id < b.id ? a : b;
        Wallet second = a.id < b.id ? b : a;

        synchronized (first) {
            synchronized (second) {

                if (a.balance >= amount) {
                    a.balance -= amount;
                    b.balance += amount;
                }
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Wallet w1 = new Wallet(1);
        Wallet w2 = new Wallet(2);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                transfer(w1, w2, 10);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                transfer(w2, w1, 10);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final Wallet 1 Balance: " + w1.balance);
        System.out.println("Final Wallet 2 Balance: " + w2.balance);
        System.out.println("Total Balance: "
                + (w1.balance + w2.balance));
        System.out.println("Deadlock avoided successfully.");
    }
}
