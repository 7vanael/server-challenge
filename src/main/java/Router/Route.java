package Router;

import Connection.Request;
import Connection.Response;
import org.example.RouteHandler;

import java.io.IOException;

public class Route {
    private final String method;
    private final String pathPattern;
    private final RouteHandler handler;

    public Route(String method, String pathPattern, RouteHandler handler) {
        this.method = method.toUpperCase();
        this.pathPattern = pathPattern;
        this.handler = handler;
    }

    public boolean matches(String method, String path) {
        if (!this.method.equals(method.toUpperCase())) {
            return false;
        }

        if (this.pathPattern.equals(path)) {
            return true;
        }

        if (this.pathPattern.equals("/*")) {
            return true;
        }

        return false;
    }

    public Response handle(Request request) throws IOException, IOException {
        return handler.handle(request);
    }
}

