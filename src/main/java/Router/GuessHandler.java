package Router;

import Connection.Request;
import Connection.Response;
import Game.GuessingGame;
import org.example.RouteHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class GuessHandler implements RouteHandler {

    private Path rootPath;
    private String serverName;
    private String postForm = "<html>\n" +
            "\n" +
            "<h2>Can you guess the magic number?</h2>\n" +
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
        GuessingGame gameState = new GuessingGame(request);
        String html = generateFormResponse(gameState);
        Response response = createHtmlResponse(200, html);
        setGameStateCookies(response, gameState);
        return response;
    }

    private Response handleGet(Request request) {
        GuessingGame gameState;
        if (request.getQueryString() != null && request.getQueryString().contains("newgame=true")) {
            gameState = new GuessingGame(request);
            gameState.startNewGame();
        } else {
            gameState = new GuessingGame(request);
        }

        String html = generateFormResponse(gameState);
        Response response = createHtmlResponse(200, html);

        setGameStateCookies(response, gameState);
        return response;
    }

    private String generateFormResponse(GuessingGame gameState) {
        StringBuilder html = new StringBuilder();
        int remainingGuesses = 7 - gameState.getAttempts();
        if (gameState.isInProgress()) {
            html.append(postForm);
            if (gameState.getErrorMessage() != null) {
                html.append("<p>").append(gameState.getErrorMessage()).append("</p>");
            }
        }

        if (gameState.getTarget() != -1) {
            html.append("<p>I'm thinking of a number between 1 and 100!</p>");
            html.append("<p>Attempts: ").append(gameState.getAttempts()).append("</p>");
        }
        if (!gameState.getPriorGuesses().isEmpty()) {
            html.append("<h3>Prior guesses:</h3><ul>");
            appendGuesses(html, gameState);
            html.append("</ul>");
            html.append("<p>Remaining guesses: ")
                    .append(remainingGuesses)
                    .append("</p>");
            if (!gameState.isInProgress()) {
                if (gameState.isGameWon()) {
                    html.append("<h2>You won! The number was ").append(gameState.getTarget()).append("!</h2>");
                } else {
                    html.append("<h2>No guesses left! The number was ").append(gameState.getTarget()).append("!</h2>");
                }
                html.append("<p><a href=\"/guess?newgame=true\">Start New Game</a></p>");
            }
        }
        html.append("</html>");
        return html.toString();
    }

    private Response createHtmlResponse(int statusCode, String html) {
        byte[] htmlBytes = html.getBytes(StandardCharsets.UTF_8);
        return new Response(serverName, statusCode, "text/html", html)
                .addHeader("Content-Type", "text/html; charset=utf-8")
                .addHeader("Content-Length", String.valueOf(htmlBytes.length))
                .addHeader("Server", serverName)
                .addHeader("Connection", "close");
    }

    private void appendGuesses(StringBuilder html, GuessingGame gameState) {
        for (int i = 0; i < gameState.getPriorGuesses().size(); i++) {
            int guess = gameState.getPriorGuesses().get(i);
            html.append("<li>Guess ").append(i + 1).append(": ").append(guess);

            if (guess < gameState.getTarget()) {
                html.append(" - Too low!");
            } else if (guess > gameState.getTarget()) {
                html.append(" - Too high!");
            } else {
                html.append(" - Just Right!");
            }

            html.append("</li>");
        }
    }

    private void setGameStateCookies(Response response, GuessingGame gameState) {
        System.out.println("** Cookies being set: **");
        System.out.println("  target = " + gameState.getTarget());
        System.out.println("  attempts = " + gameState.getAttempts());
        System.out.println("  priorGuesses = " + gameState.getPriorGuesses());

        response.addCookie("target=" + gameState.getTarget() + "; Path=/; Max-Age=3600");
        response.addCookie("attempts=" + gameState.getAttempts() + "; Path=/; Max-Age=3600");

        for (int i = 0; i < gameState.getPriorGuesses().size(); i++) {
            response.addCookie("guess" + (i + 1) + "=" + gameState.getPriorGuesses().get(i) + "; Path=/; Max-Age=3600");
        }
    }

}
