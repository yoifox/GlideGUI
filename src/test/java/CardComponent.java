import core.body.ui.Button;
import core.body.ui.Color;

public class CardComponent extends Button
{
    public Card card;
    public boolean isSelected = false;

    public CardComponent(float width, float height, Color bc)
    {
        super(width, height, bc);
    }
}
