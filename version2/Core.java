import java.awt.Point;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Inject {}

interface PlayerActions {
}

interface PlayerInputs {
}

abstract class GameManager<TPlayer> {
    List<TPlayer> players;

    abstract List<TPlayer> getActivePlayers();

    abstract TPlayer getPlayer(int index);

    abstract void end();
}

abstract class InOutManager {
    abstract List<String> readActions(int lines, int timeout);
    abstract void sendInputs(List<String> lines);
}

abstract class Player {
    @Inject InOutManager inOutManager;

    int index;
    String nickName;
    boolean active = true;
    String deathReason;

    Integer expectedNbLines = null;

    boolean isActive() {
        return active;
    }

    int getIndex() {
        return index;
    }

    List<String> sendInputsAndWaitActions(List<String> inputs, int timeout, int expectedNumberOfLi) throws ReadActionsException, PlayerTimeoutException {
//        int lines = expectedNbLines != null ? expectedNbLines : inOutManager.getExpectedNbLines();
//        inOutManager.sendInputs(serialize(inputs));
//        return deserialize(inOutManager.readActions(lines, timeout));
        return new ArrayList<>();
    }

    void die(String reason) {
        this.deathReason = reason;
        this.active = false;
    }
}

interface ResourcesManager {
    void loadConfig(String uri);
    void loadTexture(String id);
    void loadFont(String id);
    void registerAndLoadReplay(String id, int gameId);
    void registerAndLoadTexture(String id, String uri);
}

abstract class Referee<TPlayer> {
    @Inject GameManager<TPlayer> gameManager;
    @Inject World world;
    @Inject ResourcesManager resourcesManager;

    void load() {
    }

    void init() {
    }

    abstract void start();

    abstract void update();

    Map<Integer, Integer> getScore() {
        return new HashMap<>();
    }

    int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }
}

class World {
    int width;
    int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    void update() {
        update(1.0);
    }

    void update(double t) {
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Sprite createSprite() {
        return null;
    }
}

class Entity {
    private int x;
    private int y;
    private double rotation;
    private double scale;
    boolean dirty = false;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getRotation() {
        return rotation;
    }

    public double getScale() {
        return scale;
    }

    public void setX(int x) {
        dirty = this.x != x;
        this.x = x;
    }

    public void setY(int y) {
        dirty = this.y != y;
        this.y = y;
    }

    public void setRotation(double rotation) {
        dirty = this.rotation != rotation;
        this.rotation = rotation;
    }

    public void setScale(double scale) {
        dirty = this.scale != scale;
        this.scale = scale;
    }
}

class Sprite extends Entity {
    int textureId;
    Point anchor;
    
    public void setPosition(int i, int j) {
        // TODO Auto-generated method stub
        
    }

    public void setTexture(String string, double d, double e) {
        // TODO Auto-generated method stub
        
    }

    public void setTexture(String string) {
        // TODO Auto-generated method stub
        
    }

    public void setColor(String string) {
        // TODO Auto-generated method stub
        
    }
}

class ReadActionsException extends Exception {
    private static final long serialVersionUID = 5339510515204270837L;
}

class PlayerTimeoutException extends Exception {
    private static final long serialVersionUID = 8387976923557968095L;
}
