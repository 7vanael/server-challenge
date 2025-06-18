package Connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {
    private MockSocket mocket;
    private InputStream in;
    private Request request;
    private String target;

    @BeforeEach
    public void setUp(){
        mocket = null;
        in = null;
        target = null;
        request = null;
    }

    @Test
    public void getRequestParsesIntoParts() throws IOException {
        target = "/";
        processGetRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/index.html", request.getPath());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals(0, request.getErrorCode());
        assertTrue(request.isValid());
    }

    @Test
    public void getRequestWithLeadingSlashStillParses() throws IOException {
        target = "/index.html";
        processGetRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/index.html", request.getPath());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals(0, request.getErrorCode());
        assertTrue(request.isValid());
    }

    @Test
    public void emptyGETParsesIntoParts() throws IOException{
        target = "";
        processGetRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/index.html", request.getPath());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals(0, request.getErrorCode());
        assertTrue(request.isValid());
    }

    @Test
    public void invalidRequestFormatReturns400Error() throws IOException {
        target = "";
        String clientMessage = "Getter  Get" + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(clientMessage);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);
        assertEquals(400, request.getErrorCode());
        Assertions.assertFalse(request.isValid());
    }

    @Test
    public void emptyRequestReturns400Error() throws IOException {
        target = "";
        String clientMessage = "";
        mocket = new MockSocket(clientMessage);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);
        assertEquals(400, request.getErrorCode());
        Assertions.assertFalse(request.isValid());
    }

    @Test
    public void requestHeadersAreParsed() throws IOException{
        String requestLine = "GET /form HTTP/1.1\r\n";
        String headers = "Host: localhost:7654\r\n" +
                "Content-Type: multipart/form-data; boundary=123456\r\n" +
                "Content-Length: 0\r\n" + "\r\n";
        mocket = new MockSocket(requestLine + headers);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);

        assertEquals("/form", request.getPath());
        assertEquals("localhost:7654", request.getHeader("Host"));
        assertEquals("multipart/form-data; boundary=123456", request.getHeader("Content-Type"));
        assertEquals("0", request.getHeader("Content-Length"));
    }

    @Test
    public void requestWithQueryParametersParses() throws IOException{
        target = "/form?foo=1&bar=2";
        processGetRequest();

        assertEquals("/form", request.getPath());
        assertEquals("GET", request.getMethod());
        assertEquals("foo=1&bar=2", request.getQueryString());
    }

    @Test
    public void multipartFormWithTwoFields() throws IOException {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String requestLine = "POST /test HTTP/1.1\r\n";
        String headers = "Host: localhost:8080\r\n" +
                "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n";

        String body = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"username\"\r\n" +
                "\r\n" +
                "john_doe\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"email\"\r\n" +
                "\r\n" +
                "john@example.com\r\n" +
                "--" + boundary + "--\r\n";

        headers += "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n";

        mocket = new MockSocket(requestLine + headers + body);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);

        assertTrue(request.isValid());
        assertEquals("POST", request.getMethod());
        assertEquals("/test", request.getPath());
        assertEquals(2, request.getMultipartParts().size());

        assertEquals("john_doe", request.getMultipartValue("username"));
        assertEquals("john@example.com", request.getMultipartValue("email"));

        Request.MultipartPart usernamePart = request.getMultipartPart("username");
        assertNotNull(usernamePart);
        assertEquals("username", usernamePart.getName());
        assertNull(usernamePart.getFilename());
        assertFalse(usernamePart.isFile());
    }

    @Test
    public void multipartFormWithFileUpload() throws IOException {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String requestLine = "POST /upload HTTP/1.1\r\n";
        String headers = "Host: localhost:8080\r\n" +
                "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n";

        String fileContent = "This is test file content\nLine 2 of the file";
        String body = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"description\"\r\n" +
                "\r\n" +
                "My test file\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                fileContent + "\r\n" +
                "--" + boundary + "--\r\n";

        headers += "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n";

        mocket = new MockSocket(requestLine + headers + body);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);

        assertTrue(request.isValid());
        assertEquals(2, request.getMultipartParts().size());

        assertEquals("My test file", request.getMultipartValue("description"));

        Request.MultipartPart filePart = request.getMultipartPart("file");
        assertNotNull(filePart);
        assertEquals("file", filePart.getName());
        assertEquals("test.txt", filePart.getFilename());
        assertEquals("text/plain", filePart.getContentType());
        assertTrue(filePart.isFile());
        assertEquals(fileContent, filePart.getContentAsString());
    }

    @Test
    public void nonMultipartRequestStillWorks() throws IOException {
        String requestLine = "POST /form HTTP/1.1\r\n";
        String body = "username=john&password=secret";
        String headers = "Host: localhost:7654\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n";

        mocket = new MockSocket(requestLine + headers + body);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);

        assertTrue(request.isValid());
        assertEquals("POST", request.getMethod());
        assertEquals("/form", request.getPath());
        assertEquals("application/x-www-form-urlencoded", request.getHeaders().get("content-type"));
        assertEquals(body, new String(request.getBody()));
        assertEquals(0, request.getMultipartParts().size()); // No multipart parts
    }

    @Test
    public void emptyMultipartForm() throws IOException {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String requestLine = "POST /upload HTTP/1.1\r\n";
        String headers = "Host: localhost:8080\r\n" +
                "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n";

        String body = "--" + boundary + "--\r\n";
        headers += "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n";

        mocket = new MockSocket(requestLine + headers + body);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);

        assertEquals(0, request.getMultipartParts().size());
    }

    @Test
    public void malformedBoundaryHandling() throws IOException {
        String requestLine = "POST /upload HTTP/1.1\r\n";
        String headers = "Host: localhost:8080\r\n" +
                "Content-Type: multipart/form-data\r\n"; // Missing boundary

        String body = "invalid multipart data";
        headers += "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n";

        mocket = new MockSocket(requestLine + headers + body);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);

        assertTrue(request.isValid());
        assertEquals(0, request.getMultipartParts().size());
    }

    @Test
    public void requestWithQueryParametersStillWorks() throws IOException {
        String requestLine = "GET /form?foo=1&bar=2 HTTP/1.1\r\n";
        String headers = "Host: localhost:7654\r\n\r\n";

        mocket = new MockSocket(requestLine + headers);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);

        assertEquals("/form", request.getPath());
        assertEquals("GET", request.getMethod());
        assertEquals("foo=1&bar=2", request.getQueryString());
        assertTrue(request.isValid());
    }

    private void processGetRequest() throws IOException {
        String clientMessage = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(clientMessage);
        in = mocket.getInputStream();
        request = Request.parseRequest(in);
    }
}
