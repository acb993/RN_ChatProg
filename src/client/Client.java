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
    private HashMap<Integer, String> avaiableChannel;

    public Client(String ip, int port, User user) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.enteredChannel = new ArrayList<>();
        this.avaiableChannel = new HashMap<>();
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

    public Boolean sendMessage(Message message) {
        return writer.addMessage(message);
    }

    public Boolean sendCommand(String command) {
        return writer.addCommand(command);
    }

    public Boolean addMessageToChannel(String sMessage) {
        return true;
    }

    public void getUser(String sMessage) {
    }

    public synchronized String getChannel(String sMessage) {
        ArrayList<String> channelParts = new ArrayList<>(Arrays.asList(sMessage.split(" ")));
        return avaiableChannel.put(Integer.valueOf(channelParts.get(1)), channelParts.get(0));
    }

    public void createChannel(String sMessage) {
    }

    public synchronized Boolean joinChannel(int channelID) {
        for (Map.Entry<Integer, String> entry : avaiableChannel.entrySet()) {
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


    public void setID(int id) {
        user.setID(id);
    }

    public void serverMessage(String sMessage) {
        System.out.println(sMessage);
    }

}
