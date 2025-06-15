import java.util.HashMap;
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
    private final String serverName;
    private int statusCode;
    private String statusText;
    private String contentType;
    private byte[] body;
    private Map<String, String> headers;

    public Response(String serverName) {
        this.serverName = serverName;
        this.statusCode = 200;
        this.statusText = STATUS_CODES.get(200);
        this.contentType = "text/html";
        this.body = new byte[0];
        this.headers = new HashMap<>();
    }

    public Response(String serverName, int statusCode, String contentType, String body) {
        this.serverName = serverName;
        this.statusCode = statusCode;
        this.statusText = STATUS_CODES.get(statusCode);
        this.contentType = contentType;
        this.body = body.getBytes();
        this.headers = new HashMap<>();
    }

    public Response(String serverName, int statusCode, String contentType, byte[] body) {
        this.serverName = serverName;
        this.statusCode = statusCode;
        this.statusText = STATUS_CODES.getOrDefault(statusCode, "");
        this.contentType = contentType;
        this.body = body.clone();
        this.headers = new HashMap<>();
    }

    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        this.statusText = STATUS_CODES.getOrDefault(statusCode, "");
        return this;
    }

    public Response setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Response setBody(String body) {
        this.body = body.getBytes();
        return this;
    }

    public Response setBody(byte[] body) {
        this.body = body.clone();
        return this;
    }

    public Response addHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public Response setHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body.clone();
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
}
