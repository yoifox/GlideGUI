package core.body.ui;

import core.body.Texture;
import org.joml.Vector2f;
import core.body.Body2d;
import core.body.ColorValue;
import core.utils.MathUtils;
import core.utils.Transformation;

public class Component extends Body2d
{
    public float width = 0, height = 0;
    public boolean isWidthPercentage = false;
    public boolean isHeightPercentage = false;
    public boolean isXPercentage = false;
    public boolean isYPercentage = false;
    public float[] margin = new float[] {0, 0, 0, 0}; //only pixels
    public float[] uvs = new float[] {0, 0, 0, 1, 1, 0, 1, 1};
    public boolean[] isUvPercentage = new boolean[] {true, true, true, true, true, true, true, true};
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
    public Component shadow(int px, float x, float y)
    {
        shadow(px, x, y, ColorValue.COLOR_BLACK);
        return this;
    }
    public Component shadow(int px, float x, float y, ColorValue color)
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
    public Component boxShadow(int px, float x, float y)
    {
        boxShadow(px, x, y, ColorValue.COLOR_BLACK);
        return this;
    }
    public Component boxShadow(int px, float x, float y, ColorValue color)
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

    public Component positionEachFrame(float x, float y)
    {
        doEveryFrame(new Runnable() {
            @Override
            public void run() {
                Component.this.x = x;
                Component.this.y = y;
                isXPercentage = true;
                isYPercentage = true;
                origin = ORIGIN_CENTER;
            }
        });
        return this;
    }

    public Component center()
    {
        x = 0.5f;
        y = 0.5f;
        isXPercentage = true;
        isYPercentage = true;
        origin = ORIGIN_CENTER;
        return this;
    }

    public Component fill()
    {
        x = 1;
        y = 1;
        isXPercentage = true;
        isYPercentage = true;
        origin = ORIGIN_TOP_LEFT;
        return this;
    }

    public Component setRoundness(float topLeft, float topRight, float bottomLeft, float bottomRight)
    {
        bc.roundness = new float[] {topLeft, topRight, bottomLeft, bottomRight};
        return this;
    }

    public Component setOrigin(int origin)
    {
        this.origin = origin;
        return this;
    }

    public Component setBorder(float width, ColorValue color)
    {
        bc.borderWidth = width;
        bc.borderColor = color;
        return this;
    }

    public Component setBackground(ColorValue color)
    {
        if(bc.texture != null) bc.texture = null;
        bc.color = color;
        return this;
    }

    public Component setBackground(Texture texture)
    {
        bc.texture = texture;
        return this;
    }

    //Returns width in pixels
    public float getWidth()
    {
        if(parent instanceof Component component)
            return isWidthPercentage ? width * component.getWidth() : width;
        else
            return isWidthPercentage ? width * scene.window.getWidth() : width;
    }

    //Returns height in pixels
    public float getHeight()
    {
        if(parent instanceof Component component)
            return isHeightPercentage ? height * component.getHeight() : height;
        else
            return isHeightPercentage ? height * scene.window.getHeight() : height;
    }

    //Returns x in pixels
    public float getX()
    {
        if(parent instanceof Component component)
            return isXPercentage ? Transformation.getTopLeftCorner(component).x + x * component.getWidth() : x;
        else
            return isXPercentage ? x * scene.window.getWidth() : x;
    }

    //Returns y in pixels
    public float getY()
    {
        if(parent instanceof Component component)
            return isYPercentage ? Transformation.getTopLeftCorner(component).y + y * component.getHeight() : y;
        else
            return isYPercentage ? y * scene.window.getHeight() : y;
    }

    //returns pixel value of roundness
    public float[] getRoundness()
    {
        if(bc == null) return new float[]{0, 0, 0, 0};
        float max = Math.max(width, height);
        return new float[] {bc.isRoundnessPercentage[0] ? bc.roundness[0] * max : bc.roundness[0],
                            bc.isRoundnessPercentage[1] ? bc.roundness[1] * max : bc.roundness[1],
                            bc.isRoundnessPercentage[2] ? bc.roundness[2] * max : bc.roundness[2],
                            bc.isRoundnessPercentage[3] ? bc.roundness[3] * max : bc.roundness[3]};
    }

    //returns pixel value of uvs
    public float[] getUvs()
    {
        return new float[] {isUvPercentage[0] ? uvs[0] * getWidth() : uvs[0],
                            isUvPercentage[1] ? uvs[1] * getHeight() : uvs[1],
                            isUvPercentage[2] ? uvs[2] * getWidth() : uvs[2],
                            isUvPercentage[3] ? uvs[3] * getHeight() : uvs[3],
                            isUvPercentage[4] ? uvs[4] * getWidth() : uvs[4],
                            isUvPercentage[5] ? uvs[5] * getHeight() : uvs[5],
                            isUvPercentage[6] ? uvs[6] * getWidth() : uvs[6],
                            isUvPercentage[7] ? uvs[7] * getHeight() : uvs[7]};
    }

    public Vector2f getTopLeftCorner()
    {
        return Transformation.getTopLeftCorner(this);
    }

    public boolean isOutsideParentBounds(float x, float y)
    {
        if(parent instanceof Component component)
        {
            if(visibleOutsideParentBounds) return false;
            return !MathUtils.pointInQuad(component, x, y);
        }
        return !scene.mouseInput.isInWindow();
    }
}
