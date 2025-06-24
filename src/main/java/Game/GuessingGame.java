package Game;

import Connection.Request;

import java.util.ArrayList;

public class GuessingGame {

        int target = -1;
        int attempts = 0;
        ArrayList<Integer> priorGuesses = new ArrayList<>();

        public GuessingGame(Request request){

        }
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
