package Main;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    private static final Logger logger = LogManager.getLogger(App.class);

    final private int portNumber = 6565;
    private String address;
    private boolean isHost;

    private Socket clientSocket;
    private ServerSocket serverSocket;

    private PrintWriter out;
    private BufferedReader in;


    private void connectAsClient(String address) throws IOException {
        clientSocket = new Socket(address, portNumber);
        isHost = false;
        logger.log(Level.INFO, "Starting connection as client on address " + address + ':' + portNumber);
    }

    private void makeServer() throws IOException {
        serverSocket = new ServerSocket(portNumber);
        isHost = true;
        logger.log(Level.INFO, "Starting connection as server on port" + portNumber);
        logger.log(Level.INFO, "Wairing for client to connect...");
        clientSocket = serverSocket.accept();
    }

    public Connection(String address) throws IOException {
        try {
            connectAsClient(address);
        } catch (UnknownHostException e) {
            makeServer();
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
}
