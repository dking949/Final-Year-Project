package FinalYearProject;
import java.io.IOException;

/**
 * Created by I320248 on 22/12/2016.
 */
public class Driver {

    public static void main(String args[]) throws IOException{

        Maze rlMaze = new Maze();
        Agent rlAgent = new Agent(rlMaze);

        rlAgent.runTrial();
    }


}
