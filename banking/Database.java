
package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    // private static final String URL = "jdbc:sqlite:banking.db";
    private static final String URL =
        System.getProperty("banking.db.url", "jdbc:sqlite:banking.db");

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);

        try (var stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            conn.close();
            throw e;
        }

        return conn;
    }

    public static void createTables() {

        String createAccountTable = """
                CREATE TABLE IF NOT EXISTS accounts (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    balance INTEGER NOT NULL CHECK(balance >= 0)
                );
                """;

        String createTransactionTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY,
                    senderId INTEGER,
                    receiverId INTEGER,
                    amount INTEGER NOT NULL CHECK(amount > 0),
                    type TEXT NOT NULL CHECK(type IN ('deposit', 'withdraw', 'transfer')),
                    FOREIGN KEY (senderId) REFERENCES accounts(id),
                    FOREIGN KEY (receiverId) REFERENCES accounts(id),
                    CHECK (
                        (type = 'deposit' AND senderId IS NULL AND receiverId IS NOT NULL)
                        OR
                        (type = 'withdraw' AND senderId IS NOT NULL AND receiverId IS NULL)
                        OR
                        (type = 'transfer' AND senderId IS NOT NULL AND receiverId IS NOT NULL AND senderId != receiverId)
                    )
                );
                """;

        try (Connection conn = connect();
                var stmt = conn.createStatement()) {

            stmt.execute(createAccountTable);
            stmt.execute(createTransactionTable);

            System.out.println("Database tables created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
