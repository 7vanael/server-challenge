import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpConnection extends Connection {

    private boolean active = true;
    private Path rootPath;
    private Path targetPath;


    public HttpConnection(Socket clientSocket, String root, int id) {
        this.clientSocket = clientSocket;
        this.root = root;
        this.id = id;
        this.rootPath = Paths.get(root);
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

            System.out.println("about to read request line...");
            String request = in.readLine();
            System.out.println("request line: " + request);

            if(request == null){
                System.out.println("ERROR: That request was null!");
                return;
            }
            String[] requestLine = request.split(" ");

            if (requestLine.length < 3) {
//              Send error
                return;
            }
            String method = requestLine[0];
            String path = requestLine[1];
            String protocol = requestLine[2]; //Probably don't need this?

            switch (method) {
                case "GET":
                    System.out.println("processing GET with path: " + path);
//                    targetPath = Paths.get(path);
                    if(path.isEmpty() || path.equals("/")){
                        path = "index.html";
                    }
                    if(path.startsWith("/")) path = path.substring(1);

                    targetPath = rootPath.resolve(path).normalize();
                    System.out.println("resolved target path: " + targetPath);
                    System.out.println(Files.exists(targetPath));
                    if(!Files.exists(targetPath)){
                        String errorResponse = "HTTP/1.1 404 Not-Found\r\n\r\n";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }else {
                        String response = "HTTP/1.1 200 OK\r\n\r\n";
                        System.out.println(response);
                        out.println(response);
                        out.flush();

                        bodyOut.write(Files.readAllBytes(targetPath));
                        bodyOut.flush();
                    }
                    break;
                case "POST":
                    String response = "HTTP/1.1 405 Not-Allowed\r\n";
                    out.println(response);
                    out.println();
                    out.flush();

                    break;
            }

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
}
