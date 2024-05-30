import java.util.ArrayList;

public class GameLevel {
    private int width;
    private int height;
    private ArrayList<Platform> platforms;
    private String bgImageName;
    private Position spawnPosition;
    private Position keyPosition;
    private Position exitPosition;

    public GameLevel() {
        width = 0;
        height = 0;
        platforms = new ArrayList<Platform>();
        bgImageName = "";
        keyPosition = new Position();
        exitPosition = new Position();
    }

    public GameLevel(int w, int h, String bgImage, Position spawnPos, Position keyPos, Position exitPos, ArrayList<Platform> p) {
        width = w;
        height = h;
        bgImageName = bgImage;
        spawnPosition = spawnPos;
        keyPosition = keyPos;
        exitPosition = exitPos;
        platforms = p;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
