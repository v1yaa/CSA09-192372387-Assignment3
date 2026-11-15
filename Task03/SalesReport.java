import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SalesReport {
    // Manual-close version. If reading throws before close(), the reader can leak.
    static void generateManual(List<Path> inputs, Path output) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(output);
        try {
            for (Path input : inputs) {
                BufferedReader reader = Files.newBufferedReader(input);
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                } finally {
                    reader.close();
                }
            }
        } finally {
            writer.close();
        }
    }

    // Safe version: each reader and the writer are closed automatically.
    static void generateSafe(List<Path> inputs, Path output) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            for (Path input : inputs) {
                try (BufferedReader reader = Files.newBufferedReader(input)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.err.println("Failed while processing " + input + ": " + e.getMessage());
                    throw e;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Paths.get("sample-data");
        Files.createDirectories(dir);
        Path a = dir.resolve("store-a.txt");
        Path b = dir.resolve("store-b.txt");
        Files.write(a, Arrays.asList("Store A,1000", "Store A,1250"));
        Files.write(b, Arrays.asList("Store B,900", "Store B,1100"));
        List<Path> stores = Arrays.asList(a, b);
        generateManual(stores, dir.resolve("manual-report.txt"));
        generateSafe(stores, dir.resolve("safe-report.txt"));
        System.out.println("Manual and try-with-resources reports generated.");
        System.out.println("Reports equal: " + Files.readAllLines(dir.resolve("manual-report.txt"))
                .equals(Files.readAllLines(dir.resolve("safe-report.txt"))));
    }
}
