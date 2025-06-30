package Router;

import Main.HttpConstants;
import Connection.Request;
import Connection.Response;
import Main.RouteHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler implements RouteHandler {

    private Path rootPath;
    private String serverName;

    public FileHandler(Path rootPath, String serverName){
        this.rootPath = rootPath;
        this.serverName = serverName;
    }


    @Override
    public Response handle(Request request) throws IOException {
        String requestPath = request.getPath();

        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }

        Path targetPath = rootPath.resolve(requestPath).normalize();

        if (!Files.exists(targetPath) || Files.isDirectory(targetPath)) {
            return NotFoundResponse.createResponse(serverName);
        }

        byte[] fileBytes = Files.readAllBytes(targetPath);
        String contentType = getContentType(targetPath);

        return new Response(serverName, 200, contentType, fileBytes);
    }

    private String getContentType(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return HttpConstants.DEFAULT_CONTENT_TYPE;
        }
        String extension = fileName.substring(lastDot);
        return HttpConstants.CONTENT_TYPES.getOrDefault(extension, HttpConstants.DEFAULT_CONTENT_TYPE);
    }
}
