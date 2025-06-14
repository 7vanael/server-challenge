import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private int port;
    private String root;
    private String name = "Challenge Server";

    private ServerSocket serverSocket;
    private boolean running = false;
    ArrayList connections = new ArrayList<>();

    public Server(int port, String root) {
        this.port = port;
        this.root = root;
        System.out.println("Server constructed");
        System.out.println("port: " + port);
        System.out.println("root: " + root);
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server socket initialized");
        running = true;
        while (running) {
            Socket clientSocket = serverSocket.accept();
//            new Thread(() -> handleClient(clientSocket)).start();
        }

    }

//    Router needed; where does the request go?
//

    //factory to make client sockets; pass factory in.
//
//    private void handleClient(Socket clientSocket) {
//        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//             OutputStream out = clientSocket.getOutputStream()
//        ) {

//    CONNECTION INTERFACE PERHAPS??? - done
//    Connection connection = new Connection (serverSocket, clientSocket);
//    connections.add(connection);
//    connection.run();
//            String line;
//            String request = in.readLine();
//            while(!request.isEmpty()){
//                  parse the request..
//            }

//        }

//    }

    public void stopServer() throws IOException {
        running = false;
        serverSocket.close();
    }

}
