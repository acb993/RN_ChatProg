package client;

import util.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Writer extends Thread {

    private DataOutputStream output;
    private Queue<String> protocolQueue;
    private Queue<Message> messageQueue;

    public Writer(Socket socket) throws IOException {
        this.output = new DataOutputStream(socket.getOutputStream());
        this.protocolQueue = new LinkedList<>();
        this.messageQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                try {
                    waitForOutput();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!protocolQueue.isEmpty()) {
                    send(protocolQueue.poll());
                } else if (!messageQueue.isEmpty()) {
                    send(messageQueue.poll());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message) throws IOException {
        output.writeBytes(message + "\r\n");
    }

    private void send(Message message) {
        message.getBody().stream().forEach(line -> {
            try {
                output.writeBytes(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private synchronized void waitForOutput() throws InterruptedException {
        if (messageQueue.isEmpty() && protocolQueue.isEmpty()) {
            wait();
        }
    }

    public synchronized Boolean addMessage(Message message) {
        if (messageQueue.contains(message)) {
            return false;
        }
        messageQueue.add(message);
        notifyAll();
        return true;
    }

    public synchronized Boolean addCommand(String command) {
        protocolQueue.add(command);
        notifyAll();
        return true;
    }
}
