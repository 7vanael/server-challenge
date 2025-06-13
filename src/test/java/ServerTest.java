import org.example.Main;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ServerTest {

    private Server server;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp(){
        //problem: This is a real connection. How will we make it fake?
        outContent.reset();
        System.setOut(new PrintStream(outContent));
        Main.port = 80;
        Main.root = ".";
    }

    @AfterAll
    public static void tearDown(){
        System.setOut(originalOut);
    }

    @Test
    public void serverIsInitializedWithPortAndRoot(){
        Server server = new Server(Main.port, Main.root);
        String result = outContent.toString();
        Assertions.assertTrue(result.contains("Server constructed"));
        Assertions.assertTrue(result.contains("port: 80"));
        Assertions.assertTrue(result.contains("root: testroot"));
    }

//    once we have a connection, then we can maybe use a factory for tracking the connection?
//    So, when we start the serversocket, a factory should be fed in. (must feed in factory)

//    When recieving a connection, accept all requests. (no authorization)
//    track the connections.. if you're multithreaded, how to tell where the responses should go
//    ID for each connection made by the connection factory?


//    Router to point connection at the target & perform desired task.

//    @Test
//    public void startServerStartsTheServer() throws IOException {
//        Server server = new Server(Main.port, Main.root);
//        server.startServer();
//        String result = outContent.toString();
//        server.stopServer();
//        Assertions.assertTrue(result.contains("Server socket initialized"));
//    }

}
