package Router;

import Connection.Request;
import Connection.Response;
import org.example.RouteHandler;

import java.io.IOException;

public class MockRouteHandler implements RouteHandler {
    private String responseBody;
    private boolean handleCalled = false;

    public MockRouteHandler(String responseBody){
        this.responseBody = responseBody;
    }
    @Override
    public Response handle(Request request) throws IOException {
        handleCalled = true;
        return new Response("TestServer", 200, "text/plain", responseBody);
    }

    public boolean wasHandleCalled(){
        return handleCalled;
    }
}
