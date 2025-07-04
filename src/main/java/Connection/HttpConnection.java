package Connection;

import Router.Router;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpConnection extends Connection {
    private Router router;

    public HttpConnection(Socket clientSocket, String root, Router router) {
        this.router = router;
        this.clientSocket = clientSocket;
        this.root = root;
    }

    @Override
    public void run() {
        try (BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {

            RequestI request = Request.parseRequest(clientSocket.getInputStream());
            Response response = router.route(request);
            writeResponse(out, response);

        } catch (IOException e) {
            System.out.println("Error in connection run: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
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
        if (response.getCookies() != null) {
            for(String cookie : response.getCookies()){
                headerBuilder.append("Set-Cookie: ").append(cookie).append("\r\n");
            }
        }
        headerBuilder.append("\r\n");
        out.write(headerBuilder.toString().getBytes(StandardCharsets.UTF_8));

        if (response.getBody().length > 0) {
            out.write(response.getBody());
        }
        out.flush();
    }
}
