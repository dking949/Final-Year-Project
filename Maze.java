package FinalYearProject;
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
    final int[] mazeDims = { 5, 5, 4 };

    public int[] getDimension() {

        return mazeDims;
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
        if( state[0] == 0 && action == W )
            return false;
            // East border
        else if( state[0] == 4 && action == E )
            return false;
            // North border
        else if( state[1] == 0 && action == N )
            return false;
            // South border
        else if( state[1] == 4 && action == S )
            return false;
        else return true;
    }

    public boolean endState( int[] state ) {

        // Absorbing state in south-east corner.
        //agent has found exit to maze
        if( state[0] == 3 && state[1] == 3 ) {
            return true;
        }
        else return false;
    }


    public double getReward( int[] state, int action ) {

        // Square in the west of the goal state.
        if( state[0] == 2 && state[1] == 3 ) {
            if ( action == E )
                return 1;
            else return 0;
        }
        // Square in the south of the goal state.
        if( state[0] == 4 && state[1] == 3 ) {
            if ( action == W )
                return 1;
            else return 0;
        }
        // Square in the south of the goal state.
        if( state[0] == 3 && state[1] == 2 ) {
            if ( action == S )
                return 1;
            else return 0;
        }
        // Square in the south of the goal state.
        if( state[0] == 3 && state[1] == 4 ) {
            if ( action == N )
                return 1;
            else return 0;
        }
        else return 0;
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
