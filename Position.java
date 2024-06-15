/**
 * Position class
 * 
 * @author NJudd
 */
public class Position {
    private float fltXPos, fltYPos;

    /**
     * Default constructor
     */
    public Position() {
        fltXPos = 0;
        fltYPos = 0;
    }

    /**
     * Constructor: sets x and y position
     * 
     * @param xPos x position
     * @param yPos y position
     */
    public Position(float xPos, float yPos) {
        fltXPos = xPos;
        fltYPos = yPos;
    }

    /**
     * Getter method for fltXPos
     * 
     * @return the x position
     */
    public float getPosX() {
        return fltXPos;
    }

    /**
     * Getter method for fltYPos
     * 
     * @return the y position
     */
    public float getPosY() {
        return fltYPos;
    }

    /**
     * Setter method for fltXPos
     * 
     * @param x the x position
     */
    public void setPosX(float x) {
        fltXPos = x;
    }

    /**
     * Setter method for fltYPos
     * 
     * @param y the y position
     */
    public void setPosY(float y) {
        fltYPos = y;
    }
}
