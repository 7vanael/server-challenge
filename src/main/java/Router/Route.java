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
        System.out.println("Input method: '" + method + "'");
        System.out.println("Input path: '" + path + "'");
        System.out.println("Stored method: '" + this.method + "'");
        System.out.println("Stored pathPattern: '" + this.pathPattern + "'");
        System.out.println("Method comparison: '" + this.method + "'.equals('" + method.toUpperCase() + "') = " + this.method.equals(method.toUpperCase()));
        System.out.println("Path comparison: '" + this.pathPattern + "'.equals('" + path + "') = " + this.pathPattern.equals(path));


        if (!this.method.equals(method.toUpperCase())) {
            System.out.println("Method mismatch - returning false");

            return false;
        }

        if (this.pathPattern.equals(path)) {
            System.out.println("Exact Path match - returning true");

            return true;
        }

        if (this.pathPattern.endsWith("/*")) {
            String prefix = this.pathPattern.substring(0, this.pathPattern.length() - 2);
            System.out.println("Wildcard check: prefix='" + prefix + "', path.startsWith(prefix)=" + path.startsWith(prefix));
            if (path.startsWith(prefix)) {
                System.out.println("Wildcard match - returning true");
                return true;
            }
        }
        System.out.println("No match - returning false");

        return false;
    }

    public Response handle(Request request) throws IOException {
        return handler.handle(request);
    }
}

