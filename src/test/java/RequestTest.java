import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestTest {
    private MockSocket mocket;
    private BufferedReader in;
    private Path rootpath = Paths.get("testroot");

    private String target;
    private Request request;

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

        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals("index.html", request.getPath());
        Assertions.assertEquals("HTTP/1.1", request.getProtocol());
        Assertions.assertEquals(0, request.getErrorCode());
    }

    @Test
    public void emptyGETParsesIntoParts() throws IOException{
        target = "";
        processGetRequest();

        Assertions.assertEquals("GET", request.getMethod());
        Assertions.assertEquals("index.html", request.getPath());
        Assertions.assertEquals("HTTP/1.1", request.getProtocol());
        Assertions.assertEquals(0, request.getErrorCode());
    }

    @Test
    public void invalidGETReturns404Error() throws IOException {
        target = "junk";
        processGetRequest();
        Assertions.assertEquals(404, request.getErrorCode());
    }

    @Test
    public void invalidGETPathReturnsForbiden403Error() throws IOException {
        target = "../..";
        processGetRequest();
        Assertions.assertEquals(403, request.getErrorCode());
    }

    @Test
    public void invalidRequestFormatReturns400Error() throws IOException {
        target = "";
        String clientMessage = "Getter  Get" + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(clientMessage);
        in = new BufferedReader(new InputStreamReader(mocket.getInputStream()));
        request = new Request(in, rootpath);
        Assertions.assertEquals(400, request.getErrorCode());
    }


        private void processGetRequest() throws IOException {
        String clientMessage = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(clientMessage);
        in = new BufferedReader(new InputStreamReader(mocket.getInputStream()));
        request = new Request(in, rootpath);
    }
}
