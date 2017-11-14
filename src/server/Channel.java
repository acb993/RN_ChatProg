package server;


import util.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Channel extends Thread {

    private List<ClientConnection> userList;
    private int channelID;
    private String channelName;
    private Queue<Message> messages;



    public Channel(int ID,String channelName)
    {
        this.channelID=ID;
        this.channelName=channelName;
        userList = new ArrayList<>();
        messages= new LinkedList<>();
    }

    @Override
    public void run()
   {
        while(!interrupted()){
            try {
                waitformessage();
                sendAllUser(messages.poll());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

        }
   }

    private synchronized void waitformessage() throws InterruptedException {
        while(messages.isEmpty()){
            wait();
            System.out.println("aufgewacht!S");
        }
    }

    public synchronized boolean addUser(ClientConnection user)
    {
        if(!userList.contains(user)){
            userList.add(user);
            return true;
        }else{
            return false;
        }

    }

    public synchronized boolean removeUser(ClientConnection user)
    {
        if(userList.contains(user)){
            userList.remove(user);
            return true;
        }else{
            return false;
        }
    }

    public List<ClientConnection> getAllUser() {
        return userList;
    }

    public int getUserCount(){
       return userList.size();
    }

    public String getChannelName() {
        return channelName;
    }

    public int getChannelID() {
        return channelID;
    }

    public boolean hasUser(int clientID){
        return userList.parallelStream().anyMatch(clientConnection -> clientConnection.getClientId()==clientID);
    }

    private synchronized void sendAllUser(Message message){
        System.out.println("Nachricht wird gesendet");
        userList.parallelStream().forEach(user -> user.addMessageToQueue(message));
    }

    //TODO mit tasso besprechen ob eine Message Klasse gebaut werden soll.
    public synchronized boolean addMessageToQueue(Message message){
        messages.add(message);
        notifyAll();
        return true;
    }
}
