package smeechy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public final class Block implements Serializable {
    private final long serialVersionUID = 2L;

    private final Miner MINER;
    private final long ID;
    private final long DATE_CREATED;
    private final String PREVIOUS_HASH;
    private final ArrayList<Transaction> DATA;

    private int magic;

    /**
     * Instantiates a new {@code Block} with specified {@code id} and {@code previousHash}. Sets {@code magic} to
     * default of 0.
     * @param id            this {@code Block}'s unique identifier.
     * @param previousHash  a reference to the hash of the last proved {@code Block} in the {@code Blockchain}.
     */
    public Block(Miner miner, long id, String previousHash, ArrayList<Transaction> data) {
        this(miner, id, previousHash, data, 0);
    }

    /**
     * Instantiates a new {@code Block} with specified {@code id}, {@code previousHash}, and {@code magic}.
     * @param id            this {@code Block}'s unique identifier.
     * @param previousHash  a reference to the hash of the last proved {@code Block} in the {@code Blockchain}.
     * @param magic         used to generate a valid hash for this {@code Block}.
     */
    public Block(Miner miner, long id, String previousHash, ArrayList<Transaction> data, int magic) {
        this.MINER = miner;
        this.ID = id;
        this.DATE_CREATED = new Date().getTime();
        this.PREVIOUS_HASH = previousHash;
        this.DATA = data;
        this.magic = magic;
    }

    public Miner getMiner() {
        return this.MINER;
    }

    /**
     *
     * @return  the hash of the {@code Block} immediately preceding this one in the {@code Blockchain}.
     */
    public String getPreviousHash() {
        return this.PREVIOUS_HASH;
    }

    /**
     * Uses SHA-256 encryption.
     * @return  a hash from all of the {@code Block}'s current field values.
     */
    public String generateHash() {
        return StringUtil.applySha256(this.MINER.getId() + this.ID + this.DATE_CREATED + this.PREVIOUS_HASH + this.DATA + this.magic);
    }

    /**
     * Used by {@code Miner}s while generating a valid {@code Block}.
     * @param magic the new value of the {@code Block}'s magic number.
     */
    public void setMagic(int magic) {
        this.magic = magic;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Id: ").append(this.ID).append("\n");
        builder.append("Timestamp: ").append(this.DATE_CREATED).append("\n");
        builder.append("Magic number: ").append(this.magic).append("\n");
        builder.append("Hash of the previous block: \n").append(this.PREVIOUS_HASH).append("\n");
        builder.append("Hash of the block: \n").append(this.generateHash()).append("\n");
        builder.append("Block data: ");
        if (this.DATA.isEmpty()) {
            builder.append(" none");
        } else {
            for (Transaction datum : this.DATA) {
                builder.append("\n").append(datum);
            }
        }
        return builder.toString();
    }
}
