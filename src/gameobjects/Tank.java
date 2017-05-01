package gameobjects;

/**
 *
 * @author aaron
 */
public class Tank extends Entity implements Moveable{
    
    protected int fuelLevel;
    protected int healthLevel;
    protected int remainingShots;
    
    protected int orientation;
    protected int speed;
    
    
    public Tank(int xCoordinate, int yCoordinate){
        super(xCoordinate, yCoordinate);
    }
    
    @Override
    public void move(){
        
    }
}
