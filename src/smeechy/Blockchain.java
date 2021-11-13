package smeechy;

import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public final class Blockchain {
    private final ArrayList<Block> blocks;
    private final int zeros;

    /**
     *
     * @param zeros The number of zeros the hash of each proved {@code Block} must begin with.
     */
    public Blockchain(int zeros) {
        this.blocks = new ArrayList<>();
        this.zeros = zeros;
    }

    /**
     *
     * @return  the current number of {@code Blocks} in this {@code Blockchain}.
     */
    public int getLength() {
        return this.blocks.size();
    }

    /**
     * Generates a new {@code Block} for the {@code Blockchain}. Continuously tries random integers until new
     * {@code Block} generates hash with proper number of leading zeros. After each successful generation, outputs
     * the generated {@code Block} to the console along with the duration in seconds the process took.
     */
    public void generateBlock() {
        LocalTime start = LocalTime.now();
        Block block;
        if (this.blocks.isEmpty()) {
            block = new Block(1, "0");
        } else {
            block = new Block(blocks.size() + 1, blocks.get(blocks.size() - 1).generateHash());
        }
        String hash = block.generateHash();
        String regex = String.format("^0{%d,}.*", this.zeros);
        Random prng = new Random();
        while (!hash.matches(regex)) {
            block.setMagic(prng.nextInt());
            hash = block.generateHash();
        }
        System.out.println(block);
        System.out.println("Block was generating for " +
                Duration.between(start, LocalTime.now()).getSeconds() + " seconds\n");
        this.blocks.add(block);
    }

    /**
     * Lazily iterates through every {@code Block} from the first and compares hash values.
     *
     * @return  {@code true} if every {@code Block} in the {@Blockchain} contains the correct hash
     * of the one immediately before it, otherwise {@code false}.
     */
    public boolean validate() {
        String previousHash = "0";
        for (Block block : this.blocks) {
            if (!block.getPreviousHash().equals(previousHash)) return false;
            previousHash = block.generateHash();
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Block block : this.blocks) {
            builder.append(block.toString());
        }
        return builder.toString();
    }
}

final class Block {
    private final long id;
    private final long timestamp;
    private final String previousHash;
    private int magic;

    public Block(long id, String previousHash) {
        this(id, previousHash, 0);
    }

    public Block(long id, String previousHash, int magic) {
        this.id = id;
        this.timestamp = new Date().getTime();
        this.previousHash = previousHash;
        this.magic = magic;
    }

    public String generateHash() {
        return StringUtil.applySha256(this.id + this.timestamp + this.previousHash + this.magic);
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Block:\n");
        builder.append("Id: ").append(this.id).append("\n");
        builder.append("Timestamp: ").append(this.timestamp).append("\n");
        builder.append("Hash of the previous block: \n").append(this.previousHash).append("\n");
        builder.append("Hash of the block: \n").append(this.generateHash());
        return builder.toString();
    }
}

final class StringUtil {
    /* Applies Sha256 to a string and returns a hash. */
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte elem: hash) {
                String hex = Integer.toHexString(0xff & elem);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}