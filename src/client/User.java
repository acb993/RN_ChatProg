package client;

public class User {

    private int id;
    private Profile profile;
    private String username;
    private Client connection;

    public User(String username, String firstName, String lastName, String eMail){
        this.username = username;
        this.profile = new Profile(firstName,lastName,eMail);
    }

    public void createConnection(String id, int port) {
        connection = new Client(id, port);
        connection.start();
    }

    public void joinChatRoom() {}

    public void leaveChatRoom() {}

    public void createChannel() {}

    public void closeChannel() {}
}
