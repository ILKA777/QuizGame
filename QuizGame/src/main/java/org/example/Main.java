package org.example;

import ru.ilka.app.controller.QuizGame;

public class Main {
    public static void main(String[] args) {
        // Предположим что наша программа может работать с разными api, поэтому мы передаем URL как параметр.
        QuizGame game = new QuizGame("http://jservice.io/api/random");
        game.startGame();
    }
}