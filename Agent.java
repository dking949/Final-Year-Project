package FinalYearProject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.lang.*;
import java.lang.reflect.*;
import java.io.File;

public class Agent {

    Maze thisWorld;
    Policy policy;

//    // Learning types
//    public static final int Q_LEARNING = 1;
//
//    // Action selection types
//    public static final int E_GREEDY = 1;

    //int learningMethod;
    int actionSelection;

    double epsilon;

    double alpha;     //learning rate
    double gamma;     //discount factor
    double potentialFunc;
    double newPotentialFunc;
    double tao;       //The scaling factor used to scale potential function

    int[] dimSize;
    int[] state;
    int[] newstate;
    int action;
    double reward;
    int iterations = 500;
    int movesMade = 0;

    File results = new File("C:\\Users\\I320248\\Documents\\4th Year Docs\\Final Year Project\\results.txt");


    int epochs;

    boolean random = false;

    public Agent(Maze world) {
        // Getting the world from the invoking method.
        thisWorld = world;

        // Get dimensions of the world.
        dimSize = thisWorld.getDimension();

        // Creating new policy with dimensions to suit the world.
        policy = new Policy( dimSize );

        // Initializing the policy with the initial values defined by the world.
        policy.initValues( thisWorld.getInitValues() );

        // set default values
        epsilon = 0.1;

        alpha = 0.9;
        gamma = 1;
        tao = 1;

        System.out.println( "RLearner initialised" );

        results.getParentFile().mkdirs();

    }

    // execute one trial
    public void runTrial() throws IOException{
        //System.out.println( "Learning! \n");

        FileWriter fstream = new FileWriter(results);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Moves" + "\t" + "Total Reward" + "\n");
        for( int i = 0 ; i < iterations ; i++ ) {

            if (i%20==0) epsilon = epsilon/2;   //decay the exploration
            //double[] res = runWithPotential();
            double[] res2 = runWithNoPotential();
            //out.write(String.valueOf((int)res[0]) + "\t" + String.valueOf(res[1]) + "\n");
            out.write(/*String.valueOf((int)res[0]) + "\t" + String.valueOf(res[1]) + "\n" + */String.valueOf((int)res2[0]) + "\n");
        }

        out.close();
    }

    // execute one epoch
    public double[] runWithPotential() {

        System.out.println("New Iteration\n" +
                "************\n" +
                "************\n" +
                "*************\n" +
                "*************\n");

        // Reset state to start position defined by the world.
        state = new int[]{0, 0};
        potentialFunc = 0; newPotentialFunc = 0;
        movesMade = 0;
        //Q_LEARNING

        double this_Q;
        double max_Q;
        double new_Q;
        double [] toReturn = new double[2];
        double cumulativeReward = 0;
        int oldManhattenDist, newManhattenDist;
        double ShapingReward = 0;

        while( !thisWorld.endState(state) ) {
            System.out.println("----------------");
            System.out.println("Current State: " + state[0] + "," + state[1]);

            //reset ShapingReward
            ShapingReward = 0;

            action = selectAction( state );
            //if action chosen is not valid
            if(!thisWorld.validAction(state, action)){
                //Agent has hit a wall
                newstate = state;   //newstate = currentstate
            }
            else{
                newstate = thisWorld.getNextState( state, action );
            }

            reward = thisWorld.getReward(newstate);
            oldManhattenDist = thisWorld.calcManhatten(state);
            newManhattenDist = thisWorld.calcManhatten(newstate);

            if(newManhattenDist < oldManhattenDist){
                //Agent moved toward goal
                newPotentialFunc += 1;
            }
            else {
                //Agent moved away from goal
                newPotentialFunc -= 1;
            }


            this_Q = policy.getQValue( state, action );
            max_Q = policy.getMaxQValue( newstate );

            // Q LEARNING FORMULA HERE
            if(newManhattenDist>= 0){
                ShapingReward = gamma*newPotentialFunc - potentialFunc;
                new_Q = this_Q + alpha * ( reward + tao*ShapingReward + gamma * max_Q - this_Q);

            }
            //need else for when s = s0 (goal state)
            else{
                new_Q = this_Q + alpha * ( reward + gamma * max_Q - this_Q);
            }

            policy.setQValue( state, action, new_Q );

            // Set state to the new state.
            state = newstate;
            potentialFunc = newPotentialFunc;
            movesMade++;

            cumulativeReward += reward + ShapingReward;

        }
        toReturn[0] = movesMade;
        toReturn[1] = cumulativeReward;

        return toReturn;
    }

