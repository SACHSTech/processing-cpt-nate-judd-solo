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
        // Level 1
        levels.add(new GameLevel(
                2000,
                "background1.jpg",
                new Position(50, 650),
                new Position(1075, 70),
                new Position(1750, 625),
                createStaticPlatforms(1),
                createMovingPlatforms(1),
                createBirds(1)));

        // Level 2
        levels.add(new GameLevel(
                3500,
                "background2.jpg",
                new Position(50, 335),
                new Position(60, 690),
                new Position(3300, 275),
                createStaticPlatforms(2),
                createMovingPlatforms(2),
                createBirds(2)));

        // Level 3
        levels.add(new GameLevel(
                3000,
                "background3.jpg",
                new Position(50, 650),
                new Position(200, 690),
                new Position(2800, 650),
                createStaticPlatforms(3),
                createMovingPlatforms(3),
                createBirds(3)));

        // Level 4
        levels.add(new GameLevel(
                4000,
                "background4.jpg",
                new Position(50, 650),
                new Position(200, 690),
                new Position(3800, 650),
                createStaticPlatforms(4),
                createMovingPlatforms(4),
                createBirds(4)));

        // Level 5
        levels.add(new GameLevel(
                2500,
                "background5.jpg",
                new Position(50, 650),
                new Position(200, 690),
                new Position(100, 100),
                createStaticPlatforms(5),
                createMovingPlatforms(5),
                createBirds(5)));
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
            platforms.add(new Platform(p, 30, 11, 1525, 725));
        } else if (levelNumber == 2) {
            platforms.add(new Platform(p, 30, 8, 20, 425));
            platforms.add(new Platform(p, 30, 4, 20, 750));
            platforms.add(new Platform(p, 30, 3, 450, 315));
            platforms.add(new Platform(p, 30, 7, 800, 500));
            platforms.add(new Platform(p, 30, 3, 1250, 500));
            platforms.add(new Platform(p, 30, 5, 1500, 425));
            platforms.add(new Platform(p, 30, 6, 1850, 350));
            platforms.add(new Platform(p, 30, 5, 2300, 350));
            platforms.add(new Platform(p, 30, 4, 2500, 100));
            platforms.add(new Platform(p, 30, 6, 2850, 710));
            platforms.add(new Platform(p, 30, 10, 3100, 510));
            platforms.add(new Platform(p, 30, 3, 3305, 375));
        } else if (levelNumber == 3) {
            platforms.add(new Platform(p, 30, 99, 20, 750));
        } else if (levelNumber == 4) {
            platforms.add(new Platform(p, 30, 130, 20, 750));
        } else if (levelNumber == 5) {
            platforms.add(new Platform(p, 30, 82, 20, 750));
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
        } else if (levelNumber == 2) {
            platforms.add(new MovingPlatform(p, 30, 4, 3, p.width, 600));
            platforms.add(new MovingPlatform(p, 30, 4, 3, p.width + 600, 600));
            platforms.add(new MovingPlatform(p, 30, 5, 2, p.width, 225));
            platforms.add(new MovingPlatform(p, 30, 5, 2, p.width + 300, 150));
        } else if (levelNumber == 3) {
            platforms.add(new MovingPlatform(p, 30, 5, 2, 20, 370));
        } else if (levelNumber == 4) {
            platforms.add(new MovingPlatform(p, 30, 5, 2, 20, 370));
        } else if (levelNumber == 5) {
            platforms.add(new MovingPlatform(p, 30, 5, 2, 20, 370));
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
        } else if (levelNumber == 2) {
            birds.add(new Bird(p, 90, 75, 3, 450, 0, false));
            birds.add(new Bird(p, 90, 75, 2, 1250, 0, true));
        } else if (levelNumber == 3) {
            birds.add(new Bird(p, 90, 75, 3, 450, 0, false));
        } else if (levelNumber == 4) {
            birds.add(new Bird(p, 90, 75, 3, 450, 0, false));
        } else if (levelNumber == 5) {
            birds.add(new Bird(p, 90, 75, 3, 450, 0, false));
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
