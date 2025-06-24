package Router;

import Connection.Request;
import Connection.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuessHandlerTest {
    private String serverName = "Test Server";
    private Path rootPath = Paths.get("testroot");
    private GuessHandler handler = new GuessHandler(rootPath, serverName);


    @Test
    public void getWithoutCookiesRendersForm() throws IOException {
        Request request = createMockRequest("GET", "/guess", "", "");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("<h2>POST Form</h2>"));
    }

//    @Test
//    public void getWithoutCookiesStartsNewGame() throws IOException {
//        Request request = createMockRequest("GET", "/guess", "", "");
//        Response response = handler.handle(request);
//
//        // Should set target cookie
//        assertTrue(response.getHeaders().containsKey("Set-Cookie"));
//        List<String> setCookies = response.getHeaders().get("Set-Cookie");
//        boolean hasTargetCookie = setCookies.stream()
//                .anyMatch(cookie -> cookie.startsWith("target="));
//        assertTrue(hasTargetCookie);
//    }

//    @Test
//    public void getWithExistingCookiesPreservesGameState() throws IOException {
//        String cookies = "target=42; attempts=2; guess1=35; guess2=48";
//        Request request = createMockRequest("GET", "/guess", cookies, "");
//        Response response = handler.handle(request);
//        String body = new String(response.getBody());
//
//        assertTrue(body.contains("Attempts: 2"));
//        assertTrue(body.contains("Guess #1: 35"));
//        assertTrue(body.contains("Guess #2: 48"));
//    }
//
//    @Test
//    public void postWithValidGuessUpdatesState() throws IOException {
//        String cookies = "target=42; attempts=1; guess1=35";
//        Request request = createMockRequest("POST", "/guess", cookies, "40");
//        Response response = handler.handle(request);
//
//        // Check that cookies are updated
//        List<String> setCookies = response.getHeaders().get("Set-Cookie");
//        assertTrue(setCookies.stream().anyMatch(cookie -> cookie.startsWith("attempts=2")));
//        assertTrue(setCookies.stream().anyMatch(cookie -> cookie.startsWith("guess2=40")));
//    }
//
//    @Test
//    public void postWithInvalidNumberShowsError() throws IOException {
//        String cookies = "target=42; attempts=0";
//        Request request = createMockRequest("POST", "/guess", cookies,  "abc");
//        Response response = handler.handle(request);
//        String body = new String(response.getBody());
//
//        assertTrue(body.contains("Must be a number between 1 and 100"));
//
//        // Should not increment attempts
//        List<String> setCookies = response.getHeaders().get("Set-Cookie");
//        assertTrue(setCookies.stream().anyMatch(cookie -> cookie.startsWith("attempts=0")));
//    }

    @Test
    public void postWithNumberTooLowShowsError() throws IOException {
        String cookies = "target=42; attempts=0";
        Request request = createMockRequest("POST", "/guess", cookies,  "0");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Must be a number between 1 and 100"));
    }

    @Test
    public void postWithNumberTooHighShowsError() throws IOException {
        String cookies = "target=42; attempts=0";
        Request request = createMockRequest("POST", "/guess", cookies,  "101");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Must be a number between 1 and 100"));
    }

    @Test
    public void postWithEmptyGuessShowsError() throws IOException {
        String cookies = "target=42; attempts=0";
        Request request = createMockRequest("POST", "/guess", cookies,  "");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Please enter a guess"));
    }

    @Test
    public void correctGuessShowsWinMessage() throws IOException {
        String cookies = "target=42; attempts=1; guess1=35";
        Request request = createMockRequest("POST", "/guess", cookies,  "42");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("You won!"));
        assertTrue(body.contains("The number was 42"));
    }

    @Test
    public void guessTooLowShowsFeedback() throws IOException {
        String cookies = "target=42; attempts=0";
        Request request = createMockRequest("POST", "/guess", cookies,  "30");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Too low!"));
    }

    @Test
    public void guessTooHighShowsFeedback() throws IOException {
        String cookies = "target=42; attempts=0";
        Request request = createMockRequest("POST", "/guess", cookies, "50");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("Too high!"));
    }

    @Test
    public void multipleGuessesShowAllFeedback() throws IOException {
        String cookies = "target=42; attempts=2; guess1=30; guess2=50";
        Request request = createMockRequest("GET", "/guess", cookies, "");
        Response response = handler.handle(request);
        String body = new String(response.getBody());

        assertTrue(body.contains("30 - Too low!"));
        assertTrue(body.contains("50 - Too high!"));
    }

    @Test
    public void responseHasCorrectHeaders() throws IOException {
        Request request = createMockRequest("GET", "/guess", "", "");
        Response response = handler.handle(request);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Content-Type"));
        assertEquals("text/html; charset=utf-8",
                response.getHeaders().get("Content-Type"));
    }



    private Request createMockRequest(String method, String path, String cookies, String guess) {
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
            public String getCookieString(){
                return cookies;
            }

            @Override
            public HashMap<String, String> getCookies(){
                HashMap<String, String> cookieMap = new HashMap<>();
                String[] cookiePairs = cookies.split(";");
                for (String cookie : cookiePairs) {
                    if (cookie.trim().isEmpty()) continue;

                    int equalsIndex = cookie.indexOf('=');
                    if (equalsIndex > 0) {
                        String cookieName = cookie.substring(0, equalsIndex).trim();
                        String cookieValue = cookie.substring(equalsIndex + 1).trim();
                        cookieMap.put(cookieName, cookieValue);
                    }
                }
                return cookieMap;
            }
        };
    }
}
