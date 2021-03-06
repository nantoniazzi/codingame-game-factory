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

    abstract void endGame();

    abstract List<TPlayer> getPlayers();

    public void callPlayers() {
    }

    public void setTimeouts(int initialTimeout, int turnTimeout) {
        // TODO Auto-generated method stub
        
    }
}

abstract class InOutManager {
    abstract List<String> readActions(int lines, int timeout);

    abstract void sendInputs(List<String> lines);
}

class Vector {
    double x;
    double y;

    Vector mul(double factor) {
        return this;
    }
}

@FunctionalInterface
interface ImpactHandler {
    boolean apply(Impact impact);
}

class PhysicalEntity extends Entity {
    Vector speed;
    double mass;
    ImpactHandler impactHandler = this::defaultImpactBehaviour;

    void setSpeed(Vector speed) {
        this.speed.x = speed.x;
        this.speed.y = speed.y;
    }

    public PhysicalEntity(String id) {
        super(id);
    }

    PhysicalEntity onImpact(ImpactHandler impactHandler) {
        this.impactHandler = impactHandler;
        return this;
    }

    boolean defaultImpactBehaviour(Impact impact) {
        setGlobalX((int) Math.round(impact.x));
        setGlobalY((int) Math.round(impact.y));
        setSpeed(speed.mul(-1.0));
        
        return true;
    }

    ImpactHandler getImpactHandler() {
        return this.impactHandler;
    }
    
    public PhysicalEntity addChild(Entity entity) {
        super.addChild(entity);
        return this;
    }

    public PhysicalEntity setX(int x) {
        super.setX(x);
        return this;
    }

    public PhysicalEntity setY(int x) {
        super.setY(x);
        return this;
    }
}

class CirclePhysicalEntity extends PhysicalEntity {
    double radius;

    public CirclePhysicalEntity(String id) {
        super(id);
    }

    public CirclePhysicalEntity addChild(Entity entity) {
        super.addChild(entity);
        return this;
    }

    public CirclePhysicalEntity setX(int x) {
        super.setX(x);
        return this;
    }

    public CirclePhysicalEntity setY(int x) {
        super.setY(x);
        return this;
    }

    public CirclePhysicalEntity setRadius(double r) {
        this.radius = r;
        return this;
    }
    
    public CirclePhysicalEntity onImpact(ImpactHandler impactHandler) {
        this.impactHandler = impactHandler;
        return this;
    }
}

class RectPhysicalEntity extends PhysicalEntity {
    int width;
    int height;
    Point anchor;

    public RectPhysicalEntity(String id) {
        super(id);
    }

    public RectPhysicalEntity addChild(Entity entity) {
        super.addChild(entity);
        return this;
    }

    public RectPhysicalEntity setX(int x) {
        super.setX(x);
        return this;
    }

    public RectPhysicalEntity setY(int x) {
        super.setY(x);
        return this;
    }

    public RectPhysicalEntity setAnchorX(int x) {
        this.anchor.x = x;
        return this;
    }

    public RectPhysicalEntity setAnchorY(int y) {
        this.anchor.y = y;
        return this;
    }

    public RectPhysicalEntity setWidth(int width) {
        this.width = width;
        return this;
    }

    public RectPhysicalEntity setHeight(int height) {
        this.height = height;
        return this;
    }
}

class Impact {
    double x;
    double y;
    PhysicalEntity entityA;
    PhysicalEntity entityB;
    double time;
}

class PhysicalWorld {
    List<PhysicalEntity> entities = new ArrayList<>();
    @Inject World world;

    CirclePhysicalEntity createCirlce(String id) {
        CirclePhysicalEntity ret = new CirclePhysicalEntity(id);
        entities.add(ret);
        return ret;
    }

    RectPhysicalEntity createRect(String id) {
        RectPhysicalEntity ret = new RectPhysicalEntity(id);
        entities.add(ret);
        return ret;
    }
    
    void update() {
        // solve collisions and calls all world.update(delta);
    }
}

abstract class Player {
    @Inject InOutManager inOutManager;

