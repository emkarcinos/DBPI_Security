package Main;

import javax.swing.plaf.synth.SynthUI;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Scanner;

public class Interactive {
    ProtocolManager protocolManager;

    public Interactive(Reader in, Writer out) {
        this.protocolManager = new ProtocolManager(in, out);
    }

    private void beginSenderInteractive(){
        System.out.print("You are Alice. Type the message you wish to send to Bob: ");

        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();

        System.out.printf("Your message is %s.\n", message);
        System.out.println("Ciphering...");

        Commitment commitment = new Commitment(message);
        byte[] sequenceA = commitment.getSequenceA();
        byte[] sequenceB = commitment.getSequenceB();
        byte[] hash = commitment.getHash();

        try {
            System.out.println("Sending sequence A");
            protocolManager.sendMessage(sequenceA);

            System.out.println("Sending hash");
            protocolManager.sendMessage(hash);

            System.out.print("Do you want to try cheating on Bob? [Y/n] ");
            String choice = scanner.nextLine();
            if(choice.equals("n")) {
                System.out.println("You decide not to try to decieve Bob.");
            } else {
                System.out.print("Type a new message: ");
                message = scanner.nextLine();
            }

            System.out.println("Sending sequence B");
            protocolManager.sendMessage(sequenceB);

            System.out.println("Sending message");
            protocolManager.sendMessage(message);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void beginRecieverInteractive(){
        System.out.println("You are Bob. Alice wishes you to send you a message.");
        System.out.println("Waiting for Alice...");

        try {

            System.out.println("Alice has ciphered a message for you. You'll get it once Alice chooses to do so.");
            System.out.println("She send you a sequence A and hash of the ciphered message.");

            byte[] sequenceA = protocolManager.getMessageBytes();
            System.out.println("Sequence A is: " + Utils.bytesToHex(sequenceA));

            byte[] hash = protocolManager.getMessageBytes();
            System.out.println("Hash is: " + Utils.bytesToHex(hash));

            Commitment commitment = new Commitment(sequenceA, hash);

            System.out.println("Waiting for Alice to uncover the message...");
            byte[] sequenceB = protocolManager.getMessageBytes();

            System.out.println("Alice has sent you sequence B and her message");

            System.out.println("Sequence B is: " + Utils.bytesToHex(sequenceB));

            String message = protocolManager.getMessageStr();
            System.out.println("Alice's message is: " + message);

            commitment.setSequenceB(sequenceB);
            commitment.setMessage(message);

            System.out.println("Checking whether Alice has changed her message...");
            boolean isHashCorrect = commitment.checkHash();

            if(isHashCorrect)
                System.out.println("Alice didn't decieve you!");
            else
                System.out.println("Alice decieved you! The message has been changed!");

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void beginLoopAs(boolean isHost){
        // Host (the one who starts the app first) is Alice - ciphers the message.
        if(isHost)
            beginSenderInteractive();
        else
            beginRecieverInteractive();
    }
}
