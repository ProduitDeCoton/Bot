package res;

public enum Commands {
    help("/help"),
    hello("Привет"),
    startQuiz("Задай вопросы");

    private final String command;

    public String getCommand() { return command; }

    Commands(String command) {
        this.command = command;
    }
}
