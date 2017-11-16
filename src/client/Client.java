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

    public Client(String ip, int port, User user) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.enteredChannel = new ArrayList<>();
        this.availableChannel = new HashMap<>();
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
        socket.close();
    }

    public void sendMessage(Message message) {
        writer.addMessage(message);
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
}
