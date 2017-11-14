package server;

import util.Message;

import java.io.*;
import java.util.List;

public class ClientReader extends Thread {
    private ClientConnection client;
    private BufferedReader readerFromClient;
    private Server server;


    public ClientReader(DataInputStream inFromClient, ClientConnection client, Server server) {
        this.client = client;
        this.server = server;
        readerFromClient = new BufferedReader(new InputStreamReader(inFromClient));
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                analyzeInput(readerFromClient.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analyzeInput(String input) throws IOException {
        System.out.println("NEW MESSAGE     " + input);
        int zustand = client.getZustand();
        if (input.startsWith("SEND MESSAGE") && (zustand == 44)) {
            zustand = sendMessageRequest(input,zustand);
        } else if (input.startsWith("GET CHANNEL") && ((zustand == 43) || (zustand == 44))) {
            zustand = getChannelRequest(input,zustand);
        } else if (input.startsWith("GET USERS") && (zustand == 44)) {
            zustand = getUsersRequest(input, zustand);
        } else if (input.startsWith("JOIN") && ((zustand == 43) || (zustand == 44))) {
            zustand = joinRequest(input, zustand);

        } else if (input.startsWith("CREATE CHANNEL") && ((zustand == 43) || (zustand == 44))) {
            zustand = createChannelRequest(input, zustand);

        } else if (input.startsWith("LEAVE CHANNEL") && (zustand == 44)) {
            zustand = leaveChannelRequest(input, zustand);

        } else if (input.startsWith("GET ID") && (zustand == 42)) {
            zustand = getIdRequest(input, zustand);

        } else if (input.startsWith("EXIT")) {

        } else {
            client.addCommandToQueue("60 COMMAND COULD NOT BE EXECUTED");
        }
        client.setZustand(zustand);
    }

    private int sendMessageRequest(String input, int zustand) throws IOException {
        input = input.replace("SEND MESSAGE ","");
        try {
            Message message = new Message(client.getClientId(),client.getUsername(),Integer.valueOf(input));
            zustand=45;
            client.addCommandToQueue("45 OK END MESSAGGE WITH EOM");
            String line = readerFromClient.readLine();
            while(line.compareTo("EOM")!=0){
                message.addLine(line);
                line = readerFromClient.readLine();
            }
            zustand=44;
            client.addCommandToQueue(String.format("%d OK MESSAGE WILL BE SEND",zustand));
            server.sendMessageOverChannel(Integer.valueOf(input),message);
        }catch (NumberFormatException e){
            zustand=44;
            client.addCommandToQueue("60 COMMAND COULD NOT BE EXECUTED");
        }

        return zustand;
    }

    private int getChannelRequest(String input, int zustand) {
        server.getAllChannel().stream().forEach(channel -> client.addCommandToQueue(String.format("%d %s %d %d ",zustand,channel.getChannelName(),channel.getChannelID(),channel.getUserCount())));

        return zustand;
    }

    private int getUsersRequest(String input, int zustand) {
        input = input.replace("GET USERS ", "");
        try {
            List<ClientConnection> userList = server.getAllUser(Integer.valueOf(input),client.getClientId());
            if(userList!=null) {
                userList.stream().forEach(clientConnection -> client.addCommandToQueue(String.format("%d %s %d",zustand, clientConnection.getUsername(),clientConnection.getClientId())));
            }
        } catch (NumberFormatException e) {
            client.addCommandToQueue("60 COMMAND COULD NOT BE EXECUTED");
        }
        return zustand;
    }

    private int joinRequest(String input, int zustand) {
        input = input.replace("JOIN ", "");
        try {
            int channelId = Integer.valueOf(input);
            if (client.addUserToChannel(channelId)) {
                zustand = 44;
                client.addCommandToQueue(String.format("%d OK,USER JOINED CHANNEL %d", zustand,channelId));
            } else {
                client.addCommandToQueue(String.format("%d NO CHANNEL FOUND WITH ID %s",zustand, input));
            }
        } catch (NumberFormatException e) {
            client.addCommandToQueue("60 COMMAND COULD NOT BE EXECUTED");
        }
        return zustand;
    }

    private int createChannelRequest(String input, int zustand) {
        input = input.replace("CREATE CHANNEL ", "");
        if (input.contains(" ")) {
            client.addCommandToQueue("CHANNEL NAME IS NOT ALLOWED");
        } else {
            int channel = server.createChannel(input);
            client.addCommandToQueue(String.format("%d CHANNEL CREATED %d %s",zustand, channel, input));
        }
        return zustand;
    }

    private int leaveChannelRequest(String input, int zustand) {
        input = input.replace("LEAVE CHANNEL ", "");
        server.removeUserFromChannel(client, Integer.valueOf(input));
        return zustand;
    }

    private int getIdRequest(String input, int zustand) {
        input = input.replace("GET ID ", "");
        if (input.contains(" ")) {
            client.addCommandToQueue(String.format("60 USERNAME %s IS NOT ALLOWED", input));
        } else {
            zustand = 43;
            client.setUsername(input);
            client.addCommandToQueue(String.format("%d ID IS %d",zustand, client.getClientId()));
        }
        return zustand;
    }
}
