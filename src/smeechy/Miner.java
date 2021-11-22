package smeechy;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.Callable;

public final class Miner implements Callable<Tuple>, Comparable {
    private final long ID;

    private int blocksMined = 0;
    private long balance = 100L;
    private Blockchain blockchain;

    public Miner(long id, Blockchain blockchain) {
        this.ID = id;
        this.blockchain = blockchain;
    }

    /**
     * Attempts to find a valid hash for the given Block by brute-forcing with randomly assigned magic numbers.
     * @param block The Block for which to generate the hash
     */
    private void mine(Block block) {
        String hash = block.generateHash();
        String regex = String.format("^0{%d,}.*", this.blockchain.getZeros());
        Random prng = new Random();
        while (!hash.matches(regex)) {
            block.setMagic(prng.nextInt());
            hash = block.generateHash();
        }
    }

    public long getId() {
        return this.ID;
    }

    /**
     * Transfers the given amount from this Miner's wallet balance to the given recipient.
     * @param recipient The Miner that will receive the payment from this Miner.
     * @param payment   The amount that will be transferred.
     * @return  a Transaction that will be recorded in the Blockchain for the next generated Block.
     */
    public synchronized Transaction pay(Miner recipient, long payment) {
        if (payment > 0 && this.balance > payment) {
            this.balance -= payment;
            recipient.receive(payment);
            return new Transaction(this, recipient, payment);
        }
        return null;
    }

    private synchronized void receive(long credit) {
        if (credit > 0) {
            this.balance += credit;
        }
    }

    public synchronized long getBalance() {
        return this.balance;
    }

    /**
     * Invoked by the Blockchain to award a bounty to the first Miner to find a valid hash.
     * @param amount    The bounty this Miner earned. Assigned by the Blockchain.
     */
    public synchronized void giveCreditForBlock(long amount) {
        this.blocksMined++;
        this.balance += amount;
        System.out.println("Miner" + this.ID + " gets " + amount + " VC");
    }

    public int getBlocksMined() {
        return this.blocksMined;
    }

    /**
     * Creates a new Block based on the current info from the Blockchain. Records the time this process takes
     * in seconds.
     * @return  A Tuple containing the newly created Block and the time in seconds it took to generate.
     */
    @Override
    public Tuple call() {
        LocalTime start = LocalTime.now();
        Block block = new Block(this, blockchain.getLength() + 1, blockchain.getHashOfLastBlock(), blockchain.getTransactions());
        mine(block);
        LocalTime end = LocalTime.now();
        long duration = Duration.between(start, end).getSeconds();
        return new Tuple(block, duration);
    }

    @Override
    public String toString() {
        return "Miner #" + this.ID + " - " + this.blocksMined + " | V$" + this.balance;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Miner) {
            if (this.blocksMined == ((Miner) o).getBlocksMined()) {
                return (int) ((Miner) o).getBalance() - (int) this.balance;
            }
            return ((Miner) o).getBlocksMined() - this.blocksMined;
        }
        return 0;
    }
}

/**
 * Utility class for simplifying the process of recording and returning Blocks and the time they took to create.
 */
class Tuple {
    public final Block block;
    public final long duration;

    public Tuple(Block block, long duration) {
        this.block = block;
        this.duration = duration;
    }
}
