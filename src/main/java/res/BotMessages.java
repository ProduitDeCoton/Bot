package res;

public enum BotMessages {
    start("Привет, я чат-бот! Чтобы узнать что я умею, напишите \"/help\""),
    help( """
            Я умею:
            -Задать вам вопрос и проверить ваш ответ. Для этого напишите "Задай вопросы"
            -Ну и здороваться могу, да. Просто напишите "Привет\""""
    ),
    greetings("Привет)"),
    question1("Солнце - это звезда?"),
    question2("Земля крутится вокруг солнца?"),
    question3("Луна - это планета?"),
    expectedAnswer1("Да"),
    expectedAnswer2("Да"),
    expectedAnswer3("Нет"),
    ifCorrect("Правильно!"),
    ifWrong ("Неправильно :("),
    success("Викторина закончилась. Вы победили!"),
    failure("Викторина закончилась. Better luck next time!"),
    error("Я ничего не понял! Чтобы узнать, что я умею, введите \"Помощь\"");
    private final String message;

    public String getMessage() {
        return message;
    }
    BotMessages(String messages) {
        this.message = messages;
    }
}
