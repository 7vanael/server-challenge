import java.net.Socket;

public class TestConnectionFactory implements ConnectionFactory{
public int nextId = 100;

    @Override
    public HttpConnection createConnection(Socket clientSocket, String rootDirectory, String serverName, Router router) {
        nextId++;
        return new HttpConnection(clientSocket, rootDirectory, serverName, router
//                , nextId
        );
    }

    @Override
    public int getNextConnectionId() {
        return nextId;
    }
}
