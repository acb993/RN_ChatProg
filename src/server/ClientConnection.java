package server;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

public class ClientConnection {

    private ServerSocket socket;
    private int id;
    private String username;


    public ClientConnection(int id, int port) throws IOException {
        this.id=id;
        ServerSocketFactory socketFactory = SSLServerSocketFactory.getDefault();
        socket = socketFactory.createServerSocket(port);
    }

    public void run()
    {

    }
}
