package logic;

import res.*;

public abstract class BotLogic {
    public abstract void say(String message);
    public abstract String getUserMessage();
    public abstract String waitMessage();

    public void startQuiz() {
        // TODO 2: подумать над удобным хранением вопросов, чтобы можно было обернуть в цикл
        int nCorrectAnswers = 0;

        say(BotMessages.question1);
        String answer = getUserMessage();
        if (answer.equals(BotMessages.expectedAnswer1)) {
            say(BotMessages.ifCorrect);
        } else {
            say(BotMessages.ifWrong + BotMessages.expectedAnswer1);
            nCorrectAnswers++;
        }

        say(BotMessages.question2);
        answer = getUserMessage();
        if (answer.equals(BotMessages.expectedAnswer2)) {
            say(BotMessages.ifCorrect);
        } else {
            say(BotMessages.ifWrong + BotMessages.expectedAnswer2);
            nCorrectAnswers++;
        }

        say(BotMessages.question3);
        answer = getUserMessage();
        if (answer.equals(BotMessages.expectedAnswer3)) {
            say(BotMessages.ifCorrect);
        } else {
            say(BotMessages.ifWrong + BotMessages.expectedAnswer3);
            nCorrectAnswers++;
        }


        if (nCorrectAnswers == 3) say(BotMessages.success);
        else say(BotMessages.failure);
    }

    public void sendAnswer(String userMessage) {
        switch (userMessage) {
            case UserCommands.start -> say(BotMessages.start);
            case UserCommands.help -> say(BotMessages.help);
            case UserCommands.startQuiz -> startQuiz();
            case UserCommands.hello -> say(BotMessages.greetings);
            default -> say(BotMessages.error);
        }
    }

}

