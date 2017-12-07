package server;


import util.Message;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private List<Channel> channel;
    private List<ClientConnection> userList;
    private String ipAdresse;
    private String hostname;
    private int listeningPort;


    public static void main(String[] args){
        try {
            Server testServer= new Server(8080);
            testServer.run();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }



    public Server(int listeningPort) throws UnknownHostException {
        this.listeningPort = listeningPort;
        channel= new ArrayList<>();
        userList= new ArrayList<>();
        ipAdresse = InetAddress.getLocalHost().getHostAddress();
        hostname = InetAddress.getLocalHost().getHostName();
        ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
        try {
            serverSocket = socketFactory.createServerSocket(this.listeningPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
       while(!interrupted()){
           createClientConnection();

       }
    }


    public synchronized boolean removeUserFromAllChannel(ClientConnection user){
        channel.parallelStream().forEach(channel1 -> channel1.removeUser(user));
        return true;
    }


    public synchronized boolean addUserToChannel(ClientConnection user, int channelID) {
        if (checkIfChannelExists(channelID)) {
            Channel kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID() == channelID).findFirst().get();
            return kanal.addUser(user);
        }else{
            return false;
        }

    }

    public synchronized boolean removeUserFromChannel(ClientConnection user,int channelID){
        if(checkIfChannelExists(channelID)){
        Channel kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID()==channelID).findFirst().get();
        return kanal.removeUser(user);}
        else {
            return false;
        }
    }

    public synchronized int createChannel(String channelName){
        Channel kanal = new Channel(getNewChannelId(),channelName);
        channel.add(kanal);
        kanal.start();
        return kanal.getChannelID();
    }

    public synchronized void closeChannel(int id) throws InterruptedException {
        if(checkIfChannelExists(id)){
        Channel kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID()==id).findFirst().get();
        kanal.interrupt();
        kanal.join();
        channel.remove(kanal);}
    }

    public List<Channel> getAllChannel(){
        return new ArrayList<>(channel);
    }

    public List<ClientConnection> getAllUser(int channelID, int clientID){
        Channel kanal;
        if(checkIfChannelExists(channelID)) {
            kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID() == channelID).findFirst().get();
            if(!kanal.hasUser(clientID)){
                return null;
            }
            return kanal.getAllUser();
        }else{
            return null;
        }
    }

    public String gethostname(){
        return hostname;
    }

    public String getIpAdresse(){
        return ipAdresse;
    }

    public boolean sendMessageOverChannel(int channelID, Message message){
        Channel kanal;
        if(checkIfChannelExists(channelID)) {
            kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID() == channelID).findFirst().get();
            return kanal.addMessageToQueue(message);
        }else{
            return false;
        }

    }

    private ClientConnection createClientConnection(){
        try {
            ClientConnection client =  new ClientConnection(getNewUserId(),serverSocket,this);
            addClient(client);
            client.start();
            return client;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private synchronized void addClient(ClientConnection client){
        userList.add(client);
    }

    private int getNewUserId(){
        if(userList.isEmpty()){
            return 1;
        }else{
            return userList.parallelStream().max(Comparator.comparingInt(ClientConnection::getClientId)).get().getClientId()+1;
        }
    }
    private int getNewChannelId(){
        if(channel.isEmpty()){
            return 1;
        }else{
            return channel.parallelStream().max(Comparator.comparingInt(Channel::getChannelID)).get().getChannelID()+1;
        }
    }


    private boolean checkIfChannelExists(int channelId){
        return channel.parallelStream().anyMatch(channel1 -> (channel1.getChannelID()==channelId));
    }

    public synchronized void clientHasConnection() {
        notifyAll();
    }

    public synchronized void exitUser(ClientConnection clientConnection) {
        removeUserFromAllChannel(clientConnection);
        userList.remove(clientConnection);
    }

    public boolean clientIsInAnyChannel(ClientConnection client){
        return channel.parallelStream().anyMatch(channel1 -> channel1.hasUser(client.getClientId()));
    }


}
