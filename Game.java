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
                "background1.jpg",
                new Position(50, 650),
                new Position(1075, 70),
                new Position(1800, 640),
                createStaticPlatforms(1),
                createMovingPlatforms(1),
                createBirds(1)));
    }

    /**
     * Creates static platforms for the specified level
     * 
     * @param levelNumber the level number
     * @return the static platforms
     */
    public ArrayList<Platform> createStaticPlatforms(int levelNumber) {
        ArrayList<Platform> platforms = new ArrayList<Platform>();

        if (levelNumber == 1) {
            platforms.add(new Platform(p, 30, 4, 20, 750));
            platforms.add(new Platform(p, 30, 5, 225, 650));
            platforms.add(new Platform(p, 30, 7, 25, 360));
            platforms.add(new Platform(p, 30, 5, 350, 260));
            platforms.add(new Platform(p, 30, 6, 775, 400));
            platforms.add(new Platform(p, 30, 4, 850, 200));
            platforms.add(new Platform(p, 30, 6, 1000, 125));
            platforms.add(new Platform(p, 30, 8, 1250, 600));
            platforms.add(new Platform(p, 30, 12, 1525, 725));
        }

        return platforms;
    }

    /**
     * Creates moving platforms for the specified level
     * 
     * @param levelNumber the level number
     * @return the moving platforms
     */
    public ArrayList<MovingPlatform> createMovingPlatforms(int levelNumber) {
        ArrayList<MovingPlatform> platforms = new ArrayList<MovingPlatform>();

        if (levelNumber == 1) {
            platforms.add(new MovingPlatform(p, 30, 6, 2, p.width, 510));
            platforms.add(new MovingPlatform(p, 30, 6, 2, p.width + 600, 510));
        }

        return platforms;
    }

    /**
     * Creates birds for the specified level
     * 
     * @param levelNumber the level number
     * @return the birds
     */
    public ArrayList<Bird> createBirds(int levelNumber) {
        ArrayList<Bird> birds = new ArrayList<Bird>();

        if (levelNumber == 1) {
            // Level 1 has no birds
            birds.add(new Bird(p, 100, 80, 1, 100, 100, false));
        }

        return birds;
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
