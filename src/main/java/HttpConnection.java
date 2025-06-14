import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpConnection extends Connection {
    String method ;
    String path ;
    String protocol;

    private boolean active = true;
    private Path rootPath;
    private Path targetPath;
    private String serverName;
    private static Map<Integer, String> statusCodes = new HashMap<>(Map.of(
            200, "OK",
            400, "Bad-Request",
            403, "Forbidden",
            404, "Not-Found",
            501, "Not-Implemented"));
    private static Map<String, String> contentTypes = new HashMap<>(Map.of(
            ".html", "text/html",
            ".htm", "text/html",
            ".css", "text/css",
            ".js", "application/javascript",
            ".png", "image/png",
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".gif", "image/gif",
            ".txt", "text/plain",
            ".json", "application/json"));
    private static String protocolVersion = "HTTP/1.1";


    public HttpConnection(Socket clientSocket, String root, String serverName, int id) {
        this.clientSocket = clientSocket;
        this.root = root;
        this.id = id;
        this.rootPath = Paths.get(root);
        this.serverName = serverName;
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

            parseRequest(in, out);

            switch (method) {
                case "GET":
                    System.out.println("processing GET with path: " + path);
                    targetPath = rootPath.resolve(path).normalize();

                    if (!Files.exists(targetPath)) {
                        sendStatus(out, 404);
                        return;
                    } else if (!targetPath.startsWith(rootPath)) {
                        sendStatus(out, 403);
                        return;
                    } else {
                        sendStatus(out, 200);
                        out.println("Date: " + new Date().toString());
                        out.println("Server: " + serverName);
                        out.println("Connection: close");
                        out.println("Content-Type: " + getContentType(targetPath));
                        byte[] fileBytes = Files.readAllBytes(targetPath);
                        out.println("Content-Length: " + fileBytes.length);
                        out.flush();

                        bodyOut.write(Files.readAllBytes(targetPath));
                        bodyOut.flush();
                    }
                    break;
                case "POST":
                    sendStatus(out, 501);
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

    private boolean parseRequest(BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("about to read request line...");
        String request = in.readLine();
        System.out.println("request line: " + request);

        if (request == null) {
            System.out.println("ERROR: That request was null!");
            return false;
        }
        String[] requestLine = request.split(" ");

        if (requestLine.length < 3) {
            sendStatus(out, 400);
            return false;
        }
        method = requestLine[0];
        path = requestLine[1];
        protocol = requestLine[2]; //Probably don't need this?

        if (path.isEmpty() || path.equals("/")) path = "index.html";
        if (path.startsWith("/")) path = path.substring(1);

        return true;
    }

    private static void sendStatus(PrintWriter out, int statusNumber) {
        String errorString = Integer.toString(statusNumber);
        String errorDetail = statusCodes.get(statusNumber);
        String errorResponse = protocolVersion + " " + errorString + " " + errorDetail + "\r\n\r\n";
        out.print(errorResponse);
        out.flush();
    }
    private static String getContentType(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();

        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return "application/octet-stream";
        }

        String extension = fileName.substring(lastDot);
        return contentTypes.getOrDefault(extension, "application/octet-stream");
    }
}
