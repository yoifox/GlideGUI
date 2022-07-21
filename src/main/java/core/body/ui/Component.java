package core.body.ui;

import core.body.Texture;
import cz.vutbr.web.css.MediaSpec;
import cz.vutbr.web.css.MediaSpecAll;
import org.fit.cssbox.awt.GraphicsEngine;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DefaultDocumentSource;
import org.fit.cssbox.io.StreamDocumentSource;
import org.fit.cssbox.layout.Dimension;
import org.joml.Vector2f;
import core.body.Body2d;
import core.body.ColorValue;
import core.util.MathUtil;
import core.util.Transformation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Component extends Body2d
{
    public float width = 0, height = 0;
    public boolean isWidthPercentage = false;
    public boolean isHeightPercentage = false;
    public boolean isXPercentage = false;
    public boolean isYPercentage = false;
    public float[] margin = new float[] {0, 0, 0, 0}; //only pixels
    public float[] uvs = new float[] {0, 0, 0, 1, 1, 0, 1, 1}; //only percentage of an image
    public int origin = ORIGIN_TOP_LEFT;
    public Color bc;
    public boolean visibleOutsideParentBounds = false;

    public boolean isConstraint = false;
    public Component topOf;
    public Component bottomOf;
    public Component startOf;
    public Component endOf;

    public static final int MARGIN_LEFT = 0, MARGIN_RIGHT = 1, MARGIN_TOP = 2, MARGIN_BOTTOM = 3;

    public static final int UV_TOP_LEFT_U = 0, UV_TOP_LEFT_V = 1, UV_BOTTOM_LEFT_U = 2, UV_BOTTOM_LEFT_V = 3,
            UV_TOP_RIGHT_U = 4, UV_TOP_RIGHT_V = 5, UV_BOTTOM_RIGHT_U = 6, UV_BOTTOM_RIGHT_V = 7;

    public static final int ORIGIN_TOP_LEFT = 0, ORIGIN_TOP_RIGHT = 1, ORIGIN_BOTTOM_LEFT = 2, ORIGIN_BOTTOM_RIGHT = 3, ORIGIN_CENTER = 4;

    public Component()
    {
        bc = new Color();
    }
    public Component(float width, float height)
    {
        this.width = width;
        this.height = height;
        bc = new Color();
    }
    public Component(float width, float height, Color bc)
    {
        this.width = width;
        this.height = height;
        this.bc = bc == null ? new Color() : bc;
    }
    public Component(float x, float y, float rotation, float scaleX, float scaleY, float width, float height, Color bc)
    {
        super(x, y, rotation, scaleX, scaleY);
        this.width = width;
        this.height = height;
        this.bc = bc == null ? new Color() : bc;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(isConstraint)
        {
            if(topOf != null)
            {
                Vector2f pos = Transformation.getTopLeftCorner(topOf);
                setPosition(x, pos.y + topOf.getHeight() - margin[MARGIN_BOTTOM]);
            }
            if(bottomOf != null)
            {
                Vector2f pos = Transformation.getTopLeftCorner(bottomOf);
                setPosition(x, pos.y + margin[MARGIN_TOP]);
            }
            if(startOf != null)
            {
                Vector2f pos = Transformation.getTopLeftCorner(startOf);
                setPosition(pos.x + startOf.getWidth() - margin[MARGIN_RIGHT], y);
            }
            if(endOf != null)
            {
                Vector2f pos = Transformation.getTopLeftCorner(endOf);
                setPosition(pos.x + margin[MARGIN_LEFT], y);
            }
        }
    }

    public Component gradComp;
    public ColorValue gradCompColor;
    public boolean isGradComp = false;
    public final Component shadow(int px, float x, float y)
    {
        shadow(px, x, y, ColorValue.COLOR_BLACK);
        return this;
    }

    public final Component shadow(int px, float x, float y, Component shadowComp)
    {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                gradComp = shadowComp;
                gradComp.isGradComp = true;
                gradComp.visibleOutsideParentBounds = true;
                gradComp.bc = new Color(scene.grad);
                addChild(gradComp);
                gradComp.center();
                gradComp.x = x + 0.5f;
                gradComp.y = y + 0.5f;
            }
        };
        if(!isCreated())
        {
            doOnCreate(doRun);
        }
        else
        {
            doRun.run();
        }
        return this;
    }

    public final Component shadow(int px, float x, float y, ColorValue color)
    {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                gradComp = new Component(getWidth() + px + 16 * (getWidth() / scene.grad.getWidth()), getHeight() + px + 16 * (getHeight() / scene.grad.getHeight()));
                gradComp.isGradComp = true;
                gradComp.gradCompColor = color;
                gradComp.visibleOutsideParentBounds = true;
                gradComp.bc = new Color(scene.grad);
                doEveryFrame(new Runnable() {
                    @Override
                    public void run() {
                        if(gradComp != null && gradComp.isCreated())
                        {
                            gradComp.width = getWidth() + px + 16 * (getWidth() / scene.grad.getWidth());
                            gradComp.height = getHeight() + px + 16 * (getHeight() / scene.grad.getHeight());
                        }
                    }
                });
                addChild(gradComp);
                gradComp.center();
                gradComp.x = x + 0.5f;
                gradComp.y = y + 0.5f;
            }
        };
        if(!isCreated())
        {
            doOnCreate(doRun);
        }
        else
        {
            doRun.run();
        }
        return this;
    }
    public final Component boxShadow(int px, float x, float y)
    {
        boxShadow(px, x, y, ColorValue.COLOR_BLACK);
        return this;
    }
    public final Component boxShadow(int px, float x, float y, ColorValue color)
    {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                gradComp = new Component(getWidth() + px + 16 * (getWidth() / scene.boxShadow.getWidth()), getHeight() + px + 16 * (getHeight() / scene.boxShadow.getHeight()));
                gradComp.isGradComp = true;
                gradComp.gradCompColor = color;
                gradComp.visibleOutsideParentBounds = true;
                gradComp.bc = new Color(scene.boxShadow);
                doEveryFrame(new Runnable() {
                    @Override
                    public void run() {
                        if(gradComp != null && gradComp.isCreated())
                        {
                            gradComp.width = getWidth() + px + 16 * (getWidth() / scene.boxShadow.getWidth());
                            gradComp.height = getHeight() + px + 16 * (getHeight() / scene.boxShadow.getHeight());
                        }
                    }
                });
                addChild(gradComp);
                gradComp.center();
                gradComp.x = x + 0.5f;
                gradComp.y = y + 0.5f;
            }
        };
        if(!isCreated())
            doOnCreate(doRun);
        else
            doRun.run();
        return this;
    }

    public final Component positionEachFrame(String x, String y)
    {
        doEveryFrame(new Runnable() {
            @Override
            public void run() {
                position(x, y);
            }
        });
        return this;
    }

    public final Component center()
    {
        x = 0.5f;
        y = 0.5f;
        isXPercentage = true;
        isYPercentage = true;
        origin = ORIGIN_CENTER;
        return this;
    }

    public final Component fill()
    {
        x = 1;
        y = 1;
        isXPercentage = true;
        isYPercentage = true;
        origin = ORIGIN_TOP_LEFT;
        return this;
    }

    public final Component setRoundness(float topLeft, float topRight, float bottomLeft, float bottomRight)
    {
        bc.roundness = new float[] {topLeft, topRight, bottomLeft, bottomRight};
        return this;
    }

    public final Component setOrigin(int origin)
    {
        this.origin = origin;
        return this;
    }

    public final Component setBorder(float width, ColorValue color)
    {
        bc.borderWidth = width;
        bc.borderColor = color;
        return this;
    }

    public final Component setBackground(ColorValue color)
    {
        if(bc.texture != null) bc.texture = null;
        bc.color = color;
        return this;
    }

    public final Component setBackground(Texture texture)
    {
        bc.texture = texture;
        return this;
    }

    public final Component position(String x, String y)
    {
        if(x.matches("(\\d*\\.)?\\d+%"))
        {
            float xPos = Float.parseFloat(x.replace("%", ""));
            isXPercentage = true;
            this.x = xPos / 100f;
        }
        else if(x.matches("(\\d*\\.)?\\d+"))
        {
            float xPos = Float.parseFloat(x);
            isXPercentage = false;
            this.x = xPos;
        }
        else if(!x.equals(""))
        {
            throw new RuntimeException(x + " isn't a valid position value.");
        }

        if(y.matches("(\\d*\\.)?\\d+%"))
        {
            float yPos = Float.parseFloat(y.replace("%", ""));
            isYPercentage = true;
            this.y = yPos / 100f;
        }
        else if(y.matches("(\\d*\\.)?\\d+"))
        {
            float yPos = Float.parseFloat(y);
            isYPercentage = false;
            this.y = yPos;
        }
        else if(!y.equals(""))
        {
            throw new RuntimeException(y + " isn't a valid position value.");
        }

        return this;
    }

    public final Component setDimensions(String width, String height)
    {
        if(width.matches("(\\d*\\.)?\\d+%"))
        {
            float newWidth = Float.parseFloat(width.replace("%", ""));
            isWidthPercentage = true;
            this.width = newWidth / 100f;
        }
        else if(width.matches("(\\d*\\.)?\\d+"))
        {
            float newWidth = Float.parseFloat(width);
            isWidthPercentage = false;
            this.width = newWidth;
        }
        else if(!width.equals(""))
        {
            throw new RuntimeException(width + " isn't a valid dimension value.");
        }

        if(height.matches("(\\d*\\.)?\\d+%"))
        {
            float newHeight = Float.parseFloat(height.replace("%", ""));
            isHeightPercentage = true;
            this.height = newHeight / 100f;
        }
        else if(height.matches("(\\d*\\.)?\\d+"))
        {
            float newHeight = Float.parseFloat(height);
            isHeightPercentage = false;
            this.height = newHeight;
        }
        else if(!height.equals(""))
        {
            throw new RuntimeException(height + " isn't a valid dimension value.");
        }
        return this;
    }

    //Returns width in pixels
    public final float getWidth()
    {
        if(parent instanceof Component component)
            return Math.abs(isWidthPercentage ? width * component.getWidth() : width);
        else
            return Math.abs(isWidthPercentage ? width * scene.window.getWidth() : width);
    }

    //Returns height in pixels
    public final float getHeight()
    {
        if(parent instanceof Component component)
            return Math.abs(isHeightPercentage ? height * component.getHeight() : height);
        else
            return Math.abs(isHeightPercentage ? height * scene.window.getHeight() : height);
    }

    //Returns x in pixels
    public final float getX()
    {
        if(parent instanceof Component component)
            return isXPercentage ? Transformation.getTopLeftCorner(component).x + x * component.getWidth() : x;
        else
            return isXPercentage ? x * scene.window.getWidth() : x;
    }

    //Returns y in pixels
    public final float getY()
    {
        if(parent instanceof Component component)
            return isYPercentage ? Transformation.getTopLeftCorner(component).y + y * component.getHeight() : y;
        else
            return isYPercentage ? y * scene.window.getHeight() : y;
    }

    //returns pixel value of roundness
    public final float[] getRoundness()
    {
        if(bc == null) return new float[]{0, 0, 0, 0};
        float max = Math.max(width, height);
        return new float[] {bc.isRoundnessPercentage[0] ? bc.roundness[0] * max : bc.roundness[0],
                            bc.isRoundnessPercentage[1] ? bc.roundness[1] * max : bc.roundness[1],
                            bc.isRoundnessPercentage[2] ? bc.roundness[2] * max : bc.roundness[2],
                            bc.isRoundnessPercentage[3] ? bc.roundness[3] * max : bc.roundness[3]};
    }

    //returns pixel value of uvs
    public final float[] getUvs()
    {
        return uvs;
    }

    public final Vector2f getTopLeftCorner()
    {
        return Transformation.getTopLeftCorner(this);
    }

    public final boolean isOutsideParentBounds(float x, float y)
    {
        if(parent instanceof Component component)
        {
            if(visibleOutsideParentBounds) return false;
            return !MathUtil.pointInQuad(component, x, y);
        }
        return !scene.mouseInput.isInWindow();
    }


    private Document w3cDoc;
    private DOMSource docSource;
    public Component openHTML(String url)
    {
        docSource = null;
        try {
            docSource = new DefaultDOMSource(new DefaultDocumentSource(url));
            w3cDoc = docSource.parse();
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BufferedImage img = null;
                try {
                    img = renderDocument(w3cDoc);
                    bc.texture = scene.objectLoader.loadTexture(img);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        };
        if(isCreated())
            runnable.run();
        else
            doOnCreate(runnable);
        return this;
    }

    public Component openHTML(InputStream is)
    {
        docSource = null;
        try {
            docSource = new DefaultDOMSource(new StreamDocumentSource(is, new URL("http://nothing.com/"), "html/css"));
            w3cDoc = docSource.parse();
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BufferedImage img = null;
                try {
                    img = renderDocument(w3cDoc);
                    bc.texture = scene.objectLoader.loadTexture(img);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        };
        if(isCreated())
            runnable.run();
        else
            doOnCreate(runnable);
        return this;
    }

    private BufferedImage renderDocument(org.w3c.dom.Document doc) throws MalformedURLException
    {
        MediaSpec media = new MediaSpecAll();
        media.setDimensions(getWidth(), getHeight());
        media.setDeviceDimensions(getWidth(), getHeight());

        DOMAnalyzer da = new DOMAnalyzer(doc, docSource.getDocumentSource().getURL());
        da.setMediaSpec(media);
        da.attributesToStyles();
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.addStyleSheet(null, CSSNorm.formsStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.getStyleSheets();

        GraphicsEngine engine = new GraphicsEngine(da.getRoot(), da, docSource.getDocumentSource().getURL())
        {
            @Override
            protected void setupGraphics(Graphics2D g)
            {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
                g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            }
        };
        engine.setUseKerning(true);
        engine.setAutoMediaUpdate(true);
        engine.getConfig().setClipViewport(true);
        engine.getConfig().setLoadImages(true);
        engine.getConfig().setLoadBackgroundImages(true);

        engine.createLayout(new Dimension((int) getWidth(), (int) getHeight()));
        return engine.getImage();
    }
}
