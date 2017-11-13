package server;

import com.sun.xml.internal.bind.v2.TODO;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class ClientConnection extends Thread {

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream inFromClient;
    private DataOutputStream outFromServer;
    private Server server;
    private int id;
    private String username;
    private Queue<String> outGoingMessage;


    public ClientConnection(int id, ServerSocket serverSocket) throws IOException {
        this.id=id;
        this.serverSocket = serverSocket;
    }

    public void run()
    {
        try {
            socket = serverSocket.accept();
            inFromClient = new DataInputStream((socket.getInputStream()));
            outFromServer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(!interrupted()){

        }
    }

    private void pushMessage(String message){

    }
    public int getid(){
        return id;
    }

    //TODO addMessageToQueue hat noch keine funktion.
    public synchronized boolean addMessageToQueue(String message){
        return true;
    }

    public boolean hasConnection(){
        return socket!=null;
    }

    private boolean addUserToChannel(int channelID){
        return server.addUserToChannel(this,channelID);
    }
}
