import core.*;
import core.body.*;
import core.body.ui.*;
import java.util.*;

public class SinglePlayer extends Scene
{
    CardComponent[] cardComponents = new CardComponent[12];
    HorizontalList row1 = new HorizontalList();
    HorizontalList row2 = new HorizontalList();
    HorizontalList row3 = new HorizontalList();
    VerticalList table = new VerticalList();

    Stack<Card> deck = new Stack<>();

    Texture cards;
    //Texture cardsSelected;
    Color cardsBc;
    Color cardsSelectedBc;

    boolean gameEnded = false;

    List<CardComponent> selected = new ArrayList<>();

    int setFoundCount = 0;
    Text cardsLeft;
    Text time;
    Text setsFound;
    Component bc;

    @Override
    public void onCreate()
    {
        super.onCreate();
        window.setSizeLimits(750, 750, Window.UNLIMITED_SIZE, Window.UNLIMITED_SIZE);

        window.setTitle("Set");

        initTopText();

        bc = new Component(0.9f, 0.9f, new Color(new ColorValue(1, 1, 1, 0.85f), 0, null, new float[] {32, 32, 32, 32}));
        bc.boxShadow(48, 0, 0);
        bc.center(); bc.isWidthPercentage = true; bc.isHeightPercentage = true;
        addBody(bc);

        deck.addAll(Util.readAllCards());
        addBody(cardsLeft);
        addBody(time);
        addBody(setsFound);
        start();
    }

    void initTopText()
    {
        cardsLeft = new Text("Cards left: 69", 32, fontArial);
        cardsLeft.origin = Component.ORIGIN_CENTER;
        cardsLeft.x = 0.5f; cardsLeft.isXPercentage = true;
        cardsLeft.y = 24;
        cardsLeft.setTextColor(new ColorValue(0.1f, 0.1f, 0.1f, 1));

        time = new Text("Time: 0:00", 32, fontArial);
        time.origin = Component.ORIGIN_TOP_LEFT;
        time.x = 0.1f; time.isXPercentage = true;
        time.y = 6;
        time.setTextColor(new ColorValue(0.1f, 0.1f, 0.1f, 1));

        setsFound = new Text("Sets: 0", 32, fontArial);
        setsFound.origin = Component.ORIGIN_TOP_RIGHT;
        setsFound.x = 0.9f; setsFound.isXPercentage = true;
        setsFound.y = 6;
        setsFound.setTextColor(new ColorValue(0.1f, 0.1f, 0.1f, 1));
    }

    void start()
    {
        Collections.shuffle(deck);

        cards = objectLoader.loadTexture(getClass(), "/cards.png");
        //cardsSelected = objectLoader.loadTexture(getClass(), "/cardsSelected.png");

        cardsBc = new Color(cards);
        cardsSelectedBc = new Color(cards);
        cardsSelectedBc.addColor = new ColorValue(-0.3f, -0.3f, -0.3f, 0);

        for(int i = 0; i < 12; i++)
        {
            cardComponents[i] = new CardComponent(130, 203, Color.COLOR_BLACK);
            cardComponents[i].visibleOutsideParentBounds = true;
            onClick(cardComponents[i]);
        }

        initLists();

        table.components.add(row1);
        table.components.add(row2);
        table.components.add(row3);
        table.updateEveryFrame = true;
        table.space = 12;
        table.center();
        bc.addChild(table);
        fillCards();
    }

    boolean isAnimationPlaying = false;
    float animTime = 0;
    float gameTime = 0;
    @Override
    public void update(float delta)
    {
        super.update(delta);
        if(!Float.isInfinite(delta) && !gameEnded)
        {
            gameTime += delta;
            int secs = Math.round(gameTime);
            time.setText("Time: " + (secs % 3600) / 60 + ":" + ((secs % 60) < 10 ? ("0" + secs % 60) : secs % 60));
        }
        if(isAnimationPlaying)
        {
            if(animTime > 0.4)
            {
                animTime = 0;
                isAnimationPlaying = false;
                for(CardComponent cardComponent : selected)
                {
                    cardComponent.scaleX = 1;
                    cardComponent.scaleY = 1;
                }

                List<CardComponent> toReplace = new ArrayList<>(selected);
                for(CardComponent cardComponent : selected)
                    cardComponent.isSelected = false;
                selected.clear();
                for(CardComponent cardComponent : toReplace)
                    replaceCard(cardComponent);
                if(!Util.containsSet(cardComponents))
                {
                    noSet();
                }
                cardsLeft.setText("Cards left: " + deck.size());
            }
            else
            {
                for(CardComponent cardComponent : selected)
                {
                    if(cardComponent.scaleX > 0)
                    {
                        cardComponent.scaleX -= 2f * delta;
                        cardComponent.scaleY -= 2f * delta;
                    }
                }
                animTime += delta;
            }
        }
    }

    void finishGame()
    {
        gameEnded = true;
        for (CardComponent cardComponent : cardComponents)
            cardComponent.onClickListener = null;

        Component component = new Component(500, 270);
        component.bc = new Color(new ColorValue(1, 1, 1, 0.98f), 0, null, new float[] {48, 48, 48, 48});
        component.boxShadow(42, 0, 0);
        component.center();

        Text text = new Text("Game ended", 32, fontArial);
        text.center(); text.y = 0.17f;
        component.addChild(text);

        Button button = new Button(80, 48, new Color(new ColorValue(1, 1, 1, 1), 0, null, new float[] {32, 32, 32, 32}));
        button.center(); button.y = 0.7f;
        button.boxShadow(32, 0, 0, new ColorValue(1, 1, 1, 0.35f));
        component.addChild(button);
        Text p = new Text("Restart", 19, fontArial);
        p.center(); p.y = 0.53f;
        p.setTextColor(new ColorValue(0.5f, 0.5f, 0.5f, 1));
        button.addChild(p);

        button.onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(Button button) {
                gameEnded = true;
                changeScene(new SinglePlayer());
            }
        };

