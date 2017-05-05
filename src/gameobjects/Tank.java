package gameobjects;

/**
 *
 * @author aaron
 */
public class Tank extends Entity{
    
    protected int owner;
    
    protected int fuelLevel;
    protected int healthLevel;
    protected int remainingShots;
    
    protected int orientation;
    protected int speed;
    
    protected int newXCoordinate;
    protected int newYCoordinate;
    
    protected String message;
    protected Action action;
    
    public static final int MAX_SPEED = 2;
    public static final int[][] DIRECTIONS = new int[][]{
        { 1,  0},
        { 0, -1},
        {-1,  0},
        { 0,  1}
    };
    
    public static enum Action {
        FASTER, SLOWER, CW, CCW
    }
    
    public Tank(int xCoordinate, int yCoordinate, int owner){
        super(xCoordinate, yCoordinate);
        this.type = Type.TANK;
        this.owner = owner;
        
        this.fuelLevel = 100;
        this.healthLevel = 100;
        this.remainingShots = 25;
    }
    
    public void applyAction(){
        switch(action){
            case FASTER:
                if(speed < Tank.MAX_SPEED){
                    speed++;
                }
                break;
            case SLOWER:
                if(speed > 0){
                    speed--;
                }
                break;
            case CW:
                orientation = (orientation - 1) % 4;
                break;
            case CCW:
                orientation = (orientation + 1) % 4;
                break;
        }
    }
    
    public void rotateCW(){
        this.action = Action.CW;
    }
    
    public void rotateCCW(){
        this.action = Action.CCW;
    }
    
    public void faster(){
        this.action = Action.FASTER;
    }
    
    public void slower(){
        this.action = Action.SLOWER;
    }
    
    public String getMessage(){
        return message;
    }
    
    public void setMessage(String m){
        this.message = m;
    }
    
    public int getNewXCoordinate() {
        return newXCoordinate;
    }

    public void setNewXCoordinate(int newXCoordinate) {
        this.newXCoordinate = newXCoordinate;
    }

    public int getNewYCoordinate() {
        return newYCoordinate;
    }

    public void setNewYCoordinate(int newYCoordinate) {
        this.newYCoordinate = newYCoordinate;
    }
    
    public void setOrientation(int o){
        this.orientation = o;
    }
    
    public int getOrientation(){
        return orientation;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    
    
    public String toPlayerOutput(){
        return super.playerOutput(fuelLevel, healthLevel, orientation, speed, remainingShots);
    }
}
