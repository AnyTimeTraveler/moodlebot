import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.GotoCallback;
import org.simonscode.telegrammenulibrary.SimpleMenu;

public class CleanupTest {
    public static void main(String[] args) {
        SimpleMenu sm = new SimpleMenu();

        Callback ca = new GotoCallback(sm);
        Callback cb = new GotoCallback(sm);

        System.out.println(ca.equals(cb));
    }
}
