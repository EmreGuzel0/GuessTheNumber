package com.project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage window;
    private Scene loginScene;
    private String currentUser;

    private UserManager userManager = new UserManager();
    private GameLogic gameLogic = new GameLogic();
    private int currentMaxRange = 100;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Guess The Number");
        buildLoginScene();
        window.setScene(loginScene);
        window.show();
    }

    private void buildLoginScene() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("Login or Register");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Username");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        Label messageLabel = new Label();
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton);

        loginButton.setOnAction(e -> {
            String user = usernameInput.getText();
            if (userManager.authenticate(user, passwordInput.getText())) {
                currentUser = user;
                usernameInput.clear();
                passwordInput.clear();
                messageLabel.setText("");
                openGameScene();
            } else {
                messageLabel.setText("Invalid credentials!");
                messageLabel.setStyle("-fx-text-fill: #ff4d4d; -fx-font-weight: bold;");
            }
        });

        registerButton.setOnAction(e -> {
            String user = usernameInput.getText();
            String pass = passwordInput.getText();
            if (user.trim().isEmpty() || pass.trim().isEmpty()) {
                messageLabel.setText("Empty fields!");
                messageLabel.setStyle("-fx-text-fill: #ff4d4d;");
            } else if (userManager.isUserExists(user)) {
                messageLabel.setText("User exists!");
                messageLabel.setStyle("-fx-text-fill: #ff4d4d;");
            } else {
                userManager.registerUser(user, pass);
                messageLabel.setText("Registered! Now login.");
                messageLabel.setStyle("-fx-text-fill: #4CAF50;");
            }
        });

        layout.getChildren().addAll(titleLabel, usernameInput, passwordInput, buttonBox, messageLabel);
        loginScene = new Scene(layout, 350, 400);
    }

    private void openGameScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label welcomeLabel = new Label("Welcome " + currentUser + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        ToggleGroup difficultyGroup = new ToggleGroup();
        RadioButton easyBtn = new RadioButton("Easy");
        RadioButton mediumBtn = new RadioButton("Medium");
        RadioButton hardBtn = new RadioButton("Hard");
        easyBtn.setToggleGroup(difficultyGroup);
        mediumBtn.setToggleGroup(difficultyGroup);
        hardBtn.setToggleGroup(difficultyGroup);
        mediumBtn.setSelected(true);
        easyBtn.setStyle("-fx-text-fill: white;");
        mediumBtn.setStyle("-fx-text-fill: white;");
        hardBtn.setStyle("-fx-text-fill: white;");

        HBox radioBox = new HBox(15);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.getChildren().addAll(easyBtn, mediumBtn, hardBtn);

        Label rangeInfoLabel = new Label("Range: 1 to 100");
        rangeInfoLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-style: italic;");

        TextField guessInput = new TextField();
        guessInput.setMaxWidth(100);
        guessInput.setStyle("-fx-font-size: 16px; -fx-alignment: center;");

        Button guessButton = new Button("Guess");
        guessButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        Label feedbackLabel = new Label("");
        feedbackLabel.setStyle("-fx-font-weight: bold;");

        Button restartButton = new Button("Play Again");
        restartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        restartButton.setVisible(false);

        Button leaderboardButton = new Button("View Leaderboard");
        leaderboardButton.setStyle("-fx-background-color: #673ab7; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        logoutButton.setOnAction(e -> {
            currentUser = null;
            window.setScene(loginScene);
        });

        gameLogic.generateNewNumber(currentMaxRange);

        difficultyGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            currentMaxRange = (newT == easyBtn) ? 50 : (newT == mediumBtn) ? 100 : 500;
            rangeInfoLabel.setText("Range: 1 to " + currentMaxRange);
            gameLogic.generateNewNumber(currentMaxRange);
            feedbackLabel.setText("");
            guessButton.setDisable(false);
            restartButton.setVisible(false);
        });

        guessButton.setOnAction(e -> {
            try {
                int guess = Integer.parseInt(guessInput.getText());
                int result = gameLogic.checkGuess(guess);
                if (result == -1) {
                    feedbackLabel.setText("Too low!");
                    feedbackLabel.setStyle("-fx-text-fill: #ffeb3b;");
                } else if (result == 1) {
                    feedbackLabel.setText("Too high!");
                    feedbackLabel.setStyle("-fx-text-fill: #ffeb3b;");
                } else {
                    String diff = ((RadioButton) difficultyGroup.getSelectedToggle()).getText();
                    userManager.saveScore(currentUser, diff, gameLogic.getAttempts());
                    feedbackLabel.setText("Correct! " + gameLogic.getAttempts() + " attempts.");
                    feedbackLabel.setStyle("-fx-text-fill: #4CAF50;");
                    guessButton.setDisable(true);
                    restartButton.setVisible(true);
                }
            } catch (Exception ex) {
                feedbackLabel.setText("Invalid number!");
                feedbackLabel.setStyle("-fx-text-fill: #ff4d4d;");
            }
            guessInput.clear();
        });

        guessInput.setOnAction(e -> guessButton.fire());
        leaderboardButton.setOnAction(e -> showLeaderboard());

        restartButton.setOnAction(e -> {
            gameLogic.generateNewNumber(currentMaxRange);
            feedbackLabel.setText("");
            guessButton.setDisable(false);
            restartButton.setVisible(false);
        });

        root.getChildren().addAll(welcomeLabel, radioBox, rangeInfoLabel, guessInput, guessButton, feedbackLabel, restartButton, leaderboardButton, logoutButton);
        window.setScene(new Scene(root, 400, 600));
    }

    private void showLeaderboard() {
        Stage scoreStage = new Stage();
        scoreStage.setTitle("High Scores");

        VBox scoreLayout = new VBox(10);
        scoreLayout.setPadding(new Insets(20));
        scoreLayout.setAlignment(Pos.CENTER);

        Label title = new Label("Score History");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<String> scoreList = new ListView<>();
        scoreList.getItems().addAll(userManager.getAllScores());

        scoreLayout.getChildren().addAll(title, scoreList);
        scoreStage.setScene(new Scene(scoreLayout, 300, 400));
        scoreStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}