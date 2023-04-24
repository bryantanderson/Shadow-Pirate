import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;
import java.util.Random;

/** This is an abstract Enemy class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public abstract class Enemy {
    private double x;
    private double y;
    private double oldX;
    private double oldY;
    private double speed;
    private static Random r = new Random();

    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 15;
    private final static int HEALTH_X_OFFSET = 20;
    private final static int HEALTH_Y_OFFSET = 35;
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);
    private final DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        this.speed = 0.2+0.5*r.nextDouble();;
        COLOUR.setBlendColour(GREEN);
    }
    /**
     * Method that returns the x coordinate of the Enemy
     * @return double This is the x coordinate of the Enemy
     */
    public double getX() {
        return x;
    }
    /**
     * Method that returns the y coordinate of the Enemy
     * @return double This is the y coordinate of the Enemy
     */
    public double getY() {
        return y;
    }
    /**
     * Method that returns the speed of the Enemy
     * @return double This is the speed of the Enemy
     */
    public double getSpeed() {
        return speed;
    }
    /**
     * Method that sets the speed of the Enemy
     * @param speed This is the new speed of the Enemy
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    /**
     * Method that sets the x and y coordinate of the Enemy to the given values
     * @param x This is the new x coordinate of the Enemy
     * @param y This is the new y coordinate of the Enemy
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Method that sets the x coordinate to the given value
     * @param x This is the new x coordinate of the Enemy
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * Method that sets the y coordinate to the given value
     * @param y This is the new y coordinate of the Enemy
     */
    public void setY(double y) {
        this.y = y;
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
    public abstract void update(boolean isAtBoundary, ArrayList<Block> blocks, ArrayList<Bomb> bombs,
                                boolean levelZeroStatus, double sailorX, double sailorY);
    /**
     * Method that checks for collisions between blocks and bombs, depending on the level
     * @param blocks This is the array list of blocks
     * @param bombs This is the array list of bombs
     * @param levelZeroStatus This is boolean that tells the class whether it is currently level one or zero
     */
    public abstract void checkCollisions(ArrayList<Block> blocks, ArrayList<Bomb> bombs, boolean levelZeroStatus);
    /**
     * Method that returns the bounding box of the enemy
     * @return Rectangle This returns the bounding box of the enemy
     */
    public abstract Rectangle getBoundingBox();
    /**
     * Method that stores the old coordinates of the enemy
     */
    public void setOldPoints() {
        oldX = x;
        oldY = y;
    }
    /**
     * Method that moves the enemy back to its previous position
     */
    public void moveBack() {
        x = oldX;
        y = oldY;
    }
    /**
     * Method that renders the current health as a percentage on screen
     * @param currentHealth This is the current health of the Enemy
     * @param maxHealth This is the max health of the Enemy
     */
    public void renderHealthPoints(double currentHealth, double maxHealth){
        double percentageHP = ((double) currentHealth/maxHealth) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", x - HEALTH_X_OFFSET,
                y - HEALTH_Y_OFFSET, COLOUR);
    }
}
