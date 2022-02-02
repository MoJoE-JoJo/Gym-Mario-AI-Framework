package gym;

import engine.core.*;
import engine.helper.MarioActions;
import py4j.GatewayServer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MarioGym {
    static String level;
    static int gameSeconds;

    //Visualisation
    static JFrame window = null;
    static MarioRender render = null;
    static VolatileImage renderTarget = null;
    static Graphics backBuffer = null;
    static Graphics currentBuffer = null;

    //Game and character
    static MarioWorld world = null;
    static Py4JAgent agent = null;
    static MarioTimer agentTimer = null;

    //GameLoop
    static ArrayList<MarioEvent> gameEvents = null;
    static ArrayList<MarioAgentEvent> agentEvents = null;

    //Step related
    static int reward = 0;
    static float lastXLocation = 0;
    static long currentTime = 0;


    public static void main(String[] args) {
        MarioGym gym = new MarioGym();
        // app is now the gateway.entry_point
        GatewayServer server = new GatewayServer(gym);
        server.start();
        System.out.println("Started");
    }

    public static StepReturnType step(boolean left, boolean right, boolean down, boolean speed, boolean jump){
        agentInput(left, right, down, speed, jump);
        StepReturnType returnVal = new StepReturnType();
        returnVal.done = false;
        returnVal.reward = 10;
        returnVal.state = new int[16][16];
        returnVal.info = new ArrayList<String>();
        returnVal.info.add("Yolo swaggins");
        return returnVal;
    }

    public static void init(String levelFilePath, int timer, int marioState, boolean visual, int fps, float scale){
        if (visual) {
            window = new JFrame("Mario AI Framework");
            render = new MarioRender(scale);
            window.setContentPane(render);
            window.pack();
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            render.init();
            window.setVisible(true);
        }
        agent = new Py4JAgent();
        world = new MarioWorld(null);

        world.visuals = visual;
        level = getLevel(levelFilePath);
        world.initializeLevel(level, 1000 * gameSeconds);
        if (visual) {
            world.initializeVisuals(render.getGraphicsConfiguration());
        }
        world.mario.isLarge = marioState > 0;
        world.mario.isFire = marioState > 1;

        world.update(new boolean[MarioActions.numberOfActions()]);
        currentTime = System.currentTimeMillis();

        //initialize graphics
        renderTarget = null;
        backBuffer = null;
        currentBuffer = null;
        if (visual) {
            renderTarget = render.createVolatileImage(MarioGame.width, MarioGame.height);
            backBuffer = render.getGraphics();
            currentBuffer = renderTarget.getGraphics();
            render.addFocusListener(render); //TODO: Maybe not needed
        }

        agentTimer = new MarioTimer(MarioGame.maxTime);
        agent.initialize(new MarioForwardModel(world.clone()), agentTimer);

        ArrayList<MarioEvent> gameEvents = new ArrayList<>();
        ArrayList<MarioAgentEvent> agentEvents = new ArrayList<>();
    }

    public static void reset(){

    }

    public static void render(){
        System.out.println("Render called!");
    }

    public static void playGame(String levelFile, int time, int marioState, boolean visuals){
        MarioGame game = new MarioGame();
        printResults(game.runGame(agent, getLevel(levelFile), time, marioState, visuals));
    }

    public static void agentInput(boolean left, boolean right, boolean down, boolean speed, boolean jump){
        boolean[] actions = new boolean[]{left, right, down, speed, jump};
        agent.setActions(actions);
    }

    private static String getLevel(String filepath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
        }
        return content;
    }

    private static void printResults(MarioResult result) {
        System.out.println("****************************************************************");
        System.out.println("Game Status: " + result.getGameStatus().toString() +
                " Percentage Completion: " + result.getCompletionPercentage());
        System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() +
                " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
        System.out.println("Mario State: " + result.getMarioMode() +
                " (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
        System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() +
                " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() +
                " Falls: " + result.getKillsByFall() + ")");
        System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() +
                " Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
        System.out.println("****************************************************************");
    }
}
