package client;


public class User {

    private int id;
    private Profile profile;
    private String username;
    private Client connection;



    public User() {

    }

    public void createConnection(String id, int port) {
        connection = new Client(id, port,this);
        connection.start();
    }

    public void joinChatRoom() {
        connection.sendCommand("");
    }

    public void leaveChatRoom() {}

    public void createChannel() {}

    public void closeChannel() {}

    private void getID() {
        connection.sendCommand("GET ID");
    }

    public int setID(int id) {
        return this.id = id;
    }
}
