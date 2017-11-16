package client;

import util.Message;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class Reader extends Thread {

    private DataInputStream input;
    private Client client;

    private static final String NOT_LOGGED_IN_STATE = "42";
    private static final String NOT_IN_ROOM_STATE = "43";
    private static final String IN_ROOM_STATE = "44";
    private static final String MESSAGE_MODE_STATE = "45";
    private static final String ERROR_MODE_STATE = "60";
    private static final String EMPTY_STRING_REPLACEMENT = "";

    private static final String GET_ID_ANSWER_PART = " ID IS ";
    private static final String GET_CHANNEL_ANSWER_PART = "-";
    private static final String GET_CHANNEL_ANSWER_PART_EMPTY = " ";
    private static final String JOIN_ANSWER_PART = " OK USER JOINED CHANNEL ";
    private static final String CREATE_CHANNEL_ANSWER_PART = " CHANNEL CREATED ";
    private static final String LEAVE_CHANNEL_ANSWER_PART = " OK LEAVE CHANNEL ";
    private static final String GET_USER_ANSWER_PART = "-";
    private static final String GET_USER_ANSWER_PART_EMPTY = " ";
    private static final String SEND_MESSAGE_ANSWER_PART = " OK END MESSAGE WITH EOM ";
    private static final String EOM_ANSWER_PART = " OK MESSAGE WILL BE SEND ";
    private static final String NEW_MESSAGE_ANSWER_PART = " NEW MESSAGE ";

    public Reader(Socket socket, Client client) throws IOException {
        this.input = new DataInputStream(socket.getInputStream());
        this.client = client;
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                if (input.available() > 0) {
                    analyze(input.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void analyze(String sMessage) throws IOException {
        client.serverMessage(sMessage);
        if (sMessage.startsWith(NOT_LOGGED_IN_STATE)) {
            //    notLoggedIn(sMessage.replace(NOT_LOGGED_IN_STATE, EMPTY_STRING_REPLACEMENT));

        } else if (sMessage.startsWith(NOT_IN_ROOM_STATE)) {
            notInRoom(sMessage.replace(NOT_IN_ROOM_STATE, EMPTY_STRING_REPLACEMENT));

        } else if (sMessage.startsWith(IN_ROOM_STATE)) {
            inRoom(sMessage.replace(IN_ROOM_STATE, EMPTY_STRING_REPLACEMENT));

        } else if (sMessage.startsWith(MESSAGE_MODE_STATE)) {
            messageMode(sMessage.replace(MESSAGE_MODE_STATE, EMPTY_STRING_REPLACEMENT));

            // } else if (sMessage.startsWith(ERROR_MODE_STATE)) {
            //     errorMode(sMessage.replace(ERROR_MODE_STATE, EMPTY_STRING_REPLACEMENT));
        }
    }

    //private synchronized void notLoggedIn(String sMessage) {
    //    client.serverMessage(sMessage);
    //}

    private synchronized void notInRoom(String sMessage) throws IOException {
        if (sMessage.startsWith(GET_ID_ANSWER_PART)) {
            sMessage = sMessage.replace(GET_ID_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            client.setID(Integer.valueOf(sMessage));

        } else if (sMessage.startsWith(GET_CHANNEL_ANSWER_PART)) {
            multiChannel(sMessage);

        } else if (sMessage.startsWith(GET_CHANNEL_ANSWER_PART_EMPTY)) {
            singleChannel(sMessage);

        } else if (sMessage.startsWith(LEAVE_CHANNEL_ANSWER_PART)) {
            sMessage = sMessage.replace(LEAVE_CHANNEL_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            client.removeChannel(Integer.valueOf(sMessage));

        }
    }

    private synchronized void inRoom(String sMessage) throws IOException {
        if (sMessage.startsWith(JOIN_ANSWER_PART)) {
            sMessage = sMessage.replace(JOIN_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            client.joinChannel(Integer.valueOf(sMessage));

        } else if (sMessage.startsWith(LEAVE_CHANNEL_ANSWER_PART)) {
            sMessage = sMessage.replace(LEAVE_CHANNEL_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            client.removeChannel(Integer.valueOf(sMessage));

        } else if (sMessage.startsWith(GET_USER_ANSWER_PART)) {
            List<String> erg = new ArrayList<>();
            List<String> ary = new ArrayList<>();

            sMessage = sMessage.replace(GET_USER_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            ary.add(Arrays.toString(sMessage.split(" ")));
            erg.add(ary.get(0));

            while (!sMessage.startsWith(GET_USER_ANSWER_PART_EMPTY)) {
                if (input.available() > 0) {
                    ary.clear();
                    sMessage = input.readLine();

                    sMessage = sMessage.replace(GET_USER_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
                    ary.add(Arrays.toString(sMessage.split(" ")));
                    erg.add(ary.get(0));
                }
            }
            client.getUser(erg);

        } else if (sMessage.startsWith(GET_USER_ANSWER_PART_EMPTY)) {
            List<String> erg = new ArrayList<>();
            List<String> ary = new ArrayList<>();

            ary.add(Arrays.toString(sMessage.split(" ")));
            erg.add(ary.get(0));
            client.getUser(erg);

        } else if (sMessage.startsWith(CREATE_CHANNEL_ANSWER_PART)) {
            sMessage = sMessage.replace(CREATE_CHANNEL_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            ArrayList<String> ary = new ArrayList<>(Arrays.asList(sMessage.split(" ")));
            client.createChannel(ary.get(0), Integer.valueOf(ary.get(1)));

        } else if (sMessage.startsWith(NEW_MESSAGE_ANSWER_PART)) {
            sMessage = sMessage.replace(NEW_MESSAGE_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            ArrayList<String> channelParts = new ArrayList<>(Arrays.asList(sMessage.split(" ")));
            Message message = new Message(Integer.valueOf(channelParts.get(0)), channelParts.get(1), Integer.valueOf(channelParts.get(2)));
            String row = "";
            while (!row.equals("EOM")) {
                if (input.available() > 0) {
                    row = input.readLine();
                    message.addLine(row);
                }
            }
            client.addMessageToChannel(message);

        } else if (sMessage.startsWith(GET_CHANNEL_ANSWER_PART)) {
            multiChannel(sMessage);

        } else if (sMessage.startsWith(GET_CHANNEL_ANSWER_PART_EMPTY)) {
            singleChannel(sMessage);

        }
    }

    private synchronized void messageMode(String sMessage) throws IOException {
        if (sMessage.startsWith(SEND_MESSAGE_ANSWER_PART)) {

        }
    }

    //private synchronized void errorMode(String sMessage) {
    //    client.serverMessage(sMessage);
    //}

    private synchronized void singleChannel(String sMessage) {
        HashMap<Integer, String> map = new HashMap<>();
        List<String> ary = new ArrayList<>();

        Collections.addAll(ary, sMessage.split(" "));
        map.put(Integer.valueOf(ary.get(2)), ary.get(1));
        client.getChannel(map);
    }

    private synchronized void multiChannel(String sMessage) throws IOException {
        HashMap<Integer, String> map = new HashMap<>();
        List<String> ary = new ArrayList<>();

        sMessage = sMessage.replace(GET_CHANNEL_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
        Collections.addAll(ary, sMessage.split(" "));
        map.put(Integer.valueOf(ary.get(1)), ary.get(0));

        while (!sMessage.startsWith(GET_CHANNEL_ANSWER_PART_EMPTY)) {
            if (input.available() > 0) {
                ary.clear();
                sMessage = input.readLine();

                sMessage = sMessage.replace(GET_CHANNEL_ANSWER_PART, EMPTY_STRING_REPLACEMENT);
            }
        }
        client.getChannel(map);
    }
}
