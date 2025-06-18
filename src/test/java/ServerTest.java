import org.example.ConnectionFactory;
import org.example.Main;
import Router.Router;
import org.example.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ServerTest {

    private Server server;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private Router router;
    private ConnectionFactory connectionFactory;

    @BeforeEach
    public void setUp(){
        //problem: This is a real connection. How will we make it fake?
        outContent.reset();
        System.setOut(new PrintStream(outContent));
        Main.port = 80;
        Main.root = ".";
        //Is a mock router enough to be fake?
        router = new Router(Main.name);
    }

    @AfterAll
    public static void tearDown(){
        System.setOut(originalOut);
    }

    @Test
    public void serverIsInitializedWithPortAndRoot() throws IOException {
        Server server = new Server(Main.port, Main.root, router);
        server.startServer();
        String result = outContent.toString();
        server.stopServer();
        Assertions.assertTrue(result.contains("org.example.Server constructed"));
        Assertions.assertTrue(result.contains("port: 80"));
        Assertions.assertTrue(result.contains("root: testroot"));
    }

//    @Test
//    public void startServerStartsTheServer() throws IOException {
//        org.example.Server server = new org.example.Server(org.example.Main.port, org.example.Main.root);
//        server.startServer();
//        String result = outContent.toString();
//        server.stopServer();
//        Assertions.assertTrue(result.contains("org.example.Server socket initialized"));
//    }

}
