package Connection;

import org.example.ConnectionFactory;
import Router.Router;

import java.net.Socket;

public class TestConnectionFactory implements ConnectionFactory {
public int nextId = 100;

    @Override
    public HttpConnection createConnection(Socket clientSocket, String rootDirectory, Router router) {
        nextId++;
        return new HttpConnection(clientSocket, rootDirectory, router
//                , nextId
        );
    }

    @Override
    public int getNextConnectionId() {
        return nextId;
    }
}
