package Router;

import Connection.Request;
import Connection.Response;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RouterTest {
    private Router router;
    private String serverName = "TestServer";

    @BeforeEach
    public void setUp() throws IOException {
        router = new Router(serverName);
    }

    @Test
    public void testConstructor() {
        assertNotNull(router.getRoutes());
        assertTrue(router.getRoutes().isEmpty());
    }

    @Test
    public void addRouteAddsToRoutes() {
        MockRouteHandler handler = new MockRouteHandler("Test Response");
        router.addRoute("GET", "/test", handler);
        Assertions.assertEquals(1, router.getRoutes().size());
        Assert.assertNotNull(router.getRoutes().get(0));
    }

    @Test
    public void addRouteMultipleTimesAddsMultiplesToRoutes() {
        MockRouteHandler handler = new MockRouteHandler("Test Response");
        router.addRoute("GET", "/test", handler);
        router.addRoute("GET", "/img", handler);
        router.addRoute("POST", "/guess", handler);
        Assertions.assertEquals(3, router.getRoutes().size());
    }

    @Test
    public void anAddedRouteCanBeMatchedToARequest() {
        MockRouteHandler handler = new MockRouteHandler("Test Response");
        router.addRoute("GET", "/test", handler);
        Request request = createMockRequest("GET", "/test");

        Response response = router.route(request);

        Assertions.assertEquals("Test Response", new String(response.getBody()));
        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertTrue(handler.wasHandleCalled());
    }

    @Test
    public void aRequestWithAnErrorGetsAnErrorResponseWithoutCallingHandler() {
        MockRouteHandler handler = new MockRouteHandler("Test Response");
        router.addRoute("GET", "/test", handler);
        Request request = createMockRequestWithError("GET", "/test", 400);

        Response response = router.route(request);

        Assertions.assertTrue(new String(response.getBody()).contains("400"));
        Assertions.assertEquals(400, response.getStatusCode());
        Assertions.assertFalse(handler.wasHandleCalled());
    }

    @Test
    public void aRequestWithoutMatchingHandlerGetsError404() {
        MockRouteHandler handler = new MockRouteHandler("Test Response");
        router.addRoute("GET", "/test", handler);
        Request request = createMockRequest("GET", "/junk");

        Response response = router.route(request);

        Assertions.assertTrue(new String(response.getBody()).contains("404"));
        Assertions.assertEquals(404, response.getStatusCode());
        Assertions.assertFalse(handler.wasHandleCalled());
    }

    private Request createMockRequest(String method, String path) {
        return new Request() {
            @Override
            public String getMethod() {
                return method;
            }

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public int getErrorCode() {
                return 0;
            }
        };
    }

    private Request createMockRequestWithError(String method, String path, int errorCode) {
        return new Request() {
            @Override
            public String getMethod() {
                return method;
            }

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public int getErrorCode() {
                return errorCode;
            }
        };
    }
}
