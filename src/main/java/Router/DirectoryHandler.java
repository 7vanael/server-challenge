package org.example;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryHandler implements RouteHandler{
    private Path rootPath;
    private String serverName;

    public DirectoryHandler(Path rootPath, String serverName){
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    @Override
    public Response handle(Request request) throws IOException {
        String requestPath = request.getPath();

        if (requestPath.startsWith("/files")) {
            requestPath = requestPath.substring("/files".length());
        }

        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }

        Path targetPath = rootPath.resolve(requestPath).normalize();

        if (!Files.exists(targetPath)) {
            throw new IOException("Not found");
        }

        if (Files.isDirectory(targetPath)) {
            String directoryHtml = generateDirectoryListing(targetPath, requestPath);
            return new Response(serverName, 200, "text/html", directoryHtml);
        } else {
            return serveFile(targetPath);
        }
    }
    private String generateDirectoryListing(Path directory, String relativePath) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Directory: /").append(relativePath).append("</title></head>");
        html.append("<body><h1>Directory: /").append(relativePath).append("</h1>");

        if (!relativePath.isEmpty()) {
            String parentPath = relativePath.contains("/") ?
                    relativePath.substring(0, relativePath.lastIndexOf("/")) : "";
            html.append("<p><a href=\"/files/").append(parentPath).append("\">[Parent Directory]</a></p>");
        }

        html.append("<ul>");

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
            for (Path path : paths) {
                String fileName = path.getFileName().toString();
                String fileUrl = "/files/" + (relativePath.isEmpty() ? "" : relativePath + "/") + fileName;
                boolean isDirectory = Files.isDirectory(path);

                html.append("<li><a href=\"").append(fileUrl).append("\">").append(fileName);
                html.append(isDirectory ? "/" : "").append("</a></li>");
            }
        }

        html.append("</ul>");
        html.append("<p><a href=\"/\">Home</a></p>");
        html.append("</body></html>");

        return html.toString();
    }

    private Response serveFile(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        String contentType = getContentType(filePath);

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
