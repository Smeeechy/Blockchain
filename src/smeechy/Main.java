package smeechy;

public class Main {
    public static void main(String[] args) {
        Blockchain shitcoin = new Blockchain();
        shitcoin.generateBlock();
        shitcoin.generateBlock();
        shitcoin.generateBlock();
        shitcoin.generateBlock();
        shitcoin.generateBlock();
        System.out.println("Validated: " + shitcoin.validate());
        System.out.println(shitcoin);
    }
}
