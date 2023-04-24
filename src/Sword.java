import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is a Sword class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Sword extends Item {
    private final static Image SWORD = new Image("res/items/sword.png");
    private final static Image INVENTORY_SWORD = new Image("res/items/swordIcon.png");
    // this constant represents the damage increase the Sailor receives when the Sailor picks up a sword
    private final static int DAMAGE_BUFF = 15;
    private Image currentImage;

    public Sword(int x, int y) {
        super(x, y, DAMAGE_BUFF);
        currentImage = SWORD;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        currentImage.drawFromTopLeft(getX(), getY());
    }

    /**
     * Method that returns the bounding box of the sword
     * @return Rectangle This is the bounding box of the sword
     */
    public Rectangle getBoundingBox() {
        return SWORD.getBoundingBoxAt(new Point(getX(), getY()));
    }

    /**
     * Method that tells the sword it has been picked up
     */
    public void hasBeenPickedUp() {
        currentImage = INVENTORY_SWORD;
        setPickedUp(true);
    }

    /**
     * Method that returns the damage increase the Sailor receives when the Sailor picks up a sword
     * @return int This is how much damage is added to the Sailor's damage when a sword is picked up
     */
    public int getBuff() {
        return DAMAGE_BUFF;
    }
}
