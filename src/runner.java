import java.util.ArrayList;
import java.util.Scanner;
public class runner {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<String> result;
        String text;
        String error;

        while(true) {
            System.out.print("basic > ");
            text = input.nextLine();
            basic.run("<stdin>", text);

            result = basic.getTokens();
            error = basic.getError();

            if (!error.isEmpty()) {
                System.out.println(error);
            } else {
                System.out.println(result);
            }
        }

    }

}
