package gameobjects;

/**
 *
 * @author aaron
 */
public class Dust extends Entity{

    protected int turnsLeft;
    
    public Dust(int xCoordinate, int yCoordinate) {
        super(xCoordinate, yCoordinate);
        this.type = Type.DUST;
        
        this.turnsLeft = 3;
    }
    
    public void settle(){
        turnsLeft-=1;
    }
    
    public boolean isSettled(){
        return turnsLeft == 0;
    }
    
    public String toPlayerOutput(){
        return super.playerOutput(turnsLeft);
    }
    
    
}
