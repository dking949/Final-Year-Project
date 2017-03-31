package FinalYearProject;
/**
 * Created by I320248 on 22/12/2016.
 */
import java.lang.reflect.*;
import java.lang.*;

public class Policy {

    // Array qValuesTable;
    int[] dimSize;
    double[] qValues;       //double array to hold all q values of 1 given state
    private Object qValuesTable;
    int states, actions;

    Policy( int[] dimSize ) {
        this.dimSize = dimSize;

        // Create n-dimensional array with size given in dimSize array.
        qValuesTable = Array.newInstance( double.class, dimSize );

        // Get number of states.
        states = dimSize[0];
        for( int j = 1 ; j < dimSize.length - 1 ; j++)
            states *= dimSize[j];

        // Get number of actions.
        actions = dimSize[dimSize.length - 1];
    }


    public void initValues( double initValue ) {
        int i;
        int actualdim = 0;
        int state[] = new int[dimSize.length - 1];

        for( int j = 0 ; j < states ; j++ ) {

            qValues = (double[]) myQValues( state );

            for( i = 0 ; i < actions ; i++ ) {
                //set all Q Values = 0
                Array.setDouble( qValues, i, ( initValue ));
            }

            state = getNextState( state );
        }

    }
    private int[] getNextState( int[] state ) {
        int i;
        int actualdim = 0;

        state[actualdim]++;
        if( state[actualdim] >= dimSize[actualdim] ) {
            while( ( actualdim < dimSize.length - 1 ) && ( state[actualdim] >= dimSize[actualdim] ) ) {
                actualdim++;

                if( actualdim == dimSize.length - 1 )
                    return state;

                state[actualdim]++;
            }
            for( i = 0 ; i < actualdim ; i++ )
                state[i] = 0;
            actualdim = 0;
        }
        return state;
    }

    private double[] myQValues( int[] state ) {
        int i;
        Object curTable = qValuesTable;

        for( i = 0 ; i < dimSize.length - 2 ; i++ ) {
            //descend in each dimension
            curTable = Array.get( curTable, state[i] );
        }

        //at last dimension of Array get QValues.
        return (double[]) Array.get( curTable, state[i] );
    }

    public double[] getQValuesAt( int[] state ) {
        int i;
        Object curTable = qValuesTable;
        double[] returnValues;

        for( i = 0 ; i < dimSize.length - 2 ; i++ ) {
            //descend in each dimension
            curTable = Array.get( curTable, state[i] );
        }

        //at last dimension of Array get QValues.
        qValues = (double[]) Array.get( curTable, state[i] );
        returnValues = new double[ qValues.length ];
        System.arraycopy( qValues, 0, returnValues, 0, qValues.length );
        return returnValues;
    }


    public void setQValue( int[] state, int action, double newQValue ) {
        qValues = myQValues( state );
        Array.setDouble( qValues, action, newQValue );
    }

    public double getMaxQValue( int[] state ) {
        double maxQ = -Double.MAX_VALUE;
        qValues = myQValues( state );

        for( int action = 0 ; action < qValues.length ; action++ ) {
            if( qValues[action] > maxQ ) {
                maxQ = qValues[action];
            }
        }
        return maxQ;
    }

    public double getQValue( int[] state, int action ) {
        double qValue = 0;
        qValues = myQValues( state );
        qValue = qValues[action];
        return qValue;
    }
}
