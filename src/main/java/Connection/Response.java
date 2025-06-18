package Connection;

import org.example.HttpConstants;

import java.util.HashMap;
import java.util.Map;

public class Response {
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
        this.statusText = HttpConstants.STATUS_CODES.get(200);
        this.contentType = "text/html";
        this.body = new byte[0];
        this.headers = new HashMap<>();
    }

    public Response(String serverName, int statusCode, String contentType, String body) {
        this.serverName = serverName;
        this.statusCode = statusCode;
        this.statusText = HttpConstants.STATUS_CODES.get(statusCode);
        this.contentType = contentType;
        this.body = body.getBytes();
        this.headers = new HashMap<>();
    }

    public Response(String serverName, int statusCode, String contentType, byte[] body) {
        this.serverName = serverName;
        this.statusCode = statusCode;
        this.statusText = HttpConstants.STATUS_CODES.getOrDefault(statusCode, "");
        this.contentType = contentType;
        this.body = body.clone();
        this.headers = new HashMap<>();
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
