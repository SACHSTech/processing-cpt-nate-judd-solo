import java.util.ArrayList;
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
     * @param platforms array of other platforms to avoid overlapping
     */
    public Platform(PApplet papplet, int size, ArrayList<Platform> platforms) {
        p = papplet; // Assign the PApplet reference
        intSize = size;
        intGap = 36; // 36 allows the character to crouch and fit in the gap with a little extra space (1 pixel)
        fltSpeed = p.random(1.75f, 3);
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
    public void platformShift(ArrayList<Platform> platforms) {
        fltXPos -= fltSpeed;

        // Resets position, speed and length if it goes off screen
        if (fltXPos < 0 - intLength * intSize - 30) {
            fltXPos = p.width;
            fltYPos = CreateValidYPosition(platforms);
            fltSpeed = p.random(1.75f, 3);
            intLength = (int) p.random(3, 7);
        }
    }

    /**
     * Finds a random valid y position for the platforms to be set to
     * 
     * @param platforms
     * @return the valid platform y position
     */
    private float CreateValidYPosition(ArrayList<Platform> platforms) {
        float fltNewYPos = 0;
        int intBelowGap = intSize + intGap;
        boolean blnValidY = false;

        // Finds a valid platform y position
        while (blnValidY == false) {
            // Sets the y position to a random y position
            fltNewYPos = p.random(70, p.height - 100);

            // Initailizes the y position to true
            blnValidY = true;

            // Goes through each element in the arraylist
            for (Platform platform : platforms) {
                // Y position of current platform being checked
                float fltY1 = platform.fltYPos;

                // Checks if the y position is valid
                if (fltNewYPos + intBelowGap < fltY1 || fltNewYPos > fltY1 + intBelowGap) {
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
