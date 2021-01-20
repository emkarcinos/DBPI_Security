package Main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter server IP address (leave blank if hosting): ");
            String address = scanner.nextLine();
            Connection connection = new Connection(address);
            Interactive interactive = new BlindSignature(connection.getIn(), connection.getOut());
            interactive.beginLoopAs(connection.isHost());

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
