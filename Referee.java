import java.util.ArrayList;
import java.util.List;

class PongPlayerActions implements PlayerActions {
    int desiredY;
}

class PongPlayerInputs implements PlayerInputs {
    int meY;
    int opponentY;
    int ballX;
    int ballY;
}

class PongPlayer extends Player<PongPlayerActions, PongPlayerInputs> {

    Sprite sprite;

    @Override
    PongPlayerActions serialize(List<String> actions) {
        PongPlayerActions command = new PongPlayerActions();
        command.desiredY = Integer.valueOf(actions.get(0));
        return command;
    }

    @Override
    List<String> deserialize(PongPlayerInputs inputs) {
        List<String> ret = new ArrayList<>();
        ret.add(String.valueOf(inputs.meY));
        ret.add(String.valueOf(inputs.opponentY));
        ret.add(inputs.ballX + " " + inputs.ballY);
        return ret;
    }
}

class PongReferee extends Referee<PongPlayer> {

    Sprite ball;
    
    @Override
    void load() {
        resourcesManager.loadConfig("https://www.codingame.com/game-factory/default.json");
        resourcesManager.registerAndLoadTexture("bar", "https://i.imgur.com/WJ475sa.jpg");
        resourcesManager.registerAndLoadTexture("wall", "https://i.imgur.com/PO788sa.jpg");
        resourcesManager.registerAndLoadTexture("logo", "https://i.imgur.com/44788sa.jpg");
        resourcesManager.loadTexture("ball");
        resourcesManager.loadFont("defaultFont");
        resourcesManager.registerAndLoadReplay("replay1", 548451);
        resourcesManager.registerAndLoadReplay("replay2", 548452);
    }

    @Override
    void run() {
        gameManager.setExpectedNbLineOutput(1);

        world.setSize(1000, 1000);
//        world.setGravity(0);

        ball = world.createSprite();
        ball.setPosition(world.getWidth() / 2, world.getHeight() / 2);
        ball.setTexture("ball", 0.5, 0.5);
        ball.setScale(1.0);
//        ball.enableBody(true);
//        ball.getBody().setSpeed(new Vector(1.0, 1.0));
//        ball.getBody().setMass(1.0);
//        ball.getBody().addShape(new BodyCircleShape(0, 0, 1.0));

        Sprite topWall = world.createSprite();
        topWall.setPosition(0, 0);
        topWall.setTexture("wall");
//        topWall.enableBody(true);        
//        topWall.getBody().addShape(new BodyRectShape(0, 0, world.getWidth(), 5));

        Sprite bottomWall = world.createSprite();
        bottomWall.setPosition(world.getHeight() - 5, 0);
        bottomWall.setTexture("wall");
//        bottomWall.enableBody(true);
//        bottomWall.getBody().addShape(new BodyRectShape(0, 0, world.getWidth(), 5));
        
        PongPlayer p0 = gameManager.getPlayer(0);
        p0.sprite = world.createSprite();
        p0.sprite.setPosition(10, world.getHeight() / 2);
//        p0.setTexture("bar", 0.5, 0.5);
//        p0.enableBody(true);        
//        p0.getBody().addShape(new BodyRectShape(-5, -20, 5, 20));
//        p0.setColor("0xff0000");

        PongPlayer p1 = gameManager.getPlayer(0);
        p1.sprite = world.createSprite();
        p1.sprite.setPosition(world.getWidth() - 10, world.getHeight() / 2);
//      p1.setTexture("bar", 0.5, 0.5);
//      p1.enableBody(true);        
//      p1.getBody().addShape(new BodyRectShape(-5, -20, 5, 20));
//      p1.setColor("0x00ff00");
    }

    @Override
    void update() {
        for (PongPlayer p : gameManager.getActivePlayers()) {
            PongPlayerActions actions;
            try {
                actions = p.readActions(50); // blocking (max timeout of 50 ms)
                int deltaMove = actions.desiredY - p.sprite.getY();
                deltaMove = clamp(deltaMove, -20, 20);
                p.sprite.setY(clamp(p.sprite.getY() + deltaMove, 20, world.getHeight() - 20));
            } catch (ReadActionsException | PlayerTimeoutException e) {
            }
        }

        world.update();

        if (ball.getX() < 0) {
            gameManager.getPlayer(0).die("");
        } else if (ball.getX() > world.getWidth()) {
            gameManager.getPlayer(1).die("");
        }

        for (PongPlayer p : gameManager.getActivePlayers()) {
            PongPlayerInputs inputs = new PongPlayerInputs();
            inputs.meY = p.sprite.getY();
            inputs.opponentY = gameManager.getPlayer((p.getIndex() + 1) % 2).sprite.getY();
            inputs.ballX = ball.getX();
            inputs.ballY = ball.getY();
            p.sendInputs(inputs);
        }
    }
}