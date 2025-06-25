package Router;

import Connection.Request;
import Connection.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryHandlerTest {
    private String serverName = "Test Server";
    private Path rootPath = Paths.get("testroot");
    private DirectoryHandler handler = new DirectoryHandler(rootPath, serverName);
    private Request request;
    private Response response;

    @Test
    public void testHandleRootDirectoryGeneratesHTMLListing() throws IOException {
        request = createMockRequest("GET", "/listing/img");

        response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("text/html", response.getContentType());

        String html = new String(response.getBody());
        assertTrue(html.contains("<html>"));
        assertTrue(html.contains("<ul>"));
        assertTrue(html.contains("</ul>"));
    }


    @Test
    public void testHandleDirectoryWithoutFilesPrefix() throws IOException {
        request = createMockRequest("GET", "/");

        response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertEquals("text/html", response.getContentType());

        String html = new String(response.getBody());
        assertTrue(html.contains("<html>"));
    }

    @Test
    public void testHandleNonExistentFileThrowsIOException() {
        request = createMockRequest("GET", "/files/nonexistent.txt");

        assertThrows(IOException.class, () -> {
            handler.handle(request);
        });
    }

    @Test
    public void testHandleNonExistentDirectoryThrowsIOException() {
        request = createMockRequest("GET", "/files/nonexistent/");

        assertThrows(IOException.class, () -> {
            handler.handle(request);
        });
    }

    @Test
    public void testHandleDirectoryListingContainsCorrectLinks() throws IOException {
        request = createMockRequest("GET", "/listing");

        response = handler.handle(request);

        String html = new String(response.getBody());
        assertTrue(html.contains("<li><a href="));
        assertTrue(html.contains("/index.html"));
        assertTrue(html.contains(">index.html</a></li>"));
        assertTrue(html.contains("/hello.pdf"));
        assertTrue(html.contains("/listing/img"));
    }

    @Test
    public void testHandlePathNormalizationPreventsTraversal() throws IOException {
        request = createMockRequest("GET", "/listing/../testroot/listing");

        assertThrows(IOException.class, () -> {
            handler.handle(request);
        });
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
