package gym;

import engine.core.*;
import engine.helper.Assets;
import engine.helper.GameStatus;
import engine.helper.MarioActions;
import py4j.GatewayServer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class MarioGym {
    String level;
    int gameSeconds;
    int marioState;
    //static boolean visual;

    //Visualisation
    JFrame window = null;
    MarioRender render = null;
    VolatileImage renderTarget = null;
    Graphics backBuffer = null;
    Graphics currentBuffer = null;

    //Game and character
    MarioWorld world = null;
    Py4JAgent agent = null;
    MarioTimer agentTimer = null;

    //GameLoop
    ArrayList<MarioEvent> gameEvents = null;
    ArrayList<MarioAgentEvent> agentEvents = null;

    //Step related
    float rewardPos = 0;
    float rewardPosClip = 15;
    int rewardTimePenalty = 0;
    int rewardDeathPenalty = 0;


    int winLooseReward = 200;
    int winMultiplier = 5;
    int sceneDetail = 0;
    int enemyDetail = 0;

    float totalReward = 0.0f;

    //boolean updateReward = false;
    int lastRewardMark = 0;
    float lastMarioX = 0;
    int gymID = 0;

    /*
    public static void main(String[] args) {
        MarioGym gym = new MarioGym();
        // app is now the gateway.entry_point
        GatewayServer server = new GatewayServer(gym);
        server.start();
        System.out.println("Gateway Started");
    }
    */

    private int[][][] getObservation(){
        return world.getMergedObservation3(world.mario.x, world.mario.y, 1, 1);
    }

    private boolean[] convertAgentInput(int number){
        boolean[] agentInput;
        switch (number){
            case 0:
                agentInput = new boolean[] {false, false, false, false, false};
                break;
            case 1:
                agentInput = new boolean[] {false, false, false, false, true};
                break;
            case 2:
                agentInput = new boolean[] {false, false, false, true, false};
                break;
            case 3:
                agentInput = new boolean[] {false, false, false, true, true};
                break;
            case 4:
                agentInput = new boolean[] {false, false, true, false, false};
                break;
            case 5:
                agentInput = new boolean[] {false, false, true, false, true};
                break;
            case 6:
                agentInput = new boolean[] {false, false, true, true, false};
                break;
            case 7:
                agentInput = new boolean[] {false, false, true, true, true};
                break;
            case 8:
                agentInput = new boolean[] {false, true, false, false, false};
                break;
            case 9:
                agentInput = new boolean[] {false, true, false, false, true};
                break;
            case 10:
                agentInput = new boolean[] {false, true, false, true, false};
                break;
            case 11:
                agentInput = new boolean[] {false, true, false, true, true};
                break;
            case 12:
                agentInput = new boolean[] {false, true, true, false, false};
                break;
            case 13:
                agentInput = new boolean[] {false, true, true, false, true};
                break;
            case 14:
                agentInput = new boolean[] {false, true, true, true, false};
                break;
            case 15:
                agentInput = new boolean[] {false, true, true, true, true};
                break;
            case 16:
                agentInput = new boolean[] {true, false, false, false, false};
                break;
            case 17:
                agentInput = new boolean[] {true, false, false, false, true};
                break;
            case 18:
                agentInput = new boolean[] {true, false, false, true, false};
                break;
            case 19:
                agentInput = new boolean[] {true, false, false, true, true};
                break;
            case 20:
                agentInput = new boolean[] {true, false, true, false, false};
                break;
            case 21:
                agentInput = new boolean[] {true, false, true, false, true};
                break;
            case 22:
                agentInput = new boolean[] {true, false, true, true, false};
                break;
            case 23:
                agentInput = new boolean[] {true, false, true, true, true};
                break;
            case 24:
                agentInput = new boolean[] {true, true, false, false, false};
                break;
            case 25:
                agentInput = new boolean[] {true, true, false, false, true};
                break;
            case 26:
                agentInput = new boolean[] {true, true, false, true, false};
                break;
            case 27:
                agentInput = new boolean[] {true, true, false, true, true};
                break;
            case 28:
                agentInput = new boolean[] {true, true, true, false, false};
                break;
            case 29:
                agentInput = new boolean[] {true, true, true, false, true};
                break;
            case 30:
                agentInput = new boolean[] {true, true, true, true, false};
                break;
            case 31:
                agentInput = new boolean[] {true, true, true, true, true};
                break;
            default:
                agentInput = new boolean[] {false, false, false, false, false};
        }
        return agentInput;
    }

    public StepReturnType step(int number){
        boolean[] input = convertAgentInput(number);
        agentInput(input[0],input[1],input[2],input[3],input[4]);
        gameUpdate();
        StepReturnType returnVal = new StepReturnType();
        //Done value
        if (world.gameStatus == GameStatus.RUNNING) returnVal.done = false;
        else returnVal.done = true;
        //Reward value
        returnVal.reward = rewardPos + rewardTimePenalty + rewardDeathPenalty;
        returnVal.reward = Math.max(-winLooseReward, Math.min(winLooseReward*winMultiplier, returnVal.reward));
        //State value
        returnVal.state = getObservation();
        //Info values
        returnVal.info = new HashMap<>();
        if(world.gameStatus == GameStatus.WIN) returnVal.info.put("Result", "Win");
        else if (world.gameStatus == GameStatus.LOSE) returnVal.info.put("Result", "Lose");
        returnVal.info.put("Yolo","Swaggins");
        totalReward += returnVal.reward;
        returnVal.info.put("ReturnScore", String.valueOf(totalReward));
        return returnVal;
    }

    public void init(int id,String paramLevel, String imageDirectory, int timer, int paramMarioState, boolean visual){
        gymID = id;
        level = paramLevel;
        gameSeconds = timer;
        marioState = paramMarioState;
        Assets.img = imageDirectory;
        sceneDetail = 1;
        enemyDetail = 1;

        if (visual) {
            window = new JFrame("Mario AI Framework");
            render = new MarioRender(2);
            window.setContentPane(render);
            window.pack();
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            render.init();
            window.setVisible(true);
        }

        reset(visual);
        System.out.println("Gym initialised");
    }

    public void gameUpdate(){
        if (world.gameStatus == GameStatus.RUNNING) {
            //System.out.println(currentTime);
            //get actions
            agentTimer = new MarioTimer(MarioGame.maxTime);
            boolean[] actions = agent.getActions(new MarioForwardModel(world.clone()), agentTimer);
            if (MarioGame.verbose) {
                if (agentTimer.getRemainingTime() < 0 && Math.abs(agentTimer.getRemainingTime()) > MarioGame.graceTime) {
                    System.out.println("The Agent is slowing down the game by: "
                            + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                }
            }
            //Reward info before update
            //int tickBeforeUpdate = world.currentTick;
            //float marioXBeforeUpdate = world.mario.x;


            // update world
            world.update(actions);
            gameEvents.addAll(world.lastFrameEvents);
            agentEvents.add(new MarioAgentEvent(actions, world.mario.x,
                    world.mario.y, (world.mario.isLarge ? 1 : 0) + (world.mario.isFire ? 1 : 0),
                    world.mario.onGround, world.currentTick));

            //Reward info after update
            if(world.currentTick - lastRewardMark == 30){
                lastRewardMark = world.currentTick;
                float newMarioX = world.mario.x;
                rewardPos = newMarioX - lastMarioX;
                rewardPos = Math.max(-rewardPosClip, Math.min(rewardPosClip, rewardPos));
                lastMarioX = newMarioX;
                rewardTimePenalty = -1;
            }
            else{
                rewardPos = 0.0f;
                rewardTimePenalty = 0;
            }
            //System.out.println("Tick:" + world.currentTick + " : Pos:" + rewardPos + " : Total:" + totalReward);
            int tickAfterUpdate = world.currentTick;
            float marioXAfterUpdate = world.mario.x;
            //Calculate reward components
            //rewardPos = marioXAfterUpdate - marioXBeforeUpdate;
            //rewardTimePenalty = tickBeforeUpdate - tickAfterUpdate;
            if(world.gameStatus == GameStatus.LOSE) rewardDeathPenalty = -winLooseReward;
            else if(world.gameStatus == GameStatus.WIN) rewardDeathPenalty = winLooseReward*winMultiplier;
            else rewardDeathPenalty = 0;
            //System.out.println("Postion reward: " + rewardPos + ", Time reward: " + rewardTimePenalty + ", Death reward: " + rewardDeathPenalty);

        }
    }

    public StepReturnType reset(boolean visual){
        boolean won = false;
        if(world != null)  won = (world.gameStatus == GameStatus.WIN);
        agent = new Py4JAgent();
        world = new MarioWorld(null);

        world.visuals = visual;
        world.initializeLevel(level, 1000 * gameSeconds);
        if (visual) {
            world.initializeVisuals(render.getGraphicsConfiguration());
        }
        world.mario.isLarge = marioState > 0;
        world.mario.isFire = marioState > 1;

        world.update(new boolean[MarioActions.numberOfActions()]);

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

        gameEvents = new ArrayList<>();
        agentEvents = new ArrayList<>();

        System.out.println("Gym Reset : ID=" + gymID + " : Win=" + (won ? "W" : "F")  + " : Return=" + totalReward);
        totalReward = 0;

        lastRewardMark = 0;
        lastMarioX = world.mario.x;

        StepReturnType returnVal = new StepReturnType();
        returnVal.done = false;
        returnVal.reward = 0;
        returnVal.state = getObservation();
        returnVal.info = new HashMap<>();
        return returnVal;
    }

    public void render(){
        render.renderWorld(world, renderTarget, backBuffer, currentBuffer);
    }

    public void agentInput(boolean left, boolean right, boolean down, boolean speed, boolean jump){
        boolean[] actions = new boolean[]{left, right, down, speed, jump};
        agent.setActions(actions);
    }

    public void setLevel(String levelParam){
        level = levelParam;
    }
}
