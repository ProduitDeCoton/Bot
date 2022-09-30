import java.util.Scanner;

public class Console implements Dialogs {

    @Override
    public void say(String message) {
        System.out.println(message);
    }

    @Override
    public String getMessage() {
        Scanner message = new Scanner(System.in);
        return message.toString();
    }
}
