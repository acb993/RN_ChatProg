package server;



import util.Message;


import java.io.DataInputStream;
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
    private Queue<Message> outGoingMessage;


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
            System.out.println("Verbindung aufgebaut zu: "+socket.getRemoteSocketAddress());
            while(!interrupted()){
                if(inFromClient.available()<0){

                }else if(!outGoingMessage.isEmpty()){
                    pushMessage(outGoingMessage.poll());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void pushMessage(Message message) throws IOException {
        outFromServer.writeBytes(String.format("MESSAGE FROM %s %s \r\n", message.getUserId(),message.getChannelId()));
        message.getBody().stream().forEach(line -> {
            try {
                outFromServer.writeBytes(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public int getid(){
        return id;
    }

    //TODO addMessageToQueue hat noch keine funktion.
    public synchronized boolean addMessageToQueue(Message message){
        if(outGoingMessage.contains(message)){
            return false;
        }else{
            outGoingMessage.add(message);
            return true;
        }
    }

    private void analyzeInput(String input){

    }

    public boolean hasConnection(){
        return socket!=null;
    }

    private boolean addUserToChannel(int channelID){
        return server.addUserToChannel(this,channelID);
    }
}
