package Connection;

import Router.Router;
import Main.ConnectionFactory;

import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory {

    @Override
    public Connection createConnection(Socket clientSocket, String rootDirectory, Router router) {
        return new HttpConnection(clientSocket, rootDirectory, router);
    }
}
