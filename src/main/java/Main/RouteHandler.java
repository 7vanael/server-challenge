package Main;

import Connection.RequestI;
import Connection.Response;

import java.io.IOException;

public interface RouteHandler {
    Response handle(RequestI request) throws IOException;
}
