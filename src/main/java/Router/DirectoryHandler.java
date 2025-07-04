package Router;

import Connection.RequestI;
import Connection.Response;
import Main.RouteHandler;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryHandler implements RouteHandler {
    private Path rootPath;
    private String serverName;

    public DirectoryHandler(Path rootPath, String serverName){
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    @Override
    public Response handle(RequestI request) throws IOException {
        String requestPath = request.getPath();

        requestPath = trimListingFromPath(requestPath);

        Path targetPath = rootPath.resolve(requestPath).normalize();

        if (!Files.exists(targetPath)) {
            throw new IOException("Directory Not found");
        }

        if (Files.isDirectory(targetPath)) {
            String directoryHtml = generateDirectoryListing(targetPath, requestPath);
            return new Response(serverName, 200, "text/html", directoryHtml);
        } else {
            throw new IOException("Not a Directory");
        }
    }

    private static String trimListingFromPath(String requestPath) {
        if (requestPath.startsWith("/listing")) {
            requestPath = requestPath.substring("/listing".length());
        }
        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }
        return requestPath;
    }

    private String generateDirectoryListing(Path directory, String relativePath) throws IOException {
        StringBuilder html = new StringBuilder();

        html.append("<html><ul>");

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
            for (Path path : paths) {
                String fileName = path.getFileName().toString();
                boolean isDirectory = Files.isDirectory(path);
                String fileUrl;
                if (isDirectory) {
                    fileUrl = "/listing/" + (relativePath.isEmpty() ? "" : relativePath + "/") + fileName;
                } else {
                    fileUrl = "/" + (relativePath.isEmpty() ? "" : relativePath + "/") + fileName;
                }

                html.append("<li><a href=\"").append(fileUrl).append("\">").append(fileName);
                html.append("</a></li>");
            }
        }

        html.append("</ul>");
        html.append("</body></html>");

        return html.toString();
    }

}
