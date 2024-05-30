import processing.core.PApplet;
import processing.core.PImage;

/**
 * Creates the bird class
 * 
 * @author NJudd
 */
public class Bird {
    // Instance variables
    private PApplet p; // Reference to the PApplet instance
    private PImage imgBird;
    private int intWidth, intHeight;
    private float fltXPos, fltYPos;

    /**
     * Constructor: Initializes the bird's position, size, and image.
     * 
     * @param papplet reference to the PApplet instance
     * @param bird    bird image
     * @param width   bird width
     * @param height  bird height
     * @param xPos    bird x position
     * @param yPos    bird y position
     */
    public Bird(PApplet papplet, PImage bird, int width, int height, float xPos, float yPos) {
        p = papplet;
        imgBird = bird;
        intWidth = width;
        intHeight = height;
        fltXPos = xPos;
        fltYPos = yPos;
    }
}
