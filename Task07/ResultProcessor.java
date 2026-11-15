import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class ResultProcessor {

    static class Result {
        final String file;
        final boolean success;
        final int score;
        final String message;

        Result(String file, boolean success, int score, String message) {
            this.file = file;
            this.success = success;
            this.score = score;
            this.message = message;
        }
    }

    static class ProcessFile implements Callable<Result> {

        private final Path file;

        ProcessFile(Path file) {
            this.file = file;
        }

        public Result call() {

            try {
                if (!Files.exists(file)) {
                    throw new IOException("File missing");
                }

                String value = Files.readString(file).trim();
                int score = Integer.parseInt(value);

                if (score < 0 || score > 100) {
                    throw new IllegalArgumentException("Score out of range");
                }

                return new Result(
                        file.getFileName().toString(),
                        true, score, "OK"
                );

            } catch (Exception e) {

                String error = e.getClass().getSimpleName()
                        + ": " + e.getMessage();

                System.out.println(
                        "ERROR: " + file.getFileName()
                        + " | " + error
                );

                return new Result(
                        file.getFileName().toString(),
                        false, 0, error
                );
            }
        }
    }

    static void runBenchmark(List<Path> files, int poolSize)
            throws Exception {

        ExecutorService pool =
                Executors.newFixedThreadPool(poolSize);

        ConcurrentLinkedQueue<Result> results =
                new ConcurrentLinkedQueue<>();

        long start = System.nanoTime();

        try {
            List<Future<Result>> futures = new ArrayList<>();

            for (Path file : files) {
                futures.add(pool.submit(new ProcessFile(file)));
            }

            for (Future<Result> future : futures) {
                results.add(future.get());
            }

        } finally {
            pool.shutdown();

            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        }

        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - start
        );

        long success = results.stream()
                .filter(r -> r.success)
                .count();

        long failed = results.size() - success;

        System.out.println("--------------------------------------");

        System.out.printf(
                "Pool=%d | total=%d | success=%d | failed=%d | time=%d ms%n",
                poolSize, results.size(), success, failed, elapsedMs
        );

        System.out.println("Verification: "
                + (success + failed) + " = " + results.size());
    }

    public static void main(String[] args) throws Exception {

        Path dir = Paths.get("score-files");
        Files.createDirectories(dir);

        List<Path> inputs = new ArrayList<>();

        int totalFiles = args.length > 0
                ? Integer.parseInt(args[0]) : 10000;

        // Reserve one task for a missing-file case.
        int existingFiles = totalFiles - 1;

        for (int i = 1; i <= existingFiles; i++) {

            Path p = dir.resolve(
                    String.format("student-%05d.txt", i)
            );

            if (i == 7) {
                Files.writeString(p, "corrupt");
            } else {
                Files.writeString(
                        p, String.valueOf((i * 7) % 101)
                );
            }

            inputs.add(p);
        }

        // Add one missing-file task.
        inputs.add(dir.resolve("missing-student.txt"));

        System.out.println("Total submitted files: " + inputs.size());

        for (int pool : new int[]{1, 2, 4, 8}) {
            System.out.println("\nRunning with pool size: " + pool);
            runBenchmark(inputs, pool);
        }
    }
}
