package Game;

import Router.MockRequest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GuessingGameTest {

    @Test
    public void newGameInitializes() {
        MockRequest request = new MockRequest("GET", "/guess", 0);

        GuessingGame game = new GuessingGame(request);

        assertTrue(game.getTarget() >= 1 && game.getTarget() <= 100);
        assertEquals(0, game.getAttempts());
        assertTrue(game.getPriorGuesses().isEmpty());
        assertTrue(game.isInProgress());
        assertNull(game.getErrorMessage());
    }

    @Test
    public void gameStatePersistsFromCookies() {
        MockRequest request = new MockRequest("GET", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "42");
        cookies.put("attempts", "2");
        cookies.put("guess1", "30");
        cookies.put("guess2", "50");
        request.setCookies(cookies);

        GuessingGame game = new GuessingGame(request);

        assertEquals(42, game.getTarget());
        assertEquals(2, game.getAttempts());
        assertEquals(2, game.getPriorGuesses().size());
        assertEquals(30, game.getPriorGuesses().get(0));
        assertEquals(50, game.getPriorGuesses().get(1));
    }

    @Test
    public void validGuessIsAddedToGuessList() {
        MockRequest request = new MockRequest("POST", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "1");
        cookies.put("guess1", "50");
        request.setCookies(cookies);
        request.setBody("guess=60".getBytes());

        GuessingGame game = new GuessingGame(request);

        assertEquals(2, game.getPriorGuesses().size());
        assertEquals(60, game.getPriorGuesses().get(1));
        assertNull(game.getErrorMessage());
    }

    @Test
    public void invalidGuessNotAddedToPriorGuessList() {
        MockRequest request =  new MockRequest("POST", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "0");
        request.setCookies(cookies);
        request.setBody("guess=150".getBytes());

        GuessingGame game = new GuessingGame(request);

        assertEquals(0, game.getPriorGuesses().size());
        assertEquals("Guess must be a number between 1 and 100", game.getErrorMessage());
    }

    @Test
    public void nonNumericGuessNotAddedToList() {
        MockRequest request = new MockRequest("POST", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "0");
        request.setCookies(cookies);
        request.setBody("guess=abc".getBytes());

        GuessingGame game = new GuessingGame(request);

        assertEquals(0, game.getPriorGuesses().size());
        assertEquals("Please enter a number to guess", game.getErrorMessage());
    }

    @Test
    public void newGameDoesNotCarryOverOldGame() {
        MockRequest request = new MockRequest("GET", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "42");
        cookies.put("attempts", "5");
        cookies.put("guess1", "30");
        request.setCookies(cookies);
        request.setQueryString("newgame=true");

        GuessingGame game = new GuessingGame(request);

        assertTrue(game.getTarget() >= 1 && game.getTarget() <= 100);
        assertNotEquals(42, game.getTarget());
        assertEquals(0, game.getAttempts());
        assertTrue(game.getPriorGuesses().isEmpty());
        assertNull(game.getErrorMessage());
    }

    @Test
    public void emptyGuessNeitherErrorsNorAddsToList() {
        MockRequest request = new MockRequest("POST", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "0");
        request.setCookies(cookies);
        request.setBody("guess=   ".getBytes());

        GuessingGame game = new GuessingGame(request);

        assertEquals(0, game.getPriorGuesses().size());
        assertNull(game.getErrorMessage());
    }

    @Test
    public void formWithMultipleParametersStillExtractsGuess() {
        MockRequest request = new MockRequest("POST", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "75");
        cookies.put("attempts", "0");
        request.setCookies(cookies);
        request.setBody("username=player&guess=25&action=submit".getBytes());

        GuessingGame game = new GuessingGame(request);

        assertEquals(1, game.getPriorGuesses().size());
        assertEquals(25, game.getPriorGuesses().get(0).intValue());
    }

    @Test
    public void identifiesCorrectGuess() {
        MockRequest request = new MockRequest("GET", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "50");
        cookies.put("attempts", "3");
        cookies.put("guess1", "25");
        cookies.put("guess2", "75");
        cookies.put("guess3", "50");
        request.setCookies(cookies);

        GuessingGame game = new GuessingGame(request);

        assertEquals(3, game.getPriorGuesses().size());
        assertEquals(50, game.getTarget());

        assertTrue(game.getPriorGuesses().get(0) < game.getTarget());
        assertTrue(game.getPriorGuesses().get(1) > game.getTarget());
        assertEquals(game.getPriorGuesses().get(2).intValue(), game.getTarget());
    }
    @Test
    public void maxAttemptsEndsGameOn7thGuess() {
        MockRequest request = new MockRequest("POST", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "50");
        cookies.put("attempts", "6");
        for (int i = 1; i <= 6; i++) {
            cookies.put("guess" + i, String.valueOf( i));
        }
        request.setCookies(cookies);
        request.setBody("guess=25".getBytes());

        GuessingGame game = new GuessingGame(request);

        assertEquals(7, game.getPriorGuesses().size());
        assertFalse(game.isInProgress());
        assertEquals(25, game.getPriorGuesses().get(6).intValue());
    }

    @Test
    public void gameEndsWhenCorrectNumberGuessed() {
        MockRequest request = new MockRequest("POST", "/guess", 0);
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("target", "50");
        cookies.put("attempts", "3");
        cookies.put("guess1", "25");
        cookies.put("guess2", "75");
        cookies.put("guess3", "40");
        request.setCookies(cookies);
        request.setBody("guess=50".getBytes());

        GuessingGame game = new GuessingGame(request);

        assertEquals(4, game.getPriorGuesses().size());
        assertFalse(game.isInProgress());
        assertTrue(game.isGameWon());
        assertEquals(50, game.getPriorGuesses().get(3).intValue());
    }
}
