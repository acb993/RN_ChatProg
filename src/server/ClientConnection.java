package server;


import util.Message;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientConnection extends Thread {

    private int zustand;
    private ServerSocket serverSocket;
    private Socket socket;
    private Server server;
    private int id;
    private String username;
    private ClientReader reader;
    private ClientWriter writer;


    public ClientConnection(int id, ServerSocket serverSocket, Server server) throws IOException {
        this.id = id;
        this.serverSocket = serverSocket;
        this.server = server;
    }

    public void run() {
        try {
            socket = serverSocket.accept();
            reader = new ClientReader(new DataInputStream((socket.getInputStream())), this);
            writer = new ClientWriter(new DataOutputStream(socket.getOutputStream()));
            zustand = 42;
            System.out.println("Verbindung aufgebaut zu: " + socket.getRemoteSocketAddress());
            reader.start();
            writer.start();
            addCommandToQueue(String.format("%d Hello, welcome on Server %s",zustand,InetAddress.getLocalHost()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public int getid() {
        return id;
    }

    public boolean addMessageToQueue(Message message) {
        return writer.addMessageToQueue(message);
    }

    public boolean addCommandToQueue(String message) {
        return writer.addCommandToQueue(message);
    }


    public boolean hasConnection() {
        return socket != null;
    }

    public boolean addUserToChannel(int channelID) {
        return server.addUserToChannel(this, channelID);
    }

    public synchronized void setZustand(int zustand) {
        this.zustand = zustand;
    }

    public int getZustand() {
        return zustand;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getClientId() {
        return id;
    }
}
