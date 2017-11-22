package client;


import util.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class User {

    private int id;
    private Profile profile;
    private String username;
    private Client connection;


    public User(String username) {
        this.username = username;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        User user = new User("acb993");
        user.createConnection("localhost",8080);
        Boolean run = true;

        while(run) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();

            if(s.startsWith("get id")) {
                user.getID();

            }else if(s.startsWith("get channel")) {
                user.getChannel();

            }else if(s.startsWith("create channel")) {
                s = s.replace("create channel ", "");
                user.createChannel(s);

            }else if(s.startsWith("join channel")) {
                s = s.replace("join channel ", "");
                user.joinChannel(Integer.valueOf(s));

            }else if(s.startsWith("leave channel")) {
                s = s.replace("leave channel ", "");
                user.leaveChannel(Integer.valueOf(s));

            }else if(s.startsWith("send message")) {
                s = s.replace("send message ", "");
                user.sendMessage(Integer.valueOf(s));

            }else if(s.startsWith("exit")) {
                user.closeConnection();
                run = false;
            }
        }
    }

    public void createConnection(String ip, int port) {
        connection = new Client(ip, port, this);
        connection.start();
    }

    public void closeConnection() throws IOException {
        connection.disconnect();
    }

    public int setID(int id) {
        return this.id = id;
    }

    public void getID() {
        connection.sendCommand("GET ID " + username);
    }

    public void getChannel() {
        connection.newGetChannel();
        connection.sendCommand("GET CHANNEL");
    }

    public void joinChannel(int channelID) {
        connection.sendCommand("JOIN " + channelID);
    }

    public void createChannel(String channelName) {
        connection.sendCommand("CREATE CHANNEL " + channelName);
    }

    public void leaveChannel(int channelID) {
        connection.sendCommand("LEAVE CHANNEL " + channelID);
    }

    private void getUser(int channelID) {
        connection.newGetUser(channelID);
        connection.sendCommand("GET USER " + channelID);
    }


    public void sendMessage(int channelID) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Message message = new Message(id,username,channelID);
        String s = "";
        while(!s.equals("EOM")) {
            s = br.readLine();
            message.addLine(s);
        }

        connection.sendMessage(message,channelID);
    }

    public void sendMessage(int channelID,Message message) throws IOException {
        connection.sendMessage(message,channelID);
    }

    public void closeChannel() {
    }

    public Client getClient(){
        return connection;
    }
}
