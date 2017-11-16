package client;

import util.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Channel extends Thread {

    private List<User> userList;
    private List<User> newUserList;
    private int channelID;
    private String channelName;
    private Queue<Message> messageQueue;


    public Channel(int channelID, String channelName) {
        this.channelID = channelID;
        this.channelName = channelName;
        this.userList = new ArrayList<>();
        this.messageQueue = new LinkedList<>();
    }

    public int getChannelID() {
        return channelID;
    }

    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }


    public synchronized boolean addMessageToQueue(Message message){
        messageQueue.add(message);
        notifyAll();
        return true;
    }

    public synchronized void setUserList() {
        this.userList = newUserList;
    }

    public synchronized void NewUserList() {
        newUserList = new ArrayList<>();
    }

    public synchronized void addToNewUserList(User user){
        newUserList.add(user);
    }
}
