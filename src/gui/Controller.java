package gui;

import client.Client;
import client.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class Controller {

    private  User user;
    private Client client;



    public Controller(){
        user = new User("Michel");
        user.createConnection("localhost",8080);
    }



    @FXML
    private MenuItem getConnection;

    @FXML
    private ListView<String> InChannelList;

    @FXML
    private TextArea MessageArea;

    @FXML
    private TextArea newMessagArea;

    @FXML
    private ListView<?> AvailableList;

    @FXML
    private Button ButtonSend;


    @FXML
    void sendMessage(ActionEvent event) {

    }

    @FXML
    void getConnection(ActionEvent event) {
        user.getID();
    }



    @FXML
    public void initialize() {
        ButtonSend.setOnAction(e->sendMessage(e));
        getConnection.setOnAction(e->getConnection(e));

    }

    private class Updater extends Thread{
        private Controller master;
        private Client client;


        public Updater(Controller master, Client client){
            this.master = master;
            this.client = client;
        }

        @Override
        public void run() {
            super.run();
        }


    }

}
