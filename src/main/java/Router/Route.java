package Router;

import Connection.RequestI;
import Connection.Response;
import Main.RouteHandler;

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

        if (this.pathPattern.endsWith("/*")) {
            String prefix = this.pathPattern.substring(0, this.pathPattern.length() - 2);
            return path.startsWith(prefix);
        }
        return false;
    }

    public Response handle(RequestI request) throws IOException {
        return handler.handle(request);
    }
}

