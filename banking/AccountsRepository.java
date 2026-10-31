package banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.crypto.Data;

import banking.BankAccount;
import banking.Database;

public class AccountsRepository {

    int createAccount(String name, int amount) throws SQLException {

        if (name == null || name.isBlank() || amount < 0) {
            return -1;
        }

        String sql = """
                INSERT INTO accounts(name, balance)
                VALUES(?, ?)
                """;

        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, amount);

            int rows = pstmt.executeUpdate();

            if (rows != 1) {
                return -1;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Account was created, but its generated ID could not be retrieved.");
    }

    ArrayList<BankAccount> getAllAccounts() throws SQLException {
        String sql = """
                select * from accounts
                """;
        // BankAccount[] bankaccouts=new BankAccount[]();
        ArrayList<BankAccount> bankaccounts = new ArrayList<>();
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                BankAccount b1 = new BankAccount(rs.getInt("id"), rs.getString("name"), rs.getInt("balance"));
                bankaccounts.add(b1);
            }
        }

        return bankaccounts;
    }

    BankAccount getAccount(int id) throws SQLException {
        String sql = """
                select * from accounts
                where id=?
                """;

        BankAccount b1;
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    b1 = new BankAccount(rs.getInt("id"), rs.getString("name"), rs.getInt("balance"));
                } else {
                    return null;
                }
            }

        }

        return b1;
    }

    boolean updateBalance(Connection conn, int id, int amount) throws SQLException {
        String sql = """
                update accounts
                set balance = balance + ?
                where id=?;
                """;
        BankAccount b1;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, amount);
            pstmt.setInt(2, id);

            int row = pstmt.executeUpdate();

            return row == 1;

        }
    }

    boolean recordTransaction(Connection conn, Integer senderId, Integer recieverId, int amount, String type)
            throws SQLException {
        int row = 0;
        if (type == null)
            return false;
        type = type.toLowerCase();
        if (type.equals("deposit") || type.equals("withdraw") || type.equals("transfer")) {
            String sql = """
                    insert into transactions(senderId, receiverId, amount, type)
                    values(?,?,?,?);
                    """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                if (type.equals("deposit")) {
                    pstmt.setNull(1, java.sql.Types.INTEGER);
                    pstmt.setInt(2, recieverId);
                } else if (type.equals("withdraw")) {
                    pstmt.setInt(1, senderId);
                    pstmt.setNull(2, java.sql.Types.INTEGER);
                } else if (type.equals("transfer")) {
                    pstmt.setInt(1, senderId);
                    pstmt.setInt(2, recieverId);
                }
                pstmt.setInt(3, amount);
                pstmt.setString(4, type);

                row = pstmt.executeUpdate();
            }
        }
        return row == 1;
    }

    synchronized boolean deposit(int id, int amount) throws SQLException {
        if (amount <= 0)
            return false;

        try (Connection conn = Database.connect()) {

            conn.setAutoCommit(false);

            try {

                boolean updatedBalance = updateBalance(conn, id, amount);

                if (!updatedBalance) {
                    conn.rollback();
                    return false;
                }

                boolean recordedTransaction = recordTransaction(conn, null, id, amount, "deposit");

                if (!recordedTransaction) {
                    conn.rollback();
                    return false;
                }

                conn.commit();

                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    synchronized boolean withdraw(int id, int amount) throws SQLException {
        if (amount <= 0) {
            return false;
        }

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);

            try {
                // Debit only if the account exists and has sufficient balance
                boolean withdrawn = debitAccount(conn, id, amount);

                if (!withdrawn) {
                    conn.rollback();
                    return false;
                }

                // Record the withdrawal in the same transaction
                boolean recorded = recordTransaction(
                        conn, id, null, amount, "withdraw");

                if (!recorded) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    boolean transfer(int senderId, int receiverId, int amount) throws SQLException {

        if (amount <= 0 || senderId == receiverId) {
            return false;
        }

        try (Connection conn = Database.connect()) {

            conn.setAutoCommit(false);

            try {
                // 1. Withdraw from sender
                boolean withdrawn = debitAccount(conn, senderId, amount);

                if (!withdrawn) {
                    conn.rollback();
                    return false;
                }

                // 2. Deposit into receiver
                boolean deposited = updateBalance(conn, receiverId, amount);

                if (!deposited) {
                    conn.rollback();
                    return false;
                }

                // 3. Record transfer
                boolean recorded = recordTransaction(
                        conn, senderId, receiverId, amount, "transfer");

                if (!recorded) {
                    conn.rollback();
                    return false;
                }

                // 4. Commit everything
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    ArrayList<String> getTransactionHistory() throws SQLException {
        String sql = """
                select * from transactions;
                """;

        ArrayList<String> transactions = new ArrayList<>();

        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                transactions.add("Transaction Id: " + rs.getInt("id") + "\nSenderId: " + rs.getInt("senderId")
                        + "\nRecieverID: " + rs.getInt("receiverId") + "\nAmount: " + rs.getInt("amount") + "\nType: "
                        + rs.getString("type") + "\n");
            }
        }
        return transactions;
    }

    boolean debitAccount(Connection conn, int id, int amount) throws SQLException {

        String sql = """
                UPDATE accounts
                SET balance = balance - ?
                WHERE id = ? AND balance >= ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setInt(2, id);
            pstmt.setInt(3, amount);

            return pstmt.executeUpdate() == 1;
        }
    }

    long getTotalBalance() throws SQLException {

        String sql = "SELECT SUM(balance) AS total FROM accounts";

        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }
        }

        return 0;
    }

    int getTotalAccounts() throws SQLException {

        String sql = "SELECT COUNT(*) AS total FROM accounts";

        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;
    }

    ArrayList<BankTransaction> getRecentTransactions(int limit)
            throws SQLException {

        String sql = """
                SELECT
                    t.id,
                    t.type,
                    t.amount,
                    sender.name AS sender_name,
                    receiver.name AS receiver_name
                FROM transactions t
                LEFT JOIN accounts sender
                    ON t.senderId = sender.id
                LEFT JOIN accounts receiver
                    ON t.receiverId = receiver.id
                ORDER BY t.id DESC
                LIMIT ?
                """;

        ArrayList<BankTransaction> transactions = new ArrayList<>();

        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    String sender = rs.getString("sender_name");
                    String receiver = rs.getString("receiver_name");

                    if (sender == null) {
                        sender = "-";
                    }

                    if (receiver == null) {
                        receiver = "-";
                    }

                    BankTransaction transaction = new BankTransaction(
                            rs.getInt("id"),
                            rs.getString("type"),
                            sender,
                            receiver,
                            rs.getInt("amount"));

                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }
}
