package server;

import com.sun.xml.internal.bind.v2.TODO;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class ClientConnection extends Thread {

    private ServerSocket serverSocket;
    private Socket socket;
    private Server server;
    private int id;
    private String username;
    private Queue<String> outGoingMessage;


    public ClientConnection(int id, int port) throws IOException {
        this.id=id;
        ServerSocketFactory socketFactory = SSLServerSocketFactory.getDefault();
        serverSocket = socketFactory.createServerSocket(port);
    }

    public void run()
    {
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!interrupted()){

        }
    }

    private void pushMessage(String message){

    }

    //TODO addMessageToQueue hat noch keine funktion.
    public synchronized boolean addMessageToQueue(String message){
        return true;
    }

    public boolean hasConnection(){
        return socket!=null;
    }

    private boolean addUserToChannel(int channelID){
        return server.addUserToChannel(id,channelID);
    }
}
