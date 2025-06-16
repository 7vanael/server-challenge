package Router;

import Connection.Request;
import Connection.Response;
import org.example.RouteHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class HomeHandler implements RouteHandler {
    private Path rootPath;
    private String serverName;

    public HomeHandler(Path rootPath, String serverName){
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    @Override
    public Response handle(Request request) throws IOException {
        Path indexPath = rootPath.resolve("index.html");

        byte[] fileBytes = Files.readAllBytes(indexPath);
        String contentType = "text/html";

        return new Response(serverName, 200, contentType, fileBytes)
                .addHeader("Content-Type", contentType)
                .addHeader("Content-Length", String.valueOf(fileBytes.length))
                .addHeader("Server ", serverName);
    }
}
