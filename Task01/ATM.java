import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ATM {
    private static final double BALANCE = 5000.00;

    static void withdraw(String accountText, String amountText, boolean cardFileRequired)
            throws FileNotFoundException {
        try {
            int account = Integer.parseInt(accountText);
            double amount = Double.parseDouble(amountText);

            if (cardFileRequired && !new File("card-reader.dat").exists()) {
                throw new FileNotFoundException("card-reader.dat was not found");
            }
            if (account <= 0) {
                throw new IllegalArgumentException("Account number must be positive");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }
            if (amount > BALANCE) {
                throw new IllegalStateException("Insufficient account balance");
            }
            System.out.printf("Withdrawal approved: %.2f%n", amount);
        } finally {
            System.out.println("ATM transaction cleanup completed.");
        }
    }

    public static void main(String[] args) {
        String account = args.length > 0 ? args[0] : "123456";
        String amount = args.length > 1 ? args[1] : "1000";
        boolean requireCard = args.length > 2 && args[2].equalsIgnoreCase("card");
        try {
            withdraw(account, amount, requireCard);
        } catch (NumberFormatException e) {
            System.out.println("Input error: enter numeric account and amount.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Transaction declined: " + e.getMessage());
        } catch (FileNotFoundException | SecurityException e) {
            System.out.println("Card reader access error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected ATM error: " + e.getMessage());
        }
    }
}
