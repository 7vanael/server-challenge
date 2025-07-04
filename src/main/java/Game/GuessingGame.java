package Game;

import Connection.RequestI;

import java.util.ArrayList;
import java.util.HashMap;

public class GuessingGame {

    private int target = -1;
    private int attempts = 0;
    private ArrayList<Integer> priorGuesses = new ArrayList<>();
    private boolean inProgress = true;
    private String errorMessage = null;

    public GuessingGame(RequestI request) {
        boolean isNewGameRequest = request.getQueryString() != null && request.getQueryString().contains("newgame=true");

        if (isNewGameRequest) {
            startNewGame();
        } else {
            HashMap<String, String> cookies = request.getCookies();
            populateTarget(cookies);
            populateAttempts(cookies);
            populatePriorGuesses(cookies);
            if(target == -1){
                startNewGame();
            }
        }
        parseGuess(request);
    }

    private void populateTarget(HashMap<String, String> cookies) {
        String targetStr = cookies.get("target");
        if (targetStr != null) {
            try {
                this.target = Integer.parseInt(targetStr);
            } catch (NumberFormatException e) {
            }
        }
    }

    private void populateAttempts(HashMap<String, String> cookies) {
        String attemptsStr = cookies.get("attempts");
        if (attemptsStr != null) {
            try {
                this.attempts = Integer.parseInt(attemptsStr);
            } catch (NumberFormatException e) {
                this.attempts = 0;
            }
        }
    }

    private void populatePriorGuesses(HashMap<String, String> cookies) {
        for (int i = 1; i <= attempts; i++) {
            String guessStr = cookies.get("guess" + i);
            if (guessStr != null) {
                try {
                    priorGuesses.add(Integer.parseInt(guessStr));
                } catch (NumberFormatException e) {
                    System.out.println("Prior guess not a number, cookie may have been tampered");
                }
            }
        }
    }

    private void parseGuess(String guessString) {
        if (guessString != null && !guessString.trim().isEmpty()) {
            try {
                int guess = Integer.parseInt(guessString.trim());
                if (guess >= 1 && guess <= 100) {
                    priorGuesses.add(guess);
                    attempts = priorGuesses.size();
                    if (guess == target) {
                        inProgress = false;
                    } else if (attempts >= 7) {
                        inProgress = false;
                    }
                } else {
                    errorMessage = "Guess must be a number between 1 and 100";
                }
            } catch (NumberFormatException e) {
                errorMessage = "Please enter a number to guess";
            }
        }
    }

    private void parseGuess(RequestI request) {
        boolean isNewGameRequest = request.getQueryString() != null &&
                request.getQueryString().contains("newgame=true");

        if (isNewGameRequest && "GET".equals(request.getMethod())) {
            return; // This request just produces a new form, no guess expected.
        }
        String[] body = new String(request.getBody()).split("&");
        for (String content : body) {
            String[] entry = content.split("=");
            if (entry.length == 2 && "guess".equals(entry[0])) {
                parseGuess(entry[1]);
            }
        }
    }

    public void startNewGame() {
        target = (int) (Math.random() * 100) + 1;
        attempts = 0;
        priorGuesses.clear();
        errorMessage = null;
        inProgress = true;
    }

    public boolean isGameWon() {
        return !inProgress && !priorGuesses.isEmpty() &&
                priorGuesses.get(priorGuesses.size() - 1) == target;
    }

    public int getTarget() {
        return target;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public ArrayList<Integer> getPriorGuesses() {
        return priorGuesses;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
