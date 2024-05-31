import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Creates moving platforms
 * 
 * @author NJudd
 */
public class MovingPlatform extends Platform {
    // Instance variables
    private int intSpeed;
    private PImage imgBlock;

    /**
     * Constructor: Initializes the platform's position, size, length, and image.
     * 
     * @param papplet reference to the PApplet instance
     * @param size    width and height of the platform blocks
     * @param length  amount of blocks in the platform
     * @param xPos    x position of the platform
     * @param yPos    y position of the platform
     */
    public MovingPlatform(PApplet papplet, int size, int length, int speed, float xPos, float yPos) {
        super(papplet, size, length, xPos, yPos);

        intSpeed = speed;
        imgBlock = getPApplet().loadImage("platformBlock.png");
        imgBlock.resize(getSize(), getSize());
    }

    /**
     * Getter method for the platform speed
     * 
     * @return the platform speed
     */
    public int getSpeed() {
        return intSpeed;
    }

    /**
     * Updates x position and resets position if the platform goes off screen
     * 
     * @param platforms an array of moving platforms
     */
    public void platformShift(ArrayList<MovingPlatform> platforms) {
        setPosX(getPosX() - intSpeed);

        if (getPosX() < 0 - getLength() * getSize()) {
            setPosX(getPApplet().width);
        }
    }

    /**
     * Prints the platform to the screen
     */
    public void draw() {
        for (int i = 0; i < getLength(); i++) {
            getPApplet().image(imgBlock, getPosX() + i * getSize(), getPosY());
        }
    }
}
