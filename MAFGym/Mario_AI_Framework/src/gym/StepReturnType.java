package gym;

import java.util.HashMap;
import java.util.List;

public class StepReturnType {
    int[][] state;
    int reward;
    boolean done;
    HashMap<String,String> info;

    public int[][] getState(){
        return state;
    }

    public int getReward(){
        return reward;
    }

    public boolean getDone(){
        return done;
    }

    public HashMap<String,String> getInfo(){
        return info;
    }
}
