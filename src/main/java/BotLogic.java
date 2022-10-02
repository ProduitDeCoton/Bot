import res.*;

public abstract class BotLogic {
    public abstract void say(String message);
    public abstract String getAnswer();

    public void startQuiz() {
        // TODO 2: подумать над удобным хранением вопросов, чтобы можно было обернуть в цикл
        int nCorrectAnswers = 0;
        say(BotMessages.question1.getMessage());
        String answer = getAnswer();
        if (answer.equals(BotMessages.expectedAnswer1.getMessage())) {
            say(BotMessages.ifCorrect.getMessage());
        } else {
            say(BotMessages.ifWrong.getMessage() + BotMessages.expectedAnswer1.getMessage());
            nCorrectAnswers++;
        }

        say(BotMessages.question2.getMessage());
        answer = getAnswer();
        if (answer.equals(BotMessages.expectedAnswer2.getMessage())) {
            say(BotMessages.ifCorrect.getMessage());
        } else {
            say(BotMessages.ifWrong.getMessage() + BotMessages.expectedAnswer2.getMessage());
            nCorrectAnswers++;
        }

        say(BotMessages.question3.getMessage());
        answer = getAnswer();
        if (answer.equals(BotMessages.expectedAnswer3.getMessage())) {
            say(BotMessages.ifCorrect.getMessage());
        } else {
            say(BotMessages.ifWrong.getMessage() + BotMessages.expectedAnswer3.getMessage());
            nCorrectAnswers++;
        }


        if (nCorrectAnswers == 3) say(BotMessages.success.getMessage());
        else say(BotMessages.failure.getMessage());
    }

    void startBot() {
        say(BotMessages.start.getMessage());
        while (true) {
            String answer = getAnswer();
            // TODO 1: переделать на switch-case
            if (Commands.help.getCommand().equals(answer)) {
                say(BotMessages.help.getMessage());
            } else if (Commands.startQuiz.getCommand().equals(answer)) {
                startQuiz();
            } else if (Commands.hello.getCommand().equals(answer)) {
                say(BotMessages.greetings.getMessage());
            }
            else {
                say(BotMessages.error.getMessage());
            }
        }
    }
}
