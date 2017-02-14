package FinalYearProject;

import java.util.ArrayList;

/**
 * Created by I320248 on 22/12/2016.
 */
public class Maze{

    //Actions
    final int N = 0;
    final int E = 1;
    final int S = 2;
    final int W = 3;

    // dimension: { x, y, actions }
    int[] mazeDims = new int[3];
    int[] mazeExit = new int[2];
    int northBorder = 0;
    int eastBorder;
    int southBorder;
    int westBorder = 0;
    int manDist;

    ArrayList<Reward> rewardArray = new ArrayList<Reward>();      //List of Reward flags in maze




    public Maze(int[] dims, int[] exit){
        this.mazeDims = dims;
        this.mazeExit = exit;
        this.eastBorder = dims[0]-1;
        this.southBorder = dims[1]-1;
    }

    public int[] getDimension() {

        return mazeDims;
    }

    //Calculates Manhatten Distance from Goal State to agents current State
    public int calcManhatten(int[] curState){
        manDist = Math.abs(mazeExit[0] - curState[0]) + Math.abs(mazeExit[1] - curState[1]);
        return manDist;
    }

    public void addReward(double[] reward){
        Reward newRewardFlag = new Reward(reward);
        rewardArray.add(newRewardFlag);
    }

    //get the next state given current state and action taken
    public int[] getNextState( int[] state, int action ) {

        int[] newstate = new int[state.length] ;
        System.arraycopy( state, 0, newstate, 0, state.length);

        // N-W corner in coordinates 0,0
        if( action == N )
            newstate[1]--;
        else if( action == E )
            newstate[0]++;
        else if( action == S )
            newstate[1]++;
        else if( action == W )
            newstate[0]--;
        return newstate;
    }

    public boolean validAction( int[] state, int action ) {

        // West border
        if( state[0] == westBorder && action == W )
            return false;
            // East border
        else if( state[0] == eastBorder && action == E )
            return false;
            // North border
        else if( state[1] == northBorder && action == N )
            return false;
            // South border
        else if( state[1] == southBorder && action == S )
            return false;
        else return true;
    }

    public boolean endState( int[] state ) {
        //agent has found exit to maze
        if( state[0] == mazeExit[0] && state[1] == mazeExit[1] ) {
            return true;
        }
        else return false;
    }


    public double getReward( int[] newstate) {

        if(newstate[0] == mazeExit[0] && newstate[1] == mazeExit[1]){
            return 100;
        }
        else if(rewardArray.size()>0){
            //check if new state contains any reward
            for(Reward r:rewardArray){
                if(newstate[0] == r.getxPos() && newstate[1] == r.getyPos()){
                    break;
                }
                return r.getRewardVal();
            }
            return 0;
        }
        else return 0;

        // Square in the west of the goal state.
        /*if( state[0] == mazeExit[0]-1 && state[1] == mazeExit[1] ) {
            if ( action == E )
                return 1;
            else return 0;
        }
        // Square in the south of the goal state.
        if( state[0] == mazeExit[0]+1 && state[1] == mazeExit[1] ) {
            if ( action == W )
                return 1;
            else return 0;
        }
        // Square in the south of the goal state.
        if( state[0] == mazeExit[0] && state[1] == mazeExit[1]-1 ) {
            if ( action == S )
                return 1;
            else return 0;
        }
        // Square in the south of the goal state.
        if( state[0] == mazeExit[0] && state[1] == mazeExit[1]+1) {
            if ( action == N )
                return 1;
            else return 0;
        }
        else return 0;*/
    }


    public void resetState( int[] state ) {

        // reposition to 0,0
        for( int j = 0 ; j < 2 ; j++ )
            state[j] = (int) ( Math.random() * mazeDims[j] );
    }

    public double getInitValues() {
        return 0;
    }
}
