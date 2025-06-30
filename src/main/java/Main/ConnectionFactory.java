package Main;

import Connection.Connection;
import Router.Router;

import java.net.Socket;

public interface ConnectionFactory {
    Connection createConnection(Socket clientSocket, String rootDirectory, Router router);
}
