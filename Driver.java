package FinalYearProject;
import java.io.IOException;

/**
 * Created by I320248 on 22/12/2016.
 */
public class Driver {

    public static void main(String args[]) throws IOException{

        int[] dimensions = new int[]{5,5,4};
        int[] mazeExitPoint = new int[]{4,4};
        //double[] reward1 = new double[]{5,5,0.05};       //xPos,yPos,rewardVal

        Maze rlMaze = new Maze(dimensions, mazeExitPoint);
        Agent rlAgent = new Agent(rlMaze);

       //rlMaze.addReward(reward1);

        rlAgent.runTrial();
    }


}
