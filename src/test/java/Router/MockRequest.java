package Router;

import Connection.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MockRequest extends Request {
    private String method;
    private String path;
    private int errorCode = 0;
    private boolean valid = true;
    private HashMap<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];
    private String queryString;
    private String cookieString;
    private ArrayList<String> cookies = new ArrayList<>();


    public MockRequest(String method, String path, int errorCode) {
        this.method = method;
        this.path = path;
        this.errorCode = errorCode;
    }
    private List<Request.MultipartPart> multipartParts = new ArrayList<>();

    @Override
    public List<MultipartPart> getMultipartParts() {
        return multipartParts;
    }

    public void addMultipartPart(Request.MultipartPart part) {
        multipartParts.add(part);
    }
    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }
    public String getCookieString() {
        return cookieString;
    }

    public ArrayList<String> getCookies() {
        return cookies;
    }

    public void setCookies(ArrayList<String> cookies) {
        this.cookies = cookies;
    }
}
