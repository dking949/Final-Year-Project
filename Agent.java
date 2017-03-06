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

    double epsilon;
    double alpha;               //learning rate
    double gamma;               //discount factor
    double potentialFunc;       //potential value for moving to current state
    double newPotentialFunc;    //potential value for moving to new state
    double tau;                 //The scaling factor used to scale potential function

    int[] dimSize;
    int[] state;
    int[] newstate;
    int action;
    double reward;
    int iterations = 1000;
    int movesMade = 0;

    File results = new File("C:\\Users\\I320248\\Documents\\4th Year Docs\\Final Year Project\\results.txt");
    File results2 = new File("C:\\Users\\I320248\\Documents\\4th Year Docs\\Final Year Project\\results2.txt");


    int epochs;

    boolean random = false;

    public Agent(Maze world) {
        thisWorld = world;
        dimSize = thisWorld.getDimension();
        policy = new Policy( dimSize );

        // Initializing the policy with the initial values defined by the world.
        policy.initValues( thisWorld.getInitValues() );

        // set default values
        epsilon = 0.1;
        alpha = 0.9;
        gamma = 1;
        tau = 1;

        System.out.println( "RLearner initialised" );

        results.getParentFile().mkdirs();
        results2.getParentFile().mkdirs();

    }

    // execute one trial
    public void runTrial() throws IOException{
        System.out.println( "Learning! \n");

        FileWriter fstream = new FileWriter(results);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("EpisodeNum" + "\t" + "Moves" + "\t" + "Total Reward" + "\n");

        FileWriter fstream2 = new FileWriter(results2);
        BufferedWriter out2 = new BufferedWriter(fstream2);
        out2.write("EpisodeNum" + "\t" + "Moves" + "\t" + "Total Reward" + "\n");

        for( int i = 1 ; i <= iterations ; i++ ) {

            //if (i%100==0) epsilon = epsilon/2;   //decay the exploration
            double[] res = runWithPotential();
            out.write(String.valueOf(i) + "\t" + String.valueOf((int)res[0]) + "\t" + String.valueOf(res[1]) + "\n");

        }
        newPolicy();
        setEpsilon(0.1);
        for(int j = 1; j <= iterations; j++){
            //Reset The Q values for 2nd trial

            //if (j%100==0) epsilon = epsilon/2;   //decay the exploration
            double[] res2 = runWithoutPotential();
            out2.write(String.valueOf(j) + "\t" + String.valueOf((int)res2[0]) + "\t" + String.valueOf(res2[1]) + "\n");
        }

        out.close();
        out2.close();
    }

    public double[] runWithPotential() {

        // Reset state to start position
        state = new int[]{0, 0};
        potentialFunc = 0; newPotentialFunc = 0;
        movesMade = 0;

        double this_Q;
        double max_Q;
        double new_Q;
        double [] toReturn = new double[2];
        double cumulativeReward = 0;
        int oldManhattenDist, newManhattenDist;
        double ShapingReward;

        while( !thisWorld.endState(state) ) {

            //reset ShapingReward
            ShapingReward = 0;

            action = selectAction( state );
            //if action chosen is not valid
            if(!thisWorld.validAction(state, action)){
                //Agent has hit a wall
                newstate = state;
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
                new_Q = this_Q + alpha * ( reward + tau*ShapingReward + gamma * max_Q - this_Q);

            }
            //need else for when s' = s0 (goal state)
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

    public double[] runWithoutPotential(){

        //Reset state to start point
        state = new int[]{0, 0};
        movesMade = 0;

        double this_Q;
        double max_Q;
        double new_Q;
        double [] toReturn = new double[2];
        double totalReward = 0;


        while( !thisWorld.endState(state) ) {
            action = selectAction( state );

            if(!thisWorld.validAction(state, action)){
                newstate = state;
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
        double maxQ = -Double.MAX_VALUE;    //initially set max = lowest possible double value
        int[] doubleValues = new int[qValues.length];
        int maxDV = 0;

        //Explore
        if ( Math.random() < epsilon ) {
            selectedAction = -1;
            random = true;
        }
        else {
            for( int action = 0 ; action < qValues.length ; action++ ) {
                if( qValues[action] > maxQ ) {
                    selectedAction = action;
                    maxQ = qValues[action];
                    maxDV = 0;
                    doubleValues[maxDV] = selectedAction;
                }
                //more than one Q values are the same
                else if( qValues[action] == maxQ ) {
                    maxDV++;
                    doubleValues[maxDV] = action;
                }
            }
            //Choose randomly out of the equal Q values
            if( maxDV > 0 ) {
                int randomIndex = (int) ( Math.random() * ( maxDV + 1 ) );
                selectedAction = doubleValues[randomIndex];
            }
        }

        if ( selectedAction == -1 ) {
            selectedAction = (int) (Math.random() * qValues.length);
        }
        return selectedAction;
    }

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

    //clear the policy
    public void newPolicy() {
        this.policy = new Policy( dimSize );
        this.policy.initValues( thisWorld.getInitValues() );
    }
}


