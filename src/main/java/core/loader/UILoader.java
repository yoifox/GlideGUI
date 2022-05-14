package core.loader;

import org.jdom2.Element;
import core.body.ui.Button;
import core.body.ui.Component;
import core.body.ui.Text;

import java.util.Map;

public class UILoader
{
    public Map<String, Component> loadXML(String xml)
    {
        return null;
    }

    private Component getComponent(Element element)
    {
        Component component;
        if(element.getName().equals("Button"))
        {
            component = new Button();
        }
        else if(element.getName().equals("Text"))
        {
            component = new Text("", 12, null);
        }
        else
            component = new Component();
        return component;
    }
}
