
import java.io.IOException;

// Base checked exception
class ExamException extends Exception {
    ExamException(String message) {
        super(message);
    }

    ExamException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Specific checked exceptions
class ExamWindowClosedException extends ExamException {
    ExamWindowClosedException(String message) {
        super(message);
    }
}

class StudentNotRegisteredException extends ExamException {
    StudentNotRegisteredException(String message) {
        super(message);
    }
}

class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Storage layer
class Storage {
    void save(String fileName, long sizeKb)
            throws SubmissionStorageException {

        try {
            if (sizeKb > 500) {
                throw new IOException("Disk quota exceeded");
            }

            // Simulate a storage device failure
            if (fileName.equals("storage-error.pdf")) {
                throw new IOException("Storage device is unavailable");
            }

            System.out.println("Saved " + fileName);

        } catch (IOException e) {
            // Preserve the original IOException as the cause
            throw new SubmissionStorageException(
                "Could not store " + fileName, e
            );
        }
    }
}

// Service layer
class ExaminationService {
    private final Storage storage = new Storage();

    void submit(String fileName, long sizeKb,
                boolean examOpen, boolean registered)
                throws ExamException {

        if (!examOpen) {
            throw new ExamWindowClosedException(
                "The examination deadline has passed"
            );
        }

        if (!registered) {
            throw new StudentNotRegisteredException(
                "Student is not registered for this course"
            );
        }

        storage.save(fileName, sizeKb);
    }
}

// Controller layer
public class StorageDemo {

    static void runTest(String testName, String fileName,
                        long sizeKb, boolean examOpen,
                        boolean registered) {

        ExaminationService service = new ExaminationService();

        System.out.println("\n--- " + testName + " ---");

        try {
            service.submit(fileName, sizeKb, examOpen, registered);
            System.out.println("Result: SUCCESS");

        } catch (ExamException e) {
            System.out.println("Result: FAILED");
            System.out.println("Error: " + e.getMessage());

            if (e.getCause() != null) {
                System.out.println("Cause: " +
                    e.getCause().getMessage());
            } else {
                System.out.println("Cause: No underlying cause");
            }

            // Print the complete exception chain
            if (e instanceof SubmissionStorageException) {
                System.out.println("Full chained stack trace:");
                e.printStackTrace(System.out);
            }
        }
    }

    public static void main(String[] args) {

        runTest("Valid submission",
                "answer01.pdf", 120, true, true);

        runTest("Deadline failure",
                "answer02.pdf", 120, false, true);

        runTest("Registration failure",
                "answer03.pdf", 120, true, false);

        runTest("Size/quota failure",
                "answer04.pdf", 800, true, true);

        runTest("Storage device failure",
                "storage-error.pdf", 120, true, true);
    }
}
