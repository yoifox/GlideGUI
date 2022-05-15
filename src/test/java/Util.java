
import core.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util
{
    public static boolean isSet(Card card1, Card card2, Card card3)
    {
        if(!areThreeTheSameOrCompletelyDifferent(card1.color, card2.color, card3.color)) return false;
        if(!areThreeTheSameOrCompletelyDifferent(card1.shape, card2.shape, card3.shape)) return false;
        if(!areThreeTheSameOrCompletelyDifferent(card1.filling, card2.filling, card3.filling)) return false;
        return areThreeTheSameOrCompletelyDifferent(card1.count, card2.count, card3.count);
    }

    public static boolean containsSet(CardComponent[] cards)
    {
        for(int i = 0; i < 12; i++)
        {
            for(int j = 0; j < 12; j++)
            {
                for(int h = 0; h < 12; h++)
                {
                    if(i == j && j == h) continue;
                    if(isSet(cards[i].card, cards[j].card, cards[h].card))
                        return true;
                }
            }
        }
        return false;
    }

    private static boolean areThreeTheSameOrCompletelyDifferent(int i, int ii, int iii)
    {
        if(i == ii && ii != iii) return false;
        if(i != ii && ii == iii) return false;
        return i != iii || i == ii;
    }

    public static List<Card> readAllCards()
    {
        List<Card> cards = new ArrayList<>();
        String file = Utils.loadResourceString(Util.class, "/cards.txt");
        file = file.replace(" ", "");
        file = file.replace("\n\n", "\n");
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if(line.equals("") || line.equals("\n")) continue;

            String[] c = line.split(":");
            int xStart = Integer.parseInt(c[0].split(",")[0]);
            int yStart = Integer.parseInt(c[0].split(",")[1]);
            int xEnd = Integer.parseInt(c[1].split(",")[0]);
            int yEnd = Integer.parseInt(c[1].split(",")[1]);

            String color = c[2];
            String count = c[3];
            String shape = c[4];
            String filling = c[5];

            int colorI = 0, countI = 0, shapeI = 0, fillingI = 0;
            if(color.equals("red")) colorI = Card.COLOR_RED;
            if(color.equals("green")) colorI = Card.COLOR_GREEN;
            if(color.equals("purple")) colorI = Card.COLOR_PURPLE;
            if(count.equals("one")) countI = Card.COUNT_ONE;
            if(count.equals("two")) countI = Card.COUNT_TWO;
            if(count.equals("three")) countI = Card.COUNT_THREE;
            if(shape.equals("diamond")) shapeI = Card.SHAPE_DIAMOND;
            if(shape.equals("ellipse")) shapeI = Card.SHAPE_ELLIPSE;
            if(shape.equals("wave")) shapeI = Card.SHAPE_WAVE;
            if(filling.equals("hollow")) fillingI = Card.FILLING_HOLLOW;
            if(filling.equals("solid")) fillingI = Card.FILLING_SOLID;
            if(filling.equals("stripes")) fillingI = Card.FILLING_STRIPES;

            cards.add(new Card(colorI, shapeI, countI, fillingI, new int[] {xStart, yStart, xEnd, yEnd}));
        }
        return cards;
    }
}
