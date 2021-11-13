package smeechy;

import java.io.*;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final File file = new File("/Users/darynsmith/IdeaProjects/BlockchainTest/data/blockchain.bin");
    private static Blockchain blockchain;

    public static void main(String[] args) {
        if (loadBlockchain()) {
            if (blockchain.validate()) {
                System.out.println("Valid blockchain loaded from file.");
            } else {
                System.out.println("Blockchain loaded from file is invalid.");
                createNewBlockchain();
            }
        } else {
            System.out.println("Unable to load blockchain from file.");
            createNewBlockchain();
        }
        generateNBlocks(5);
        System.out.println(blockchain.preview());
    }

    private static void createNewBlockchain() {
        System.out.print("Enter how many zeros the hash must start with: ");
        int zeros = scanner.nextInt();
        blockchain = new Blockchain(zeros);
    }

    private static void generateNBlocks(int n) {
        for (int i = 0; i < n; i++) {
            blockchain.generateBlock();
            saveBlockchain();
        }
    }

    public static boolean saveBlockchain() {
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            oos.writeObject(blockchain);
            return true;
        } catch (FileNotFoundException fnf) {
            System.out.println("File not found at " + file.getAbsolutePath());
        } catch (InvalidClassException ic) {
            System.out.println("Invalid class");
        } catch (NotSerializableException ns) {
            System.out.println("Not serializable");
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }
        return false;
    }

    public static boolean loadBlockchain() {
        try (FileInputStream fis = new FileInputStream(file);
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
