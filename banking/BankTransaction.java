package banking;

public class BankTransaction {

    private final int id;
    private final String type;
    private final String senderName;
    private final String receiverName;
    private final int amount;

    public BankTransaction(
            int id,
            String type,
            String senderName,
            String receiverName,
            int amount) {

        this.id = id;
        this.type = type;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public int getAmount() {
        return amount;
    }
}