    public double[] runWithNoPotential(){

        //Reset state to start point
        state = new int[]{0, 0};
        movesMade = 0;
        //Q_LEARNING

        double this_Q;
        double max_Q;
        double new_Q;
        double [] toReturn = new double[2];
        double totalReward = 0;


        while( !thisWorld.endState(state) ) {

            action = selectAction( state );

            if(!thisWorld.validAction(state, action)){
                //Agent has hit a wall
                newstate = state;   //newstate = currentstate
            }
            else{
                newstate = thisWorld.getNextState( state, action );
            }

            reward = thisWorld.getReward(newstate);

            this_Q = policy.getQValue( state, action );
            max_Q = policy.getMaxQValue( newstate );

            // Calculate new Value for Q **FORMULA**
            new_Q = this_Q + alpha * ( reward + gamma * max_Q - this_Q );
            policy.setQValue( state, action, new_Q );

            // Set state to the new state.
            state = newstate;
            movesMade++;
            totalReward += reward;
        }
        toReturn[0] = movesMade;
        toReturn[1] = totalReward;


        return toReturn;
    }



    private int selectAction( int[] state ) {

        double[] qValues = policy.getQValuesAt( state );
        int selectedAction = -1;


        //E_GREEDY

        random = false;
        double maxQ = -Double.MAX_VALUE;
        int[] doubleValues = new int[qValues.length];
        int maxDV = 0;


        //Explore
        if ( Math.random() < epsilon ) {
            selectedAction = -1;
            System.out.println("Random Choice!");
            random = true;
        }
        else {

            for( int action = 0 ; action < qValues.length ; action++ ) {
                //System.out.println("Qvalue[" + action + "]: " + qValues[action]);
                if( qValues[action] > maxQ ) {
                    selectedAction = action;
                    maxQ = qValues[action];
                    maxDV = 0;
                    doubleValues[maxDV] = selectedAction;
                }
                else if( qValues[action] == maxQ ) {
                    maxDV++;
                    doubleValues[maxDV] = action;
                }
            }

            if( maxDV > 0 ) {
                int randomIndex = (int) ( Math.random() * ( maxDV + 1 ) );
                selectedAction = doubleValues[ randomIndex ];
            }
        }

        // Select random action if all qValues == 0 or exploring.
        if ( selectedAction == -1 ) {

            // System.out.println( "Exploring ..." );
            selectedAction = (int) (Math.random() * qValues.length);
        }

        // Choose new action if not valid.
        /*while( ! thisWorld.validAction(state, selectedAction) ) {

            selectedAction = (int) (Math.random() * qValues.length);
            // System.out.println( "Invalid action, new one:" + selectedAction);
        }*/

        //System.out.println("SelectedAction = " + selectedAction);
        return selectedAction;
    }

    /* private double getMaxQValue( int[] state, int action ) {

	double maxQ = 0;

	double[] qValues = policy.getQValuesAt( state );

	for( action = 0 ; action < qValues.length ; action++ ) {
	    if( qValues[action] > maxQ ) {
		maxQ = qValues[action];
	    }
	}
	return maxQ;
    }
    */


    public Policy getPolicy() {

        return policy;
    }

    public void setAlpha( double a ) {

        if( a >= 0 && a < 1 )
            alpha = a;
    }

    public void setGamma( double g ) {

        if( g > 0 && g < 1 )
            gamma = g;
    }

    public double getGamma() {

        return gamma;
    }

    public void setEpsilon( double e ) {

        if( e > 0 && e < 1 )
            epsilon = e;
    }

    public double getEpsilon() {

        return epsilon;
    }

    public void setEpisodes( int e ) {

        if( e > 0 )
            epochs = e;
    }

    public int getEpisodes() {

        return epochs;
    }

    public int getActionSelection() {

        return actionSelection;
    }


    //AK: let us clear the policy
    public Policy newPolicy() {
        policy = new Policy( dimSize );
        // Initializing the policy with the initial values defined by the world.
        policy.initValues( thisWorld.getInitValues() );
        return policy;
    }
}


