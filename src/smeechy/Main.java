package smeechy;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter how many zeros the hash must start with: ");
        int zeros = scanner.nextInt();
        Blockchain dogecoin = new Blockchain(zeros);
        while (true) {
            dogecoin.generateBlock();
        }
    }
}
