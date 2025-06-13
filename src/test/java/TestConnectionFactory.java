import java.net.Socket;

public class TestConnectionFactory implements ConnectionFactory{
public int nextId = 100;

    @Override
    public HttpConnection createConnection(Socket clientSocket, String rootDirectory) {
        nextId++;
        return new HttpConnection(clientSocket, rootDirectory, nextId);
    }

    @Override
    public int getNextConnectionId() {
        return nextId;
    }
}
