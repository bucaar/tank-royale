package gameobjects;

/**
 *
 * @author aaron
 */
public class Shot extends Entity{

    protected int owner;
    protected int turnsLeft;
    
    public Shot(int xCoordinate, int yCoordinate, int owner) {
        super(xCoordinate, yCoordinate);
        this.type = Type.SHOT;
        this.owner = owner;
        
        this.turnsLeft = 4;
    }
    
    
}
