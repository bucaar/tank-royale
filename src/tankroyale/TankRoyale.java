package tankroyale;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 * @author aaron
 */
public class TankRoyale{
    protected ServerSocket listener;
    protected final ArrayList<Player> players = new ArrayList<>();
    protected final int numberOfPlayers = 2;
    
    
    public TankRoyale(){
        try{
            listener = new ServerSocket(4949);
        }
        catch(IOException e){
            listener = null;
        }
    }
    
    public void start(){
        if(listener == null){
            System.out.println("No active listener. Ending game.");
            return;
        }
        
        System.out.println("Starting Tank Royale");
        
        try{
            for(int i=0;i<numberOfPlayers;i++){
                players.add( new Player(listener.accept(), this));
            }
            for(Player p : players){
                p.start();
            }
        }
        catch(IOException e){
            System.out.println("Could not connect to player.");
            deactivatePlayers();
            return;
        }
        
        //game loop
        while(allPlayersActive()){
            //wait for player's input
            while(allPlayersActive() && !allPlayersHaveMessage()){
                synchronized(this){
                    try{
                        this.wait();
                    }
                    catch(InterruptedException e){
                    }
                }
            }
            
            if(!allPlayersActive()){
                break;
            }
            
            processPlayerMessages();
        }
        
        System.out.println("End of Tank Royale!");
    }
    
    public void processPlayerMessages(){
        for(Player p : players){
            String message = p.getMessage();
            System.out.println("Player " + p.getUserId()+ ": " + message);
        }
    }
    
    public boolean allPlayersActive(){
        for(Player p : players){
            if(!p.active()){
                return false;
            }
        }
        return true;
    }
    
    public boolean allPlayersHaveMessage(){
        for(Player p : players){
            if(p.messageSize() == 0){
                return false;
            }
        }
        return true;
    }
    
    public void stop(){
        if(listener != null){
            try{
                listener.close();
            }
            catch(IOException e){
                System.out.println("Could not close socket.");
            }
        }
        deactivatePlayers();
    }
    
    public void deactivatePlayers(){
        for(Player p : players){
            if(p != null){
                p.deactivate();
            }
        }
    }
    
    public static void main(String[] args) throws IOException{
        TankRoyale game = new TankRoyale();
        game.start();
        game.stop();
    }
    
}
