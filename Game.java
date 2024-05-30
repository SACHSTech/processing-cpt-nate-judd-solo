import java.util.ArrayList;
import processing.core.PApplet;

/**
 * Game class
 * 
 * @author NJudd
 */
public class Game extends GameLevel {
    // Instance variables
    private ArrayList<GameLevel> levels = new ArrayList<GameLevel>();

    /**
     * Constructor
     * 
     * @param papplet reference to the PApplet instance
     */
    public Game(PApplet papplet) {

        ArrayList<Platform> platforms = new ArrayList<Platform>();
        platforms.add(new Platform(papplet, 30, 4, 25, 745));

        ArrayList<MovingPlatform> movingPlatforms = new ArrayList<MovingPlatform>();
        movingPlatforms.add(new MovingPlatform(papplet, 30, 5, 2, papplet.width, 500));

        GameLevel level1 = new GameLevel(
                3000,
                1000,
                "Background1.png",
                new Position(40, 650),
                new Position(0, 0),
                new Position(0, 0),
                platforms,
                movingPlatforms);

        levels.add(level1);
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
