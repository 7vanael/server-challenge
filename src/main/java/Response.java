import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

public class Response {
    private static final Map<Integer, String> STATUS_CODES = Map.of(
            200, "OK",
            400, "Bad Request",
            403, "Forbidden",
            404, "Not Found",
            405, "Method Not Allowed",
            500, "Internal Server Error",
            501, "Not Implemented"
    );

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", "text/html",
            ".htm", "text/html",
            ".css", "text/css",
            ".js", "application/javascript",
            ".png", "image/png",
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".gif", "image/gif",
            ".txt", "text/plain",
            ".json", "application/json"
    );

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final PrintWriter headerOut;
    private final BufferedOutputStream bodyOut;
    private final String serverName;

    public Response(PrintWriter headerOut, BufferedOutputStream bodyOut, String serverName) {
        this.headerOut = headerOut;
        this.bodyOut = bodyOut;
        this.serverName = serverName;
    }
