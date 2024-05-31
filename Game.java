import java.util.ArrayList;
import processing.core.PApplet;

/**
 * Game class
 * 
 * @author NJudd
 */
public class Game {
    // Instance variables
    private ArrayList<GameLevel> levels = new ArrayList<GameLevel>();
    private PApplet p;

    /**
     * Constructor
     * 
     * @param papplet reference to the PApplet instance
     */
    public Game(PApplet papplet) {
        p = papplet;
        initializeLevels();
    }

    /**
     * Initializes levels
     */
    public void initializeLevels() {
        levels.add(new GameLevel(
                2000,
                "Background1.png",
                new Position(40, 500),
                new Position(0, 0),
                new Position(0, 0),
                createStaticPlatforms(1),
                createMovingPlatforms(1)));
    }

    /**
     * Creates static platforms for the specified level
     * 
     * @param papplet     reference to the PApplet instance
     * @param levelNumber the level number
     * @return the static platforms
     */
    public ArrayList<Platform> createStaticPlatforms(int levelNumber) {
        ArrayList<Platform> platforms = new ArrayList<Platform>();

        if (levelNumber == 1) {
            platforms.add(new Platform(p, 30, 4, 25, 745));
        }

        return platforms;
    }

    /**
     * Creates moving platforms for the specified level
     * 
     * @param papplet     reference to the PApplet instance
     * @param levelNumber the level number
     * @return the moving platforms
     */
    public ArrayList<MovingPlatform> createMovingPlatforms(int levelNumber) {
        ArrayList<MovingPlatform> platforms = new ArrayList<MovingPlatform>();

        if (levelNumber == 1) {
            platforms.add(new MovingPlatform(p, 30, 5, 2, p.width, 500));
        }

        return platforms;
    }

    /**
     * Getter method for the GameLevel
     * 
     * @param levelNumber the level number
     * @return the GameLevel at the specified level number
     */
    public GameLevel getLevel(int levelNumber) {
        return levels.get(levelNumber - 1);
    }
}
