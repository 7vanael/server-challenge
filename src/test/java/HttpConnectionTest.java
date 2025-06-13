import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpConnectionTest {
    private TestConnectionFactory factory;
    private String rootDirectory = "testroot";
    private MockSocket mocket;
    private String target;

    @BeforeEach
    public void setUp() throws IOException {
        factory = new TestConnectionFactory();
    }
    @Test
    public void indexHtmlGETReturnsHello(){
        target = "index.html";
        String request = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(request);
        HttpConnection connection = factory.createConnection(mocket, rootDirectory);

        connection.run();

        String[] response = mocket.getResponse().split("\r\n\r\n");
        String[] header = response[0].split("\n");
        String[] status = header[0].split(" ");
        String[] body = response[1].split("\n");

        Assertions.assertTrue(status[0].equals("HTTP/1.1"));
        Assertions.assertTrue(status[1].equals("200"));
        Assertions.assertTrue(status[2].equals("OK"));
        Assertions.assertTrue(response[1].contains("Hello, World!"));
    }

    @Test
    public void almostEmptyGETReturnsHello(){
        target = "/";
        String request = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(request);
        HttpConnection connection = factory.createConnection(mocket, rootDirectory);

        connection.run();

        String[] response = mocket.getResponse().split("\r\n\r\n");
        String[] header = response[0].split("\n");
        String[] status = header[0].split(" ");
        String[] body = response[1].split("\n");

        Assertions.assertTrue(status[0].equals("HTTP/1.1"));
        Assertions.assertTrue(status[1].equals("200"));
        Assertions.assertTrue(status[2].equals("OK"));
        Assertions.assertTrue(response[1].contains("Hello, World!"));
    }

    @Test
    public void emptyGETReturnsHello(){
        target = "";
        String request = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(request);
        HttpConnection connection = factory.createConnection(mocket, rootDirectory);

        connection.run();

        String[] response = mocket.getResponse().split("\r\n\r\n");
        String[] header = response[0].split("\n");
        String[] status = header[0].split(" ");
        String[] body = response[1].split("\n");

        Assertions.assertTrue(status[0].equals("HTTP/1.1"));
        Assertions.assertTrue(status[1].equals("200"));
        Assertions.assertTrue(status[2].equals("OK"));
        Assertions.assertTrue(response[1].contains("Hello, World!"));
    }

@Test
    public void invalidGETReturnsError(){
        target = "junk";
        String request = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(request);
        HttpConnection connection = factory.createConnection(mocket, rootDirectory);

        connection.run();

        String[] response = mocket.getResponse().split("\r\n\r\n");
        String[] header = response[0].split("\n");
        String[] status = header[0].split(" ");

        Assertions.assertTrue(status[0].equals("HTTP/1.1"));
        Assertions.assertTrue(status[1].equals("404"));
        Assertions.assertTrue(status[2].equals("Not-Found"));
    }
}
