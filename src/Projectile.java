import bagel.DrawOptions;
import bagel.Drawing;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is a projectile class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Projectile {

    private final static Image BLACKBEARD_PROJECTILE = new Image("res/blackbeard/blackbeardProjectile.png");
    private final static Image PIRATE_PROJECTILE = new Image("res/pirate/pirateProjectile.png");
    private final static int PIRATE_PROJECTILE_DAMAGE = 10;
    private final static int BLACKBEARD_PROJECTILE_DAMAGE = 20;
    private final static DrawOptions DRAW = new DrawOptions();
    private final static int REVERSE_DIRECTION = -1;

    private double x;
    private double y;
    private double speedX;
    private double speedY;
    private double angle;
    // if the projectile has collided with the Sailor, this boolean will stop the projectile from updating
    private boolean collided = false;
    // will use this boolean to check who is firing, true meaning a pirate is firing, false meaning Blackbeard is firing
    private boolean pirateFiring;
    private Image currentImage;

    public Projectile(double x, double y, double speed, double angle, boolean pirateFiring,
                      double sailorX, double sailorY) {
        this.x = x;
        this.y = y;
        // if the Sailor is to the left of the Enemy, the projectile goes left
        if (sailorX <= x) {
            this.speedX = speed * REVERSE_DIRECTION;
        } else {
            // if the Sailor is to the right of the Enemy, the projectile goes right
            this.speedX = speed;
        }
        // if the Sailor is above the Enemy, the projectile will go upwards
        if (sailorY <= y) {
            this.speedY = speed * REVERSE_DIRECTION;
        } else {
            // if the Sailor is below the Enemy, the projectile will go downwards
            this.speedY = speed;
        }
        this.angle = angle;
        this.pirateFiring = pirateFiring;
        // change the image of the projectile depending on who is firing it
        if (pirateFiring) {
            currentImage = PIRATE_PROJECTILE;
        } else {
            currentImage = BLACKBEARD_PROJECTILE;
        }
    }

    /**
     * Method that performs state update
     */
    public void update() {
        x += speedX;
        y += speedY;
        if (!collided) {
            currentImage.draw(x, y, DRAW.setRotation(angle));
        }
    }

    /**
     * Method that returns the coordinates of the projectile in a Point class
     * @return Point This returns the current position of the projectile
     */
    public Point getPoint() {
        return new Point(x,y);
    }
    /**
     * Method that sets the collided variable to true
     */
    public void hasCollided() {
        collided = true;
    }
    /**
     * Method that returns the `collided` boolean
     * @return boolean This returns whether the projectile has collided with the Sailor
     */
    public boolean isCollided() {
        return collided;
    }
    /**
     * Method that returns the damage a projectile shot by a pirate does
     * @return int This returns how much damage a projectile fired by the pirate does to a Sailor
     */
    public int getPirateProjectileDamage() {
        return PIRATE_PROJECTILE_DAMAGE;
    }
    /**
     * Method that returns the damage a projectile shot by Blackbeard does
     * @return int This returns how much damage a projectile fired by Blackbeard does to a Sailor
     */
    public int getBlackbeardProjectileDamage() {
        return BLACKBEARD_PROJECTILE_DAMAGE;
    }
}
