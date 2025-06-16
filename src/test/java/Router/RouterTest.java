import org.example.Request;
import org.example.Response;
import org.example.Router;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RouterTest {
    private Router router;
    private String serverName = "TestServer";
    private Path tempDir;
    private Path rootPath;

    //Use a router that's a regular router, but add routes to it?
    // need a method to add a route- can have methods in main to add the rouse
    //then the tests can initialize the mock routes..

    //router doesn't define the routes- those have to be defined
    //Perhaps in the server initialization in main?

    @BeforeEach
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("router-test");
        rootPath = tempDir;
        router = new Router(rootPath, serverName);

        createTestFiles();
    }

    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectory(tempDir);
    }

    private void createTestFiles() throws IOException {
        Path indexFile = rootPath.resolve("index.html");
        Files.write(indexFile, "<html><body>Hello, World!</body></html>".getBytes());

        Path subDir = rootPath.resolve("images");
        Files.createDirectory(subDir);

        Path imageFile = subDir.resolve("test.png");
        Files.write(imageFile, "fake-png-data".getBytes());
    }

    @Test
    public void routeGetValidFileReturns200() throws IOException {
        Request request = createMockRequest("GET", "index.html");

        Response response = router.route(request);
        String body = new String(response.getBody());

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("text/html", response.getContentType());
        Assertions.assertTrue(body.contains("Hello, World!"));
    }

    @Test
    public void routeGetNonexistentFileReturns404() {
        Request request = createMockRequest("GET", "nonexistent.html");

        Response response = router.route(request);

        Assertions.assertEquals(404, response.getStatusCode());
        Assertions.assertEquals("text/html", response.getContentType());
        Assertions.assertTrue(new String(response.getBody()).contains("404"));
    }

    @Test
    public void routeGetImageFileReturnsCorrectContentType() {
        Request request = createMockRequest("GET", "images/test.png");
        Response response = router.route(request);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void routePostReturns501() {
        Request request = createMockRequest("POST", "index.html");
        Response response = router.route(request);
        Assertions.assertEquals(501, response.getStatusCode());
    }

    @Test
    public void routeUnsupportedMethodReturns405() {
        Request request = createMockRequest("DELETE", "index.html");
        Response response = router.route(request);
        Assertions.assertEquals(405, response.getStatusCode());
    }

    @Test
    public void routeRequestWithErrorCodeReturnsError() {
        Request request = createMockRequestWithError("GET", "index.html", 400);
        Response response = router.route(request);
        Assertions.assertEquals(400, response.getStatusCode());
    }

    private Request createMockRequest(String method, String path) {
        return new Request() {
            @Override
            public String getMethod() { return method; }
            @Override
            public String getPath() { return path; }
            @Override
            public int getErrorCode() { return 0; }
        };
    }

    private Request createMockRequestWithError(String method, String path, int errorCode) {
        return new Request() {
            @Override
            public String getMethod() { return method; }
            @Override
            public String getPath() { return path; }
            @Override
            public int getErrorCode() { return errorCode; }
        };
    }

    private void deleteDirectory(Path dir) throws IOException {
        Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException ignored) {
                    }
                });
    }
}
