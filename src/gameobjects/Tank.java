package gameobjects;

/**
 *
 * @author aaron
 */
public class Tank extends Entity{
    
    protected int fuelLevel;
    protected int healthLevel;
    protected int remainingShots;
    
    protected int orientation;
    protected int speed;
    
    protected int owner;
    
    public Tank(int xCoordinate, int yCoordinate, int owner){
        super(xCoordinate, yCoordinate);
        this.owner = owner;
    }
    
    public void cw(){
        orientation -= 1;
        if(orientation < 0){
            orientation += 4;
        }
    }
    
    public void ccw(){
        orientation += 1;
        if(orientation > 3){
            orientation -= 4;
        }
    }
    
    public String playerOutput(){
        return super.playerOutput("TANK", fuelLevel, healthLevel, orientation, speed, remainingShots);
    }
}
