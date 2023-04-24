import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is a bomb class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */

public class Bomb {
    private final static Image BOMB = new Image("res/bomb.png");
    private final static Image EXPLOSION = new Image("res/explosion.png");
    private final static int EXPLOSION_DURATION = 500;
    private final static double REFRESH_RATE = 60/1000.0;
    private final static int DAMAGE = 10;

    private int x;
    private int y;
    // this boolean will be used to start the timer for the explosion of the bomb
    private boolean startCounter = false;
    // this boolean represents whether the bomb has dealt damage to the sailor or not
    private boolean doneDamage = false;
    private int counter;
    // this boolean represents whether the bomb has disappeared or not. it will be true after
    // the bomb explodes
    private boolean disappear = false;
    private Image currentImage;

    public Bomb(int startX, int startY){
        this.x = startX;
        this.y = startY;
        currentImage = BOMB;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        if (startCounter) {
            counter++;
            // makes the bomb disappear after it finishes exploding
            if ((counter / REFRESH_RATE) >= EXPLOSION_DURATION) {
                disappear = true;
            }
        }
        if (!disappear) {
            currentImage.drawFromTopLeft(x, y);
        }
    }

    /**
     * Method that returns the bounding box of the bomb
     * @return Rectangle This is the bounding box of the bomb
     */
    public Rectangle getBoundingBox(){
        return currentImage.getBoundingBoxAt(new Point(x, y));
    }

    /**
     * Method that triggers the explosion process of the bomb
     */
    public void explosion() {
        currentImage = EXPLOSION;
        startCounter = true;
        doneDamage = true;
    }

    /**
     * Method that returns the boolean disappear
     * @return boolean This returns whether the bomb has disappeared or not
     */
    public boolean disappeared() {
        return disappear;
    }

    /**
     * Method that returns the damage the bomb deals
     * @return int This is how much damage the bomb deals when the Sailor collides with it
     */
    public int getDamage() { return DAMAGE; }

    /**
     * Method that returns the boolean doneDamage
     * @return boolean This represents whether the bomb has dealt damage to the Sailor or not
     */
    public boolean hasDoneDamage() {
        return doneDamage;
    }

}
