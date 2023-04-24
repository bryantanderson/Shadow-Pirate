import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;

/** This is a Blackbeard class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Blackbeard extends Enemy {

    private final static Image BLACKBEARD_LEFT = new Image("res/blackbeard/blackbeardLeft.png");
    private final static Image BLACKBEARD_RIGHT = new Image("res/blackbeard/blackbeardRight.png");
    private final static Image BLACKBEARD_HIT_LEFT = new Image("res/blackbeard/blackbeardHitLeft.png");
    private final static Image BLACKBEARD_HIT_RIGHT = new Image("res/blackbeard/blackbeardHitRight.png");
    private final static double PROJECTILE_SPEED = 0.8;
    private final static double REFRESH_RATE = 60/1000.0;
    private final static int ATTACK_RANGE = 200;
    private final static int MAX_HEALTH = 90;
    private final static int COOLDOWN_PERIOD = 1500;
    private final static int INVINCIBLE_PERIOD = 1500;
    Random r = new Random();
    private final boolean moveHorizontal = r.nextBoolean();
    private final ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private boolean idle = true;
    private boolean inCooldown = false;
    private boolean invincible = false;
    private int currentHealth;
    private boolean dead = false;
    private Image currentImage;
    private boolean collided = false;
    private boolean hitBySailor = false;
    private int counter = 0;
    private int counterTwo = 0;

    public Blackbeard(double x, double y) {
        super(x, y);
        currentImage = BLACKBEARD_RIGHT;
        this.currentHealth = MAX_HEALTH;
    }
    /**
     * Method that returns the bounding box of Blackbeard
     */
    public Rectangle getBoundingBox() {
        return currentImage.getBoundingBoxAt(new Point(getX(), getY()));
    }

    /**
     * Method that performs state update
     * @param isAtBoundary This boolean represents whether the Pirate is at the bounds or not
     * @param blocks This is the array list of blocks
     * @param bombs This is the array list of bombs
     * @param levelZeroStatus This is boolean that tells the class whether it is currently level one or zero
     * @param sailorX This is the x coordinate of the sailor
     * @param sailorY This is the y coordinate of the sailor
     */
    public void update(boolean isAtBoundary, ArrayList<Block> blocks, ArrayList<Bomb> bombs, boolean levelZeroStatus,
                       double sailorX, double sailorY) {
        if (!dead) {
            if (moveHorizontal) {
                setOldPoints();
                hitsBoundary(isAtBoundary);
                setPosition((getX() + getSpeed()), getY());
                currentImage.draw(getX(), getY());
                checkCollisions(blocks, bombs, levelZeroStatus);
                if (collided) {
                    reverseDirection();
                }
            } else {
                setOldPoints();
                hitsBoundary(isAtBoundary);
                setPosition(getX(), (getY() + getSpeed()));
                currentImage.draw(getX(), getY());
                checkCollisions(blocks, bombs, levelZeroStatus);
                if (collided) {
                    reverseDirection();
                }
            }
            if (sailorWithinRange(sailorX, sailorY) && !inCooldown) {
                double angle = calculateFiringAngle(sailorX, sailorY);
                fireProjectileAt(angle, sailorX, sailorY);
                inCooldown = true;
            }
            if (inCooldown) {
                counterTwo++;
                if ((counterTwo / REFRESH_RATE) >= COOLDOWN_PERIOD) {
                    inCooldown = false;
                    counterTwo = 0;
                }
            }
            // code to put Blackbeard into invincibility when it is hit by the sailor
            if (hitBySailor) {
                invincible = true;
                idle = false;
                updateInvincibilityImage();
                counter++;
                if ((counter / REFRESH_RATE) >= INVINCIBLE_PERIOD) {
                    hitBySailor = false;
                    invincible = false;
                    idle = true;
                    updateIdleImage();
                    counter = 0;
                }
            }
            if (isDead()) {
                dead = true;
            }
            renderHealthPoints(currentHealth, MAX_HEALTH);
        }
        for (Projectile current: projectiles) {
            current.update();
        }
    }
    /**
     * Method that reverses the direction in which Blackbeard is moving
     */
    private void reverseDirection() {
        if (moveHorizontal) {
            if (currentImage == BLACKBEARD_LEFT) {
                currentImage = BLACKBEARD_RIGHT;
            } else if (currentImage == BLACKBEARD_RIGHT) {
                currentImage = BLACKBEARD_LEFT;
            }
        }
        setSpeed(getSpeed() * -1);
    }

    /**
     * Method that checks for collisions between Blackbeard and blocks / bombs, depending on the level
     * @param blocks This is the array list of blocks
     * @param bombs This is the array list of bombs
     * @param levelZeroStatus This is boolean that tells the class whether it is currently level one or zero
     */
    public void checkCollisions(ArrayList<Block> blocks, ArrayList<Bomb> bombs, boolean levelZeroStatus){
        // check collisions and print log
        collided = false;
        Rectangle blackbeardBox = currentImage.getBoundingBoxAt(new Point(getX(), getY()));
        if (levelZeroStatus) {
            for (Block current : blocks) {
                Rectangle blockBox = current.getBoundingBox();
                if (blackbeardBox.intersects(blockBox)) {
                    moveBack();
                    collided = true;
                }
            }
        } else {
            for (Bomb current : bombs) {
                Rectangle bombBox = current.getBoundingBox();
                if (blackbeardBox.intersects(bombBox)) {
                    moveBack();
                    collided = true;
                }
            }
        }
    }
    /**
     * Method that checks whether the Sailor is within Blackbeard's attack range
     * @param sailorX This is the x coordinate of the sailor
     * @param sailorY This is the y coordinate of the sailor
     */
    private boolean sailorWithinRange(double sailorX, double sailorY) {
        return  Math.sqrt( Math.pow(Math.abs(sailorX - getX()), 2) + Math.pow(Math.abs(sailorY - getY()), 2) ) <=
                ATTACK_RANGE;
    }
    /**
     * Method that causes Blackbeard to bounce back and change direction when it reaches the level boundary
     * @param isAtBoundary This is the boolean that tells the method whether the pirate is at the level bounds
     */
    private void hitsBoundary(boolean isAtBoundary) {
        if (isAtBoundary) {
            moveBack();
            reverseDirection();
        }
    }
    /**
     * Method that deducts the Sailor's damage from Blackbeard's health points
     * @param damage This is how much damage the Sailor will receive
     */
    public void hitBySailor(int damage) {
        hitBySailor = true;
        currentHealth -= damage;
    }
    /**
     * Method that calculates the angle at which the projectile needs to be rotated when fired at the Sailor
     * @param targetX This is the x coordinate of the target
     * @param targetY This is the y coordinate of the target
     */
    private double calculateFiringAngle(double targetX, double targetY) {
        if ( ((targetX <= getX()) && (targetY >= getY())) || ((targetX >= getX()) && (targetY <= getY())) ){
            return Math.PI - Math.atan(Math.abs(targetY - getY()) / Math.abs(targetX - getX()));
        } else if (targetY == getY() && targetY <= getY()) {
            return Math.PI;
        } else if (targetY == getY() && targetY >= getY()) {
            return 0;
        }
        return Math.atan(Math.abs(targetY - getY()) / Math.abs(targetX - getX()));
    }
    /**
     * Method that fires a projectile at the sailor
     * @param sailorX This is the x coordinate of the Sailor
     * @param sailorY This is the y coordinate of the Sailor
     * @param angle This is the angle the projectile will be fired at
     */
    private void fireProjectileAt(double angle, double sailorX, double sailorY) {
        projectiles.add(new Projectile(getX(), getY(), PROJECTILE_SPEED, angle, false, sailorX, sailorY));
    }

    /**
     * Method that switches the image of Blackbeard into the invincible images
     */
    private void updateInvincibilityImage() {
        if (currentImage == BLACKBEARD_LEFT) {
            currentImage = BLACKBEARD_HIT_LEFT;
        } else if (currentImage == BLACKBEARD_RIGHT) {
            currentImage = BLACKBEARD_HIT_RIGHT;
        }
    }
    /**
     * Method that switches the image of Blackbeard back to the default images
     */
    private void updateIdleImage() {
        if (currentImage == BLACKBEARD_HIT_LEFT) {
            currentImage = BLACKBEARD_LEFT;
        } else if (currentImage == BLACKBEARD_HIT_RIGHT) {
            currentImage = BLACKBEARD_RIGHT;
        }
    }
    /**
     * Method that checks whether Blackbeard is dead
     * @return boolean This returns whether the pirate is dead
     */
    private boolean isDead() {
        return (currentHealth <= 0);
    }
    /**
     * Method that returns whether Blackbeard is in its invincibility state
     * @return boolean This returns whether the pirate is in its invincibility state
     */
    public boolean isInvincible() {
        return invincible;
    }
    /**
     * Method that returns an arraylist of all the projectiles that have been fired by Blackbeard
     * @return ArrayList<Projectile> This returns all the projectiles the pirate has fired
     */
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
    /**
     * Method that returns Blackbeard's current health
     * @return int This returns the pirate's current health
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Method that returns Blackbeard's maximum health
     * @return int This returns the pirate's maximum health
     */
    public int getMaxHealth() {
        return MAX_HEALTH;
    }
}
