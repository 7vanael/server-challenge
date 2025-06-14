import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("text/html", response.getContentType());
        Assertions.assertEquals(0, response.getBody().length);
        Assertions.assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    public void stringBodyConstructorSetsAllFields() {
        String body = "<html><body>Hello World</body></html>";
        response = new Response(serverName, 200, "text/html", body);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("text/html", response.getContentType());
        Assertions.assertArrayEquals(body.getBytes(), response.getBody());
        Assertions.assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    public void byteArrayConstructorSetsAllFields() {
        byte[] body = "Some File Content Here".getBytes();
        response = new Response(serverName, 200, "application/octet-stream", body);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("application/octet-stream", response.getContentType());
        Assertions.assertArrayEquals(body, response.getBody());
        Assertions.assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    public void setStatusCodeReturnsSameObjectWithUpdatedField() {
        response = new Response(serverName);
        Response result = response.setStatusCode(500);

        Assertions.assertSame(response, result);
        Assertions.assertEquals(500, response.getStatusCode());
    }

    @Test
    public void setContentTypeReturnsSameObjectWithUpdatedField() {
        response = new Response(serverName);
        Response result = response.setContentType("application/json");

        Assertions.assertSame(response, result);
        Assertions.assertEquals("application/json", response.getContentType());
    }

    @Test
    public void setStringBodyReturnsSameObjectWithUpdatedField() {
        response = new Response(serverName);
        String testBody = "Test content";
        Response result = response.setBody(testBody);

        Assertions.assertSame(response, result);
        Assertions.assertArrayEquals(testBody.getBytes(), response.getBody());
    }

    @Test
    public void setByteArrayBodyReturnsSameObjectWithUpdatedField() {
        response = new Response(serverName);
        byte[] testBody = "Binary test".getBytes();
        Response result = response.setBody(testBody);

        Assertions.assertSame(response, result);
        Assertions.assertArrayEquals(testBody, response.getBody());
    }

    @Test
    public void addHeaderReturnsSameObjectWithUpdatedField() {
        response = new Response(serverName);
        Response result = response.addHeader("Cache-Control", "no-cache");

        Assertions.assertSame(response, result);
        Assertions.assertEquals("no-cache", response.getHeaders().get("Cache-Control"));
    }

    @Test
    public void setHeadersReplacesAllHeaders() {
        response = new Response(serverName);
        response.addHeader("Old-Header", "old-value");

        Map<String, String> newHeaders = new HashMap<>();
        newHeaders.put("New-Header", "new-value");
        newHeaders.put("Another-Header", "another-value");

        Response result = response.setHeaders(newHeaders);

        Assertions.assertSame(response, result);
        Assertions.assertEquals(2, response.getHeaders().size());
        Assertions.assertEquals("new-value", response.getHeaders().get("New-Header"));
        Assertions.assertEquals("another-value", response.getHeaders().get("Another-Header"));
        Assertions.assertNull(response.getHeaders().get("Old-Header"));
    }

    @Test
    public void fluentInterfaceChaining() {
        String body = "Chained response";
        response = new Response(serverName)
                .setStatusCode(201)
                .setContentType("application/json")
                .setBody(body)
                .addHeader("Location", "/api/resource/123")
                .addHeader("Cache-Control", "no-store");

        Assertions.assertEquals(201, response.getStatusCode());
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertArrayEquals(body.getBytes(), response.getBody());
        Assertions.assertEquals("/api/resource/123", response.getHeaders().get("Location"));
        Assertions.assertEquals("no-store", response.getHeaders().get("Cache-Control"));
    }

    @Test
    public void getBodyReturnsDefensiveCopy() {
        byte[] originalBody = "Original content".getBytes();
        response = new Response(serverName, 200, "text/plain", originalBody);

        byte[] retrievedBody = response.getBody();
        retrievedBody[0] = 'X'; // Modify the retrieved array

        // Original should be unchanged
        Assertions.assertArrayEquals(originalBody, response.getBody());
    }

    @Test
    public void getHeadersReturnsDefensiveCopy() {
        response = new Response(serverName);
        response.addHeader("Test-Header", "test-value");

        Map<String, String> retrievedHeaders = response.getHeaders();
        retrievedHeaders.put("Malicious-Header", "malicious-value");

        // Original should be unchanged
        Assertions.assertEquals(1, response.getHeaders().size());
        Assertions.assertNull(response.getHeaders().get("Malicious-Header"));
    }

    @Test
    public void multipleHeadersCanBeAdded() {
        response = new Response(serverName);
        response.addHeader("Header1", "value1")
                .addHeader("Header2", "value2")
                .addHeader("Header3", "value3");

        Map<String, String> headers = response.getHeaders();
        Assertions.assertEquals(3, headers.size());
        Assertions.assertEquals("value1", headers.get("Header1"));
        Assertions.assertEquals("value2", headers.get("Header2"));
        Assertions.assertEquals("value3", headers.get("Header3"));
    }
}
