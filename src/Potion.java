import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is a Potion class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Potion extends Item {
    private final static Image POTION = new Image("res/items/potion.png");
    private final static Image INVENTORY_POTION = new Image("res/items/potionIcon.png");
    // this constant represents how much health the Sailor gains when it picks up potion
    private final static int POTION_BUFF = 25;
    private Image currentImage;

    public Potion(int x, int y) {
        super(x, y, POTION_BUFF);
        currentImage = POTION;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        currentImage.drawFromTopLeft(getX(), getY());
    }

    /**
     * Method that returns the bounding box of the potion
     * @return Rectangle This is the bounding box of the potion
     */
    public Rectangle getBoundingBox() {
        return POTION.getBoundingBoxAt(new Point(getX(), getY()));
    }

    /**
     * Method that tells the potion is has been picked up
     */
    public void hasBeenPickedUp() {
        currentImage = INVENTORY_POTION;
        setPickedUp(true);
    }


}
