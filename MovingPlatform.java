import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Creates the platform class
 * 
 * @author NJudd
 */
public class MovingPlatform {
    // Instance variables
    private float fltXPos;
    private float fltYPos;
    private float fltSpeed;
    private int intSize;
    private int intLength;
    private int intGap;
    private PApplet p; // Reference to the PApplet instance
    private PImage imgBlock;

    /**
     * Constructor: Initializes the platform's position, size, length, and image.
     * 
     * @param papplet   reference to the PApplet instance
     * @param size      width and height of the platform blocks
     * @param heightMC  height of the character
     * @param platforms array of other platforms to avoid overlapping
     */
    public MovingPlatform(PApplet papplet, int size, int heightMC, ArrayList<MovingPlatform> platforms) {
        p = papplet; // Assign the PApplet reference
        intSize = size;
        intGap = heightMC;
        fltSpeed = 2;
        intLength = (int) p.random(3, 7);
        fltXPos = p.width;
        fltYPos = CreateValidYPosition(platforms); // Sets the y position to a valid position
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
     * Getter method for the platform speed
     * 
     * @return the platform speed
     */
    public float getPlatformSpeed() {
        return fltSpeed;
    }

    /**
     * Getter method for intLength
     * 
     * @return the length of the platform
     */
    public int getPlatformLength() {
        return intLength;
    }

    /**
     * Setter method for fltXPos
     * 
     * @param x the x position
     */
    public void setPlatformPositionX(float x) {
        fltXPos = x;
    }

    /**
     * Updates the x-position of the platform based on the inputted speed and
     * resets it if it goes off the side of the screen.
     */
    public void platformShift(ArrayList<MovingPlatform> platforms) {
        fltXPos -= fltSpeed;

        // Resets position, speed and length if it goes off screen
        if (fltXPos < 0 - intLength * intSize - 30) {
            fltXPos = p.width;
            fltYPos = CreateValidYPosition(platforms);
            fltSpeed = 2;
            intLength = (int) p.random(3, 7);
        }
    }

    /**
     * Finds a random valid y position for the platforms to be set to
     * 
     * @param platforms
     * @return the valid platform y position
     */
    private float CreateValidYPosition(ArrayList<MovingPlatform> platforms) {
        float fltNewYPos = 0;
        int intBelowGap = intSize + intGap;
        boolean blnValidY = false;

        // Finds a valid platform y position
        while (blnValidY == false) {
            // Sets the y position to a random y position
            fltNewYPos = p.random(80, p.height - 100);

            // Initailizes the y position to true
            blnValidY = true;

            // Goes through each element in the arraylist
            for (MovingPlatform platform : platforms) {
                // Position of current platform being checked
                float fltX1 = platform.fltXPos;
                float fltY1 = platform.fltYPos;

                // Checks if the y position is valid
                if (fltX1 < p.width / 2) {
                    blnValidY = true;
                } else if (fltNewYPos + intBelowGap < fltY1 || fltNewYPos > fltY1 + intBelowGap) {
                    blnValidY = true;
                } else {
                    blnValidY = false;
                    break;
                }
            }
        }
        // The valid position
        return fltNewYPos;
    }

    /**
     * Prints the platform to the screen with a random length when called
     */
    public void draw() {
        // Draw the platform blocks based on its length
        for (int i = 0; i <= intLength; i++) {
            p.image(imgBlock, fltXPos + i * intSize, fltYPos);
        }
    }
}
