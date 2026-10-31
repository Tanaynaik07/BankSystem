package banking;

import java.sql.SQLException;

public class Transfer implements Runnable {

    private final int senderId;
    private final int receiverId;
    private final int amount;

    private final AccountsRepository AR = new AccountsRepository();

    Transfer(int senderId, int receiverId, int amount) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            boolean transferred = AR.transfer(senderId, receiverId, amount);

            if (transferred) {
                System.out.println("Transfer successful");
            } else {
                System.out.println("Transfer unsuccessful");
            }

        } catch (SQLException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
}