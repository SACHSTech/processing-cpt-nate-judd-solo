import processing.core.PApplet;
import processing.core.PImage;

/**
 * Creates the platform class
 * 
 * @author NJudd
 */
public class Platform extends Position {
    // Instance variables
    private int intSize;
    private int intLength;
    private PApplet p; // Reference to the PApplet instance
    private PImage imgBlock;

    /**
     * Constructor: Initializes the platform's position, size, length, and image.
     * 
     * @param papplet   reference to the PApplet instance
     * @param size      width and height of the platform blocks
     */
    public Platform(PApplet papplet, int size, int length, float xPos, float yPos) {
        super(xPos, yPos);

        p = papplet; // Assign the PApplet reference
        intSize = size;
        intLength = length;
        imgBlock = p.loadImage("platformBlock2.png");
        imgBlock.resize(intSize, intSize);
    }

    /**
     * Getter method for intLength
     * 
     * @return the length of the platform
     */
    public int getLength() {
        return intLength;
    }

    /**
     * Prints the platform to the screen
     */
    public void draw() {
        for (int i = 0; i < intLength; i++) {
            p.image(imgBlock, getPositionX() + i * intSize, getPositionY());
        }
    }
}
