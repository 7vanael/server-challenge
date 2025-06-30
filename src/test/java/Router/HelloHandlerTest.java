package Router;

import Connection.Request;
import Connection.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HelloHandlerTest {
    private String serverName = "Test Server";
    private Path rootPath = Paths.get("testroot");
    private HelloHandler handler = new HelloHandler(rootPath, serverName);

    @Test
    public void routeGetValidFileReturns200() throws IOException {
        Request request = createMockRequest("GET", "/hello");

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("text/html", response.getContentType());
        Assertions.assertTrue(body.contains("Hello, Welcome!"));
    }

    @Test
    public void successfulResponseIncludesHeaders() throws IOException {
        Request request = createMockRequest("GET", "/hello");

        Response response = handler.handle(request);

        Assertions.assertEquals("text/html", response.getHeaders().get("Content-Type"));
        Assertions.assertNotNull(response.getHeaders().get("Content-Length"));
        Assertions.assertEquals(serverName, response.getHeaders().get("Server"));
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
