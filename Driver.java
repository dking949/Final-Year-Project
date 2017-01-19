package FinalYearProject;
import java.io.IOException;

/**
 * Created by I320248 on 22/12/2016.
 */
public class Driver {

    public static void main(String args[]) throws IOException{

        int[] dimensions = new int[]{10,10,4};
        int[] mazeExitPoint = new int[]{8,8};

        Maze rlMaze = new Maze(dimensions, mazeExitPoint);
        Agent rlAgent = new Agent(rlMaze);

        rlAgent.runTrial();
    }


}
