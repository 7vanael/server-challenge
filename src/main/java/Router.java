import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Path rootPath;
    private final String serverName;

    public Router(Path rootPath, String serverName) {
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    public Response route(Request request) {
        if (request.getErrorCode() != 0) {
            return createErrorResponse(request.getErrorCode());
        }
        switch (request.getMethod()) {
            case "GET":
                return handleGet(request);
            case "POST":
                return createErrorResponse(501);
            default:
                return createErrorResponse(405);
        }
    }

    private Response handleGet(Request request) {
        try {
            Path targetPath = rootPath.resolve(request.getPath()).normalize();
            if (!targetPath.startsWith(rootPath)) {
                return createErrorResponse(403);
            }
            if (!Files.exists(targetPath)) {
                return createErrorResponse(404);
            }
//            if (Files.isDirectory(targetPath)) {
//                return handleDirectoryListing(request);
//            } else
                if (Files.isRegularFile(targetPath)) {
                return handleFileServing(targetPath);
            } else {
                return createErrorResponse(404);
            }
        } catch (IOException e) {
            return createErrorResponse(500);
        }
    }

    private Response handleFileServing(Path targetPath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(targetPath);
        String contentType = getContentType(targetPath);

        return new Response(serverName, 200, contentType, fileBytes)
                .addHeader("Content-Type", contentType)
                .addHeader("Content-Length", String.valueOf(fileBytes.length));
    }


    private Response createErrorResponse(int statusCode) {
        String errorHtml = generateErrorPage(statusCode);

        return new Response(serverName, statusCode, "text/html", errorHtml)
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(errorHtml.getBytes().length));
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

    private String generateErrorPage(int statusCode) {
        String statusText = HttpConstants.STATUS_CODES.get(statusCode);
        return "<html><head><title>" + statusCode + " " + statusText + "</title></head>" +
                "<body><h1>" + statusCode + " " + statusText + "</h1>" +
                "<p>Server: " + serverName + "</p></body></html>";
    }

//    private Response handleDirectoryListing(Request request) {
//        // For now, return a simple directory listing
//        // Later you can extract this to a separate DirectoryListingGenerator
//        String directoryHtml = generateSimpleDirectoryListing(request);
//
//        return new Response(serverName, 200, "text/html", directoryHtml)
//                .addHeader("Content-Type", "text/html")
//                .addHeader("Content-Length", String.valueOf(directoryHtml.getBytes().length));
//    }

//    private String generateSimpleDirectoryListing(Request request) {
//        // Simple placeholder for now
//        return "<html><head><title>Directory Listing</title></head>" +
//                "<body><h1>Directory Listing for " + request.getPath() + "</h1>" +
//                "<p>Directory listing coming soon!</p></body></html>";
//    }
}
