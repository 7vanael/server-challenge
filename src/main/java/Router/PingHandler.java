package Router;

import Connection.RequestI;
import Connection.Response;
import Main.RouteHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PingHandler implements RouteHandler {

    private Path rootPath;
    private String serverName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PingHandler(Path rootPath, String serverName) {
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    @Override
    public Response handle(RequestI request) throws IOException {
        startTime = LocalDateTime.now();
        String delay = request.getSegment();
        try {
            int seconds = Integer.parseInt(delay);
            Thread.sleep(1000L * seconds);
        } catch (NumberFormatException e) {
            System.out.println("Segment not a number; unable to sleep");
        } catch (InterruptedException e){
            System.out.println("Interruption occurred during sleep: " + e.getMessage());
        }
        endTime = LocalDateTime.now();

        String pingHtml = "<html><head><title>Ping</title></head>" +
                "<body><h2>Ping</h2>" +
                "<li>start time: " + startTime.format(formatter) + "</li>" +
                "<li>end time: " + endTime.format(formatter) + "</li></body></html>";
        String contentType = "text/html";

        return new Response(serverName, 200, contentType, pingHtml);
    }
}
