package gameobjects;

/**
 *
 * @author aaron
 */
public class Shot extends Entity{

    protected int owner;
    protected int turnsLeft;
    
    public Shot(int xCoordinate, int yCoordinate, int turnsLeft, int owner) {
        super(xCoordinate, yCoordinate);
        this.type = Type.SHOT;
        this.owner = owner;
        
        this.turnsLeft = turnsLeft;
    }
    
    public void decreaseTurnsLeft(){
        turnsLeft--;
    }
    
    public int getTurnsLeft(){
        return turnsLeft;
    }
    
    public String toPlayerOutput(){
        return super.playerOutput(turnsLeft, owner);
    }
}
