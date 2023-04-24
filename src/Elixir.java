import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is an Elixir class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Elixir extends Item {
    private final static Image ELIXIR = new Image("res/items/elixir.png");
    private final static Image INVENTORY_ELIXIR = new Image("res/items/elixirIcon.png");
    // this constant represents how much health the Sailor gains when it picks up elixir
    private final static int ELIXIR_BUFF = 35;
    private Image currentImage;

    public Elixir(int x, int y) {
        super(x, y, ELIXIR_BUFF);
        currentImage = ELIXIR;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        currentImage.drawFromTopLeft(getX(), getY());
    }

    /**
     * Method that returns the bounding box of the elixir
     * @return Rectangle This is the bounding box of the elixir
     */
    public Rectangle getBoundingBox() {
        return ELIXIR.getBoundingBoxAt(new Point(getX(), getY()));
    }

    /**
     * Method that tells the elixir it has been picked up by the Sailor
     */
    public void hasBeenPickedUp() {
        currentImage = INVENTORY_ELIXIR;
        setPickedUp(true);
    }

    /**
     * Method that returns how much health the elixir adds to the Sailor
     * @return int This is how much health is added to the Sailor's maximum health when elixir is picked up
     */
    public int getBuff() {
        return ELIXIR_BUFF;
    }
}
