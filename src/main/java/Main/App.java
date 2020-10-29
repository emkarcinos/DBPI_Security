package Main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args){
        try {
            Connection connection = new Connection("localhost");
            Interactive interactive = new Interactive(connection.getIn(), connection.getOut());
            interactive.beginLoopAs(connection.isHost());

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
