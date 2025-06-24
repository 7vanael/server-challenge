package Router;

import Connection.Request;
import Connection.Response;
import org.example.RouteHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuessHandler implements RouteHandler {

    private Path rootPath;
    private String serverName;
    private String postForm = "<html>\n" +
            "\n" +
            "<h2>POST Form</h2>\n" +
            "<form method=\"post\" action=\"/guess\">\n" +
            " <label>Guess:</label>\n" +
            " <input type=\"text\" name=\"guess\"/>\n" +
            " <input type=\"submit\" value=\"Submit\"/>\n" +
            "</form>";

    public GuessHandler(Path rootPath, String serverName) {
        this.rootPath = rootPath;
        this.serverName = serverName;
    }

    @Override
    public Response handle(Request request) throws IOException {
        if (request.getMethod().equals("GET")) {
            return handleGet(request);
        } else {
            return handlePost(request);
        }
    }

    private Response handlePost(Request request) {
        GameState gameState = readGameFromCookies(request);
        if(gameState.target == -1){
            gameState.startNewGame();
        }
        String errorMessage = null;
        String guessString = getGuess(request);
        errorMessage = parseGuess(guessString, gameState, errorMessage);
        String html = generateFormResponse(gameState, errorMessage);
        Response response = createHtmlResponse(200, html);
        setGameStateCookies(response, gameState);
//        addGameStateCookies(response);
        return response;
    }

    private static String parseGuess(String guessString, GameState gameState, String errorMessage) {
        if(guessString != null && !guessString.trim().isEmpty()){
            try{
                int guess = Integer.parseInt(guessString.trim());
                if(guess >= 1 && guess <= 100){
                    gameState.addGuess(guess); // This updates the game state!
                }else {
                    errorMessage = "Guess must be a number between 1 and 100";
                }
            }catch (NumberFormatException e){
                errorMessage = "Please enter a number to guess";
            }
        }
        return errorMessage;
    }

    private static String getGuess(Request request) {
        String[] body = new String(request.getBody()).split("&");
        for (String content : body) {
            String[] entry = content.split("=");
            if (entry.length == 2 && "guess".equals(entry[0])) {
                return entry[1];
            }
        }
        return null;
    }

    private Response handleGet(Request request) {
        GameState gameState = readGameFromCookies(request);

        if (gameState.target == -1) {
            gameState.startNewGame();
        }

        String html = generateFormResponse(gameState, null);
        Response response = createHtmlResponse(200, html);

        setGameStateCookies(response, gameState);
//        addGameStateCookies(response);
        return response;
    }

    private static void addGameStateCookies(Response response) {
        System.out.println("**Adding cookies to headers: **");
        for (String cookie : response.getCookies()) {
            System.out.println("  Set-Cookie: " + cookie);
            response.addHeader("Set-Cookie", cookie);
        }
    }

    private String generateFormResponse(GameState gameState, String errorMessage) {
        StringBuilder html = new StringBuilder();
        int remainingGuesses = 7 - gameState.priorGuesses.size();
        html.append(postForm);
        if (errorMessage != null) {
            html.append("<p>").append(errorMessage).append("</p>");
        }
        if (gameState.target != -1) {
            html.append("<p>I'm thinking of a number between 1 and 100!</p>");
            html.append("<p>Attempts: ").append(gameState.attempts).append("</p>");
        }
        if (!gameState.priorGuesses.isEmpty()) {
            html.append("<h3>Prior guesses:</h3><ul>");
            appendGuesses(html, gameState);
            html.append("</ul");
            html.append("<p>Remaining guesses: ")
                    .append(remainingGuesses)
                    .append("</p>");
            int lastGuess = gameState.priorGuesses.get(gameState.priorGuesses.size() - 1);
            if (lastGuess == gameState.target) {
                html.append("<h2>You won! The number was ").append(gameState.target).append("!</h2>");
//                html.append("<p><a href=\"/guess?newgame=true\">Start New Game</a></p>");
            }
            if (gameState.priorGuesses.size() >= 7 && lastGuess != gameState.target) {
                html.append("<h2>No guesses left! The number was ").append(gameState.target).append("!</h2>");
//                html.append("<p><a href=\"/guess?newgame=true\">Start New Game</a></p>");

            }
        }
        html.append("</html>");
        return html.toString();
    }

    private Response createHtmlResponse(int statusCode, String html) {
        byte[] htmlBytes = html.getBytes(StandardCharsets.UTF_8);
        return new Response(serverName, statusCode, "text/html", html)
                .addHeader("content-Type", "text/html; charset=utf-8")
                .addHeader("Content-Length", String.valueOf(htmlBytes.length))
                .addHeader("Server", serverName)
                .addHeader("Connection", "close")
                .addHeader("X-Debug-Test", "This-Should-Show-Up");
    }

    private GameState readGameFromCookies(Request request) {
        System.out.println("** All Request headers: **");
        HashMap<String, String> headers = request.getHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            System.out.println(" " + header.getKey() + " = " + header.getValue());
        }
        String rawCookies = request.getCookieString();
        System.out.println("DEBUG: Raw cookie string: '" + rawCookies + "'");

        HashMap<String, String> cookies = request.getCookies();
        System.out.println("**Cookies received: **");
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            System.out.println(" " + cookie.getKey() + " = " + cookie.getValue());
        }
        GameState gameState = new GameState();

        // Read target
        String targetStr = cookies.get("target");
        if (targetStr != null) {
            try {
                gameState.target = Integer.parseInt(targetStr);
            } catch (NumberFormatException e) {
                gameState.target = -1; // Invalid, will trigger new game
            }
        }

        // Read attempts
        String attemptsStr = cookies.get("attempts");
        if (attemptsStr != null) {
            try {
                gameState.attempts = Integer.parseInt(attemptsStr);
            } catch (NumberFormatException e) {
                gameState.attempts = 0;
            }
        }

        // Read all guesses (guess1, guess2, etc.)
        for (int i = 1; i <= gameState.attempts; i++) {
            String guessStr = cookies.get("guess" + i);
            if (guessStr != null) {
                try {
                    gameState.priorGuesses.add(Integer.parseInt(guessStr));
                } catch (NumberFormatException e) {
                    // Skip invalid guess
                }
            }
        }
        System.out.println("DEBUG: GameState after reading cookies:");
        System.out.println("  target = " + gameState.target);
        System.out.println("  attempts = " + gameState.attempts);
        System.out.println("  priorGuesses size = " + gameState.priorGuesses.size());

        return gameState;
    }

    private void appendGuesses(StringBuilder html, GameState gameState) {
        for (int i = 0; i < gameState.priorGuesses.size(); i++) {
            int guess = gameState.priorGuesses.get(i);
            html.append("<li>Guess #").append(i + 1).append(": ").append(guess);

            if (guess < gameState.target) {
                html.append(" - Too low!");
            } else if (guess > gameState.target) {
                html.append(" - Too high!");
            } else {
                html.append(" - Just Right!");
            }

            html.append("</li>");
        }
    }

    private void setGameStateCookies(Response response, GameState gameState) {
        System.out.println("** Cookies being set: **");
        System.out.println("  target = " + gameState.target);
        System.out.println("  attempts = " + gameState.attempts);
        System.out.println("  priorGuesses = " + gameState.priorGuesses);

        // Set target
        response.addCookie("target=" + gameState.target + "; Path=/; Max-Age=3600");

        // Set attempts
        response.addCookie("attempts=" + gameState.attempts + "; Path=/; Max-Age=3600");

        // Set each guess
        for (int i = 0; i < gameState.priorGuesses.size(); i++) {
            response.addCookie("guess" + (i + 1) + "=" + gameState.priorGuesses.get(i) + "; Path=/; Max-Age=3600");
        }

        // Clear any old guesses beyond current count
        // (This handles case where user might have had more guesses before)
        for (int i = gameState.priorGuesses.size() + 1; i <= 10; i++) { // Clear up to 10 old guesses
            response.addCookie("guess" + i + "=; Path=/; Max-Age=0"); // Max-Age=0 deletes cookie
        }
    }


    private static class GameState {
        int target = -1;
        int attempts = 0;
        ArrayList<Integer> priorGuesses = new ArrayList<>();

        void startNewGame() {
            target = (int) (Math.random() * 100) + 1; // Random number 1-100
            attempts = 0;
            priorGuesses.clear();
        }

        void addGuess(int guess) {
            priorGuesses.add(guess);
            attempts++;
        }
    }

}
