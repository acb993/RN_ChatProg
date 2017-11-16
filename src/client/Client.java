package client;

import util.Message;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Client extends Thread {

    private Socket socket;
    private String ip;
    private int port;
    private Reader reader;
    private Writer writer;
    private User user;
    private List<Channel> enteredChannel;
    private HashMap<Integer, String> availableChannel;
    private HashMap<Integer, String> newAvailableChannel;
    private int zustand;

    public Client(String ip, int port, User user) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.enteredChannel = new ArrayList<>();
        this.availableChannel = new HashMap<>();
        this.newAvailableChannel = new HashMap<>();
        zustand=0;
    }

    @Override
    public void run() {
        try {
            connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws IOException {
        SocketFactory factory = SocketFactory.getDefault();
        socket = factory.createSocket(ip, port);
        reader = new Reader(socket, this);
        writer = new Writer(socket);
        reader.start();
        writer.start();
    }

    public void disconnect() throws IOException {
        reader.interrupt();
        writer.interrupt();
        try {
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socket.close();
    }

    public void sendMessage(Message message, int channelID) {
        writer.addCommand("SEND MESSAGE " + channelID);
        try {
            waitForZustand(45);
            if(zustand==60){
                return;
            }
            writer.addMessage(message);
        } catch (InterruptedException e) {
            System.out.println("Message could not be send");
            return;
        }
    }

    private synchronized void waitForZustand(int zustand) throws InterruptedException {
        while(this.zustand!=zustand&&this.zustand!=60){
            wait();
        }
    }

    public synchronized void setZustand(int zustand){
        this.zustand = zustand;
        notifyAll();
    }
    public int getZustand(){
        return zustand;
    }

    public void sendCommand(String command) {
        writer.addCommand(command);
    }

    public void setID(int id) {
        user.setID(id);
    }

    public synchronized HashMap getChannel(HashMap map) {
        return availableChannel = map;
    }

    public void createChannel(String channelName, int channelID) {
        Channel channel = new Channel(channelID,channelName);
        enteredChannel.add(channel);
        availableChannel.put(channelID, channelName);
    }

    public synchronized Boolean addMessageToChannel(Message message) {
        for (Channel channel : enteredChannel) {
            if (channel.getChannelID() == message.getChannelId()) {
                return channel.addMessageToQueue(message);
            }
        }

        return false;
    }

    public void getUser(List erg) {

    }


    public synchronized Boolean joinChannel(int channelID) {
        for (Map.Entry<Integer, String> entry : availableChannel.entrySet()) {
            if (entry.getKey() == channelID) {
                return enteredChannel.add(new Channel(channelID, entry.getValue()));
            }
        }
        return false;
    }

    public synchronized Boolean removeChannel(int channelID) {
        for (Channel channel : enteredChannel) {
            if (channel.getChannelID() == channelID) {
                channel.interrupt();
                return enteredChannel.remove(channel);
            }
        }
        return false;
    }

    public void serverMessage(String sMessage) {
        System.out.println(sMessage);
    }

    public synchronized void newGetChannel() {
        newAvailableChannel.clear();
    }

    public synchronized void updateChannelList(List<String> channel){
        String channelName = channel.get(0);
        int channelID = Integer.valueOf(channel.get(1));
        if(channel.get(0).startsWith("-")){
            newAvailableChannel.put(channelID,channelName.substring(1,channelName.length()));
        }else{
            newAvailableChannel.put(channelID,channelName);
            availableChannel=newAvailableChannel;
        }
    }

    public synchronized void newGetUser(int channelID) {
        Channel channel = enteredChannel.parallelStream().filter(channel1 -> channel1.getChannelID() == channelID).findFirst().get();
        channel.NewUserList();
    }


    public synchronized void updateUserList(List<String> user){
        String userName = user.get(0);
        int channelID = Integer.valueOf(user.get(1));
        Channel channel = enteredChannel.parallelStream().filter(channel1 -> channel1.getChannelID() == channelID).findFirst().get();
        if(user.get(0).startsWith("-")){
            channel.addToNewUserList(new User(userName));
        }else{
            channel.addToNewUserList(new User(userName));
            channel.setUserList();
        }
    }

    public String getChannelName(int channelId){
        return availableChannel.get(channelId);
    }
}
