package res;

public enum Res {
    startMessage ("Привет, я чат-бот! Чтобы узнать что я умею, напишите \"Помощь\""),
    helpMessage ( """
            Я умею:
            -Задать вам вопрос и проверить ваш ответ. Для этого напишите "Задай вопрос"
            -Ну и здороваться могу, да. Просто напишите "Привет\""""
    ),
    greetingMessage ("Привет)"),
    questionMessage1 ("Солнце - это звезда?"),
    questionMessage2 ("Земля крутится вокруг солнца?"),
    questionMessage3 ("Луна - это планета?"),
    answerMessage1 ("Да"),
    answerMessage2 ("Да"),
    answerMessage3 ("Нет"),
    errorMessage ("Я ничего не понял! Чтобы узнать, что я умею, введите \"Помощь\"");

    private final String message;

    public String getMessage() {
        return message;
    }
    Res(String messages) {
        this.message = messages;
    }
}
