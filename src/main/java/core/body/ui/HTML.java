package core.body.ui;

import org.fit.cssbox.io.*;
import org.w3c.dom.Document;
import java.io.InputStream;

public class HTML extends Component
{
    public HTML(float width, float height) {
        super(width, height);
    }

    public HTML() {

    }

    private Document w3cDoc;
    private DOMSource docSource;
    public void open(String url)
    {
        openHTML(url);
    }

    public void open(InputStream is)
    {
        openHTML(is);
    }
}
