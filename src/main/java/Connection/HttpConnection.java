package Connection;

import Router.Router;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpConnection extends Connection {
    private boolean active = true;
    private Router router;

    public HttpConnection(Socket clientSocket, String root, Router router) {
        this.router = router;
        this.clientSocket = clientSocket;
        this.root = root;
    }

    @Override
    public void run() {
        System.out.println("Connection initialized and Running");
//        inputStreamReader: bytes->characters. BufferedReader: characters-> lines.
        try (BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {

            Request request = Request.parseRequest(clientSocket.getInputStream());
            Response response = router.route(request);
            writeResponse(out, response);

        } catch (IOException e) {
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private void writeResponse(BufferedOutputStream out, Response response) throws IOException {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("HTTP/1.1 ").append(response.getStatusCode())
                .append(" ").append(response.getStatusText()).append("\r\n");
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            headerBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        headerBuilder.append("\r\n");
        out.write(headerBuilder.toString().getBytes(StandardCharsets.UTF_8));

        if (response.getBody().length > 0) {
            out.write(response.getBody());
        }
        out.flush();
    }

    public boolean getActive(){
        return active;
    }
}
