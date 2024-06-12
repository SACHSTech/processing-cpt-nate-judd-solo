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
    private PImage imgBird, imgBird1, imgBird2;
    private int intWidth;
    private int intHeight;
    private int intSpeed;
    private int intBirdCount = 0;

    /**
     * Constructor: Initializes the bird's position, size, and image.
     * 
     * @param papplet reference to the PApplet instance
     * @param width   bird width
     * @param height  bird height
     * @param xPos    bird x position
     * @param yPos    bird y position
     */
    public Bird(PApplet papplet, int width, int height, int speed, float xPos, float yPos) {
        super(xPos, yPos);
        p = papplet;
        intWidth = width;
        intHeight = height;
        intSpeed = speed;

        imgBird1 = p.loadImage("bird1.png");
        imgBird1.resize(intWidth, intHeight);
        imgBird2 = p.loadImage("bird2.png");
        imgBird2.resize(intWidth, intHeight);
        imgBird = imgBird1;
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
    public void flyingBird(ArrayList<Bird> birds, boolean blnCaught) {
        int intTop = 0;

        if (blnCaught) {
            intTop = -150;
        } else {
            intTop = 0;
        }

        setPosY(getPosY() + intSpeed);

        if (getPosY() < intTop) {
            setPosY(intTop);
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
        intBirdCount += 1;

        if (intBirdCount <= 30) {
            imgBird = imgBird1;
        } else if (intBirdCount > 30 && intBirdCount <= 60) {
            imgBird = imgBird2;
        } else {
            intBirdCount = 0;
        }

        p.image(imgBird, getPosX(), getPosY());
    }
}
