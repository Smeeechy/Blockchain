package smeechy;

public class Transaction {
    private final Miner payee;
    private final Miner recipient;
    private final long amount;

    public Transaction(Miner payee, Miner recipient, long amount) {
        this.payee = payee;
        this.recipient = recipient;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "#" + this.payee.getId() + " ---V$" + this.amount + "--> #" + this.recipient.getId();
    }
}
