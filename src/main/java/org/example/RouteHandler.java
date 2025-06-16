package org.example;

import Connection.Request;
import Connection.Response;

import java.io.IOException;

public interface RouteHandler {
    Response handle(Request request) throws IOException;
}
