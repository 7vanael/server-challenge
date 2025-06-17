package Router;

import Connection.Request;
import Connection.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlerTest {
    private String serverName = "Test Server";
    private Path rootPath = Paths.get("testroot");
    private FileHandler handler = new FileHandler(rootPath, serverName);
    private Request request;
    private Response response;

    @Test
    public void testHandleHTMLFileReturnsCorrectContentAndType() throws IOException {
        Request request = createMockRequest("GET", "/index.html");

        Response response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
        assertTrue(response.getBody().length > 0);
        assertEquals("text/html", response.getHeaders().get("Content-Type"));
        assertNotNull(response.getHeaders().get("Content-Length"));
    }

    @Test
    public void testHandleJPGFileReturnsCorrectContentType() throws IOException {
        // Assumes you have a JPG file in testroot/img/
        Request request = createMockRequest("GET", "/img/autobot.jpg");

        Response response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("image/jpeg", response.getContentType());
        assertTrue(response.getBody().length > 0);
        assertEquals("image/jpeg", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testHandlePNGFileReturnsCorrectContentType() throws IOException {
        // Assumes you have a PNG file in testroot/img/
        Request request = createMockRequest("GET", "/img/decepticon.png");

        Response response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("image/png", response.getContentType());
        assertTrue(response.getBody().length > 0);
        assertEquals("image/png", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testHandlePDFFileReturnsCorrectContentType() throws IOException {
        // Assumes you have a PDF file in testroot/
        Request request = createMockRequest("GET", "/hello.pdf");

        Response response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.getBody().length > 0);
        assertEquals("application/pdf", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testHandleNonExistentFileReturns404() throws IOException {
        Request request = createMockRequest("GET", "/nonexistent.txt");

        Response response = handler.handle(request);

        assertEquals(404, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
    }

    @Test
    public void testHandleDirectoryInsteadOfFileReturns404() throws IOException {
        // Trying to serve a directory as a file should fail
        Request request = createMockRequest("GET", "/img");

        Response response = handler.handle(request);

        assertEquals(404, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
    }

    @Test
    public void testHandlePathNormalizationWorks() throws IOException {
        // Test that path normalization works for valid paths
        Request request = createMockRequest("GET", "/./index.html");

        Response response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
    }

    @Test
    public void testHandleNestedFileInSubdirectory() throws IOException {
        Request request = createMockRequest("GET", "/img/autobot.jpg");

        Response response = handler.handle(request);

        int bodyLength = response.getBody().length;
        assertEquals(200, response.getStatusCode());
        assertTrue( bodyLength > 0);
        assertEquals("image/jpeg", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testHandleContentLengthHeaderIsCorrect() throws IOException {
        Request request = createMockRequest("GET", "/index.html");

        Response response = handler.handle(request);

        String contentLength = response.getHeaders().get("Content-Length");
        assertNotNull(contentLength);
        assertEquals(String.valueOf(response.getBody().length), contentLength);
    }


    @Test
    public void testHandleFileServingReturnsFileContent() throws IOException {
        request = createMockRequest("GET", "/index.html");

        response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
        assertTrue(response.getBody().length > 0);
    }


    private Request createMockRequest(String method, String path) {
        return new Request() {
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
                return 0;
            }
        };
    }
}
