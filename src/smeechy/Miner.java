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

    public synchronized void giveCreditForBlock(long amount) {
        this.blocksMined++;
        this.balance += amount;
        System.out.println("Miner" + this.ID + " gets " + amount + " VC");
    }

    public int getBlocksMined() {
        return this.blocksMined;
    }

    @Override
    public Tuple call() {
        LocalTime start = LocalTime.now();
        Block block = new Block(this, blockchain.getLength() + 1, blockchain.getHashOfLastBlock(), blockchain.getTransactions());
        mine(block);
        LocalTime end = LocalTime.now();
        long duration = Duration.between(start, end).toMillis();
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

class Tuple {
    public final Block block;
    public final long duration;

    public Tuple(Block block, long duration) {
        this.block = block;
        this.duration = duration;
    }
}
