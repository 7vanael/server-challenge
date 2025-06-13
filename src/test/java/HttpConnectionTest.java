import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpConnectionTest {
    private TestConnectionFactory factory;
    private String rootDirectory = "testroot";
    private String target;

    @BeforeEach
    public void setUp() throws IOException {
        factory = new TestConnectionFactory();
    }
    @Test
    public void emptyGETReturnsHello(){
        target = "/";
        String request = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        MockSocket socket = new MockSocket(request);
        HttpConnection connection = factory.createConnection(socket, rootDirectory);

        connection.run();

        String[] response = socket.getResponse().split("\r\n\r\n");
        String[] header = response[0].split("\n");
        String[] status = header[0].split(" ");
        String[] body = response[1].split("\n");

        Assertions.assertTrue(status[0].equals("HTTP/1.1"));
        Assertions.assertTrue(status[1].equals("200"));
        Assertions.assertTrue(status[2].equals("OK"));
        Assertions.assertTrue(response[1].contains("Hello, World!"));
    }
}
