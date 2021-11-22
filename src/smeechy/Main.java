package smeechy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int MAX_BLOCKCHAIN_LENGTH = 15;
    private static final int MAX_THREAD_COUNT = 5;
    private static final int MAX_TRANSACTION_AMOUNT = 100;
    private static final File FILE = new File("/data/blockchain.bin");

    private static Blockchain blockchain = new Blockchain();

    public static void main(String[] args) {
        List<Miner> miners = new ArrayList<>();
        for (int i = 0; i < MAX_THREAD_COUNT; i++) {
            miners.add(new Miner(i + 1, blockchain));
        }
        ExecutorService minerPool;
        while (blockchain.getLength() < MAX_BLOCKCHAIN_LENGTH) {
            try {
                trade(miners);
                minerPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
                Tuple result = minerPool.invokeAny(miners);
                blockchain.addBlock(result);
                minerPool.shutdown();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void trade(List<Miner> miners) {
        Miner payee;
        Miner recipient;
        while (true) {
            payee = miners.get(new Random().nextInt(MAX_THREAD_COUNT));
            do {
                recipient = miners.get(new Random().nextInt(MAX_THREAD_COUNT));
            } while (payee.getId() == recipient.getId());
            long amount = new Random().nextInt((int) payee.getBalance());
            Transaction transaction = payee.pay(recipient, amount);
            if (transaction != null) {
                blockchain.addTransaction(transaction);
                if (amount > MAX_TRANSACTION_AMOUNT) {
                    break;
                }
            }
        }
    }

    /**
     * Saves the current blockchain to a predetermined file.
     *
     * @return {@code true} if saved successfully, otherwise {@code false}.
     */
    public static boolean saveBlockchain() {
        try (FileOutputStream fos = new FileOutputStream(FILE);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            if (!FILE.exists()) {
                FILE.createNewFile();
            }
            oos.writeObject(blockchain);
            return true;
        } catch (FileNotFoundException fnf) {
            System.out.println("File not found at " + FILE.getAbsolutePath());
        } catch (InvalidClassException ic) {
            System.out.println("Invalid class");
        } catch (NotSerializableException ns) {
            System.out.println("Not serializable");
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }
        return false;
    }

    /**
     * Loads a blockchain from a predetermined file.
     *
     * @return {@code true} if loaded successfully, otherwise {@code false}.
     */
    public static boolean loadBlockchain() {
        try (FileInputStream fis = new FileInputStream(FILE);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            blockchain = (Blockchain) ois.readObject();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

