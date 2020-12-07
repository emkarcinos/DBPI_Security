package Main;

public interface Interactive {

    void beginSenderInteractive();

    void beginRecieverInteractive();

    public default void beginLoopAs(boolean isHost){
        // Host (the one who starts the app first) is Alice - ciphers the message.
        if(isHost)
            beginSenderInteractive();
        else
            beginRecieverInteractive();
    }
}
