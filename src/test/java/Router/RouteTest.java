package Router;

import Connection.Request;
import Connection.Request;
import Connection.Response;
import Main.RouteHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public class RouteTest {

    private MockRouteHandler handler = new MockRouteHandler("Test Response");

    @Test
    public void testConstructorStoresMethodAsUppercase() {
        Route route = new Route("get", "/test", handler);

        Assertions.assertTrue(route.matches("GET", "/test"));
        Assertions.assertTrue(route.matches("get", "/test"));
    }

    @Test
    public void testMatchesExactPath() {
        Route route = new Route("GET", "/home", handler);

        Assertions.assertTrue(route.matches("GET", "/home"));
        Assertions.assertFalse(route.matches("GET", "/other"));
    }

    @Test
    public void testMatchesMethodCaseInsensitive() {
        Route route = new Route("POST", "/submit", handler);

        Assertions.assertTrue(route.matches("POST", "/submit"));
        Assertions.assertTrue(route.matches("post", "/submit"));
        Assertions.assertTrue(route.matches("Post", "/submit"));
        Assertions.assertTrue(route.matches("pOsT", "/submit"));
    }

    @Test
    public void testMatchesWildcardPath() {
        Route route = new Route("GET", "/*", handler);

        Assertions.assertTrue(route.matches("GET", "/anything"));
        Assertions.assertTrue(route.matches("GET", "/home"));
        Assertions.assertTrue(route.matches("GET", "/"));
        Assertions.assertTrue(route.matches("GET", "test"));
    }

    @Test
    public void testDoesNotMatchDifferentPath() {
        Route route = new Route("GET", "/exact", handler);

        Assertions.assertFalse(route.matches("GET", "/different"));
        Assertions.assertFalse(route.matches("GET", "/exact/sub"));
        Assertions.assertFalse(route.matches("GET", "/exac"));
        Assertions.assertFalse(route.matches("GET", "exact")); // Missing slash
    }

    @Test
    public void testHandleCallsRouteHandler() throws IOException {
        Route route = new Route("GET", "/test", handler);
        Request request = createMockRequest("GET", "/test");

        Response response = route.handle(request);

        Assertions.assertEquals("Test Response", new String(response.getBody()));
        Assertions.assertTrue(handler.wasHandleCalled());
    }

    @Test
    public void testHandleThrowsIOExceptionWhenHandlerThrows() {
        ThrowingRouteHandler throwingHandler = new ThrowingRouteHandler();
        Route route = new Route("GET", "/test", throwingHandler);
        Request request = createMockRequest("GET", "/test");

        Assertions.assertThrows(IOException.class, () -> {
            route.handle(request);
        });
    }

    @Test
    public void testSpecialCharactersInPath() {
        Route route = new Route("GET", "/users/123", handler);

        Assertions.assertTrue(route.matches("GET", "/users/123"));
        Assertions.assertFalse(route.matches("GET", "/users/456"));
    }

    @Test
    public void testEmptyPath() {
        Route route = new Route("GET", "", handler);

        Assertions.assertTrue(route.matches("GET", ""));
        Assertions.assertFalse(route.matches("GET", "/"));
    }

    @Test
    public void testRootPath() {
        Route route = new Route("GET", "/", handler);

        Assertions.assertTrue(route.matches("GET", "/"));
        Assertions.assertFalse(route.matches("GET", ""));
        Assertions.assertFalse(route.matches("GET", "/home"));
    }

    @Test
    public void testPathWithoutLeadingSlash() {
        Route route = new Route("GET", "index.html", handler);

        Assertions.assertTrue(route.matches("GET", "index.html"));
        Assertions.assertFalse(route.matches("GET", "/index.html"));
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

    private static class ThrowingRouteHandler implements RouteHandler {
        @Override
        public Response handle(Request request) throws IOException {
            throw new IOException("Test exception");
        }
    }
}
