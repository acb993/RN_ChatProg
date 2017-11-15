package client;

import util.Message;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Client extends Thread {

    private Socket socket;
    private String ip;
    private int port;
    private Reader reader;
    private Writer writer;
    private User user;
    private List<Channel> enteredChannel;
    private List<HashMap<Integer,String>> avaiableChannel;

    public Client(String ip, int port, User user) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.enteredChannel = new ArrayList<>();
        this.avaiableChannel = new ArrayList<>();
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
        reader = new Reader(socket,this);
        writer = new Writer(socket);
        reader.start();
        writer.start();
    }

    private void disconnect() throws IOException {
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

    public Boolean addMessageToChannel() {
        return true;
    }

    public void getChannel(String sMessage) {
        ArrayList<String> channelParts = new ArrayList<>(Arrays.asList(sMessage.split(" ")));
        HashMap<Integer,String> channel = new HashMap<>();
        channel.put(Integer.valueOf(channelParts.get(1)),channelParts.get(0));
        avaiableChannel.add(channel);
    }

    public void removeChannel(int channelID) {
        for (Channel channel: enteredChannel) {
            if(channel.getChannelID() == channelID) {
                channel.interrupt();
                enteredChannel.remove(channel);
            }
        }
    }

    public void joinChannel(String channelID) {
        enteredChannel.add(new Channel(channelID,))
    }

    public void setID(int id) {
        user.setID(id);
    }

    public void serverMessage(String sMessage) {
        System.out.println(sMessage);
    }

}
