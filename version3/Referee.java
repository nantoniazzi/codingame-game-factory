import java.util.ArrayList;
import java.util.List;

class PongPlayer extends Player {
    RectPhysicalEntity entity;
}

class PongReferee extends Referee<PongPlayer> {

    @Inject PhysicalWorld physicalWorld;
    CirclePhysicalEntity ball;

    @Override
    void onLoad() {
        resourcesManager.loadConfig("https://www.codingame.com/game-factory/default.json");
        resourcesManager.registerAndLoadTexture("bar", "https://i.imgur.com/WJ475sa.jpg");
        resourcesManager.registerAndLoadTexture("wall", "https://i.imgur.com/PO788sa.jpg");
        resourcesManager.registerAndLoadTexture("logo", "https://i.imgur.com/44788sa.jpg");
        resourcesManager.loadTexture("ball");
        resourcesManager.loadFont("defaultFont");
        resourcesManager.registerAndLoadReplay("replay1", 548451);
        resourcesManager.registerAndLoadReplay("replay2", 548452);
    }

    /**
     * Executed when the viewer is loaded
     */
    public void onInit() {
        world.setSize(1000, 1000);
        world.setSplashLogo("logo");
        replayManager.playLoop("replay1", "replay2");
    }

    /**
     * Override the ball collision behaviour
     * @param impact parameters
     */
    private boolean handleBallImpact(Impact impact) {
        // We override the impact behaviour if we collide with players
        if (impact.entityB.getId().startsWith("physical-player")) {
            ball.setSpeed(new Vector()); // TODO: compute the new impact direction
            return true;
        }
        
        return false;
    }
    
    /**
     * Executed when the game is played
     */
    @Override
    void onStart() {
        ball = physicalWorld.createCirlce("physical-ball")
                .setX(world.getWidth() / 2)
                .setY(world.getHeight() / 2)
                .setRadius(5.0)
                .onImpact(this::handleBallImpact)
                .addChild(world.createSprite("ball").setTexture("ball", 0.5, 0.5));

        physicalWorld.createRect("physical-top-wall")
                .setWidth(world.getWidth())
                .setHeight(2)
                .addChild(world.createSprite("top-wall").setTexture("wall"));

        physicalWorld.createRect("physical-top-wall")
                .setY(world.getHeight() - 2)
                .setWidth(world.getWidth())
                .setHeight(2)
                .addChild(world.createSprite("bottom-wall").setTexture("wall"));

        gameManager.getPlayer(0).entity = physicalWorld.createRect("physical-player-0")
                .setX(10)
                .setY(world.getHeight() / 2)
                .setAnchorX(2)
                .setAnchorY(10)
                .setWidth(4)
                .setHeight(20)
                .addChild(world.createSprite("p0").setColor("0xff0000").setTexture("bar", 0.5, 0.5));

        gameManager.getPlayer(1).entity = physicalWorld.createRect("physical-player-1")
                .setX(world.getWidth() - 10)
                .setY(world.getHeight() / 2)
                .setAnchorX(2)
                .setAnchorY(10)
                .setWidth(4)
                .setHeight(20)
                .addChild(world.createSprite("p0").setColor("0x00ff00").setTexture("bar", 0.5, 0.5));
    }

    /**
     * Game loop
     */
    @Override
    void onUpdate() {
        // Send new inputs with the updated positions
        for (PongPlayer p : gameManager.getActivePlayers()) {
            List<String> inputs = new ArrayList<>();
            inputs.add(String.valueOf(p.entity.getY()));
            inputs.add(String.valueOf(gameManager.getPlayer((p.getIndex() + 1) % 2).entity.getY()));
            inputs.add(String.format("%d %d", ball.getX(), ball.getY()));

            try {
                List<String> action = p.sendInputsAndWaitActions(inputs, 50, 1); // blocking (max timeout of 50 ms)
                int deltaMove = Integer.parseInt(action.get(0)) - p.entity.getY();
                deltaMove = clamp(deltaMove, -20, 20);
                int newPosY = p.entity.getY() + deltaMove;
                p.entity.setY(clamp(newPosY, 20, world.getHeight() - 20));
            } catch (ReadActionsException | PlayerTimeoutException e) {
            }
        }

        physicalWorld.update();

        if (ball.getX() < 0) {
            gameManager.getPlayer(0).die("give the reason explanation...");
        } else if (ball.getX() >= world.getWidth()) {
            gameManager.getPlayer(1).die("give the reason explanation...");
        }
    }

    /**
     * Game finished, ensure that we have assigned the correct score to each players
     */
    @Override
    void onEnd() {
        for (PongPlayer p : gameManager.getPlayers()) {
            p.setScore(p.isActive() ? 1 : 0);
        }
    }
}