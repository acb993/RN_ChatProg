package server;

import client.Client;
import util.Message;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<Channel> channel;
    private List<ClientConnection> userList;
    private String ipAdresse;
    private String hostname;
    private int listeningPort;


    public static void main(String[] args){

    }

    public Server() throws UnknownHostException {
        channel= new ArrayList<>();
        userList= new ArrayList<>();
        ipAdresse = InetAddress.getLocalHost().getHostAddress();
        hostname = InetAddress.getLocalHost().getHostName();
        ServerSocketFactory socketFactory = SSLServerSocketFactory.getDefault();
        try {
            serverSocket = socketFactory.createServerSocket(listeningPort);
        } catch (IOException e) {
            e.printStackTrace();
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
            return new ClientConnection(getNewUserId(),serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized int getNewUserId(){

        return userList.parallelStream().max((us1,us2)->Integer.compare(us1.getid(),us2.getid())).get().getid()+1;
    }
    private synchronized int getNewChannelId(){
        return channel.parallelStream().max((ch1, ch2)->Integer.compare(ch1.getChannelID(),ch2.getChannelID())).get().getChannelID()+1;
    }
}
