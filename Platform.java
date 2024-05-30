import processing.core.PApplet;
import processing.core.PImage;

/**
 * Static platform class
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
     * @param papplet reference to the PApplet instance
     * @param size    width and height of the platform blocks
     * @param length  amount of blocks in the platform
     * @param xPos    x position of the platform
     * @param yPos    y position of the platform
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
     * Getter method for the reference to the PApplet instance
     * 
     * @return th reference to the PApplet instance
     */
    public PApplet getPApplet() {
        return p;
    }

    /**
     * Getter method for size
     * 
     * @return the width and height of the platform block
     */
    public int getSize() {
        return intSize;
    }

    /**
     * Getter method for length
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
            p.image(imgBlock, getPosX() + i * intSize, getPosY());
        }
    }
}
