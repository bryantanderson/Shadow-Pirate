import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;

/** This is a Sailor class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Sailor{
    private final static Image SAILOR_LEFT = new Image("res/sailor/sailorLeft.png");
    private final static Image SAILOR_RIGHT = new Image("res/sailor/sailorRight.png");
    private final static Image SAILOR_HIT_LEFT = new Image("res/sailor/sailorHitLeft.png");
    private final static Image SAILOR_HIT_RIGHT = new Image("res/sailor/sailorHitRight.png");
    private final static int MOVE_SIZE = 3;
    private final static double REFRESH_RATE = 60/1000.0;

    private final static int WIN_STAGE_0_X = 990;
    private final static int WIN_STAGE_0_Y = 630;

    private final static int HEALTH_X = 10;
    private final static int HEALTH_Y = 25;
    // INVENTORY_X is the x coordinate for all the inventory item icons
    private final static int INVENTORY_X = 12;
    // represents the gaps between the y coordinates of the inventory icon items
    private final static int INVENTORY_ICON_GAPS = 40;
    private final static int GREEN_BOUNDARY = 100;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private final static int ATTACK_DURATION = 1000;
    private final static int COOLDOWN_PERIOD = 2000;

    private Potion potion;
    private Sword sword;
    private Elixir elixir;
    // this is the y coordinate of the first item icon
    private int inventoryY = 40;
    private int maxHealth = 100;
    private int healthPoints;
    private int damage = 15;
    private int oldX;
    private int oldY;
    private int x;
    private int y;
    private Image currentImage;
    private boolean readyToAttack = true;
    private boolean sailorAttacking = false;
    private boolean coolingDown = false;
    // the next 4 coordinates store the level bounds
    private int topLeftX;
    private int topLeftY;
    private int bottomRightX;
    private int bottomRightY;
    // this counter will be used to time the attack phase
    private int counter = 0;
    // this counter will be used to time the cooldown phase
    private int counterTwo = 0;
    // this boolean represents whether the Sailor has won level 1 or not
    private boolean wonGame = false;

    public Sailor(int startX, int startY){
        this.x = startX;
        this.y = startY;
        this.healthPoints = maxHealth;
        this.currentImage = SAILOR_RIGHT;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     * @param input This is the input from the user
     * @param blocks This is the array list of blocks
     * @param bombs This is the array list of bombs
     * @param pirates This is the array list of the pirates
     * @param blackbeard This is the blackbeard in the level 1
     * @param treasure This is the treasure in level 1
     * @param stageZeroStatus This is whether it is level one or zero
     */
    public void update(Input input, ArrayList<Block> blocks, ArrayList<Bomb> bombs, ArrayList<Pirate> pirates,
                       Blackbeard blackbeard, Treasure treasure, boolean stageZeroStatus){
        // store old coordinates every time the sailor moves
        if (input.isDown(Keys.UP)){
            setOldPoints();
            move(0, -MOVE_SIZE);
        }else if (input.isDown(Keys.DOWN)){
            setOldPoints();
            move(0, MOVE_SIZE);
        }else if (input.isDown(Keys.LEFT)){
            setOldPoints();
            move(-MOVE_SIZE,0);
            // use Sailor attack image if Sailor is attacking
            if (sailorAttacking) {
                currentImage = SAILOR_HIT_LEFT;
            } else {
                currentImage = SAILOR_LEFT;
            }
        }else if (input.isDown(Keys.RIGHT)){
            setOldPoints();
            move(MOVE_SIZE,0);
            // use Sailor attack image if Sailor is attacking
            if (sailorAttacking) {
                currentImage = SAILOR_HIT_RIGHT;
            } else {
                currentImage = SAILOR_RIGHT;
            }
        }
        // set the Sailor to attack mode if the key S is pressed
        if (input.wasPressed(Keys.S) && readyToAttack) {
            attackMode();
            setToAttackImage();
        }
        // this starts the counter for how long the Sailor can be in attack mode for
        if (sailorAttacking) {
            counter++;
            if ((counter / REFRESH_RATE) >= ATTACK_DURATION) {
                sailorCoolingDown();
                counter = 0;
            }
        }
        // // this starts the counter for how long the Sailor must cool down for
        if (coolingDown) {
            counterTwo++;
            if ((counterTwo / REFRESH_RATE) >= COOLDOWN_PERIOD) {
                sailorCanAttack();
                counterTwo = 0;
            }
        }
        // if the Sailor is in stage 1, the items can appear on the screen
        if (!stageZeroStatus) {
            sword.update();
            potion.update();
            elixir.update();
        }
        currentImage.drawFromTopLeft(x, y);
        // check whether the Sailor intersects with any items or entities in the level
        checkCollisions(blocks, bombs, pirates, blackbeard, stageZeroStatus);
        checkItems(stageZeroStatus, treasure);
        // bounce back if Sailor hits borders
        if (isOutOfBound()) {
            moveBack();
        }
        renderHealthPoints();
    }

    /**
     * Method that checks for collisions between sailor, blocks or bombs, pirates, blackbeard, given the level
     * @param blocks This is the array list of blocks
     * @param bombs This is the array list of bombs
     * @param pirates This is the array list of the pirates
     * @param blackbeard This is the blackbeard in the level 1
     * @param stageZeroStatus This is whether it is level one or zero
     */

    // stageZeroStatus represents whether stage Zero is running or not
    private void checkCollisions(ArrayList<Block> blocks, ArrayList<Bomb> bombs, ArrayList<Pirate> pirates,
                                 Blackbeard blackbeard, boolean stageZeroStatus){
        // check collisions and print log
        Rectangle sailorBox = getBoundingBox();
        if (stageZeroStatus) {
            for (Block current : blocks) {
                Rectangle blockBox = current.getBoundingBox();
                if (sailorBox.intersects(blockBox)) {
                    moveBack();
                }
            }
        } else {
            // stage 1 items
            Rectangle blackbeardBox = blackbeard.getBoundingBox();
            if (sailorBox.intersects(blackbeardBox) && !blackbeard.isInvincible() && sailorAttacking) {
                blackbeard.hitBySailor(damage);
                System.out.println("Sailor inflicts " + damage + " damage points on Blackbeard." +
                        " Blackbeard's current health: " + blackbeard.getCurrentHealth() + "/" +
                        blackbeard.getMaxHealth());
            }
            for (Projectile currentProjectile: blackbeard.getProjectiles()) {
                Point projectileBox = currentProjectile.getPoint();
                if (sailorBox.intersects(projectileBox) && !currentProjectile.isCollided()) {
                    currentProjectile.hasCollided();
                    tookDamage(currentProjectile.getBlackbeardProjectileDamage());
                    System.out.println("Blackbeard inflicts " + currentProjectile.getBlackbeardProjectileDamage() +
                            " damage points on Sailor. Sailor's current health: " + healthPoints + "/" + maxHealth);
                }
            }
            // loops through the bombs, check whether the Sailor has collided with any of them
            for (Bomb current : bombs) {
                Rectangle bombBox = current.getBoundingBox();
                if (sailorBox.intersects(bombBox) && !current.disappeared()) {
                    moveBack();
                    // trigger the explosion of the bomb
                    if (!current.hasDoneDamage()) {
                        current.explosion();
                        tookDamage(current.getDamage());
                        System.out.println("Bomb inflicts " + current.getDamage() + " damage points on Sailor. " +
                                "Sailor's current health: " + healthPoints + "/" + maxHealth);
                    }
                }
            }
         }
        for (Pirate current :pirates) {
            Rectangle pirateBox = current.getBoundingBox();
            if (sailorBox.intersects(pirateBox) && !current.isInvincible() && sailorAttacking) {
                current.hitBySailor(damage);
                System.out.println("Sailor inflicts " + damage + " damage points on Pirate." +
                        " Pirate's current health: " + current.getCurrentHealth() + "/" + current.getMaxHealth());
            }
            for (Projectile currentProjectile: current.getProjectiles()) {
                Point projectileBox = currentProjectile.getPoint();
                if (sailorBox.intersects(projectileBox) && !currentProjectile.isCollided()) {
                    currentProjectile.hasCollided();
                    tookDamage(currentProjectile.getPirateProjectileDamage());
                    System.out.println("Pirate inflicts " + currentProjectile.getPirateProjectileDamage() +
                            " damage points on Sailor. Sailor's current health: " + healthPoints + "/" + maxHealth);
                }
            }
        }

    }

    /**
     * Method that checks for collisions between sailor and items and treasure
     * @param treasure This is the treasure in level 1
     * @param stageZeroStatus This is whether it is level one or zero
     */
    private void checkItems(boolean stageZeroStatus, Treasure treasure) {
        Rectangle sailorBox = getBoundingBox();
        if (!stageZeroStatus) {
            Rectangle treasureBox = treasure.getBoundingBox();
            Rectangle potionBox = potion.getBoundingBox();
            Rectangle swordBox = sword.getBoundingBox();
            Rectangle elixirBox = elixir.getBoundingBox();
            if (sailorBox.intersects(treasureBox)) {
                wonGame = true;
            }
            if (sailorBox.intersects(potionBox) && !potion.isPickedUp()) {
                potion.hasBeenPickedUp();
                potion.setCoordinates(INVENTORY_X, inventoryY);
                inventoryY += INVENTORY_ICON_GAPS;
                if (healthPoints <= (maxHealth - potion.getBuff())) {
                    healthPoints += potion.getBuff();
                } else {
                    healthPoints = maxHealth;
                }
                System.out.println("Sailor finds Potion. Sailor's current health: " + healthPoints + "/" + maxHealth);
            }
            if (sailorBox.intersects(swordBox) && !sword.isPickedUp()) {
                sword.hasBeenPickedUp();
                sword.setCoordinates(INVENTORY_X, inventoryY);
                inventoryY += INVENTORY_ICON_GAPS;
                damage += sword.getBuff();
                System.out.println("Sailor finds Sword. Sailor's damage points increased to " + damage);
            }
            if (sailorBox.intersects(elixirBox) && !elixir.isPickedUp()) {
                elixir.hasBeenPickedUp();
                elixir.setCoordinates(INVENTORY_X, inventoryY);
                inventoryY += INVENTORY_ICON_GAPS;
                maxHealth += elixir.getBuff();
                healthPoints = maxHealth;
                System.out.println("Sailor finds Elixir. Sailor's current health: " + healthPoints + "/" + maxHealth);
            }
        }
    }

    /**
     * Method that puts the Sailor into attack state
     */
    private void attackMode() {
        sailorAttacking = true;
        readyToAttack = false;
    }

    /**
     * Method that puts the Sailor into cool down state
     */
    private void sailorCoolingDown() {
        coolingDown = true;
        sailorAttacking = false;
        readyToAttack = false;
    }

    /**
     * Method that puts the Sailor into ready to attack state
     */
    private void sailorCanAttack() {
        coolingDown = false;
        readyToAttack = true;
    }

    /**
     * Method that changes the Sailor images into the attack images
     */
    private void setToAttackImage() {
        if (currentImage == SAILOR_LEFT) {
            currentImage = SAILOR_HIT_LEFT;
        } else if (currentImage == SAILOR_RIGHT) {
            currentImage = SAILOR_HIT_RIGHT;
        }
    }

    /**
     * Method that moves the sailor given the direction
     * @param xMove This is how much the Sailor moves in the x direction
     * @param yMove This is how much the Sailor moves in the y direction
     */
    private void move(int xMove, int yMove){
        x += xMove;
        y += yMove;
    }

    /**
     * Method that stores the old coordinates of the sailor
     */
    private void setOldPoints() {
        oldX = x;
        oldY = y;
    }

    /**
     * Method that moves the sailor back to its previous position
     */
    private void moveBack() {
        x = oldX;
        y = oldY;
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/maxHealth) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        } else if (percentageHP == GREEN_BOUNDARY) {
            COLOUR.setBlendColour(GREEN);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that checks if sailor's health is <= 0
     * @return boolean This returns whether the sailor is dead
     */
    public boolean isDead(){
        return healthPoints <= 0;
    }

    /**
     * Method that checks if sailor has reached the ladder
     * @return boolean This returns whether the sailor has gone past the stage zero winning bounds
     */
    public boolean hasWonStageZero(){
        return (x >= WIN_STAGE_0_X) && (y > WIN_STAGE_0_Y);
    }

    /**
     * Method that signals that the sailor has intersected the treasure box
     * @return boolean This returns whether the sailor has won the level 1
     */
    public boolean hasWonStageOne() {
        return wonGame;
    }

    /**
     * Method that checks if sailor has gone out-of-bound
     * @return boolean This returns whether the sailor has gone out of bounds
     */
    public boolean isOutOfBound(){
        return (y > bottomRightY) || (y < topLeftY) || (x < topLeftX) ||
                (x > bottomRightX);
    }

    /**
     * Method that sets the potion attribute of the Sailor
     * @param potion This is the potion in level 1
     */
    public void setPotion(Potion potion) {
        this.potion = potion;
    }

    /**
     * Method that sets the sword attribute of the Sailor
     * @param sword This is the sword in level 1
     */
    public void setSword(Sword sword) {
        this.sword = sword;
    }

    /**
     * Method that sets the elixir attribute of the Sailor
     * @param elixir This is the elixir in level 1
     */
    public void setElixir(Elixir elixir) {
        this.elixir = elixir;
    }

    /**
     * Method that puts the coordinates of the level bounds into attributes in Sailor
     * @param topLeftY This is the y coordinate of the top left of the level bound
     * @param topLeftX This is the x coordinate of the top left of the level bound
     * @param bottomRightY This is the y coordinate of the bottom right of the level bound
     * @param bottomRightX This is the x coordinate of the bottom right of the level bound
     */
    public void setBorders(int topLeftY, int topLeftX, int bottomRightY, int bottomRightX) {
        this.topLeftY = topLeftY;
        this.topLeftX = topLeftX;
        this.bottomRightY = bottomRightY;
        this.bottomRightX = bottomRightX;
    }

    /**
     * Method that deducts `damage` from the Sailor's health
     * @param damage This is how much damage the Sailor takes
     */
    private void tookDamage(int damage) {
        healthPoints -= damage;
    }

    /**
     * Method that returns the x coordinate of the Sailor
     * @return int This returns the x coordinate of the Sailor
     */
    public int getX() {
        return x;
    }

    /**
     * Method that returns the y coordinate of the Sailor
     * @return int this returns the y coordinate of the Sailor
     */
    public int getY() {
        return y;
    }

    /**
     * Method that returns the bounding box of the sailor
     */
    private Rectangle getBoundingBox() {
        return currentImage.getBoundingBoxAt(new Point(x + (currentImage.getWidth() / 4),
                y + (currentImage.getHeight() / 4)));
    }
}