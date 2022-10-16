import logic.BotLogic;

import java.util.Scanner;

public class Console extends BotLogic {

    @Override
    public void say(String message) {
        System.out.println(message);
    }

    @Override
    public String getUserMessage() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    @Override
    public String waitMessage() {
        return getUserMessage();
    }

    public static void main(String[] args) {
        BotLogic consoleBot = new Console();
        consoleBot.sendAnswer(consoleBot.getUserMessage());
    }
}