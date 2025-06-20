package Connection;

import Router.Router;
import Router.HomeHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class HttpConnectionTest {
    private TestConnectionFactory factory;
    private String rootDirectory = "testroot";
    private String serverName = "Challenge Server";
    private MockSocket mocket;
    private Router router;
    private String target;
    private HttpConnection connection;

    @BeforeEach
    public void setUp(){
        factory = new TestConnectionFactory();
        router = new Router(serverName);
        router.addRoute("GET", "/", new HomeHandler(Paths.get(rootDirectory), serverName));
        router.addRoute("GET", "index.html", new HomeHandler(Paths.get(rootDirectory), serverName));
        router.addRoute("GET", "/index.html", new HomeHandler(Paths.get(rootDirectory), serverName));
    }

    @Test
    public void testValidGetRequest() throws IOException {
        String httpRequest = "GET index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        mocket = new MockSocket(httpRequest);
        connection = factory.createConnection(mocket, rootDirectory, router);
        connection.run();

        String response = mocket.getResponse();
        Assertions.assertTrue(response.contains("200"));
        Assertions.assertTrue(response.contains("Hello, World!"));
        Assertions.assertTrue(mocket.isClosed());
    }

    @Test
    public void testReturns404ForUnknownRoute() throws IOException {
        String httpRequest = "GET /unknown HTTP/1.1\r\nHost: localhost\r\n\r\n";
        mocket = new MockSocket(httpRequest);
        connection = factory.createConnection(mocket, rootDirectory, router);
        connection.run();

        String response = mocket.getResponse();
        Assertions.assertTrue(response.contains("HTTP/1.1 404"));
        Assertions.assertTrue(response.contains("Not Found"));
    }

    @Test
    public void almostEmptyGETReturnsHello() {
        String httpRequest = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        mocket = new MockSocket(httpRequest);
        connection = factory.createConnection(mocket, rootDirectory, router);
        connection.run();

        String response = mocket.getResponse();
        Assertions.assertTrue(response.contains("200"));
        Assertions.assertTrue(response.contains("Hello, World!"));
        Assertions.assertTrue(mocket.isClosed());
    }

    @Test
    public void emptyGETReturnsHello() {
        String request = "GET  HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(request);
        connection = factory.createConnection(mocket, rootDirectory, router);
        connection.run();

        String response = mocket.getResponse();
        Assertions.assertTrue(response.contains("200"));
        Assertions.assertTrue(response.contains("Hello, World!"));
        Assertions.assertTrue(mocket.isClosed());
    }

    @Test
    public void invalidGETReturnsError() {
        target = "junk";
        String request = "GET " + target + " HTTP/1.1\r\nHost: localhost\r\n";
        mocket = new MockSocket(request);
        connection = factory.createConnection(mocket, rootDirectory, router);
        connection.run();

        String response = mocket.getResponse();
        Assertions.assertTrue(response.contains("HTTP/1.1 404"));
        Assertions.assertTrue(response.contains("Not Found"));
    }
}