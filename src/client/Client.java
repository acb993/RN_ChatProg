package client;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private Socket socket;
    private String ip;
    private int port;
    private Reader reader;
    private Writer writer;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws IOException {
        SocketFactory factory = SSLSocketFactory.getDefault();
        socket = factory.createSocket(ip, port);
        reader = new Reader(socket);
        writer = new Writer(socket);
        reader.start();
        writer.start();
    }

    private void disconnect() throws IOException {
        reader.interrupt();
        writer.interrupt();
        socket.close();
    }
}
