package server;

import util.Message;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
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
        createClientConnection();

        while(!interrupted()){
            if(userList.get(-1).hasConnection()){
                createClientConnection();
            }
        }
    }
//  TODO removeUserFromChannel hat noch keine Funktionalitaet und gibt false zurueck
    public synchronized boolean removeUserFromAllChannel(ClientConnection user){
        channel.parallelStream().forEach(channel1 -> channel1.removeUser(user));
        return true;
    }


    public synchronized boolean addUserToChannel(ClientConnection user, int channelID) {
        Channel kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID()==channelID).findFirst().get();
        return kanal.addUser(user);
    }

    public synchronized boolean removeUserFromChannel(ClientConnection user,int channelID){
        Channel kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID()==channelID).findFirst().get();
        return kanal.removeUser(user);
    }

    public synchronized int createChannel(String channelName){
        Channel kanal = new Channel(getNewChannelId(),channelName);
        channel.add(kanal);
        return kanal.getChannelID();
    }

    public synchronized void closeChannel(int id){
        Channel kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID()==id).findFirst().get();
        channel.remove(kanal);
    }

    public List<Channel> getAllChannel(){
        return new ArrayList<>(channel);
    }

    public String gethostname(){
        return hostname;
    }

    public String getIpAdresse(){
        return ipAdresse;
    }
    public boolean sendMessageOverChannel(int channelID, Message message){
        Channel kanal = channel.parallelStream().filter(channel1 -> channel1.getChannelID()==channelID).findFirst().get();
        return kanal.addMessageToQueue(message);
    }

    private synchronized ClientConnection createClientConnection(){
        try {
            ClientConnection client =  new ClientConnection(getNewUserId(),serverSocket);
            client.start();
            return client;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized int getNewUserId(){
        if(userList.isEmpty()){
            return 1;
        }else{
            return userList.parallelStream().max(Comparator.comparingInt(ClientConnection::getid)).get().getid()+1;
        }
    }
    private synchronized int getNewChannelId(){
        if(channel.isEmpty()){
            return 1;
        }else{
            return channel.parallelStream().max(Comparator.comparingInt(Channel::getChannelID)).get().getChannelID()+1;
        }
    }
}
