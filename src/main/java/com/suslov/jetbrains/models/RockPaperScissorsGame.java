package com.suslov.jetbrains.models;

import com.suslov.jetbrains.exceptions.GameException;
import com.suslov.jetbrains.util.ConsoleUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RockPaperScissorsGame {
    private String ratingPath = "src\\main\\resources\\rating.txt";
    private final Scanner scanner;
    private boolean isEnd;
    private String userName;
    private int rating;
    private List<String> userOptions;

    public void setRatingPath(String ratingPath) {
        this.ratingPath = ratingPath;
    }

    public String getRatingPath() {
        return ratingPath;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public String getUserName() {
        return userName;
    }

    public int getRating() {
        return rating;
    }

    public List<String> getUserOptions() {
        return userOptions;
    }

    public RockPaperScissorsGame() {
        this(new Scanner(System.in));
    }

    public RockPaperScissorsGame(Scanner scanner) {
        this.scanner = scanner;
    }

    public void initialize() throws GameException {
        greetUser();
        loadUserRating();
        readUserOptionList();
    }

    void greetUser() {
        ConsoleUtil.displayMessage("Enter your name:");
        userName = scanner.nextLine();
        ConsoleUtil.displayMessage(String.format("Hello, %s", userName));
    }

    void loadUserRating() throws GameException {
        try (BufferedReader reader = Files.newBufferedReader(getPathToRatingFile())) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith(userName)) {
                    String[] keyValue = line.split(" ");
                    rating = Integer.parseInt(keyValue[1]);
                }
            }
        } catch (IOException e) {
            throw new GameException(e.getMessage());
        }
    }

    Path getPathToRatingFile() throws GameException {
        try {
            Path filePath = Paths.get(ratingPath);
            if (!Files.exists(filePath)) {
                return Files.createFile(filePath);
            }
            return filePath;
        } catch (IOException | RuntimeException e) {
            throw new GameException(e.getMessage());
        }
    }

    void readUserOptionList() {
        ConsoleUtil.displayMessage("Enter your list of options for the game separated by comma " +
                "(or empty line for the classic case [rock, paper,scissors]):");
        userOptions = new ArrayList<>(Arrays.asList(scanner.nextLine().split(",")));
        if (userOptions.size() < 3) {
            userOptions = Arrays.asList("rock", "paper", "scissors");
        }
    }

    public void launch() {
        ConsoleUtil.displayMessage("Okay, let's start");
        while (!isEnd) {
            processNextStage(scanner.nextLine());
        }
    }

    void processNextStage(String inputShape) {
        try {
            if (checkForSpecialCommand(inputShape)) {
                return;
            }

            if (!userOptions.contains(inputShape)) {
                throw new GameException(ConsoleUtil.INPUT_ERROR);
            }
            determineResult(inputShape, doComputerAction());
        } catch (GameException exp) {
            ConsoleUtil.displayMessage(exp.getMessage());
        }
    }

    boolean checkForSpecialCommand(String inputShape) throws GameException {
        if (inputShape.equals("!rating")) {
            ConsoleUtil.displayMessage(String.format("Your rating: %d", rating));
            return true;
        } else if (inputShape.equals("!exit")) {
            isEnd = true;
            saveUserRating();
            return true;
        }
        return false;
    }

    void saveUserRating() throws GameException {
        try (BufferedWriter writer = Files.newBufferedWriter(getPathToRatingFile())) {
            writer.write(String.format("%s %d", userName, rating));
            writer.flush();
        } catch (IOException ex) {
            throw new GameException(ex.getMessage());
        }
    }

    void determineResult(String userShape, String compShape) {
        if (userShape.equals(compShape)) {
            ConsoleUtil.displayMessage(String.format(ConsoleUtil.IS_DRAW, compShape));
            refreshUserRating(50);
            return;
        }

        int index = -1;
        for (String shape : userOptions) {
            if (shape.equals(userShape)) {
                index = userOptions.indexOf(shape);
                break;
            }
        }

        // Take all options without picked one from the user's list.
        // First are the options that follow the chosen one in the original list;
        // then, there are the ones that precede it.
        List<String> newOptions = new ArrayList<>();
        if (index != userOptions.size() - 1) {
            for (int i = index + 1; i < userOptions.size(); i++) {
                newOptions.add(userOptions.get(i));
            }
        }
        for (int i = 0; i < index; i++) {
            newOptions.add(userOptions.get(i));
        }

        int half = newOptions.size() >> 1;
        if (newOptions.indexOf(compShape) < half) {
            ConsoleUtil.displayMessage(String.format(ConsoleUtil.COMP_WIN, compShape));
        } else {
            ConsoleUtil.displayMessage(String.format(ConsoleUtil.USER_WIN, compShape));
            refreshUserRating(100);
        }
    }

    String doComputerAction() {
        int shapeNumber = new Random().nextInt(userOptions.size());
        return userOptions.get(shapeNumber);
    }

    void refreshUserRating(int points) {
        rating += points;
    }

    public void close() {
        scanner.close();
    }
}
