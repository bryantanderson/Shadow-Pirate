import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is a Treasure class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Treasure {
    private final static Image TREASURE = new Image("res/treasure.png");
    private int x;
    private int y;

    /**
     * Method that performs state update
     */
    public void update() {
        TREASURE.drawFromTopLeft(x, y);
    }

    public Treasure(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Method that returns the bounding box of the Treasure
     * @return Rectangle This is the bounding box of the Treasure
     */
    public Rectangle getBoundingBox() {
        return TREASURE.getBoundingBoxAt(new Point(x, y));
    }
}
