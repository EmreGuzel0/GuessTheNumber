package com.project;

import java.util.Random;

public class GameLogic {
    private int targetNumber;
    private int attempts;

    public void generateNewNumber(int maxRange) {
        Random random = new Random();
        targetNumber = random.nextInt(maxRange) + 1;
        attempts = 0;
    }

    public int checkGuess(int guess) {
        attempts++;
        if (guess < targetNumber) {
            return -1;
        } else if (guess > targetNumber) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getAttempts() {
        return attempts;
    }
}