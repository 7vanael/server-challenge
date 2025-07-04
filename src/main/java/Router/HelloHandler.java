package Router;

import Connection.RequestI;
import Connection.Response;
import Main.RouteHandler;

import java.io.IOException;
import java.nio.file.Path;

public class HelloHandler implements RouteHandler {

    private Path rootPath;
    private String serverName;

    public HelloHandler(Path rootPath, String serverName){
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    @Override
    public Response handle(RequestI request) throws IOException {
        String welcomeHtml = "<html><head><title>Hello, Welcome!</title></head>" +
                "<body><h1>Hello, Welcome!</h1>" +
                "<p>You've reached the Hello page on Server: " + serverName + "</p></body></html>";
        String contentType = "text/html";

        return new Response(serverName, 200, contentType, welcomeHtml);
    }
}
