package server;

import java.util.List;

public class Server {
    private List<Channel> channel;
    private List<ClientConnection> userList;


    public Server(){

    }

    public boolean addUserToChannel(int id, int channelID) {
   //     channel.parallelStream().anyMatch(ch -> {ch.getChannelID()==channelID;});
        return true;
    }
}
