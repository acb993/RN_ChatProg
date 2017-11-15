package server;

import util.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Queue;

public class ClientWriter extends Thread {

    private  boolean noPushing = false;
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
        try {
            while (!interrupted()) {

                    waitfornewmessage();

                if (!outGoingCommand.isEmpty()) {
                    outFromServer.writeBytes(outGoingCommand.poll() + "\r\n");
                } else if (!outGoingMessage.isEmpty()&&!noPushing) {
                    pushMessage(outGoingMessage.poll());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        System.out.println("ClientWriter ist Interrupted");
            try {
                outFromServer.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Thread.currentThread().interrupt();
            return;
        }

    }

    public synchronized void noPushing(){
        noPushing=true;
    }
     public synchronized void pushing(){
        noPushing=false;
        notifyAll();
     }

    private synchronized void waitfornewmessage() throws InterruptedException {

       while (outGoingCommand.isEmpty() &&(outGoingMessage.isEmpty()||noPushing)) {
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
        outFromServer.writeBytes(String.format("NEW MESSAGE %s %s %s \r\n", message.getChannelId(),message.getUsername(),message.getUserId()));
        message.getBody().stream().forEach(line -> {
            try {
                outFromServer.writeBytes(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
