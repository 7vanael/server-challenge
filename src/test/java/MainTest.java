import org.example.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;

import static org.junit.Assert.assertTrue;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp(){
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
    public void printsUsageWithoutStartingServer() {
        Main.main(new String[] {"-h"});
        String output = outContent.toString();
        Assertions.assertTrue(output.contains("-p     Specify the port.  Default is 80."));
        Assertions.assertTrue(output.contains("-r     Specify the root directory.  Default is the current working directory."));
        Assertions.assertTrue(output.contains("-h     Print this help message"));
        Assertions.assertTrue(output.contains("-x     Print the startup configuration without starting the server"));
    }

    @Test
    public void printsConfigWithoutStartingServer() {
        Main.main(new String[] {"-x"});
        String output = outContent.toString();
        Assertions.assertTrue(output.contains("Challenge Server"));
        Assertions.assertTrue(output.contains("Running on port: 80"));
        Assertions.assertTrue(output.contains("Serving files from: " + new File(".").getAbsolutePath()));
    }

    @Test
    public void defaultPort80(){
        Main.main(new String[]{"-x"});
        String output = outContent.toString();
        String expected = "Running on port: 80";
        Assertions.assertTrue(output.contains(expected));
    }

    @Test
    public void defaultRootCurrentDirectory(){
        Main.main(new String[]{"-x"});
        String output = outContent.toString();
        String expected = "Serving files from: " + new File(".").getAbsolutePath();
        Assertions.assertTrue(output.contains(expected));
    }

    @Test
    public void portCanBeSetWithP(){
        Main.main(new String[]{"-p", "200", "-x"});
        String output = outContent.toString();
        String expected = "Running on port: 200";
        Assertions.assertTrue(output.contains(expected));
    }

    @Test
    public void rootCanBeSetWithR(){
        Main.main(new String[]{"-r", "testroot", "-x"});
        String output = outContent.toString();
        String expected = "Serving files from: " + new File("testroot").getAbsolutePath();
        Assertions.assertTrue(output.contains(expected));
    }

    @Test
    public void pFlagWithoutNumberPrintsInvalidPort(){
        String expected = "Invalid port";
        Main.main(new String[]{"-p", "*"});
        String output = outContent.toString();
        Assertions.assertTrue(output.contains(expected));
    }

    @Test
    public void pFlagWithDecimalNumberPrintsPortRange(){
        String expected = "Invalid port";
        Main.main(new String[]{"-p", "8.6"});
        Assertions.assertTrue(outContent.toString().contains(expected));
    }

    @Test
    public void pFlagWithoutArgumentPrintsUsage(){
        String expected = "-p     Specify the port.  Default is 80.";
        Main.main(new String[]{"-p"});
        String output = outContent.toString();
        Assertions.assertTrue(output.contains(expected));
    }

    @Test
    public void pFlagWithAnOccupiedPortReportsPortIsInUse() throws IOException {
        String expected = "Port already in use:";
        try(ServerSocket socket = new ServerSocket(0)){
            int occupiedPort = socket.getLocalPort();
            Main.main(new String[]{"-p", String.valueOf(occupiedPort)});
            String output = outContent.toString();
            Assertions.assertTrue(output.contains(expected));
        }
    }

    @Test
    public void pFlagWithNegativeNumberPrintsPortRange(){
        String expected = "Port must be between 1 and";
        Main.main(new String[]{"-p", "-4"});
        Assertions.assertTrue(outContent.toString().contains(expected));
    }

    @Test
    public void rFlagWithoutArgumentPrintsUsage(){
        String expected = "-r     Specify the root directory.  Default is the current working directory.";
        Main.main(new String[]{"-r"});
        Assertions.assertTrue(outContent.toString().contains(expected));
    }

    @Test
    public void rFlagWithEmptyArgumentPrintsUsage(){
        String expected = "-r     Specify the root directory.  Default is the current working directory.";
        Main.main(new String[]{"-r", ""});
        Assertions.assertTrue(outContent.toString().contains(expected));
    }

    @Test
    public void rFlagWithInvalidRootPrintsDirectoryDoesNotExist(){
        String expected = "Directory does not exist";
        Main.main(new String[]{"-r", "junk"});
        Assertions.assertTrue(outContent.toString().contains(expected));
    }

    @Test
    public void rFlagWithInvalidRootPrintsInvalidDirectory(){
        String expected = "Not a directory";
        Main.main(new String[]{"-r", "pom.xml"});
        Assertions.assertTrue(outContent.toString().contains(expected));
    }


}
