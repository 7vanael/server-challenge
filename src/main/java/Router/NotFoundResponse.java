package Router;

import Connection.Response;

public class NotFoundResponse{

    public static Response createResponse(String serverName) {
        String errorHtml = "<html><head><title>404 Not Found</title></head>" +
                "<body><h1>404 Not Found</h1>" +
                "<p>Server: " + serverName + "</p></body></html>";
        return new Response(serverName, 404, "text/html", errorHtml)
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(errorHtml.getBytes().length));
    }
}
