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
     * Constructor: Initializes the platform block's position, size, length, and
     * image. Ensures that the platform does not overlap with existing platforms.
     * 
     * @param P     reference to the PApplet instance
     * @param size  width and height of the platform blocks
     * @param other array of other platforms to avoid overlapping
     */
    public Platform(PApplet P, int size, Platform[] other) {
        p = P; // Assign the PApplet reference
        intSize = size;
        intSpeed = (int) p.random(1, 4);
        intLength = (int) p.random(1, 5);
        fltXPos = p.random(p.width / 2 , p.width);
        fltYPos = p.random(100, p.height - 100 - intSize);
        adjustPosition(other); // Adjust position to avoid overlap with other platforms
        imgBlock = p.loadImage("platformBlock.png");
        imgBlock.resize(intSize, intSize);
    }

    /**
     * Adjusts the position of the platform to avoid overlap with existing
     * platforms.
     * 
     * @param other array of other platforms to check against
     */
    private void adjustPosition(Platform[] other) {
        // Repeatedly reposition the platform until it does not overlap with others
        while (checkOverlap(other)) {
            fltXPos = p.random(p.width);
            fltYPos = p.random(p.height);
        }
    }

    /**
     * Checks if this platform overlaps with any other platform.
     * 
     * @param other array of other platforms to check against
     * @return true if overlap is found, false otherwise
     */
    private boolean checkOverlap(Platform[] other) {
        // Iterate through each platform in the array
        for (Platform p : other) {
            // Checks if a slot in the array does not current hold a Platform object
            if (p != null) {
                float otherX = p.getPlatformPositionX();
                float otherY = p.getPlatformPositionY();
                float otherLength = p.getPlatformLength();

                // Check for overlap between this platform and the other platform
                if (fltXPos < otherX + otherLength * intSize && fltXPos + intLength * intSize > otherX
                        && fltYPos < otherY + intSize && fltYPos + intSize > otherY) {
                    return true; // Overlap detected
                }
            }
        }
        return false; // No overlap detected
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
     * Getter method for intLength
     * 
     * @return the length of the platform
     */
    public int getPlatformLength() {
        return intLength;
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
        // Draw the platform blocks based on its length
        for (int i = 0; i <= intLength; i++) {
            p.image(imgBlock, fltXPos + i * intSize, fltYPos);
        }
    }
}
