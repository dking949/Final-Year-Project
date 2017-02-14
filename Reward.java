package FinalYearProject;

/**
 * Created by I320248 on 24/01/2017.
 */
public class Reward {

    private int xPos, yPos;
    private double rewardVal;

    public Reward(double[] reward){
        this.xPos = (int)reward[0];
        this.yPos = (int)reward[1];
        this.rewardVal = reward[2];
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public double getRewardVal() {
        return rewardVal;
    }

    public void setRewardVal(double rewardVal) {
        this.rewardVal = rewardVal;
    }
}
