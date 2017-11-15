package client;

import util.Message;

import java.util.Queue;

public class Channel extends Thread {

    private int channelID;
    private String channelName;
    private Queue<Message> messageQueue;

    public Channel(int channelID, String channelName) {
        this.channelID = channelID;
        this.channelName = channelName;
    }

    public int getChannelID() {
        return channelID;
    }

    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }
}
