package gym;

import engine.core.MarioWorld;
import py4j.GatewayServer;

import java.util.ArrayList;
import java.util.List;

public class MarioGym {
    MarioWorld world;

    int reward = 0;
    float lastXLocation = 0;


    public static void main(String[] args) {
        MarioGym gym = new MarioGym();
        // app is now the gateway.entry_point
        GatewayServer server = new GatewayServer(gym);
        server.start();
        System.out.println("Started");
    }

    public StepReturnType step(boolean left, boolean right, boolean down, boolean speed, boolean jump){
        System.out.println("Step called!");
        System.out.println("Left: " + left);
        System.out.println("Right: " + right);
        System.out.println("Down: " + down);
        System.out.println("Speed: " + speed);
        System.out.println("Jump: " + jump);
        StepReturnType returnVal = new StepReturnType();
        returnVal.done = false;
        returnVal.reward = 10;
        returnVal.state = new int[16][16];
        returnVal.info = new ArrayList<String>();
        returnVal.info.add("Yolo swaggins");
        return returnVal;
    }

    public void reset(){
        System.out.println(" called!");
    }

    public void render(){
        System.out.println("Render called!");
    }
}
