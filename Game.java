import java.util.ArrayList;

import processing.core.PApplet;

public class Game {

    private ArrayList<GameLevel> levels = new ArrayList<GameLevel>();
    
    public Game(PApplet papplet) {

        ArrayList<Platform> platforms = new ArrayList<Platform>();
        platforms.add(new Platform(papplet, 30, 4, 100, 100));

        GameLevel level1 = new GameLevel(
                3000,
                1000,
                "Background1.png",
                new Position(),
                new Position(),
                new Position(),
                platforms);

        levels.add(level1);
    }

    public GameLevel getLevel(int levelNumber) {
        return levels.get(levelNumber - 1);
    }

}
