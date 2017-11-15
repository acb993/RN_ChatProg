package client;


import util.Message;

import java.io.IOException;

public class User {

    private int id;
    private Profile profile;
    private String username;
    private Client connection;


    public User(String username) {
        this.username = username;
    }

    private void createConnection(String id, int port) {
        connection = new Client(id, port, this);
        connection.start();
    }

    private void closeConnection() throws IOException {
        connection.disconnect();
    }

    public int setID(int id) {
        return this.id = id;
    }

    private void getID() {
        connection.sendCommand("GET ID " + username);
    }

    private void getChannel() {
        connection.sendCommand("GET CHANNEL");
    }

    private void joinChannel(int channelID) {
        connection.sendCommand("JOIN " + channelID);
    }

    private void createChannel(String channelName) {
        connection.sendCommand("CREATE CHANNEL " + channelName);
    }

    private void leaveChannel(int channelID) {
        connection.sendCommand("LEAVE CHANNEL " + channelID);
    }

    private void getUser(int channelID) {
        connection.sendCommand("GET USER " + channelID);
    }

    private void startMessage(int channelID) {
        connection.sendCommand("SEND MESSAGE " + channelID);
    }

    private void sendMessage(int channelID) {
        connection.sendMessage(new Message(id, username, channelID));
    }

    private void closeChannel() {
    }
}
