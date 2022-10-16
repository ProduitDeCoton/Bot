package logic;

import res.*;

public abstract class BotLogic {
    public abstract void say(String message);
    public abstract String getUserMessage();
    public abstract String waitMessage();

    public void startQuiz() {
        // TODO 2: подумать над удобным хранением вопросов, чтобы можно было обернуть в цикл
        int nCorrectAnswers = 0;
        say(BotMessages.question1.getMessage());
        String answer = waitMessage();
        if (answer.equals(BotMessages.expectedAnswer1.getMessage())) {
            say(BotMessages.ifCorrect.getMessage());
        } else {
            say(BotMessages.ifWrong.getMessage() + BotMessages.expectedAnswer1.getMessage());
            nCorrectAnswers++;
        }

        say(BotMessages.question2.getMessage());
        answer = waitMessage();
        if (answer.equals(BotMessages.expectedAnswer2.getMessage())) {
            say(BotMessages.ifCorrect.getMessage());
        } else {
            say(BotMessages.ifWrong.getMessage() + BotMessages.expectedAnswer2.getMessage());
            nCorrectAnswers++;
        }

        say(BotMessages.question3.getMessage());
        answer = waitMessage();
        if (answer.equals(BotMessages.expectedAnswer3.getMessage())) {
            say(BotMessages.ifCorrect.getMessage());
        } else {
            say(BotMessages.ifWrong.getMessage() + BotMessages.expectedAnswer3.getMessage());
            nCorrectAnswers++;
        }


        if (nCorrectAnswers == 3) say(BotMessages.success.getMessage());
        else say(BotMessages.failure.getMessage());
    }

    public void sendAnswer(String userMessage) {
        switch (userMessage) {
            case UserCommands.start -> say(BotMessages.start.getMessage());
            case UserCommands.help -> say(BotMessages.help.getMessage());
            case UserCommands.startQuiz -> startQuiz();
            case UserCommands.hello -> say(BotMessages.greetings.getMessage());
            default -> say(BotMessages.error.getMessage());
        }
    }

}

