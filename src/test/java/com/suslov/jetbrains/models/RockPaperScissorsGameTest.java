package com.suslov.jetbrains.models;

import com.suslov.jetbrains.exceptions.GameException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Mikhail Suslov
 */
public class RockPaperScissorsGameTest {
    private RockPaperScissorsGame gameTest;

    @After
    public void tearDown() {
        gameTest.close();
    }

    @Test
    public void readEmptyUserOptionList() {
        Scanner scanner = new Scanner("\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();
        Assert.assertEquals(Arrays.asList("rock", "paper", "scissors"), gameTest.getUserOptions());
    }

    @Test
    public void readUserOptionListLessThanThreeElements() {
        Scanner scanner = new Scanner("lizard,spock\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();
        Assert.assertEquals(Arrays.asList("rock", "paper", "scissors"), gameTest.getUserOptions());
    }

    @Test
    public void readUserOptionListThreeElements() {
        Scanner scanner = new Scanner("scissors,lizard,spock\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();
        Assert.assertEquals(Arrays.asList("scissors", "lizard", "spock"), gameTest.getUserOptions());
    }

    @Test
    public void readUserOptionListMoreThanThreeElements() {
        Scanner scanner = new Scanner("rock,paper,scissors,lizard,spock\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();
        Assert.assertEquals(Arrays.asList("rock", "paper", "scissors", "lizard", "spock"), gameTest.getUserOptions());
    }

    @Test
    public void getDefaultPathToRatingFile() {
        gameTest = new RockPaperScissorsGame();
        Assert.assertEquals(gameTest.getRatingPath(), gameTest.getPathToRatingFile().toString());
    }

    @Test(expected = GameException.class)
    public void getIncorrectPathToRatingFile() {
        gameTest = new RockPaperScissorsGame();
        gameTest.setRatingPath("\\//");
        gameTest.getPathToRatingFile();
    }

    @Test
    public void loadExistingUserRating() {
        gameTest = new RockPaperScissorsGame(new Scanner("Mike\n"));
        gameTest.greetUser();
        gameTest.setRatingPath("src\\test\\resources\\rating1.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(gameTest.getPathToRatingFile())) {
            writer.write(String.format("%s %d", "Mike", 155));
            writer.flush();
        } catch (IOException ex) {
            throw new GameException(ex.getMessage());
        }

        gameTest.loadUserRating();

        Assert.assertEquals(155, gameTest.getRating());
    }

    @Test
    public void loadNotExistingUserRating() {
        gameTest = new RockPaperScissorsGame(new Scanner("Mike\n"));
        gameTest.greetUser();
        gameTest.setRatingPath("src\\test\\resources\\rating2.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(gameTest.getPathToRatingFile())) {
            writer.write(String.format("%s %d", "John", 160));
            writer.flush();
        } catch (IOException ex) {
            throw new GameException(ex.getMessage());
        }

        gameTest.loadUserRating();

        Assert.assertEquals(0, gameTest.getRating());
    }

    @Test
    public void loadUserRatingWithoutFile() {
        gameTest = new RockPaperScissorsGame(new Scanner("Mike\n"));
        gameTest.greetUser();
        gameTest.setRatingPath("src\\test\\resources\\rating3.txt");

        gameTest.loadUserRating();

        Assert.assertEquals(0, gameTest.getRating());
    }

    @Test
    public void saveUserRating() {
        gameTest = new RockPaperScissorsGame(new Scanner("Mike\n"));
        gameTest.setRatingPath("src\\test\\resources\\rating4.txt");
        gameTest.greetUser();

        gameTest.saveUserRating();

        int ratingFromFile = 0;
        try (BufferedReader reader = Files.newBufferedReader(gameTest.getPathToRatingFile())) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("Mike")) {
                    String[] keyValue = line.split(" ");
                    ratingFromFile = Integer.parseInt(keyValue[1]);
                }
            }
        } catch (IOException e) {
            throw new GameException(e.getMessage());
        }
        Assert.assertEquals(gameTest.getRating(), ratingFromFile);
    }

    @Test
    public void checkForSpecialCommandRating() {
        gameTest = new RockPaperScissorsGame(new Scanner("Mike\n"));
        gameTest.greetUser();
        Assert.assertTrue(gameTest.checkForSpecialCommand("!rating"));
    }

    @Test
    public void checkForSpecialCommandExit() {
        gameTest = new RockPaperScissorsGame(new Scanner("Mike\n"));
        gameTest.greetUser();
        gameTest.setRatingPath("src\\test\\resources\\rating5.txt");
        Assert.assertTrue(gameTest.checkForSpecialCommand("!exit"));
    }

    @Test
    public void checkForSpecialCommandInvalid() {
        gameTest = new RockPaperScissorsGame();
        Assert.assertFalse(gameTest.checkForSpecialCommand("paper"));
    }

    @Test
    public void determineResultDraw() {
        Scanner scanner = new Scanner("\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();

        gameTest.determineResult("rock", "rock");
        Assert.assertEquals(50, gameTest.getRating());
    }

    @Test
    public void determineResultUserWin() {
        Scanner scanner = new Scanner("\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();

        gameTest.determineResult("rock", "scissors");
        Assert.assertEquals(100, gameTest.getRating());
    }

    @Test
    public void determineResultCompWin() {
        Scanner scanner = new Scanner("\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();

        gameTest.determineResult("rock", "paper");
        Assert.assertEquals(0, gameTest.getRating());
    }

    @Test
    public void refreshUserRating() {
        Scanner scanner = new Scanner("\n");
        gameTest = new RockPaperScissorsGame(scanner);

        gameTest.refreshUserRating(50);

        Assert.assertEquals(50, gameTest.getRating());
    }

    @Test
    public void doComputerActionCorrectDistributionValues() {
        Scanner scanner = new Scanner("\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();

        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            String computerAction = gameTest.doComputerAction();
            result.put(computerAction, result.getOrDefault(computerAction, 0) + 1);
        }

        double rockPercent = 100 * result.get("rock") / 1000.0;
        double paperPercent = 100 * result.get("paper") / 1000.0;
        double scissorsPercent = 100 * result.get("scissors") / 1000.0;

        Assert.assertTrue(rockPercent > 20 && rockPercent < 40);
        Assert.assertTrue(paperPercent > 20 && paperPercent < 40);
        Assert.assertTrue(scissorsPercent > 20 && scissorsPercent < 40);
    }

    @Test
    public void launchWithExit() {
        Scanner scanner = new Scanner("Mike\nrock,paper,scissors,lizard,spock\nrock\npaper\n!rating\n!exit\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.readUserOptionList();

        gameTest.launch();

        Assert.assertTrue(gameTest.isEnd());
    }

    @Test
    public void greetUser() {
        Scanner scanner = new Scanner("John\n");
        gameTest = new RockPaperScissorsGame(scanner);

        gameTest.greetUser();

        Assert.assertEquals("John", gameTest.getUserName());
    }

    @Test
    public void initialize() {
        Scanner scanner = new Scanner("Elen\nrock,paper,scissors,lizard,spock\n");
        gameTest = new RockPaperScissorsGame(scanner);
        gameTest.setRatingPath("src\\test\\resources\\rating6.txt");
        gameTest.initialize();

        Assert.assertEquals("Elen", gameTest.getUserName());
        Assert.assertEquals(Arrays.asList("rock", "paper", "scissors", "lizard", "spock"), gameTest.getUserOptions());
        Assert.assertEquals(0, gameTest.getRating());
    }
}