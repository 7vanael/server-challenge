import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory{
private int id = 0;

    @Override
    public Connection createConnection(Socket clientSocket, String rootDirectory) {
        id++;
        return new HttpConnection(clientSocket, rootDirectory, id);
    }

    @Override
    public int getNextConnectionId() {
        return id;
    }

}
