package test;

import core.Scene;
import core.body.*;
import core.body.ui.*;
import core.util.Transformation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class Test extends Scene
{
    @Override
    public void onCreate() {
        super.onCreate();
        Component component = new Component(100, 100, Color.COLOR_RED);
        addBody(component);

        String css = """
                <style>
                    body {
                        background-image: linear-gradient(to right, blue , purple);
                    }
                </style>
                <body>
                </body>
                """;
        Component component1 = new Component(100, 100)
                .openHTML(new ByteArrayInputStream(css.getBytes(StandardCharsets.UTF_8)))
                .position("110", "0");
        addBody(component1);

        Component component2 = new Component(100, 100)
                .openHTML(new ByteArrayInputStream(css.getBytes(StandardCharsets.UTF_8)))
                .position("220", "0")
                .setBorder(4, ColorValue.COLOR_GRAY);
        addBody(component2);

        Component component3 = new Component(100, 100)
                .openHTML(new ByteArrayInputStream(css.getBytes(StandardCharsets.UTF_8)))
                .position("330", "0")
                .setBorder(4, ColorValue.COLOR_GRAY)
                .setRoundness(32, 32, 32, 32);
        addBody(component3);

        Component component4 = new Component(100, 100)
                .openHTML(new ByteArrayInputStream(css.getBytes(StandardCharsets.UTF_8)))
                .position("440", "0")
                .setBorder(4, ColorValue.COLOR_GRAY)
                .setRoundness(32, 32, 32, 32)
                .shadow(42, 0.04f, 0.04f, new ColorValue(0, 0, 0, 0.7f));
        addBody(component4);

        Button button = ((Button) ((Button) new Button(100, 100)
                .setBackground(ColorValue.COLOR_LIGHT_GRAY)
                .setBorder(2, ColorValue.COLOR_GRAY))
                .onHover(ColorValue.COLOR_GRAY)
                .position("550", "0")
                .addChild(new Text("Button", 32, fontRoboto).center()));
        addBody(button);

        VerticalList verticalList = new VerticalList(200, 500, 1, new ArrayList<>());
        for(int i = 0; i < 10; i++)
        {
            verticalList.components.add(((Button) ((Button) new Button(100, 100)
                    .setBackground(ColorValue.COLOR_LIGHT_GRAY)
                    .setBorder(2, ColorValue.COLOR_GRAY))
                    .onHover(ColorValue.COLOR_GRAY)
                    .position("550", "0")));
        }
        verticalList.position("660", "0");
        verticalList.adjustHeight = false;
        verticalList.setScrollbar(new VerticalScrollbar(10, Color.COLOR_GRAY, verticalList));
        verticalList.gravity = Layout.GRAVITY_TOP_MIDDLE;
        verticalList.bc = new Color(0.8f, 0.8f, 0.8f, 0.8f);
        addBody(verticalList);



        HorizontalList horizontalList = new HorizontalList(500, 200, 1, new ArrayList<>());
        for(int i = 0; i < 10; i++)
        {
            horizontalList.components.add(((Button) ((Button) new Button(100, 100)
                    .setBackground(ColorValue.COLOR_LIGHT_GRAY)
                    .setBorder(2, ColorValue.COLOR_GRAY))
                    .onHover(ColorValue.COLOR_GRAY)
                    .position("550", "0")));
        }
        horizontalList.position("0", "110");
        horizontalList.adjustWidth = false;
        horizontalList.setScrollbar(new HorizontalScrollbar(10, Color.COLOR_GRAY, horizontalList));
        horizontalList.gravity = Layout.GRAVITY_LEFT_MIDDLE;
        horizontalList.bc = new Color(0.8f, 0.8f, 0.8f, 0.8f);
        addBody(horizontalList);

        TextArea textArea = (TextArea) new TextArea(500, 200, 32, fontRoboto)
                .setBorder(2, ColorValue.COLOR_GRAY)
                .position("0", "330");
        addBody(textArea);

        String htmlStr = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                body {
                  margin: 0;
                }
                                
                .header {
                  background-color: #f1f1f1;
                  padding: 20px;
                  text-align: center;
                }
                </style>
                </head>
                <body>
                                
                <div class="header">
                <h1><span style="color: #ff0000;">HTML</span> + <span style="color: #33cccc;">CSS</span></h1>
                </div>
                
                <h4><span style="color: #ffff00;">text</span> <span style="color: #00ffff;">text</span> <span style="color: #993366;">text</span> <span style="color: #ff0000;">text</span> <span style="color: #000080;">text</span> <span style="color: #00ff00;">text</span> <span style="color: #008080;">text</span> <span style="color: #003300;">text</span> <span style="color: #333300;">text</span> <span style="color: #993300;">text</span> <span style="color: #000000;">text&nbsp;</span></h4>
                                
                </body>
                </html>
                """;
        Component html = new Component(1000, 200)
                .position("0", "540")
                .openHTML(new ByteArrayInputStream(htmlStr.getBytes(StandardCharsets.UTF_8)))
                .setBorder(2, ColorValue.COLOR_GRAY);
        addBody(html);

        Texture[] textures = new Texture[376];
        File dir = new File("C:\\Users\\User\\Desktop\\frames");
        int i = 0;
        for(File file : Objects.requireNonNull(dir.listFiles()))
        {
            textures[i] = objectLoader.loadTexture(file.getPath());
            i++;
        }
        AnimatedTexture texture = new AnimatedTexture(textures, 0.03f);
        Component animated = new Component(400, 260, new Color(texture));
        animated.position("500", "750");
        addBody(animated);

        Component image = new Component(400, 200)
                .openHTML(new ByteArrayInputStream("<p><img src=\"https://kerenagam.co.il/wp-content/uploads/2021/06/Thinking-of-getting-a-cat.png\" alt=\"https://kerenagam.co.il/wp-content/uploads/2021/06/Thinking-of-getting-a-cat.png\" width=\"400\" height=\"200\" /></p>".getBytes(StandardCharsets.UTF_8)))
                .position("0", "750");
        addBody(image);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        window.setTitle(1f / delta + "");
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.1f);
    }
}
