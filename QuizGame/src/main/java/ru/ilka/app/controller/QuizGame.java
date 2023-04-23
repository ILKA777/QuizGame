package ru.ilka.app.controller;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ilka.app.model.Question;

import java.io.IOException;
import java.util.Scanner;

public class QuizGame {
    private static String URL;
    private OkHttpClient client;
    private Gson gson;
    private int score;

    public QuizGame(String url) {
        client = new OkHttpClient();
        gson = new Gson();
        score = 0;
        URL = url;
    }

    public Question fetchQuestion() throws IOException {
        Request request = new Request.Builder().url(URL).build();
        Response response = client.newCall(request).execute();
        System.out.println(response.code());

        if (!response.isSuccessful()) {
            if (response.code() >= 500) {
                throw new IOException("Ошибка на сервере (ошибка 500). Повторите запрос позже.");
            } else {
                throw new IOException("Не удалось получить вопрос. Код ответа: " + response.code());
            }
        }

        String json = response.body().string();
        Question[] questions = gson.fromJson(json, Question[].class);
        return questions[0];
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        boolean playing = true;
        int answered = 0;
        int correctQuestions = 0;
        int errorCount = 0;
        final int maxErrors = 3;

        System.out.println("Добро пожаловать на матч века онлайн!");
        System.out.println("Если хотите выйти введите 'exit'.");

        while (playing) {
            Question question;
            try {
                question = fetchQuestion();
            } catch (IOException e) {
                System.out.println("Ошибка при получении вопроса: " + e.getMessage());
                errorCount++;
                if (errorCount >= maxErrors) {
                    System.out.println("Превышено максимальное количество ошибок (" + maxErrors + ")." +
                            " Игра завершается.");
                    break;
                }
                System.out.println("Попробуйте еще раз.");
                continue;
            }

            // Сбросываем счетчика ошибок после успешного получения вопроса.
            errorCount = 0;

            System.out.println("Категория: " + question.getCategory().getTitle());
            System.out.println("Вопрос: " + question.getQuestion());
            System.out.println("Ответ: ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("exit")) {
                playing = false;
            } else {
                answered++;
                if (answer.equalsIgnoreCase(question.getAnswer())) {
                    System.out.println("Правильно!");
                    score += question.getValue();
                    correctQuestions++;
                } else {
                    System.out.println("Неправильно! Правильный ответ: " + question.getAnswer());
                }
                System.out.println("Текущий счет: " + score);
            }
        }

        System.out.println("Игра окончена!");
        System.out.println("Всего вопросов: " + answered);
        System.out.println("Правильных ответов: " + correctQuestions);
        System.out.println("Общий счет: " + score);
    }
}
