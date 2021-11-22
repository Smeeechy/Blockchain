package smeechy;

import java.io.Serializable;
import java.util.ArrayList;

public final class Blockchain implements Serializable {
    private final long serialVersionUID = 2L;

    private final long BOUNTY = 100L;
    private final ArrayList<Block> BLOCKS = new ArrayList<>();

    private ArrayList<Transaction> newTransactions = new ArrayList<>();
    private ArrayList<Transaction> oldTransactions = new ArrayList<>();
    private int zeros;

    public Blockchain() {
        this(0);
    }

    /**
     *
     * @param zeros The number of zeros the hash of each proved {@code Block} must begin with.
     */
    public Blockchain(int zeros) {
        this.zeros = zeros;
    }

    /**
     *
     * @return  the current number of {@code Blocks} in this {@code Blockchain}.
     */
    public int getLength() {
        return this.BLOCKS.size();
    }

    public int getZeros() {
        return this.zeros;
    }

    public String getHashOfLastBlock() {
        if (this.BLOCKS.isEmpty()) {
            return "0";
        } else {
            return this.BLOCKS.get(BLOCKS.size() - 1).generateHash();
        }
    }

    public void addBlock(Tuple tuple) {
        System.out.println("Block:");
        System.out.println("Created by miner" + tuple.block.getMiner().getId());
        tuple.block.getMiner().giveCreditForBlock(BOUNTY);
        System.out.println(tuple.block);
        System.out.print("Block was generating for " + tuple.duration + " seconds\nN was ");
        if (tuple.duration < 10) {
            this.zeros++;
            System.out.print("increased to ");
        } else if (tuple.duration > 60 && this.zeros > 0) {
            this.zeros--;
            System.out.print("decreased to ");
        } else {
            System.out.print("left at ");
        }
        System.out.println(zeros + "\n");
        BLOCKS.add(tuple.block);
        this.oldTransactions = this.newTransactions;
        this.newTransactions = new ArrayList<>();
    }

    /**
     * Lazily iterates through every {@code Block} from the first and compares hash values.
     *
     * @return  {@code true} if every {@code Block} in the {@code Blockchain} contains the correct hash
     * of the {@code Block} immediately before it, otherwise {@code false}.
     */
    public boolean isValid() {
        String previousHash = "0";
        for (Block block : this.BLOCKS) {
            if (!block.getPreviousHash().equals(previousHash)) return false;
            previousHash = block.generateHash();
        }
        return true;
    }

    public synchronized void addTransaction(Transaction transaction) {
        this.newTransactions.add(transaction);
    }

    public ArrayList<Transaction> getTransactions() {
        return this.oldTransactions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Block block : this.BLOCKS) {
            builder.append(block.toString());
        }
        return builder.toString();
    }
}