package Connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseTest {
    private Response response;
    private String serverName = "Challenge Test";

    @BeforeEach
    public void setUp() {
        response = null;
    }

    @Test
    public void defaultConstructorSetsCorrectDefaults() {
        response = new Response(serverName);

        assertEquals(200, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
        assertEquals(0, response.getBody().length);
        assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    public void stringBodyConstructorSetsAllFields() {
        String body = "<html><body>Hello World</body></html>";
        response = new Response(serverName, 200, "text/html", body);

        assertEquals(200, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
        Assertions.assertArrayEquals(body.getBytes(), response.getBody());
        assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    public void byteArrayConstructorSetsAllFields() {
        byte[] body = "Some File Content Here".getBytes();
        response = new Response(serverName, 200, "application/octet-stream", body);

        assertEquals(200, response.getStatusCode());
        assertEquals("application/octet-stream", response.getContentType());
        Assertions.assertArrayEquals(body, response.getBody());
        assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    public void addHeaderReturnsSameObjectWithUpdatedField() {
        response = new Response(serverName);
        Response result = response.addHeader("Cache-Control", "no-cache");

        Assertions.assertSame(response, result);
        assertEquals("no-cache", response.getHeaders().get("Cache-Control"));
    }

    @Test
    public void multipleHeadersCanBeAdded() {
        response = new Response(serverName);
        response.addHeader("Header1", "value1")
                .addHeader("Header2", "value2")
                .addHeader("Header3", "value3");

        Map<String, String> headers = response.getHeaders();
        assertEquals(3, headers.size());
        assertEquals("value1", headers.get("Header1"));
        assertEquals("value2", headers.get("Header2"));
        assertEquals("value3", headers.get("Header3"));
    }

    @Test
    public void getBodyReturnsCopyNotOriginalModifiableObject() {
        byte[] originalBody = "Original content".getBytes();

        response = new Response(serverName, 200, "text/plain", originalBody);

        byte[] retrievedBody = response.getBody();
        retrievedBody[0] = 'X';

        Assertions.assertArrayEquals(originalBody, response.getBody());
    }

    @Test
    public void getHeadersReturnsCopyNotOriginalModifiableObject() {
        response = new Response(serverName);
        response.addHeader("Test-Header", "test-value");

        Map<String, String> retrievedHeaders = response.getHeaders();
        retrievedHeaders.put("Malicious-Header", "malicious-value");

        assertEquals(1, response.getHeaders().size());
        Assertions.assertNull(response.getHeaders().get("Malicious-Header"));
    }

    @Test
    public void cookiesCanBeAddedAndRetrieved(){
        response = new Response(serverName);
        response.addCookie("target=42");
        assertTrue(response.getCookies().contains("target=42"));
    }

    @Test
    public void multipleCookiesCanBeAddedAndChained(){
        response = new Response(serverName);
        response.addCookie("target=42")
                .addCookie("guess1=37");
        assertTrue(response.getCookies().contains("target=42"));
        assertTrue(response.getCookies().contains("guess1=37"));
    }

}
