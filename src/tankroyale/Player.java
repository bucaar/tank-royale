package tankroyale;

import gameobjects.Tank;
import java.util.ArrayList;

/**
 *
 * @author aaron
 */
public class Player {
    protected String name;
    
    protected ArrayList<Tank> tanks;
    
    public Player(String name){
        this.name = name;
    }
    
    public void addTank(Tank t){
        tanks.add(t);
    }
}
