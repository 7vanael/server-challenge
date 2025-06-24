package Router;

import Connection.Request;
import Connection.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PingHandlerTest {

    private String serverName = "Test Server";
    private Path rootPath = Paths.get("testroot");
    private PingHandler handler = new PingHandler(rootPath, serverName);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Test
    public void pingWithoutArgumentImmediatelyReturns() throws IOException {
    Request request = createMockRequest("GET", "/ping");

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("<h2>Ping</h2>"));
        assertTrue(body.contains("<li>start time: " + startTime.format(formatter) + "</li>"));
        assertTrue(body.contains("<li>end time: " + endTime.format(formatter) + "</li>"));
    }

    @Test
    public void pingWithArgumentDelays() throws IOException {
    Request request = createMockRequestWithSegment("GET", "/ping", "1");

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(1);
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("<h2>Ping</h2>"));
        assertTrue(body.contains("<li>start time: " + startTime.format(formatter) + "</li>"));
        assertTrue(body.contains("<li>end time: " + endTime.format(formatter) + "</li>"));
    }

    @Test
    public void pingWithArgumentDelaysVariesWithSegment() throws IOException {
    Request request = createMockRequestWithSegment("GET", "/ping", "2");

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(2);
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("<h2>Ping</h2>"));
        assertTrue(body.contains("<li>start time: " + startTime.format(formatter) + "</li>"));
        assertTrue(body.contains("<li>end time: " + endTime.format(formatter) + "</li>"));
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

    private Request createMockRequestWithSegment(String method, String path, String segment) {
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
            public String getSegment(){
                return segment;
            }

            @Override
            public int getErrorCode() {
                return 0;
            }
        };
    }
}
