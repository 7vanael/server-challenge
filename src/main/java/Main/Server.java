package Main;

import Connection.HttpConnection;
import Router.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private String root;
    private String name = "Challenge Server";
    private Router router;
    private ServerSocket serverSocket;
    private boolean running = false;


    public Server(int port, String root, Router router) {
        this.port = port;
        this.root = root;
        this.router = router;
    }

    public void startServer() {
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(this::acceptConnections).start();
    }

    public void acceptConnections() {
        running = true;
        while (running) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                Thread thread = new Thread(new HttpConnection(clientSocket, this.root, this.router));
                thread.start();
            } catch (IOException e) {
                if (running) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void stopServer() throws IOException {
        running = false;
        serverSocket.close();
    }
}
