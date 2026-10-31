// import java.io.IOException;
package banking;

import java.sql.SQLException;

import banking.AccountsRepository;

public class Main {
    public static void main(String args[]) throws InterruptedException {
        System.out.println("Running Main");

        Database.createTables();

        AccountsRepository AR = new AccountsRepository();

        ConsoleUi console = new ConsoleUi();

        System.out.print(console.choice);

        while (console.choice != 8) {

            console.start();
        }

        // BankAccount b1 = new BankAccount(10000);
        // BankAccount b2 = new BankAccount(34000);

        // b1.getDetails();
        // BankAccount b3 = new BankAccount(5000);
        // b2.getDetails();
        // b3.getDetails();

        // Thread t1 = new Thread(new Transfer(b1, b2, 5000));

        // b3.withdraw(2000);
        // b3.getDetails();
        // b1.deposite(100000);
        // t1.start();

        // t1.join();
        // b1.getDetails();
        // b2.getDetails();

    }
}
