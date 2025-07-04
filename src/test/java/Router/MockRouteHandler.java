package Router;

import Connection.RequestI;
import Connection.Response;
import Main.RouteHandler;

import java.io.IOException;

public class MockRouteHandler implements RouteHandler {
    private String responseBody;
    private boolean handleCalled = false;

    public MockRouteHandler(String responseBody){
        this.responseBody = responseBody;
    }

    @Override
    public Response handle(RequestI request) throws IOException {
        handleCalled = true;
        return new Response("Test Server", 200, "text/plain", responseBody);
    }

    public boolean wasHandleCalled(){
        return handleCalled;
    }
}
