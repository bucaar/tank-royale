package gameobjects;

import tankroyale.TankRoyale;

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
    
    protected int shootX;
    protected int shootY;
    
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
        FORWARD, BACKWARD, CW, CCW, SHOOT, NOTHING
    }
    
    public Tank(int xCoordinate, int yCoordinate, int orientation, int owner){
        super(xCoordinate, yCoordinate);
        this.type = Type.TANK;
        this.owner = owner;
        
        this.fuelLevel = 100;
        this.healthLevel = 100;
        this.remainingShots = 25;
        
        this.speed = 0;
        this.orientation = orientation;
        
        this.message = "";
        this.action = Action.NOTHING;
    }
    
    public void rotateCW(){
        this.action = Action.CW;
    }
    
    public void rotateCCW(){
        this.action = Action.CCW;
    }
    
    public void nothing(){
        this.action = Action.NOTHING;
    }
    
    public void forward(){
        this.action = Action.FORWARD;
        if(this.speed < MAX_SPEED){
            speed++;
        }
    }
    
    public void backward(){
        this.action = Action.BACKWARD;
        if(this.speed > -MAX_SPEED){
            speed--;
        }
    }
    
    public void shoot(int x, int y){
        //TODO: distance of shot?
        
        //if x, y is in the map, set the action
        if(x >= 0 && x < TankRoyale.BOARD_WIDTH &&
                y >= 0 && y < TankRoyale.BOARD_HEIGHT){
            this.action = Action.SHOOT;
            this.shootX = x;
            this.shootY = y;
        }
    }
    
    public void reduceHealth(int h){
        this.healthLevel -= h;
    }
    
    public void reduceFuel(int f){
        this.fuelLevel -= f;
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

    public int getShotX() {
        return shootX;
    }

    public int getShotY() {
        return shootY;
    }
    
    public int getHealth(){
        return healthLevel;
    }
    
    public int getFuel(){
        return fuelLevel;
    }
    
    public Action getAction(){
        return action;
    }
    
    public int getOwner(){
        return owner;
    }
    
    public int getShotsLeft(){
        return remainingShots;
    }
    
    public void reduceShots(int s){
        remainingShots -= s;
    }
    
    public String toPlayerOutput(){
        return super.playerOutput(fuelLevel, healthLevel, orientation, speed, remainingShots, owner);
    }
}