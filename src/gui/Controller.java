package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button ButtonSend;

    @FXML
    void sendMessage(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert ButtonSend != null : "fx:id=\"ButtonSend\" was not injected: check your FXML file 'sample.fxml'.";

    }
}

