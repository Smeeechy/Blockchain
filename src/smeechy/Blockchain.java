package smeechy;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;

public final class Blockchain {
    private final ArrayList<Block> blocks;

    public Blockchain() {
        this.blocks = new ArrayList<>();
    }

    public void generateBlock() {
        if (this.blocks.isEmpty()) {
            blocks.add(new Block(1, "0"));
        } else {
            blocks.add(new Block(blocks.size() + 1, blocks.get(blocks.size() - 1).getHash()));
        }
    }

    public boolean validate() {
        String previousHash = "0";
        for (Block block : this.blocks) {
            if (!block.getPreviousHash().equals(previousHash)) return false;
            previousHash = block.getHash();
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
    private final String previousHash;
    private final long timestamp;

    public Block(long id, String previousHash) {
        this.id = id;
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
    }

    public String getHash() {
        return StringUtil.applySha256(this.id + this.previousHash + this.timestamp);
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Block:\n");
        builder.append("Id: " + this.id + "\n");
        builder.append("Timestamp: " + this.timestamp + "\n");
        builder.append("Hash of the previous block: \n" + this.previousHash + "\n");
        builder.append("Hash of the block: \n" + this.getHash() + "\n\n");
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