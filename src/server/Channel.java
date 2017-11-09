package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Channel extends Thread {

    private List<ClientConnection> user;
    private int channelID;
    private String channelName;
    private Queue<String> messages;



    public Channel(int ID,String ChannelName)
    {
        this.channelID=ID;
        this.channelName=ChannelName;
        user = new ArrayList<>();
        messages= new LinkedList<>();
    }

    @Override
    public void run()
    {

    }
}