        button.onHoverListener = new Button.OnHoverListener() {
            @Override
            public void onEnter(Button button) {
                button.bc.color = new ColorValue(0.8f, 0.8f, 0.8f,1);
            }

            @Override
            public void onLeave(Button button) {
                button.bc.color = new ColorValue(1, 1, 1,1);
            }
        };

        bc.addChild(component);
    }

    void onClick(CardComponent card)
    {
        card.onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(Button button) {
                if(gameEnded) return;
                if(!card.isSelected)
                {
                    card.bc = cardsSelectedBc;
                    card.isSelected = true;
                    selected.add(card);
                    if(selected.size() == 3)
                    {
                        if(Util.isSet(selected.get(0).card, selected.get(1).card, selected.get(2).card))
                        {
                            isAnimationPlaying = true;
                            setFoundCount++;
                            setsFound.setText("Sets: " + setFoundCount);
                        }
                        else
                        {
                            for(CardComponent cardComponent : selected) {
                                cardComponent.isSelected = false;
                                cardComponent.bc = cardsBc;
                            }
                            selected.clear();
                        }
                    }
                }
                else
                {
                    card.bc = cardsBc;
                    card.isSelected = false;
                    selected.remove(card);
                }
            }
        };
    }

    void initLists()
    {
        row1.components.add(cardComponents[0]);
        row1.components.add(cardComponents[1]);
        row1.components.add(cardComponents[2]);
        row1.components.add(cardComponents[3]);

        row2.components.add(cardComponents[4]);
        row2.components.add(cardComponents[5]);
        row2.components.add(cardComponents[6]);
        row2.components.add(cardComponents[7]);

        row3.components.add(cardComponents[8]);
        row3.components.add(cardComponents[9]);
        row3.components.add(cardComponents[10]);
        row3.components.add(cardComponents[11]);

        row1.updateEveryFrame = true;
        row2.updateEveryFrame = true;
        row3.updateEveryFrame = true;

        row1.space = 12;
        row2.space = 12;
        row3.space = 12;
    }

    void replaceCard(CardComponent cardComponent)
    {
        if(deck.empty()) {
            finishGame();
            return;
        }
        Card card = deck.pop();
        cardComponent.uvs[Component.UV_TOP_LEFT_U] = card.startX / (float)cards.getWidth();
        cardComponent.uvs[Component.UV_TOP_LEFT_V] = card.endY / (float)cards.getHeight();
        cardComponent.uvs[Component.UV_BOTTOM_LEFT_U] = card.endX / (float)cards.getWidth();
        cardComponent.uvs[Component.UV_BOTTOM_LEFT_V] = card.endY / (float)cards.getHeight();
        cardComponent.uvs[Component.UV_BOTTOM_RIGHT_U] = card.endX / (float)cards.getWidth();
        cardComponent.uvs[Component.UV_BOTTOM_RIGHT_V] = card.startY / (float)cards.getHeight();
        cardComponent.uvs[Component.UV_TOP_RIGHT_U] = card.startX / (float)cards.getWidth();
        cardComponent.uvs[Component.UV_TOP_RIGHT_V] = card.startY / (float)cards.getHeight();

        cardComponent.bc = new Color(cards);
        cardComponent.card = card;
    }

    void noSet()
    {
        if(deck.size() == 0)
        {
            finishGame();
            return;
        }
        for(CardComponent cardComponent : cardComponents)
        {
            deck.add(cardComponent.card);
        }
        Collections.shuffle(deck);
        fillCards();
    }

    void fillCards()
    {
        for(int i = 0; i < 12; i++)
        {
            Card card = deck.pop();
            //cardComponents[i].uvs = new float[] {card.startX, card.startY, card.endX, card.startY, card.startX, card.endY, card.endX, card.endY};

            cardComponents[i].uvs[Component.UV_TOP_LEFT_U] = card.startX / (float)cards.getWidth();
            cardComponents[i].uvs[Component.UV_TOP_LEFT_V] = card.endY / (float)cards.getHeight();
            cardComponents[i].uvs[Component.UV_BOTTOM_LEFT_U] = card.endX / (float)cards.getWidth();
            cardComponents[i].uvs[Component.UV_BOTTOM_LEFT_V] = card.endY / (float)cards.getHeight();
            cardComponents[i].uvs[Component.UV_BOTTOM_RIGHT_U] = card.endX / (float)cards.getWidth();
            cardComponents[i].uvs[Component.UV_BOTTOM_RIGHT_V] = card.startY / (float)cards.getHeight();
            cardComponents[i].uvs[Component.UV_TOP_RIGHT_U] = card.startX / (float)cards.getWidth();
            cardComponents[i].uvs[Component.UV_TOP_RIGHT_V] = card.startY / (float)cards.getHeight();

            //cardComponents[i].isUvPercentage = new boolean[] {false, false, false, false, false, false, false, false};
            cardComponents[i].bc = cardsBc;
            cardComponents[i].card = card;
        }
        if(!Util.containsSet(cardComponents))
        {
            for(CardComponent cardComponent : cardComponents)
            {
                deck.add(cardComponent.card);
            }
            Collections.shuffle(deck);
            fillCards();
        }
    }
}
