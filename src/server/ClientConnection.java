package server;



import util.Message;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class ClientConnection extends Thread {

    private int zustand;
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
            zustand=42;
            System.out.println("Verbindung aufgebaut zu: "+socket.getRemoteSocketAddress());
            while(!interrupted()){
                if(inFromClient.available()<0){
                    analyzeInput(inFromClient.readLine());
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

    private void analyzeInput(String input) throws IOException {
        if(input.startsWith("SEND MESSAGE")&&(zustand==44)){

        }else if(input.startsWith("GET CHANNEL")&&((zustand==43)||(zustand==44))){

        }else if(input.startsWith("GET USERS")&&(zustand==44)){

        }else if(input.startsWith("JOIN")&&((zustand==43)||(zustand==44))){
                input.replace("JOIN ","");
                int channelId = Integer.valueOf(input);
                if(addUserToChannel(channelId)){
                    outFromServer.writeBytes(String.format("OK,USER JOINED CHANNEL %d\r\n", channelId));
                    zustand=44;
                }else{
                    outFromServer.writeBytes(String.format("NO CHANNEL FOUND WITH ID %s\r\n", input));
                }
        }else if(input.startsWith("CREATE CHANNEL")&&((zustand==43)||(zustand==44))){

        }else if(input.startsWith("LEAVE CHANNEL")&&(zustand==44)){

        }else if(input.startsWith("GET ID")&&(zustand==42)){
                input.replace("GET ID ","");
                if(input.contains("")){
                    outFromServer.writeBytes(String.format("60 USERNAME %s IS NOT ALLOWED\r\n",input));
                }else{
                    zustand=43;
                    username=input;
                    outFromServer.writeBytes(String.format("ID IS %i", id));
                }
        }else if(input.startsWith("EXIT")){

        }else {
                outFromServer.writeBytes("60 COMMAND COULD NOT BE EXECUTED");
        }
    }

    public boolean hasConnection(){
        return socket!=null;
    }

    private boolean addUserToChannel(int channelID){
        return server.addUserToChannel(this,channelID);
    }
}
