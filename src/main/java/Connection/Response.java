package Connection;

import Main.HttpConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {
    private final String serverName;
    private int statusCode;
    private String statusText;
    private String contentType;
    private byte[] body;
    private Map<String, String> headers;
    private List<String> cookies = new ArrayList<>();

    public Response(String serverName) {
        this.serverName = serverName;
        this.statusCode = 200;
        this.statusText = HttpConstants.STATUS_CODES.get(200);
        this.contentType = "text/html";
        this.body = new byte[0];
        this.headers = new HashMap<>();
        setInitialHeaders();
    }

    public Response(String serverName, int statusCode, String contentType, String body) {
        this.serverName = serverName;
        this.statusCode = statusCode;
        this.statusText = HttpConstants.STATUS_CODES.get(statusCode);
        this.contentType = contentType;
        this.body = body.getBytes();
        this.headers = new HashMap<>();
        setInitialHeaders();
    }

    public Response(String serverName, int statusCode, String contentType, byte[] body) {
        this.serverName = serverName;
        this.statusCode = statusCode;
        this.statusText = HttpConstants.STATUS_CODES.getOrDefault(statusCode, "");
        this.contentType = contentType;
        this.body = body.clone();
        this.headers = new HashMap<>();
        setInitialHeaders();
    }

    private void setInitialHeaders() {
        this.addHeader("Content-Type", this.contentType);
        this.addHeader("Content-Length", String.valueOf(this.body.length));
        this.addHeader("Server", this.serverName);
        this.addHeader("Connection", "close");
    }


    public Response addCookie(String cookieValue) {
        cookies.add(cookieValue);
        return this;
    }

    public List<String> getCookies() {
        return new ArrayList<>(cookies);
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
