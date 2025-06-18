package Router;

import Connection.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotFoundResponseTest {
    @Test
    public void notFoundProvides404ErrorResponse(){
        Response response = NotFoundResponse.createResponse("Test Server");

        String body = new String(response.getBody());
        assertEquals(404, response.getStatusCode());
        assertEquals("text/html", response.getHeaders().get("Content-Type"));
        assertTrue(!body.isEmpty());
        Assertions.assertNotNull(response.getHeaders().get("Content-Length"));
        assertTrue(body.contains("404 Not Found"));
    }
}
