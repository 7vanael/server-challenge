package Router;

import Connection.Request;
import Connection.Response;
import org.example.RouteHandler;

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
    public Response handle(Request request) throws IOException {
        Path indexPath = rootPath.resolve(request.getPath());

        String welcomeHtml = "<html><head><title>Hello, Welcome!</title></head>" +
                "<body><h1>Hello, Welcome!</h1>" +
                "<p>You've reached the Hello page on Server: " + serverName + "</p></body></html>";
        String contentType = "text/html";

        return new Response(serverName, 200, contentType, welcomeHtml)
                .addHeader("Content-Type", contentType)
                .addHeader("Content-Length", String.valueOf(welcomeHtml.getBytes().length))
                .addHeader("Server ", serverName);
    }
}
