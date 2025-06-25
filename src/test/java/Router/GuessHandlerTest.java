package Router;

import Connection.Request;
import Connection.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuessHandlerTest {
    private String serverName = "Test Server";
    private Path rootPath = Paths.get("testroot");
    private GuessHandler handler;

    @BeforeEach
    public void setUp() {
        handler = new GuessHandler(rootPath, serverName);
    }

    private MockRequest createMockRequest(String method, String path) {
        return new MockRequest(method, path, 0);
    }

    @Test
    public void getWithoutCookiesRendersForm() throws IOException {
        MockRequest request = createMockRequest("GET", "/guess");
        Response response = handler.handle(request);
        String body = new String(response.getBody());
        List<String> cookies = response.getCookies();
        System.out.println("cookies: " + cookies);
        assertTrue(body.contains("<h2>Can you guess the magic number?</h2>"));
        assertTrue(body.contains("I'm thinking of a number between 1 and 100"));
        assertTrue(body.contains("Attempts: 0"));
        assertEquals(2, cookies.size()); //target and attempts are set

    }

    @Test
    public void getWithoutCookiesStartsNewGame() throws IOException {
        MockRequest request = createMockRequest("GET", "/guess");
        Response response = handler.handle(request);

        List<String> cookies = response.getCookies();
        boolean hasTargetCookie = cookies.stream()
                .anyMatch(cookie -> cookie.startsWith("target="));
        assertTrue(hasTargetCookie);
    }

    @Test
    public void getWithExistingCookiesPreservesGameState() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "2");
        cookies.put("guess1", "35");
        cookies.put("guess2", "48");

        MockRequest request = createMockRequest("GET", "/guess");
        request.setCookies(cookies);

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Attempts: 2"));
        assertTrue(body.contains("Guess 1: 35"));
        assertTrue(body.contains("Guess 2: 48"));
    }

    @Test
    public void postWithValidGuessUpdatesState() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "2");
        cookies.put("guess1", "35");
        cookies.put("guess2", "48");

        MockRequest request = createMockRequest("GET", "/guess");
        request.setCookies(cookies);
        request.setBody("guess=82".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("Attempts: 3"));
        assertTrue(body.contains("Guess 1: 35"));
        assertTrue(body.contains("Guess 2: 48"));
        assertTrue(body.contains("Guess 3: 82"));
    }

    @Test
    public void postWithInvalidNumberNotAddedToListAndShowsPrompt() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "0");
        MockRequest request = createMockRequest("POST", "/guess");
        request.setCookies(cookies);
        request.setBody("guess=junk".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Please enter a number to guess"));
        List<String> responseCookies = response.getCookies();
        assertTrue(body.contains("Attempts: 0"));
        assertEquals(2, responseCookies.size());
    }
    @Test
    public void postWithInvalidRangeNotAddedToListAndShowsPrompt() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "0");
        MockRequest request = createMockRequest("POST", "/guess");
        request.setCookies(cookies);
        request.setBody("guess=600".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Guess must be a number between 1 and 100"));
        List<String> responseCookies = response.getCookies();
        assertTrue(body.contains("Attempts: 0"));
        assertEquals(2, responseCookies.size());
    }

    @Test
    public void postWithEmptyGuessDoesNotCount() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "0");
        MockRequest request = createMockRequest("POST", "/guess");
        request.setCookies(cookies);
        request.setBody("guess= ".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Attempts: 0"));
    }


    @Test
    public void guessTooLowShowsFeedback() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "42");
        cookies.put("attempts", "0");
        MockRequest request = createMockRequest("GET", "/guess");
        request.setCookies(cookies);
        request.setBody("guess=35".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Guess 1: 35 - Too low!"));
    }

    @Test
    public void guessTooHighShowsFeedback() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "42");
        cookies.put("attempts", "1");
        cookies.put("guess1", "35");

        MockRequest request = createMockRequest("GET", "/guess");
        request.setCookies(cookies);
        request.setBody("guess=48".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Guess 2: 48 - Too high!"));
    }

    @Test
    public void correctGuessShowsWinMessage() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "42");
        cookies.put("attempts", "2");
        cookies.put("guess1", "35");
        cookies.put("guess2", "48");

        MockRequest request = createMockRequest("GET", "/guess");
        request.setCookies(cookies);
        request.setBody("guess=42".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("You won! The number was 42"));
    }

    @Test
    public void multipleGuessesShowAllFeedback() throws IOException {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "42");
        cookies.put("attempts", "2");
        cookies.put("guess1", "35");
        cookies.put("guess2", "48");

        MockRequest request = createMockRequest("GET", "/guess");
        request.setCookies(cookies);
        request.setBody("guess=42".getBytes());

        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("You won! The number was 42"));
        assertTrue(body.contains("Guess 3: 42 - Just Right!"));
        assertTrue(body.contains("Guess 2: 48 - Too high!"));
        assertTrue(body.contains("Guess 1: 35 - Too low!"));
    }


    @Test
    public void responseHasCorrectHeaders() throws IOException {
        MockRequest request = createMockRequest("GET", "/guess");
        Response response = handler.handle(request);
        System.out.println(response.getHeaders());
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Content-Type"));
        assertEquals("text/html; charset=utf-8",
                response.getHeaders().get("Content-Type"));
    }

}