    int index;
    String nickName;
    boolean active = true;
    String deathReason;
    Integer score;

    Integer expectedNbLines = null;
    List<String> inputs;
    List<String> actions;

    boolean isActive() {
        return active;
    }
    
    public List<String> getActions() {
        return null;
    }


    int getIndex() {
        return index;
    }

    private List<String> sendInputsAndWaitActions(List<String> inputs, int timeout, int expectedNumberOfLi)
            throws ReadActionsException, PlayerTimeoutException {
        //        int lines = expectedNbLines != null ? expectedNbLines : inOutManager.getExpectedNbLines();
        //        inOutManager.sendInputs(serialize(inputs));
        //        return deserialize(inOutManager.readActions(lines, timeout));
        return new ArrayList<>();
    }

    public void setInputs(List<String> inputs) {
        
    }

    void die(String reason) {
        this.deathReason = reason;
        this.active = false;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    public void setExpectedNbLines(int expectedNbLines) {
        this.expectedNbLines = expectedNbLines;
    }
}

interface ResourcesManager {
    void loadConfig(String uri);

    void loadTexture(String id);

    void loadFont(String id);

    void registerAndLoadReplay(String id, int gameId);

    void registerAndLoadTexture(String id, String uri);
}

interface ReplayManager {
    void playLoop(String... replayIds);
}

abstract class Referee<TPlayer> {
    @Inject GameManager<TPlayer> gameManager;
    @Inject World world;
    @Inject ResourcesManager resourcesManager;
    @Inject ReplayManager replayManager;

    void onLoad() {
    }

    void onInit() {
    }

    abstract void onFirstFrame();

    abstract void onUpdateFrame(int frameIndex);

    Map<Integer, Integer> getScore() {
        return new HashMap<>();
    }

    int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    abstract void onEnd();
}

class World {
    int width;
    int height;
    List<Entity> entities;

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

    public Entity createEntity(String id) {
        Entity ret = new Entity(id);
        entities.add(ret);
        return ret;
    }

    public Sprite createSprite(String id) {
        Sprite ret = new Sprite(id);
        entities.add(ret);
        return ret;
    }

    public Entity getEntity(String id) {
        for (Entity e : entities) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    public void setSplashLogo(String id) {
        // TODO Auto-generated method stub
    }
}

class Entity {
    private String id;
    private int x;
    private int y;
    private double rotation;
    private double scale;
    boolean dirty = false;

    Entity parent = null;
    List<Entity> children = new ArrayList<>();

    Entity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

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

    public Entity setX(int x) {
        dirty = this.x != x;
        this.x = x;
        return this;
    }

    public Entity setY(int y) {
        dirty = this.y != y;
        this.y = y;
        return this;
    }

    public Entity setRotation(double rotation) {
        dirty = this.rotation != rotation;
        this.rotation = rotation;
        return this;
    }

    public Entity setScale(double scale) {
        dirty = this.scale != scale;
        this.scale = scale;
        return this;
    }

    public Entity addChild(Entity entity) {
        this.children.add(entity);
        return this;
    }

    public void removeChild(Entity entity) {
        // TODO
    }

    public void getChild(int index) {
        children.get(index);
    }

    public Entity getParent() {
        return parent;
    }

    public void setGlobalX(int x) {
        //
    }

    public void setGlobalY(int y) {
        //
    }
}

class Sprite extends Entity {
    int textureId;
    Point anchor;

    public Sprite(String id) {
        super(id);
    }

    public Sprite setPosition(int i, int j) {
        // TODO Auto-generated method stub
        return this;
    }

    public Sprite setTexture(String string, double d, double e) {
        // TODO Auto-generated method stub
        return this;
    }

    public Sprite setTexture(String string) {
        // TODO Auto-generated method stub
        return this;
    }

    public Sprite setColor(String string) {
        // TODO Auto-generated method stub
        return this;
    }
}

class ReadActionsException extends Exception {
    private static final long serialVersionUID = 5339510515204270837L;
}

class PlayerTimeoutException extends Exception {
    private static final long serialVersionUID = 8387976923557968095L;
}
