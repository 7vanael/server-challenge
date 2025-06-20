package org.example;

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
        //These are for me
        System.out.println("Server constructed");
        System.out.println("port: " + port);
        System.out.println("root: " + root);
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server socket initialized");
    }

    public void runServer() throws IOException {
        System.out.println("Server Running");
        running = true;
        while (running) {
            Socket clientSocket = serverSocket.accept();
            Thread thread = new Thread(new HttpConnection(clientSocket, root, router));
            thread.start();
        }
    }

    public void stopServer () throws IOException {
        running = false;
        serverSocket.close();
    }
}
