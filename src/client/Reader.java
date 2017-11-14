package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Reader extends Thread {

    private DataInputStream input;

    public Reader(Socket socket) throws IOException {
        this.input = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                if (input.available() > 0) {
                    System.out.println(input.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public DataInputStream getOutput() {
        return input;
    }
}
