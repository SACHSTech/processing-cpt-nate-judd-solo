import java.util.ArrayList;

/**
 * Creates game levels
 * 
 * @author NJudd
 */
public class GameLevel {
    // Instance variables
    private int intWidth;
    private ArrayList<Platform> platforms;
    private ArrayList<MovingPlatform> movingPlatforms;
    private String strBgName;
    private Position spawnPosition;
    private Position keyPosition;
    private Position exitPosition;

    /**
     * Default constructor
     */
    public GameLevel() {
        intWidth = 0;
        platforms = new ArrayList<Platform>();
        movingPlatforms = new ArrayList<MovingPlatform>();
        strBgName = "";
        spawnPosition = new Position();
        keyPosition = new Position();
        exitPosition = new Position();
    }

    /**
     * Constructor: sets game level parameters
     * 
     * @param width    width of the level
     * @param bgImage  background image name
     * @param spawnPos character spawn position
     * @param keyPos   key position
     * @param exitPos  exit position
     * @param p1       static platforms
     * @param p2       moving platforms
     */
    public GameLevel(int width, String bgImage, Position spawnPos, Position keyPos, Position exitPos,
            ArrayList<Platform> p1, ArrayList<MovingPlatform> p2) {
        intWidth = width;
        strBgName = bgImage;
        spawnPosition = spawnPos;
        keyPosition = keyPos;
        exitPosition = exitPos;
        platforms = p1;
        movingPlatforms = p2;
    }

    /**
     * Getter method for the level width
     * 
     * @return the level width
     */
    public int getWidth() {
        return intWidth;
    }

    /**
     * Getter method for the background image name
     * 
     * @return the background image name
     */
    public String getBackground() {
        return strBgName;
    }

    /**
     * Getter method for the spawn position
     * 
     * @return the spawn position
     */
    public Position getSpawnPosition() {
        return spawnPosition;
    }

    /**
     * Getter method for the key position
     * 
     * @return the key position
     */
    public Position getKeyPosition() {
        return keyPosition;
    }

    /**
     * Getter method for the exit position
     * 
     * @return the exit position
     */
    public Position getExitPosition() {
        return exitPosition;
    }

    /**
     * Getter method for the static platforms
     * 
     * @return the platforms
     */
    public ArrayList<Platform> getStaticPlatforms() {
        return platforms;
    }

    /**
     * Getter method for the moving platforms
     * 
     * @return the platforms
     */
    public ArrayList<MovingPlatform> getMovingPlatforms() {
        return movingPlatforms;
    }

    /**
     * Gets the x position of each static platform
     * 
     * @return an ArrayList of x positions of static platforms
     */
    public ArrayList<Float> getStaticPlatformPositions() {
        ArrayList<Float> positions = new ArrayList<Float>();

        for (Platform platform : platforms) {
            positions.add(platform.getPosX());
        }

        return positions;
    }

    /**
     * Sets the x position of each static platform
     * 
     * @param positions an ArrayList of x positions to set
     */
    public void setStaticPlatformPositions(ArrayList<Float> positions) {
        for (int i = 0; i < platforms.size(); i++) {
            platforms.get(i).setPosX(positions.get(i));
        }
    }
}
