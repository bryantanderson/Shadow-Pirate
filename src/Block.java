import bagel.Drawing;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

/** This is a block class for ShadowPirate
 * @author Bryant Anderson Ciputra
 * @version 1.0
 */


public class Block{
    private final static Image BLOCK = new Image("res/block.png");
    private final int x;
    private final int y;

    public Block(int startX, int startY){
        this.x = startX;
        this.y = startY;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        BLOCK.drawFromTopLeft(x, y);
    }

    /**
     * Method that returns the bounding box of the block
     * @return Rectangle This is the bounding box of the block
     */
    public Rectangle getBoundingBox(){
        return BLOCK.getBoundingBoxAt(new Point(x + (BLOCK.getWidth() / 4),
                y + (BLOCK.getHeight() / 4)));
    }
}
