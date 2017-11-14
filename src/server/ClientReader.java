package server;

import client.Client;

import java.io.*;

public class ClientReader extends Thread{
    private ClientConnection client;
    private DataInputStream inFromClient;
    private BufferedReader readerFromClient;


    public ClientReader(DataInputStream inFromClient, ClientConnection client){
        this.inFromClient = inFromClient;
        this.client = client;
        readerFromClient= new BufferedReader(new InputStreamReader(inFromClient));
    }

    @Override
    public void run() {
        try {
            while(!interrupted()) {
//                if (inFromClient.available() > 0) {
  //                  System.out.println("Hab ne nachricht vom client!");
    //                analyzeInput(inFromClient.readLine());
      //          }
                analyzeInput(readerFromClient.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analyzeInput(String input) throws IOException {
        int zustand = client.getZustand();
        if(input.startsWith("SEND MESSAGE")&&(zustand==44)){

        }else if(input.startsWith("GET CHANNEL")&&((zustand==43)||(zustand==44))){

        }else if(input.startsWith("GET USERS")&&(zustand==44)){

        }else if(input.startsWith("JOIN")&&((zustand==43)||(zustand==44))){
            input = input.replace("JOIN ","");
            int channelId = Integer.valueOf(input);
            if(client.addUserToChannel(channelId)){
                client.addCommandToQueue(String.format("OK,USER JOINED CHANNEL %d", channelId));
                zustand=44;
            }else{
                client.addCommandToQueue(String.format("NO CHANNEL FOUND WITH ID %s", input));
            }
        }else if(input.startsWith("CREATE CHANNEL")&&((zustand==43)||(zustand==44))){

        }else if(input.startsWith("LEAVE CHANNEL")&&(zustand==44)){

        }else if(input.startsWith("GET ID")&&(zustand==42)){
            input = input.replace("GET ID ","");
            if(input.contains(" ")){
                client.addCommandToQueue(String.format("60 USERNAME %s IS NOT ALLOWED",input));
            }else{
                zustand=43;
                client.setUsername(input);
                client.addCommandToQueue(String.format("ID IS %d", client.getClientId()));
            }
        }else if(input.startsWith("EXIT")){

        }else {
            client.addCommandToQueue("60 COMMAND COULD NOT BE EXECUTED");
        }
        client.setZustand(zustand);
    }
}
