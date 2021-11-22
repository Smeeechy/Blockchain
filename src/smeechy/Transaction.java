package smeechy;

public class Transaction {
    private final Miner payee;
    private final Miner recipient;
    private final long amount;

    /**
     * Bundles data from transactions to be recorded in the Blockchain.
     * @param payee     Miner making payment
     * @param recipient Miner receiving payment
     * @param amount    Amount that is being transferred between Miners.
     */
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
