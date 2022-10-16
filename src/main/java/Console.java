import java.util.Scanner;

public class Console extends BotLogic {

    @Override
    public void say(String message) {
        System.out.println(message);
    }

    @Override
    public String getAnswer() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static void main(String[] args) {
        BotLogic consoleBot = new Console();
        consoleBot.startBot();
    }
}