import java.net.Socket;

public interface ConnectionFactory {
    Connection createConnection(Socket clientSocket, String rootDirectory, String serverName);
    int getNextConnectionId();
}
