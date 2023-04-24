import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;

/** This is a Pirate class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Pirate extends Enemy {

    private final static Image PIRATE_LEFT = new Image("res/pirate/pirateLeft.png");
    private final static Image PIRATE_RIGHT = new Image("res/pirate/pirateRight.png");
    private final static Image PIRATE_HIT_LEFT = new Image("res/pirate/pirateHitLeft.png");
    private final static Image PIRATE_HIT_RIGHT = new Image("res/pirate/pirateHitRight.png");
    private final static double PROJECTILE_SPEED = 0.4;
    private final static double REFRESH_RATE = 60/1000.0;
    private final static int ATTACK_RANGE = 100;
    private final static int MAX_HEALTH = 45;
    private final static int COOLDOWN_PERIOD = 3000;
    private final static int INVINCIBLE_PERIOD = 1500;
    Random r = new Random();
    private final boolean moveHorizontal = r.nextBoolean();
    private final ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private boolean inCooldown = false;
    private boolean invincible = false;
    private boolean idle = true;
    private int currentHealth;
    private boolean dead = false;
    private Image currentImage;
    private boolean collided = false;
    private boolean hitBySailor = false;
    // this counter will be used to time the invincibility period of the pirate
    private int counter = 0;
    // this counter will be used to time the cooldown period of the pirate
    private int counterTwo = 0;

    public Pirate(double x, double y) {
        super(x, y);
        currentImage = PIRATE_RIGHT;
        this.currentHealth = MAX_HEALTH;
    }

    /**
     * Method that returns the bounding box of the pirate
     * @return Rectangle This is the bounding box of the pirate
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
        // code to make sure the Pirate bounces back when it hits the level boundaries
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
            // if the sailor is within attack range, fire a projectile at the sailor
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
            // code to put the pirate into invincibility when it is hit by the sailor
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
        // projectiles are updated regardless of whether the pirate is dead or not
        for (Projectile current : projectiles) {
            current.update();
        }
    }

    /**
     * Method that reverses the direction in which the pirate is moving
     */
    private void reverseDirection() {
        if (moveHorizontal) {
            if (currentImage == PIRATE_LEFT) {
                currentImage = PIRATE_RIGHT;
            } else if (currentImage == PIRATE_RIGHT) {
                currentImage = PIRATE_LEFT;
            }
        }
        setSpeed(getSpeed() * -1);
    }

    /**
     * Method that switches the image of the pirate into the invincible images
     */
    private void updateInvincibilityImage() {
        if (currentImage == PIRATE_LEFT) {
            currentImage = PIRATE_HIT_LEFT;
        } else if (currentImage == PIRATE_RIGHT) {
            currentImage = PIRATE_HIT_RIGHT;
        }
    }

    /**
     * Method that switches the image of the pirate back to the default images
     */
    private void updateIdleImage() {
        if (currentImage == PIRATE_HIT_LEFT) {
            currentImage = PIRATE_LEFT;
        } else if (currentImage == PIRATE_HIT_RIGHT) {
            currentImage = PIRATE_RIGHT;
        }
    }

    /**
     * Method that checks for collisions between the pirate and blocks / bombs, depending on the level
     * @param blocks This is the array list of blocks
     * @param bombs This is the array list of bombs
     * @param levelZeroStatus This is boolean that tells the class whether it is currently level one or zero
     */
    public void checkCollisions(ArrayList<Block> blocks, ArrayList<Bomb> bombs, boolean levelZeroStatus){
        // check collisions and print log
        collided = false;
        Rectangle pirateBox = currentImage.getBoundingBoxAt(new Point(getX(), getY()));
        if (levelZeroStatus) {
            for (Block current : blocks) {
                Rectangle blockBox = current.getBoundingBox();
                if (pirateBox.intersects(blockBox)) {
                    moveBack();
                    collided = true;
                }
            }
        } else {
            for (Bomb current : bombs) {
                Rectangle bombBox = current.getBoundingBox();
                if (pirateBox.intersects(bombBox)) {
                    moveBack();
                    collided = true;
                }
            }
        }
    }

    /**
     * Method that checks whether the Sailor is within the pirate's attack range
     * @param sailorX This is the x coordinate of the sailor
     * @param sailorY This is the y coordinate of the sailor
     */
    private boolean sailorWithinRange(double sailorX, double sailorY) {
        return Math.sqrt( Math.pow(Math.abs(sailorX - getX()), 2) +
                Math.pow(Math.abs(sailorY - getY()), 2) ) <= ATTACK_RANGE;
    }

    /**
     * Method that causes the pirate to bounce back and change direction when it reaches the level boundary
     * @param isAtBoundary This is the boolean that tells the method whether the pirate is at the level bounds
     */
    private void hitsBoundary(boolean isAtBoundary) {
        if (isAtBoundary) {
            moveBack();
            reverseDirection();
        }
    }

    /**
     * Method that deducts the Sailor's damage from the pirate's health points
     * @param damage This is how much damage the Sailor will receive
     */
    public void hitBySailor(int damage) {
        currentHealth -= damage;
        hitBySailor = true;
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
        projectiles.add(new Projectile(getX(), getY(), PROJECTILE_SPEED, angle, true, sailorX, sailorY));
    }

    /**
     * Method that returns whether the pirate is in its invincibility state
     * @return boolean This returns whether the pirate is in its invincibility state
     */
    public boolean isInvincible() {
        return invincible;
    }

    /**
     * Method that checks whether the pirate is dead
     * @return boolean This returns whether the pirate is dead
     */
    private boolean isDead() {
        return (currentHealth <= 0);
    }

    /**
     * Method that returns an arraylist of all the projectiles that have been fired by the pirate
     * @return ArrayList<Projectile> This returns all the projectiles the pirate has fired
     */
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Method that returns the pirate's current health
     * @return int This returns the pirate's current health
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Method that returns the pirates' maximum health
     * @return int This returns the pirate's maximum health
     */
    public int getMaxHealth() {
        return MAX_HEALTH;
    }
}
