package core.body.ui;

import core.body.ColorValue;
import core.util.MathUtil;

public class Button extends Component
{
    public OnClickListener onClickListener;
    public OnHoverListener onHoverListener;
    private boolean isPressed = false;
    private boolean isHovering = false;
    private boolean wasHovering = false;
    public Button()
    {
    }

    public Button(float width, float height) {
        super(width, height);
    }

    public Button(float width, float height, Color bc)
    {
        super(width, height, bc);
    }

    public boolean isPressed()
    {
        return isPressed;
    }
    public boolean isHovering()
    {
        return isHovering;
    }

    public Button onHover(ColorValue color)
    {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                onHoverListener = new OnHoverListener() {
                    ColorValue normal = bc.color;
                    @Override
                    public void onEnter(Button button) {
                        bc.color = color;
                    }

                    @Override
                    public void onLeave(Button button) {
                        bc.color = normal;
                    }
                };
            }
        };

        if(isCreated() || bc != null)
            r.run();
        else
            doOnCreate(r);
        return this;
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);

        if(MathUtil.pointInQuad(this, (float) scene.mouseInput.getX(), (float) scene.mouseInput.getY()) &&
                !isOutsideParentBounds((float) scene.mouseInput.getX(), (float) scene.mouseInput.getY()))
        {
            if(scene.mouseInput.isLeftButtonPressed() || (isPressed && scene.mouseInput.isLeftButtonPressed()))
            {
                isPressed = true;
            }
            else
            {
                if(onClickListener != null)
                    if (isPressed)
                        onClickListener.onClick(Button.this);
                isPressed = false;
            }
            if(!isHovering)
            {
                if(onHoverListener != null) {
                    onHoverListener.onEnter(Button.this);
                    wasHovering = true;
                }
                isHovering = true;
            }
            else
            {
                isHovering = false;
            }
        }
        else if(isPressed && !scene.mouseInput.isLeftButtonPressed() || wasHovering)
        {
            if(wasHovering)
            {
                wasHovering = false;
                if(onHoverListener != null)
                    onHoverListener.onLeave(this);
            }
            if(isPressed && !scene.mouseInput.isLeftButtonPressed())
            {
                if(onClickListener != null)
                    onClickListener.onClick(Button.this);
                isPressed = false;
            }
        }
    }

    public interface OnClickListener
    {
        void onClick(Button button);
    }
    public interface OnRightClickListener
    {
        void onClick(Button button);
    }
    public interface OnHoverListener
    {
        void onEnter(Button button);
        void onLeave(Button button);
    }
}
