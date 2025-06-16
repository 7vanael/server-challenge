package Router;

import Connection.Request;
import Connection.Response;
import org.example.*;

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

    public List<Route> getRoutes(){
        return routes;
    }

    public Response route(Request request) {
        if (request.getErrorCode() != 0) {
            return createErrorResponse(request.getErrorCode());
        }
        System.out.println("In router, matching request: ");
        System.out.println("Request method: " + request.getMethod());
        System.out.println("Request path: " + request.getPath());
        System.out.println(routes);
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
//    public Response route(Request request) {
//        if (request.getErrorCode() != 0) {
//            return createErrorResponse(request.getErrorCode());
//        }
//        switch (request.getMethod()) {
//            case "GET":
//                return handleGet(request);
//            case "POST":
//                return createErrorResponse(501);
//            default:
//                return createErrorResponse(405);
//        }
//    }
//
//    private Response handleGet(Request request) {
//        try {
//            System.out.println("Handling get:");
//            //adds the rootpath to the back of the request path
//            Path targetPath = rootPath.resolve(request.getPath()).normalize();
//            System.out.println("request.getPath(): " + request.getPath());
//            System.out.println("targetpath: ");
//            System.out.println(targetPath);
//            System.out.println();
//
//            if (!Files.exists(targetPath)) {
//                return createErrorResponse(404);
//            }
////            if (Files.isDirectory(targetPath)) {
////                return handleDirectoryListing(request);
////            } else
////                if (Files.isRegularFile(targetPath)) {
////                return handleFileServing(targetPath);
////            } else {
////                return createErrorResponse(404);
////            }
//            return handleFileServing(targetPath);
//        } catch (IOException e) {
//            return createErrorResponse(500);
//        }
//    }

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
                "<p>org.example.Server: " + serverName + "</p></body></html>";
    }


}
