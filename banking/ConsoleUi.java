package banking;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import banking.BankAccount;

import java.sql.SQLException;

public class ConsoleUi {

    AccountsRepository AR = new AccountsRepository();
    Scanner consoleSc = new Scanner(System.in);

    int choice;

    void start() {

        System.out.println("================================\n" + //
                "       BANKING SYSTEM\n" + //
                "================================");

        System.out.println("1. Create Account\n" + //
                "2. View All Accounts\n" + //
                "3. Check Account Balance\n" + //
                "4. Deposit Money\n" + //
                "5. Withdraw Money\n" + //
                "6. Transfer Money\n" + //
                "7. View Transaction History\n" + //
                "8. Exit");

        System.out.println("Enter your choice:");

        choice = consoleSc.nextInt();
        consoleSc.nextLine(); // Consume the leftover newline
        if (choice <= 0 || choice > 8) {
            throw new IllegalArgumentException("Choose options between 1-9");
        }

        switch (choice) {
            case 1:
                System.out.println("You picked option 1");
                String name;
                int amount;
                System.out.println("Please enter your full name: ");
                name = consoleSc.nextLine();
                System.out.println("Please enter initial balance: ");
                amount = consoleSc.nextInt();

                try {
                    int generatedId = AR.createAccount(name, amount);
                    System.out.println("Account Created Successfully. \n Your Account ID: " + generatedId);
                } catch (SQLException e) {
                    System.out.println(e);
                }

                break;

            case 2:
                System.out.println("You picked option 2");
                try {

                    ArrayList<BankAccount> bankAccounts = AR.getAllAccounts();
                    for (BankAccount bankAccount : bankAccounts) {
                        bankAccount.getDetails();
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                }
                break;

            case 3:
                System.out.println("You picked option 3");
                System.out.println("Enter your Id: ");
                int id = consoleSc.nextInt();

                try {

                    checkBalance(id);
                } catch (SQLException e) {
                    // TODO: handle exception
                }
                break;
            case 4:
                System.out.println("You picked option 4");
                System.out.println("Enter your Id: ");
                id = consoleSc.nextInt();
                System.out.println("Enter Amount to be deposited");
                amount = consoleSc.nextInt();

                try {

                    boolean deposited = AR.deposit(id, amount);

                    if (deposited) {
                        System.out.println("Amount Deposited");
                    } else {
                        System.out.println("Deposit Failed");
                    }
                } catch (SQLException e) {
                    System.out.println("Error in Deposite");
                }
                break;
            case 5:
                id = consoleSc.nextInt();
                amount = consoleSc.nextInt();

                try {
                    boolean withdrawn = AR.withdraw(id, amount);

                    if (withdrawn) {
                        System.out.println("Withdrawal successful");
                    } else {
                        System.out.println("Withdrawal failed");
                    }

                } catch (SQLException e) {
                    System.out.println("Error in withdrawal: " + e.getMessage());
                }
                break;

            case 6:
                System.out.println("You picked option 6");
                System.out.println("Enter Senders Id: ");
                int sid = consoleSc.nextInt();
                System.out.println("Enter Recivers Id: ");
                int rid = consoleSc.nextInt();
                System.out.println("Enter Amount to be send ");
                amount = consoleSc.nextInt();

                try {
                    BankAccount sender = AR.getAccount(sid);
                    BankAccount reciver = AR.getAccount(rid);

                    Thread t1 = new Thread(new Transfer(sender.getId(), reciver.getId(), amount));

                    t1.start();

                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    break;
                } catch (SQLException e) {
                    System.out.println("Error in Transfer");
                    return;
                }

            case 7:
                try {
                    ArrayList<String> transactions = AR.getTransactionHistory();

                    for (String transaction : transactions) {
                        System.out.println(transaction);
                    }
                } catch (SQLException e) {
                    System.out.println("Unable to retrieve transaction history.");
                    System.out.println(e.getMessage());
                }
                break;

            default:
                break;
        }
    }

    void checkBalance(int id) throws SQLException {

        try {

            BankAccount b1 = AR.getAccount(id);
            if (b1 == null) {
                System.out.println("Account does not exist with us");
                return;
            } else {
                System.out.println("Balance is : " + b1.getBalance());
            }
        } catch (SQLException e) {
            System.out.println("Error in check balance function");
        }

    }

}