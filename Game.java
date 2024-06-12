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
                3000,
                "background2.jpg",
                new Position(50, 450),
                new Position(1085, 200),
                new Position(2800, 200),
                createStaticPlatforms(2),
                createMovingPlatforms(2),
                createBirds(2)));

        // Level 3
        levels.add(new GameLevel(
                3500,
                "background3.jpg",
                new Position(50, 335),
                new Position(60, 690),
                new Position(3300, 275),
                createStaticPlatforms(3),
                createMovingPlatforms(3),
                createBirds(3)));

        // Level 4
        levels.add(new GameLevel(
                4000,
                "background4.jpg",
                new Position(50, 0),
                new Position(2240, 40),
                new Position(3800, 650),
                createStaticPlatforms(4),
                createMovingPlatforms(4),
                createBirds(4)));

        // Level 5
        levels.add(new GameLevel(
                5000,
                "background5.jpg",
                new Position(50, 550),
                new Position(2365, 690),
                new Position(4770, 75),
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
            platforms.add(new Platform(p, 30, 8, 1250, 650));
            platforms.add(new Platform(p, 30, 11, 1525, 725));
        } else if (levelNumber == 2) {
            platforms.add(new Platform(p, 30, 4, 20, 550));
            platforms.add(new Platform(p, 30, 3, 1500, 590));
            platforms.add(new Platform(p, 30, 6, 1550, 425));
            platforms.add(new Platform(p, 30, 4, 1300, 360));
            platforms.add(new Platform(p, 30, 4, 1050, 260));
            platforms.add(new Platform(p, 30, 5, 1950, 360));
            platforms.add(new Platform(p, 30, 3, 2150, 550));
            platforms.add(new Platform(p, 30, 3, 2790, 590));
            platforms.add(new Platform(p, 30, 3, 2570, 475));
            platforms.add(new Platform(p, 30, 3, 2570, 350));
            platforms.add(new Platform(p, 30, 5, 2800, 300));
        } else if (levelNumber == 3) {
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
        } else if (levelNumber == 4) {
            platforms.add(new Platform(p, 30, 4, 20, 100));
            platforms.add(new Platform(p, 30, 3, 500, 300));
            platforms.add(new Platform(p, 30, 5, 560, 130));
            platforms.add(new Platform(p, 30, 5, 1110, 700));
            platforms.add(new Platform(p, 30, 4, 1470, 590));
            platforms.add(new Platform(p, 30, 2, 1700, 290));
            platforms.add(new Platform(p, 30, 6, 1900, 180));
            platforms.add(new Platform(p, 30, 4, 2200, 100));
            platforms.add(new Platform(p, 30, 5, 2600, 600));
            platforms.add(new Platform(p, 30, 6, 3100, 750));
            platforms.add(new Platform(p, 30, 4, 3450, 650));
            platforms.add(new Platform(p, 30, 3, 3805, 750));
        } else if (levelNumber == 5) {
            platforms.add(new Platform(p, 30, 4, 20, 650));
            platforms.add(new Platform(p, 30, 3, 2340, 750));
            platforms.add(new Platform(p, 30, 4, 4500, 340));
            platforms.add(new Platform(p, 30, 5, 4770, 225));
            platforms.add(new Platform(p, 30, 3, 650, 440));
            platforms.add(new Platform(p, 30, 3, 1650, 440));
            platforms.add(new Platform(p, 30, 3, 2650, 440));
            platforms.add(new Platform(p, 30, 3, 3650, 440));
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
            platforms.add(new MovingPlatform(p, 30, 3, 2, 1125, 700));
            platforms.add(new MovingPlatform(p, 30, 3, 2, 375, 700));
            platforms.add(new MovingPlatform(p, 30, 3, 2, 750, 700));
        } else if (levelNumber == 3) {
            platforms.add(new MovingPlatform(p, 30, 4, 3, p.width, 600));
            platforms.add(new MovingPlatform(p, 30, 4, 3, p.width + 600, 600));
            platforms.add(new MovingPlatform(p, 30, 5, 2, p.width, 225));
            platforms.add(new MovingPlatform(p, 30, 5, 2, p.width + 300, 150));
        } else if (levelNumber == 4) {
            platforms.add(new MovingPlatform(p, 30, 6, 4, p.width, 400));
        } else if (levelNumber == 5) {
            platforms.add(new MovingPlatform(p, 30, 4, 3, p.width, 540));
            platforms.add(new MovingPlatform(p, 30, 4, 3, p.width + 400, 440));
            platforms.add(new MovingPlatform(p, 30, 4, 3, p.width + 800, 340));
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
            birds.add(new Bird(p, 90, 75, 3, 1185, p.height, true));
            birds.add(new Bird(p, 90, 75, 3, 1455, 0, true));
        } else if (levelNumber == 3) {
            birds.add(new Bird(p, 90, 75, 3, 450, 0, false));
            birds.add(new Bird(p, 90, 75, 2, 1250, 0, true));
        } else if (levelNumber == 4) {
            birds.add(new Bird(p, 90, 75, 2, 500, 0, false));
            birds.add(new Bird(p, 90, 75, 2, 900, 100, true));
            birds.add(new Bird(p, 90, 75, 3, 1300, 300, false));
            birds.add(new Bird(p, 90, 75, 3, 1785, 700, false));
            birds.add(new Bird(p, 90, 75, 4, 2100, 200, true));
            birds.add(new Bird(p, 90, 75, 3, 2400, 400, false));
            birds.add(new Bird(p, 90, 75, 3, 2900, 200, false));
            birds.add(new Bird(p, 90, 75, 4, 3345, 0, true));
            birds.add(new Bird(p, 90, 75, 4, 3575, 400, false));
            birds.add(new Bird(p, 90, 75, 4, 3675, 600, true));
        } else if (levelNumber == 5) {
            birds.add(new Bird(p, 90, 75, 3, 500, 0, false));
            birds.add(new Bird(p, 90, 75, 3, 1000, 800, true));
            birds.add(new Bird(p, 90, 75, 3, 1500, 0, false));
            birds.add(new Bird(p, 90, 75, 3, 2000, 800, true));
            birds.add(new Bird(p, 90, 75, 3, 2500, 0, false));
            birds.add(new Bird(p, 90, 75, 3, 3000, 800, true));
            birds.add(new Bird(p, 90, 75, 3, 3500, 0, false));
            birds.add(new Bird(p, 90, 75, 3, 4000, 800, true));
            birds.add(new Bird(p, 110, 85, 5, 4630, 0, false));
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
