package server;

import util.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Queue;

public class ClientWriter extends Thread {

    private  boolean pushing = false;
    private Queue<Message> outGoingMessage;
    private Queue<String> outGoingCommand;
    private DataOutputStream outFromServer;

    public ClientWriter(DataOutputStream outFromServer) {
        outGoingCommand = new LinkedList<>();
        outGoingMessage = new LinkedList<>();
        this.outFromServer = outFromServer;
    }


    @Override
    public void run() {
        String zeile;
        try {
            while (!interrupted()) {

                    waitfornewmessage();

                if (!outGoingCommand.isEmpty()) {
                    zeile = outGoingCommand.poll() + "\r\n";
                    System.out.println(zeile);
                    outFromServer.writeBytes(zeile);
                } else if (!outGoingMessage.isEmpty()&&pushing) {
                    pushMessage(outGoingMessage.poll());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            try {
                outFromServer.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Thread.currentThread().interrupt();
            return;
        }

    }

     public synchronized void pushing(Boolean allowed){
        pushing=allowed;
        notifyAll();
     }

    private synchronized void waitfornewmessage() throws InterruptedException {

       while (outGoingCommand.isEmpty() &&(outGoingMessage.isEmpty()||!pushing)) {
           wait();
       }

    }

    public synchronized boolean addCommandToQueue(String command) {
        outGoingCommand.add(command);
        notifyAll();
        return true;
    }

    public synchronized boolean addMessageToQueue(Message message) {
        if (outGoingMessage.contains(message)) {
            return false;
        } else {
            outGoingMessage.add(message);
            notifyAll();
            return true;
        }
    }

    private void pushMessage(Message message) throws IOException {
        outFromServer.writeBytes(String.format("44 NEW MESSAGE %s %s %s \r\n", message.getChannelId(),message.getUsername(),message.getUserId()));
        message.getBody().stream().forEach(line -> {
            try {
                outFromServer.writeBytes(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
