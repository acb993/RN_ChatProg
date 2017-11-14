package client;

public class Start {

    public static void main(String[] args) {
        User user;
        user = new User("acb993", "anastasios", "palatiou", "anastasios.palatiou@haw-hamburg.de");
        user.createConnection("",29);
    }
}
