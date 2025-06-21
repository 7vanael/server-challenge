package org.example;

import Connection.HttpConnection;
import Router.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private int port;
    private String root;
    private String name = "Challenge Server";
    private Router router;
    private ServerSocket serverSocket;
    private boolean running = false;
    ArrayList<HttpConnection> connections = new ArrayList<>();


    public Server(int port, String root, Router router) {
        this.port = port;
        this.root = root;
        this.router = router;
        //These are for me
        System.out.println("Server constructed");
        System.out.println("port: " + port);
        System.out.println("root: " + root);
    }

        public void startServer() {
        System.out.println("in Server, about to try server socket");
        try {
            this.serverSocket = new ServerSocket(this.port);
            System.out.println("server socket created");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server socket initialized");
        new Thread(this::acceptConnections).start();
    }

    public void acceptConnections() {
        System.out.println("Server Running: accepting connections in new thread");
        running = true;
        while (running) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                System.out.println("accepted connection");
                Thread thread = new Thread(new HttpConnection(clientSocket, this.root, this.router));
                thread.start();
            } catch (IOException e) {
                if (running){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public boolean isAcceptingConnections() {
        return running;
    }
    public void stopServer() throws IOException {
        running = false;
        serverSocket.close();
    }
}
