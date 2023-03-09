package com.suslov.jetbrains;

import com.suslov.jetbrains.exceptions.GameException;
import com.suslov.jetbrains.models.RockPaperScissorsGame;
import com.suslov.jetbrains.util.ConsoleUtil;

public class RockPaperScissors {

    public static void main(String[] args) {
        RockPaperScissorsGame game = new RockPaperScissorsGame();
        try {
            game.initialize();
            game.launch();
        } catch (GameException exp) {
            ConsoleUtil.displayMessage(exp.getMessage());
        } finally {
            game.close();
        }
    }
}