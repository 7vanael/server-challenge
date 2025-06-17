package Connection;

import Router.Router;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class HttpConnection extends Connection {
    private boolean active = true;
    private Path rootPath;
    private Router router;

    public HttpConnection(Socket clientSocket, String root, Router router) {
        this.router = router;
        this.clientSocket = clientSocket;
        this.root = root;
        this.rootPath = Paths.get(root);
        this.router = router;
    }

    @Override
    public void run() {
        System.out.println("Connection initialized and Running");
        System.out.println("routes available:");
        System.out.println(router.getRoutes());
//        inputStreamReader: bytes->characters. BufferedReader: characters-> lines.
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
             BufferedOutputStream bodyOut = new BufferedOutputStream(clientSocket.getOutputStream())) {


            Request request = Request.parseRequest(in);
            System.out.println("Request: ");
            System.out.println(request.getPath() + request.getMethod());
            Response response = router.route(request);
            System.out.println("Response: ");
            System.out.println(response.getStatusCode());
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
        System.out.println("Sending Headders:");
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            out.println(header.getKey() + ": " + header.getValue());
            System.out.println(header.getKey() + ": " + header.getValue());
        }
        out.println();
        out.flush();

        if (response.getBody().length > 0) {
            bodyOut.write(response.getBody());
            bodyOut.flush();
        }
    }

    public boolean getActive(){
        return active;
    }


}
