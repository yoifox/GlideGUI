import core.Display;
import core.Looper;
import core.Window;

public class Main
{
    public static void main(String[] args)
    {
        Display.init();
        Looper.addWindow(new Window(false, 1000, 1000, new MainMenu()));
        Looper.start(false);
    }
}
