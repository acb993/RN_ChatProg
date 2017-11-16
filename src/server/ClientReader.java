package server;

import util.Message;

import java.io.*;
import java.util.List;

public class ClientReader extends Thread {
    private ClientConnection client;
    private BufferedReader readerFromClient;
    private Server server;


    ClientReader(DataInputStream inFromClient, ClientConnection client, Server server) {
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    private void analyzeInput(String input) throws IOException, InterruptedException {
        client.pushing(false);
        System.out.println("NEW MESSAGE     " + input);
        int zustand = client.getZustand();
        if (input.startsWith("SEND MESSAGE") && (zustand == 44)) {
            zustand = sendMessageRequest(input, zustand);
        } else if (input.startsWith("GET CHANNEL") && ((zustand == 43) || (zustand == 44))) {
            zustand = getChannelRequest(input, zustand);
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
            client.exit();
        } else {
            client.addCommandToQueue("60 COULD NOT FIND COMMAND");
        }
        client.pushing(true);
        client.setZustand(zustand);
    }

    private int sendMessageRequest(String input, int zustand) throws IOException {
        input = input.replace("SEND MESSAGE ", "");
        try {
            Message message = new Message(client.getClientId(), client.getUsername(), Integer.valueOf(input));
            client.addCommandToQueue("45 OK END MESSAGGE WITH EOM");
            String line = readerFromClient.readLine();
            while (line.compareTo("EOM") != 0) {
                message.addLine(line);
                line = readerFromClient.readLine();
            }
            zustand = 44;
            client.addCommandToQueue(String.format("%d OK MESSAGE WILL BE SEND", zustand));
            server.sendMessageOverChannel(Integer.valueOf(input), message);
        } catch (NumberFormatException e) {
            zustand = 44;
            client.addCommandToQueue("60 YOU COULD NOT SEND A MESSAGE TO THIS ROOM");
        }

        return zustand;
    }

    private int getChannelRequest(String input, int zustand) {
        server.getAllChannel().stream().forEach(channel -> client.addCommandToQueue(String.format("%d %s %d %d ", zustand, channel.getChannelName(), channel.getChannelID(), channel.getUserCount())));

        return zustand;
    }

    private int getUsersRequest(String input, int zustand) {
        input = input.replace("GET USERS ", "");
        try {
            List<ClientConnection> userList = server.getAllUser(Integer.valueOf(input), client.getClientId());
            if (userList != null) {
                ClientConnection user;
                for (int i = 0; i < userList.size() - 1; i++){
                    user = userList.get(i);
                    client.addCommandToQueue(String.format("44-%s %d",user.getUsername(),user.getClientId()));
                }
                user = userList.get(userList.size()-1);
                client.addCommandToQueue(String.format("44 %s %d",user.getUsername(),user.getClientId()));
            }
        } catch (NumberFormatException e) {
            client.addCommandToQueue("60 CHANNEL DOES NOT EXIST OR YOU DON'T HAVE THE PERMISSON");
        }
        return zustand;
    }

    private int joinRequest(String input, int zustand) {
        input = input.replace("JOIN ", "");
        try {
            int channelId = Integer.valueOf(input);
            if (server.addUserToChannel(client,channelId)) {
                zustand = 44;
                client.addCommandToQueue(String.format("%d OK,USER JOINED CHANNEL %d", zustand, channelId));
            } else {
                client.addCommandToQueue(String.format("%d NO CHANNEL FOUND WITH ID %s", zustand, input));
            }
        } catch (NumberFormatException e) {
            client.addCommandToQueue("60 COMMAND COULD NOT BE EXECUTED, CHANNEL ID MAY BE INCORRECT");
        }
        return zustand;
    }

    private int createChannelRequest(String input, int zustand) {
        input = input.replace("CREATE CHANNEL ", "");
        if (input.contains(" ")) {
            client.addCommandToQueue("CHANNEL NAME IS NOT ALLOWED");
        } else {
            int channel = server.createChannel(input);
            server.addUserToChannel(client,channel);
            zustand=44;
            client.addCommandToQueue(String.format("%d CHANNEL CREATED %s %d", zustand, input, channel));
        }
        return zustand;
    }

    private int leaveChannelRequest(String input, int zustand) {
        input = input.replace("LEAVE CHANNEL ", "");
        server.removeUserFromChannel(client, Integer.valueOf(input));
        zustand = (server.clientIsInAnyChannel(client)? 44 : 43);
        return zustand;
    }

    private int getIdRequest(String input, int zustand) {
        input = input.replace("GET ID ", "");
        if (input.contains(" ")) {
            client.addCommandToQueue(String.format("60 USERNAME %s IS NOT ALLOWED", input));
        } else {
            zustand = 43;
            client.setUsername(input);
            client.addCommandToQueue(String.format("%d ID IS %d", zustand, client.getClientId()));
        }
        return zustand;
    }
}
