package tankroyale;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author aaron
 */
public class TankRoyale{
    protected ServerSocket listener;
    
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
        
        System.out.println("Starting game...");
        
        Player player1 = null;
        Player player2 = null;
        
        try{
            player1 = new Player(listener.accept(), this);
            player2 = new Player(listener.accept(), this);
            
            player1.start();
            player2.start();
        }
        catch(IOException e){
            System.out.println("Could not connect to player.");
        }
        
        while(player1.active() && player2.active()){
            while(player1.messageSize() == 0 || player2.messageSize() == 0){
                synchronized(this){
                    try{
                        this.wait();
                    }
                    catch(InterruptedException e){
                    }
                }
            }
            
            String p1Message = player1.getMessage();
            String p2Message = player2.getMessage();
            
            if(p1Message == null){
                player1.deactivate();
            }
            if(p2Message == null){
                player2.deactivate();
            }
            
            System.out.println("Player 1: " + p1Message);
            System.out.println("Player 2: " + p2Message);
        }
        
        System.out.println("End of Tank Royale!");
        if(player1 != null){
            player1.deactivate();
        }
        
        if(player2 != null){
            player2.deactivate();
        }
    }
    
    public static void main(String[] args) throws IOException{
        TankRoyale game = new TankRoyale();
        game.start();
    }
    
}
