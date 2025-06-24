package Router;

import Connection.Request;
import Connection.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FormHandlerTest {
    private String serverName = "Test Server";
    private Path rootPath = Paths.get("testroot");
    private FormHandler formHandler = new FormHandler(rootPath, serverName);

    @BeforeEach
    void setUp() {
        formHandler = new FormHandler(Paths.get("testroot"), "TestServer/1.0");
    }

    @Test
    public void getRequestWithoutQueryStringShowsForms() {
        MockRequest mockRequest = new MockRequest("GET", "/form", 0);

        Response response = formHandler.handle(mockRequest);

        assertEquals(200, response.getStatusCode());
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("GET Form"));
        assertTrue(body.contains("POST Form"));
    }

    @Test
    public void getRequestWithQueryStringShowsParameters() {
        MockRequest mockRequest = new MockRequest("GET", "/form", 0);
        mockRequest.setQueryString("foo=hello&bar=world");

        Response response = formHandler.handle(mockRequest);

        assertEquals(200, response.getStatusCode());
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("foo: hello"));
        assertTrue(body.contains("bar: world"));
    }

    @Test
    public void postRequestWithoutContentTypeReturns400() {
        MockRequest mockRequest = new MockRequest("POST", "/form", 0);

        Response response = formHandler.handle(mockRequest);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void postRequestWithFileUploadShowsCorrectFormat() {
        MockRequest mockRequest = new MockRequest("POST", "/form", 0);
        mockRequest.setHeader("content-type", "multipart/form-data; boundary=test123");

        MockMultipartPart filePart = new MockMultipartPart();
        filePart.setName("file");
        filePart.setFilename("test.txt");
        filePart.setContentType("text/plain");
        filePart.setContent("Hello world!".getBytes(StandardCharsets.UTF_8));

        mockRequest.addMultipartPart(filePart);

        Response response = formHandler.handle(mockRequest);

        assertEquals(200, response.getStatusCode());
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("<li>field name: file</li>"));
        assertTrue(body.contains("<li>file name: test.txt</li>"));
        assertTrue(body.contains("<li>content type: text/plain</li>"));
        assertTrue(body.contains("<li>file size: 12</li>"));
    }

    @Test
    public void responseHasCorrectHeaders() {
        MockRequest mockRequest = new MockRequest("GET", "/form", 0);

        Response response = formHandler.handle(mockRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("TestServer/1.0", response.getHeaders().get("Server"));
        assertTrue(response.getHeaders().get("content-Type").contains("text/html"));
        assertEquals("close", response.getHeaders().get("Connection"));
        assertNotNull(response.getHeaders().get("Content-Length"));
    }

    private static class MockMultipartPart extends Request.MultipartPart {
        private String name;
        private String filename;
        private String contentType;
        private byte[] content = new byte[0];

        public void setName(String name) {
            this.name = name;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public byte[] getContent() {
            return content;
        }

        @Override
        public boolean isFile() {
            return filename != null && !filename.isEmpty();
        }

        @Override
        public String getContentAsString() {
            return new String(content, StandardCharsets.UTF_8).trim();
        }
    }

}
