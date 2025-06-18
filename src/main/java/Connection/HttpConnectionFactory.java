package Connection;

import Router.Router;
import org.example.ConnectionFactory;

import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory {
private int id = 0;

    @Override
    public Connection createConnection(Socket clientSocket, String rootDirectory, Router router) {
        return new HttpConnection(clientSocket, rootDirectory, router
        );
    }

    @Override
    public int getNextConnectionId() {
        return id;
    }

}
