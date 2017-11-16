package client;

import util.Message;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class Reader extends Thread {

    private BufferedReader input;
    private Client client;

    private static final String NOT_LOGGED_IN_STATE = "42";
    private static final String NOT_IN_ROOM_STATE = "43";
    private static final String IN_ROOM_STATE = "44";
    private static final String MESSAGE_MODE_STATE = "45";
    private static final String ERROR_MODE_STATE = "60";
    private static final String EMPTY_STRING_REPLACEMENT = "";

    private static final String GET_ID_ANSWER_PART = " ID IS ";
    private static final String JOIN_ANSWER_PART = " OK USER JOINED CHANNEL ";
    private static final String CREATE_CHANNEL_ANSWER_PART = " CHANNEL CREATED ";
    private static final String LEAVE_CHANNEL_ANSWER_PART = " OK LEAVE CHANNEL ";
    private static final String SEND_MESSAGE_ANSWER_PART = " OK END MESSAGE WITH EOM ";
    private static final String EOM_ANSWER_PART = " OK MESSAGE WILL BE SEND ";
    private static final String NEW_MESSAGE_ANSWER_PART = " NEW MESSAGE ";

    public Reader(Socket socket, Client client) throws IOException {
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.client = client;
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                    analyze(input.readLine());
                } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void analyze(String sMessage) throws IOException {
        client.serverMessage(sMessage);
        client.setZustand(Integer.valueOf(sMessage.substring(0, 2)));
        sMessage = sMessage.substring(2,sMessage.length());
        if(client.getZustand()==60){
            System.out.println(sMessage);
        }else if(sMessage.startsWith(GET_ID_ANSWER_PART)){
                getIDanswer(sMessage);
        }else if(sMessage.startsWith(JOIN_ANSWER_PART)){
                joinAnswer(sMessage);
        }else if(sMessage.startsWith(CREATE_CHANNEL_ANSWER_PART)){
                createChannelAnswer(sMessage);
        }else if(sMessage.startsWith(LEAVE_CHANNEL_ANSWER_PART)){
                leaveChannelAnswer(sMessage);
        }else if(sMessage.startsWith(SEND_MESSAGE_ANSWER_PART)){

        }else if(sMessage.startsWith(EOM_ANSWER_PART)){

        }else if(sMessage.startsWith(NEW_MESSAGE_ANSWER_PART)){
                newMessageAnswer(sMessage);
        }else{
                serverList(sMessage);
        }

    }

    private void serverList(String sMessage) {
        if(sMessage.startsWith(" ")){
            sMessage = sMessage.substring(1,sMessage.length());
        }
        List<String> line = Arrays.asList(sMessage.split(" "));
        if(line.size()==3){
            client.updateChannelList(line);
        }else if(line.size()==2){
            client.updateUserList(line);
        }
    }

    private void newMessageAnswer(String sMessage) throws IOException {
        sMessage = sMessage.replace(NEW_MESSAGE_ANSWER_PART,EMPTY_STRING_REPLACEMENT);
        List<String> messageHead = Arrays.asList(sMessage.split(" "));
        client.serverMessage(String.format("Message from %s in Channel: %s",messageHead.get(1),client.getChannelName(Integer.valueOf(messageHead.get(0)))));
        String message = input.readLine();
        while (message.compareTo("EOM")!=0){
            client.serverMessage(message);
            message = input.readLine();
        }
        client.serverMessage("");
    }

    private void leaveChannelAnswer(String sMessage) {
        sMessage = sMessage.replace(LEAVE_CHANNEL_ANSWER_PART,EMPTY_STRING_REPLACEMENT);
        client.removeChannel(Integer.valueOf(sMessage));
    }

    private void createChannelAnswer(String sMessage) {
        sMessage=sMessage.replace(CREATE_CHANNEL_ANSWER_PART,EMPTY_STRING_REPLACEMENT);
        List<String> channelInfo = Arrays.asList(sMessage.split(" "));
        client.createChannel(channelInfo.get(0), Integer.valueOf(channelInfo.get(1)));
    }

    private void joinAnswer(String sMessage) {
        sMessage=sMessage.replace(JOIN_ANSWER_PART,EMPTY_STRING_REPLACEMENT);
        client.joinChannel(Integer.valueOf(sMessage));
    }

    private void getIDanswer(String sMessage){
        sMessage = sMessage.replace(GET_ID_ANSWER_PART,EMPTY_STRING_REPLACEMENT);
        client.setID(Integer.valueOf(sMessage));
    }


}
