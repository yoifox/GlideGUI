import core.Display;
import core.Looper;
import core.Window;

import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException {
        Display.init();
        Looper.addWindow(new Window(false, 1000, 1000, new MainMenu()));
        Looper.start();
    }
}
