package banking;

import java.util.concurrent.atomic.AtomicInteger;

public class BankAccount {
   
    private final int id;
    private String name;
    private int balance;
    private static int nextId = 1;

    BankAccount(String name, int amount) {
        this(nextId++, name, amount);
    }

    BankAccount(int id, String name, int amount) {
        this.id = id;
        this.name = name;

        if (amount < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative!");
        }

        this.balance = amount;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    int getBalance() {
        return balance;
    }

    protected void setBalance(int amount) {
        if (amount >= 0) {
            balance = amount;
        }
    }

    synchronized boolean withdraw(int amount) {
        if (amount > balance || amount < 0) {
            return false;
        }
        balance -= amount;
        return true;

    }

    synchronized boolean deposite(int amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }

    synchronized void getDetails() {
        System.out.println("Id: " + getId() + " Name: " + name + " Balance: " + getBalance());
    }

}
