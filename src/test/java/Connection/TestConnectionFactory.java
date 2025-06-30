package Connection;

import Main.ConnectionFactory;
import Router.Router;

import java.net.Socket;

public class TestConnectionFactory implements ConnectionFactory {

    @Override
    public HttpConnection createConnection(Socket clientSocket, String rootDirectory, Router router) {
        return new HttpConnection(clientSocket, rootDirectory, router);
    }
}
