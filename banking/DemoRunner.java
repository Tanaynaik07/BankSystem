package banking;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Runs a repeatable demonstration of the banking repository.
 *
 * The demo uses banking-demo.db, not the regular banking.db.
 * It deletes and recreates only the demo database each time it runs.
 */
public class DemoRunner {

    public static void main(String[] args) throws Exception {
        // Set this before Database is first used.
        System.setProperty("banking.db.url", "jdbc:sqlite:banking-demo.db");

        resetDemoDatabase();

        Database.createTables();

        AccountsRepository repository = new AccountsRepository();

        System.out.println();
        System.out.println("=== JAVA BANKING SYSTEM: AUTOMATED DEMO ===");
        System.out.println();

        System.out.println("[1] Creating sample accounts...");
        int aliceId = repository.createAccount("Alice Demo", 10_000);
        int bobId = repository.createAccount("Bob Demo", 5_000);

        if (aliceId < 0 || bobId < 0) {
            throw new IllegalStateException("Could not create the sample accounts.");
        }

        System.out.println("Created account: Alice Demo (ID: " + aliceId
                + ", opening balance: ₹10,000)");
        System.out.println("Created account: Bob Demo   (ID: " + bobId
                + ", opening balance: ₹5,000)");
        System.out.println();

        System.out.println("[2] Depositing ₹2,500 into Alice's account...");
        requireSuccess(repository.deposit(aliceId, 2_500), "Deposit");
        System.out.println("Deposit successful.");
        System.out.println();

        System.out.println("[3] Withdrawing ₹1,000 from Alice's account...");
        requireSuccess(repository.withdraw(aliceId, 1_000), "Withdrawal");
        System.out.println("Withdrawal successful.");
        System.out.println();

        System.out.println("[4] Transferring ₹3,000 from Alice to Bob...");
        requireSuccess(repository.transfer(aliceId, bobId, 3_000), "Transfer");
        System.out.println("Transfer successful.");
        System.out.println();

        System.out.println("[5] Final account balances");
        System.out.println("----------------------------------------");
        printAccount(repository, aliceId);
        printAccount(repository, bobId);
        System.out.println();
        System.out.println("Total accounts: " + repository.getTotalAccounts());
        System.out.println("Total balance: ₹" + repository.getTotalBalance());
        System.out.println();

        System.out.println("[6] Recent transactions");
        System.out.println("----------------------------------------");
        ArrayList<BankTransaction> transactions = repository.getRecentTransactions(20);
        for (BankTransaction transaction : transactions) {
            System.out.printf(
                    "#%d %-8s | %s -> %s | ₹%d%n",
                    transaction.getId(),
                    transaction.getType().toUpperCase(),
                    transaction.getSenderName(),
                    transaction.getReceiverName(),
                    transaction.getAmount());
        }

        System.out.println();
        System.out.println("=== DEMO COMPLETED ===");
        System.out.println("Demo database: banking-demo.db");
        System.out.println("The regular banking.db database was not changed.");
    }

    private static void printAccount(AccountsRepository repository, int id)
            throws SQLException {
        BankAccount account = repository.getAccount(id);
        if (account == null) {
            throw new IllegalStateException("Sample account not found: " + id);
        }

        System.out.printf(
                "ID: %d | Name: %s | Balance: ₹%d%n",
                account.getId(),
                account.getName(),
                account.getBalance());
    }

    private static void requireSuccess(boolean success, String operation) {
        if (!success) {
            throw new IllegalStateException(operation + " failed during the demo.");
        }
    }

    private static void resetDemoDatabase() throws Exception {
        // Remove only the disposable demo database and its SQLite sidecar files.
        Files.deleteIfExists(Path.of("banking-demo.db"));
        Files.deleteIfExists(Path.of("banking-demo.db-wal"));
        Files.deleteIfExists(Path.of("banking-demo.db-shm"));
    }
}
