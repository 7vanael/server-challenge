import org.example.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class ServerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp(){
        System.setOut(new PrintStream(outContent));
        Main.port = 80;
        Main.root = ".";
    }

    @AfterAll
    public static void tearDown(){
        System.setOut(originalOut);
    }

    @Test
    public void printsUsageWithoutStartingServer() {
        Main.main(new String[] {"-h"});
        String output = outContent.toString();
        assertTrue(output.contains("-p     Specify the port.  Default is 80."));
        assertTrue(output.contains("-r     Specify the root directory.  Default is the current working directory."));
        assertTrue(output.contains("-h     Print this help message"));
        assertTrue(output.contains("-x     Print the startup configuration without starting the server"));
    }

    @Test
    public void printsConfigWithoutStartingServer() {
        Main.main(new String[] {"-x"});
        String output = outContent.toString();
        assertTrue(output.contains("Challenge Server"));
        assertTrue(output.contains("Running on port: 80"));
        assertTrue(output.contains("Serving files from: " + new File(".").getAbsolutePath()));
    }

    @Test
    public void defaultPort80(){
        Main.main(new String[]{"-x"});
        String output = outContent.toString();
        String expected = "Running on port: 80";
        assertTrue(output.contains(expected));
    }

    @Test
    public void defaultRootCurrentDirectory(){
        Main.main(new String[]{"-x"});
        String output = outContent.toString();
        String expected = "Serving files from: " + new File(".").getAbsolutePath();
        assertTrue(output.contains(expected));
    }

    @Test
    public void portCanBeSetWithP(){
        Main.main(new String[]{"-p", "200", "-x"});
        String output = outContent.toString();
        String expected = "Running on port: 200";
        assertTrue(output.contains(expected));
    }

    @Test
    public void rootCanBeSetWithR(){
        Main.main(new String[]{"-r", "testroot", "-x"});
        String output = outContent.toString();
        String expected = "Serving files from: " + new File("testroot").getAbsolutePath();
        assertTrue(output.contains(expected));
    }
}
