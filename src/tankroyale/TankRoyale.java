package tankroyale;

import gameobjects.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author aaron
 */
public class TankRoyale {
    
    protected InputStream in;
    protected OutputStream out;
    
    protected ArrayList<Entity> entities;
    protected ArrayList<Player> players;
    
    protected final int NUM_TANKS;
    protected final int MAX_FUEL;
    protected final int MAX_HEALTH;
    protected final int MAX_SHOTS;
    
    protected final int NUM_PLAYERS;
    
    public TankRoyale(InputStream in, OutputStream out){
        this.in = in;
        this.out = out;
        
        Properties prop = new Properties();
        InputStream propInput = null;
        
        try{
            propInput = new FileInputStream("config.properties");
            prop.load(propInput);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            if(propInput != null){
                try{ propInput.close(); }
                catch(IOException e){ e.printStackTrace();}
            }
        }
        
        NUM_TANKS = getPropInt(prop, "NUM_TANKS", 1);
        MAX_FUEL = getPropInt(prop, "MAX_FUEL", 100);
        MAX_HEALTH = getPropInt(prop, "MAX_HEALTH", 100);
        MAX_SHOTS = getPropInt(prop, "MAX_SHOTS", 25);
        NUM_PLAYERS = getPropInt(prop, "NUM_PLAYERS", 25);
    }
    
    public void setupGame(){
        entities = new ArrayList<>();
        players = new ArrayList<>();
    
        for(int p=0;p<NUM_PLAYERS;p++){
            Player player = new Player();
            
            for(int t=0;t<NUM_TANKS;t++){
                Tank tank = new Tank(0, 0);
            }
        }
    }
    
    public void playGame(){
        
    }
    
    private void print(String message){
        if(out == null){
            return;
        }
        
        try{
            out.write(message.getBytes());
            out.flush();
        }
        catch(IOException e){
            out = null;
        }
    }
    
    private void println(String message){
        print(message + "\n");
    }
    
    private int getPropInt(Properties p, String name, int def){
        try{
            return Integer.parseInt(p.getProperty(name));
        }
        catch(NumberFormatException e){
            return def;
        }
    }
    
    public static void main(String[] args) {
        TankRoyale game = new TankRoyale(System.in, System.out);
        game.setupGame();
        game.playGame();
    }
    
}
