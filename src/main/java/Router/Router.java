package Router;

import Connection.Request;
import Connection.Response;
import Main.HttpConstants;
import Main.RouteHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Router {
    private final String serverName;
    private final List<Route> routes;

    public Router(String serverName) {
        this.routes = new ArrayList<>();
        this.serverName = serverName;
    }

    public void addRoute(String method, String pathPattern, RouteHandler handler) {
        routes.add(new Route(method, pathPattern, handler));
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public Response route(Request request) {
        if (request.getErrorCode() != 0) {
            return createErrorResponse(request.getErrorCode());
        }

        for (Route route : routes) {
            if (route.matches(request.getMethod(), request.getPath())) {
                try {
                    return route.handle(request);
                } catch (IOException e) {
                    return createErrorResponse(500);
                }
            }
        }
        return createErrorResponse(404);
    }

    private Response createErrorResponse(int statusCode) {
        String errorHtml = generateErrorPage(statusCode);
        return new Response(serverName, statusCode, "text/html", errorHtml)
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(errorHtml.getBytes().length));
    }

    private String generateErrorPage(int statusCode) {
        String statusText = HttpConstants.STATUS_CODES.get(statusCode);
        return "<html><head><title>" + statusCode + " " + statusText + "</title></head>" +
                "<body><h1>" + statusCode + " " + statusText + "</h1>" +
                "Server: " + serverName + "</p></body></html>";
    }
}
