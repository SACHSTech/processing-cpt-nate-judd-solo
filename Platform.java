import processing.core.PApplet;
import processing.core.PImage;

/**
 * Creates a platform class
 * 
 * @author NJudd
 */
public class Platform {

    // Instance variables
    private float fltXPos;
    private float fltYPos;
    private int intSpeed;
    private int intSize;
    private int intLength;
    private PApplet p; // Reference to the PApplet instance
    private PImage imgBlock;

    /**
     * Constructor: Initializes the platform block's position, size, length and
     * image.
     * 
     * @param P    reference to the PApplet instance
     * @param size width and height of the platform blocks
     */
    public Platform(PApplet P, int size) {
        p = P;
        intSize = size;
        intSpeed = (int) p.random(1, 5);
        intLength = (int) p.random(1, 5); // 1 to 5 is the range
        fltXPos = p.random(p.width);
        fltYPos = p.random(p.height);
        imgBlock = p.loadImage("platformBlock.png");
        imgBlock.resize(intSize, intSize);
    }

    /**
     * Getter method for fltXPos
     * 
     * @return the x coordinate of the platform
     */
    public float getPlatformPositionX() {
        return fltXPos;
    }

    /**
     * Getter method for fltYPos
     * 
     * @return the y coordinate of the platform
     */
    public float getPlatformPositionY() {
        return fltYPos;
    }

    /**
     * Setter method for platform x and y position
     * 
     * @param XPos x position
     * @param YPos y position
     */
    public void setPlatformPosition(float XPos, float YPos) {
        fltXPos = XPos;
        fltYPos = YPos;
    }

    /**
     * Updates the x-position of the platform based on the inputted speed and
     * resets it if it goes off the side of the screen.
     * 
     * @param speed speed that the platforms move at
     */
    public void platformShift() {
        fltXPos -= intSpeed;
        // Reset position if it goes off screen
        if (fltXPos < -intLength * intSize) { 
            fltXPos = p.width;
            fltYPos = p.random(p.height);
        }
    }

    /**
     * Prints the platform to the screen with a random length when called
     */
    public void draw() {
        for (int i = 0; i <= intLength; i++) {
            p.image(imgBlock, fltXPos + i * intSize, fltYPos);
        }
        
    }
}
