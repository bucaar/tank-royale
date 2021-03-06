package gameobjects;

import tankroyale.TankRoyale;

/**
 *
 * @author aaron
 */
public abstract class Entity {
    
    protected int id;
    protected int xCoordinate;
    protected int yCoordinate;
    
    protected Type type;
    
    public static enum Type{
        TANK, SHOT, DUST
    }
    
    public Entity(int xCoordinate, int yCoordinate){
        this.id = TankRoyale.idCounter++;
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
    
    public abstract String toPlayerOutput();
    
    protected String playerOutput(int... a){
        StringBuilder args = new StringBuilder();
        for(int i=0;i<a.length;i++){
            args.append(" ").append(a[i]);
        }
        return type.name() + " " + id + " " + xCoordinate + " " + yCoordinate + args;
    }
}
