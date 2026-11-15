
public class SeatBooking {

    private final boolean[] booked;
    private int bookingsMade = 0;

    SeatBooking(int seats) {
        booked = new boolean[seats];
    }

    // Q5-1: UNSAFE VERSION - No synchronization
    public boolean bookUnsafe(int seat) {
        if (!booked[seat]) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }

            booked[seat] = true;
            bookingsMade++;
            return true;
        }

        return false;
    }

    // Q5-3: SYNCHRONIZED METHOD VERSION
    public synchronized boolean book(int seat) {
        if (!booked[seat]) {
            booked[seat] = true;
            bookingsMade++;
            return true;
        }

        return false;
    }

    // Q5-4: SYNCHRONIZED BLOCK VERSION
    public boolean bookWithBlock(int seat) {
        synchronized (this) {
            if (!booked[seat]) {
                booked[seat] = true;
                bookingsMade++;
                return true;
            }

            return false;
        }
    }

    // Unsafe booking without artificial delay for timing comparison
    public boolean bookUnsafeFast(int seat) {
        if (!booked[seat]) {
            booked[seat] = true;
            bookingsMade++;
            return true;
        }

        return false;
    }

    public int getBookingsMade() {
        return bookingsMade;
    }

    // Q5-5: Benchmark 10,000 booking attempts
    public static void benchmark(String version)
            throws InterruptedException {

        final int totalAttempts = 10000;
        final int threadCount = 100;
        final int attemptsPerThread = 100;

        SeatBooking screen = new SeatBooking(5000);
        Thread[] users = new Thread[threadCount];

        long startTime = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {

            final int threadNumber = i;

            users[i] = new Thread(() -> {

                for (int j = 0; j < attemptsPerThread; j++) {

                    int seat = (threadNumber * attemptsPerThread + j) % 5000;

                    switch (version) {

                        case "Unsafe":
                            screen.bookUnsafeFast(seat);
                            break;

                        case "Synchronized Method":
                            screen.book(seat);
                            break;

                        case "Synchronized Block":
                            screen.bookWithBlock(seat);
                            break;
                    }
                }
            });

            users[i].start();
        }

        for (Thread t : users) {
            t.join();
        }

        long endTime = System.nanoTime();

        double timeInMilliseconds =
                (endTime - startTime) / 1_000_000.0;

        System.out.println(
                version
                + " | Attempts: " + totalAttempts
                + " | Seats sold: " + screen.getBookingsMade()
                + " | Time: " + timeInMilliseconds + " ms"
        );
    }

    public static void main(String[] args)
            throws InterruptedException {

        // Q5-1 and Q5-2: Unsafe experiment
        System.out.println("========== UNSAFE VERSION ==========");

        int overbookingCount = 0;

        for (int run = 1; run <= 10; run++) {

            SeatBooking screen = new SeatBooking(1);
            Thread[] users = new Thread[100];

            for (int i = 0; i < users.length; i++) {
                users[i] = new Thread(() -> screen.bookUnsafe(0));
                users[i].start();
            }

            for (Thread t : users) {
                t.join();
            }

            int sold = screen.getBookingsMade();

            if (sold > 1) {
                overbookingCount++;
            }

            System.out.println(
                    "Run " + run + ": Seats sold = " + sold
            );
        }

        System.out.println(
                "Overbooking occurrences: "
                + overbookingCount + " out of 10 runs"
        );

        // Q5-3: Synchronized method experiment
        System.out.println("\n========== SYNCHRONIZED METHOD ==========");

        SeatBooking methodScreen = new SeatBooking(10);
        Thread[] methodUsers = new Thread[100];

        for (int i = 0; i < methodUsers.length; i++) {
            final int seat = i % 10;

            methodUsers[i] = new Thread(
                    () -> methodScreen.book(seat)
            );

            methodUsers[i].start();
        }

        for (Thread t : methodUsers) {
            t.join();
        }

        System.out.println(
                "Seats sold: " + methodScreen.getBookingsMade()
        );

        // Q5-4: Synchronized block experiment
        System.out.println("\n========== SYNCHRONIZED BLOCK ==========");

        SeatBooking blockScreen = new SeatBooking(10);
        Thread[] blockUsers = new Thread[100];

        for (int i = 0; i < blockUsers.length; i++) {
            final int seat = i % 10;

            blockUsers[i] = new Thread(
                    () -> blockScreen.bookWithBlock(seat)
            );

            blockUsers[i].start();
        }

        for (Thread t : blockUsers) {
            t.join();
        }

        System.out.println(
                "Seats sold: " + blockScreen.getBookingsMade()
        );

        // Q5-5: Timing comparison
        System.out.println("\n========== 10,000 BOOKING ATTEMPTS ==========");

        benchmark("Unsafe");
        benchmark("Synchronized Method");
        benchmark("Synchronized Block");

        System.out.println("\nAll experiments completed.");
    }
}