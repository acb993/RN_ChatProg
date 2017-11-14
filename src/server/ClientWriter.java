package server;

import util.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Queue;

public class ClientWriter extends Thread {


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
                try {
                    waitfornewmessage();
                } catch (InterruptedException e) {
                    return;
                }
                if (!outGoingCommand.isEmpty()) {
                    outFromServer.writeBytes(outGoingCommand.poll() + "\r\n");
                } else if (!outGoingMessage.isEmpty()) {
                    pushMessage(outGoingMessage.poll());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void waitfornewmessage() throws InterruptedException {
       while (outGoingCommand.isEmpty() && outGoingMessage.isEmpty()) {
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
        outFromServer.writeBytes(String.format("MESSAGE FROM %s %s \r\n", message.getUserId(), message.getChannelId()));
        message.getBody().stream().forEach(line -> {
            try {
                outFromServer.writeBytes(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
