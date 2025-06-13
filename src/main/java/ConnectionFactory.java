import java.net.Socket;

public interface ConnectionFactory {
    Connection createConnection(Socket clientSocket, String rootDirectory);
    int getNextConnectionId();
}
