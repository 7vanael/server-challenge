import Connection.Request;
import org.example.ConnectionFactory;
import org.example.Main;
import Router.Router;
import org.example.Server;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import Router.HomeHandler;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerTest {

    private Server server;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private Router router;
    private ConnectionFactory connectionFactory;

    @BeforeEach
    public void setUp() {
        //problem: This is a real connection. How will we make it fake?
//        outContent.reset();
//        System.setOut(new PrintStream(outContent));
//        Main.port = 80;
//        Main.root = "testroot";
//        //Is a mock router enough to be fake?
//        router = new Router(Main.name);
//        Server server = new Server(Main.port, Main.root, router);
    }

//    @AfterEach
//    public void after() throws IOException {
//        this.server.stopServer();
//    }

    @AfterAll
    public static void tearDown() {
        System.setOut(originalOut);
    }


    @Test
    public void startServerStarts() throws IOException, InterruptedException {
        String name = "testroot";
        Router router = new Router(name);
        Path rootPath = Paths.get(name);
        router.addRoute("GET", "/index.html", new HomeHandler(rootPath, name));
        Server server = new Server(80, name, router);
        server.startServer();
        Thread.sleep(500);
        while(!server.isAcceptingConnections()){
            Thread.sleep(10);
        }
        Socket requestSocket = new Socket(InetAddress.getLocalHost(), 80);
        String request = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" + "\r\n";

        OutputStream out = requestSocket.getOutputStream();
        out.write(request.getBytes(StandardCharsets.UTF_8));
        out.flush();
        System.out.println("request sent from test");
        Thread.sleep(1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            result.append(line).append("\r\n");
        }
        System.out.println("Response recieved: " + result);

        assertTrue(result.toString().contains("Hello, World!"));
        requestSocket.close();
        server.stopServer();
    }
//    @Test
//    public void serverIsInitializedWithPortAndRoot() throws IOException {
//
//        server.startServer();
//        String result = outContent.toString();
//        server.stopServer();
//        Assertions.assertTrue(result.contains("org.example.Server constructed"));
//        Assertions.assertTrue(result.contains("port: 80"));
//        Assertions.assertTrue(result.contains("root: testroot"));
//    }

//    @Test
//    public void startServerStartsTheServer() throws IOException {
//        org.example.Server server = new org.example.Server(org.example.Main.port, org.example.Main.root);
//        server.startServer();
//        String result = outContent.toString();
//        server.stopServer();
//        Assertions.assertTrue(result.contains("org.example.Server socket initialized"));
//    }
}


