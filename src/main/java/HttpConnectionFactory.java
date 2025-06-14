import java.net.Socket;

public class HttpConnectionFactory implements ConnectionFactory{
private int id = 0;

    @Override
    public Connection createConnection(Socket clientSocket, String rootDirectory, String serverName) {
        id++;
        return new HttpConnection(clientSocket, rootDirectory, serverName, id);
    }

    @Override
    public int getNextConnectionId() {
        return id;
    }

}
