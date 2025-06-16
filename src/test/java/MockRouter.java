import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MockRouter extends Router{
    private final Map<String, Response> mockResponses;
    private final String serverName;

    public MockRouter(Path rootPath, String serverName) {
        super(null, serverName); // No rootPath since we're mocking
        this.serverName = serverName;
        this.mockResponses = new HashMap<>();
        setupMockResponses();
    }

    @Override
    public Response route(Request request) {
        if (request.getErrorCode() != 0) {
            return createErrorResponse(request.getErrorCode());
        }
        String path = request.getPath();
        return mockResponses.getOrDefault(path, createErrorResponse(404));
    }

    private void setupMockResponses() {
        String helloHtml = "<html><body>Hello, World!</body></html>";
        Response successResponse = new Response(serverName, 200, "text/html", helloHtml)
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(helloHtml.getBytes().length));

        mockResponses.put("index.html", successResponse);
        mockResponses.put("", successResponse);
    }

    public void addMockResponse(String path, Response response) {
        mockResponses.put(path, response);
    }

    private Response createErrorResponse(int statusCode) {
        String errorHtml = "<html><body><h1>" + statusCode + " Error</h1></body></html>";
        return new Response(serverName, statusCode, "text/html", errorHtml)
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(errorHtml.getBytes().length));
    }
}
