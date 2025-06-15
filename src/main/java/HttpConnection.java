import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpConnection extends Connection {
    private boolean active = true;
    private Path rootPath;
    private Path targetPath;
    private String serverName;
    private Router router;
    private static String protocolVersion = "HTTP/1.1";


    public HttpConnection(Socket clientSocket, String root, String serverName, Router router) {
        this.clientSocket = clientSocket;
        this.root = root;
        this.rootPath = Paths.get(root);
        this.serverName = serverName;
        this.router = new Router(this.rootPath, serverName);
    }

    @Override
    public void run() {
        System.out.println("Connection initialized and Running");
//        inputStreamReader: bytes->characters. BufferedReader: characters-> lines.
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//             converts characters to bytes and includes a line flusher: use for Headers
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
//                SECOND OUTPUT METHOD because if we're sending files, they aren't characters
//                they are files we don't want to convert.: use for Body
             BufferedOutputStream bodyOut = new BufferedOutputStream(clientSocket.getOutputStream())) {

            Request request = Request.parseRequest(in, rootPath);
            Response response = router.route(request);
            writeResponse(out, bodyOut, response);

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Close connection error: " + e.getMessage());
            }
        }
    }

    private void writeResponse(PrintWriter out, BufferedOutputStream bodyOut, Response response) throws IOException {
        out.println("HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusText());

        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            out.println(header.getKey() + ": " + header.getValue());
        }
        out.println();
        out.flush();

        if (response.getBody().length > 0) {
            bodyOut.write(response.getBody());
            bodyOut.flush();
        }
    }

}
