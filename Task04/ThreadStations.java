public class ThreadStations {
    static class ExtendingStation extends Thread {
        private final String station;
        ExtendingStation(String station) { this.station = station; }
        public void run() {
            scan(station);
        }
    }

    static class RunnableStation implements Runnable {
        private final String station;
        RunnableStation(String station) { this.station = station; }
        public void run() { scan(station); }
    }

    private static void scan(String station) {
        try {
            Thread.sleep(100 + (long)(Math.random() * 400));
            System.out.println(station + " scanned 25 scripts on " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(station + " interrupted");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] stations = {
            new ExtendingStation("Station-1"),
            new Thread(new RunnableStation("Station-2")),
            new Thread(new RunnableStation("Station-3")),
            new Thread(new RunnableStation("Station-4"))
        };
        for (Thread station : stations) station.start();
        for (Thread station : stations) station.join();
        System.out.println("All stations finished; total scripts = 100");
    }
}
