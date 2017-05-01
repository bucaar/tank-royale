package gameobjects;

/**
 *
 * @author aaron
 */
public class Shot extends Entity{

    protected int owner;
    protected int turnsLeft;
    
    public Shot(int xCoordinate, int yCoordinate) {
        super(xCoordinate, yCoordinate);
    }
    
    
}
