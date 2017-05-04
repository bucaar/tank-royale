package gameobjects;

/**
 *
 * @author aaron
 */
public abstract class Entity {
    
    protected int id;
    protected int xCoordinate;
    protected int yCoordinate;
    
    private static int idCounter = 0;
    
    public Entity(int xCoordinate, int yCoordinate){
        this.id = idCounter++;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
    
    protected String playerOutput(String type, int... a){
        StringBuilder args = new StringBuilder();
        for(int i=0;i<a.length;i++){
            args.append(" ");
            args.append(a[i]);
        }
        return type + " " + id + " " + xCoordinate + " " + yCoordinate + args;
    }
}
