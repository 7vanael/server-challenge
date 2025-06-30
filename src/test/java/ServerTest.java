import Main.ConnectionFactory;
import Router.Router;
import Main.Server;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Router.HomeHandler;
import Router.HelloHandler;

import static org.junit.jupiter.api.Assertions.*;


public class ServerTest {

    private Server server;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private Router router;
    private ConnectionFactory connectionFactory;
    private int port = 80;
    private String root = "testroot";

    @BeforeEach
    public void setUp() throws InterruptedException {
        router = new Router(root);
        Path rootPath = Paths.get(root);
        router.addRoute("GET", "/", new HomeHandler(rootPath, root));
        router.addRoute("GET", "/index.html", new HomeHandler(rootPath, root));
        router.addRoute("GET", "/hello", new HelloHandler(rootPath, root));

        server = new Server(port, root, router);
        server.startServer();
        Thread.sleep(500);
    }

    @AfterEach
    public void after() throws IOException {
        if (server != null) {
            server.stopServer();
        }
    }

    private String makeRequest(String requestPath) throws IOException, InterruptedException {
        Socket requestSocket = new Socket(InetAddress.getLocalHost(), port);

        String request = "GET " + requestPath + " HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        OutputStream out = requestSocket.getOutputStream();
        out.write(request.getBytes(StandardCharsets.UTF_8));
        out.flush();

        Thread.sleep(200);

        BufferedReader in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            result.append(line).append("\r\n");
        }

        requestSocket.close();
        return result.toString();
    }

    @Test
    public void startServerStarts() throws IOException, InterruptedException {
        String result = makeRequest("/index.html");

        assertTrue(result.contains("Hello, World!"));
        assertTrue(result.contains("200 OK"));
    }

    @Test
    public void testRootPathReturnsIndex() throws IOException, InterruptedException {
        String response = makeRequest("/");

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Hello, World!"));
    }

    @Test
    public void testHelloEndpoint() throws IOException, InterruptedException {
        String response = makeRequest("/hello");

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("text/html"));
        assertTrue(response.contains("Hello, Welcome!"));
    }

    @Test
    public void testNotFoundReturns404() throws IOException, InterruptedException {
        String response = makeRequest("/nonexistent");

        assertTrue(response.contains("404"));
        assertTrue(response.contains("Not Found"));
    }

    @Test
    public void testMultipleConcurrentRequests() throws IOException, InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            futures.add(executor.submit(() -> {
                try {
                    return makeRequest("/index.html");
                } catch (Exception e) {
                    return "ERROR: " + e.getMessage();
                }
            }));
        }

        for (Future<String> future : futures) {
            String response = future.get();
            assertFalse(response.startsWith("ERROR"));
            assertTrue(response.contains("Hello, World!"));
        }

        executor.shutdown();
    }

}


