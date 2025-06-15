import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory{
private int id = 0;

    @Override
    public Connection createConnection(Socket clientSocket, String rootDirectory, String serverName, Router router) {
        id++;
        return new HttpConnection(clientSocket, rootDirectory, serverName, router
//                , id
        );
    }

    @Override
    public int getNextConnectionId() {
        return id;
    }

}
