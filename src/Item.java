import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is an abstract item class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public abstract class Item {
    // represents the benefit the Sailor receives when the Sailor picks up the item
    private int buff;
    private int x;
    private int y;
    // represents whether the item has been picked up
    private boolean pickedUp;

    public Item(int x, int y, int buff) {
        this.x = x;
        this.y = y;
        this.buff = buff;
        this.pickedUp = false;
    }

    /**
     * Method that performs state update
     */
    public abstract void update();
    /**
     * Method that returns the bounding box of the item
     */
    public abstract Rectangle getBoundingBox();
    /**
     * Method that returns the x coordinate of the item
     */
    public int getX() {
        return x;
    }
    /**
     * Method that returns the y coordinate of the item
     */
    public int getY() {
        return y;
    }
    /**
     * Method that returns buff
     */
    public int getBuff() {
        return buff;
    }
    /**
     * Method that returns pickedUp
     */
    public boolean isPickedUp() {
        return pickedUp;
    }
    /**
     * Method that sets new values for x and y
     * @param x This is the new x coordinate
     * @param y This is the new y coordinate
     */
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Method that tells the item it has been picked up
     */
    public abstract void hasBeenPickedUp();
    /**
     * Method that sets the value of pickedUp
     * @param pickedUp This is the new boolean for the pickedUp attribute
     */
    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

}
