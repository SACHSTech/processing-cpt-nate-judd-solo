import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Creates the bird class
 * 
 * @author NJudd
 */
public class Bird extends Position {
    // Instance variables
    private PApplet p; // Reference to the PApplet instance
    private PImage imgBird;
    private int intWidth;
    private int intHeight;
    private int intSpeed;

    /**
     * Constructor: Initializes the bird's position, size, and image.
     * 
     * @param papplet reference to the PApplet instance
     * @param width   bird width
     * @param height  bird height
     * @param xPos    bird x position
     * @param yPos    bird y position
     * @param right   set to true for the bird to face right and false to face left
     */
    public Bird(PApplet papplet, int width, int height, int speed, float xPos, float yPos, boolean right) {
        super(xPos, yPos);
        p = papplet;
        intWidth = width;
        intHeight = height;
        intSpeed = speed;

        if (right) {
            imgBird = p.loadImage("birdRight.png");
        } else if (!right) {
            imgBird = p.loadImage("birdLeft.png");
        }
        imgBird.resize(intWidth, intHeight);
    }

    /**
     * Getter method for width
     * 
     * @return the width of the bird
     */
    public int getBirdWidth() {
        return intWidth;
    }

    /**
     * Getter method for height
     * 
     * @return the height of the bird
     */
    public int getBirdHeight() {
        return intHeight;
    }

    /**
     * Getter method for the bird speed
     * 
     * @return the bird speed
     */
    public int getBirdSpeed() {
        return intSpeed;
    }

    /**
     * Updates x and y position and resets position if the bird goes off screen
     * 
     * @param birds an array of bird objects
     */
    public void flyingBird(ArrayList<Bird> birds) {
        setPosY(getPosY() + intSpeed);

        if (getPosY() < 0) {
            setPosY(0);
            intSpeed *= -1;
        } else if (getPosY() > p.height - intHeight) {
            setPosY(p.height - intHeight);
            intSpeed *= -1;
        }
    }

    /**
     * Prints the bird to the screen
     */
    public void draw() {
        p.image(imgBird, getPosX(), getPosY());
    }
}
