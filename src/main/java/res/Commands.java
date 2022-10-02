package res;

public enum Commands {
    yes("Да"),
    no("Нет"),
    help("/help"),
    hello("Привет"),
    startQuiz("Задай вопросы");

    private final String command;

    public String getCommand() { return command; }

    Commands(String command) {
        this.command = command;
    }
}
