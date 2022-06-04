package core.body.ui;

import core.body.Texture;
import org.joml.Vector2f;
import core.body.Body2d;
import core.body.ColorValue;
import core.util.MathUtil;
import core.util.Transformation;

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
}
