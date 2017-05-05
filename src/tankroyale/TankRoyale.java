package tankroyale;

import gameobjects.Tank;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.Rand;

/**
 *
 * @author aaron
 */
public class TankRoyale{
    private ServerSocket listener;
    
    private final ArrayList<Player> players;
    private final int numberOfPlayers = 2;
    
    private final int boardWidth = 25;
    private final int boardHeight = 25;
    
    private final Pattern fasterPattern = Pattern.compile("FASTER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern slowerPattern = Pattern.compile("SLOWER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern cwPattern = Pattern.compile("CW(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern ccwPattern = Pattern.compile("CCW(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    
    public ServerSocket getListener() {
        return listener;
    }

    public Pattern getFasterPattern() {
        return fasterPattern;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public TankRoyale(ServerSocket listener, ArrayList<Player> players) {
        this.listener = listener;
        this.players = players;
    }

    public TankRoyale(ArrayList<Player> players) {
        this.players = players;
    }
    
    public TankRoyale(){
        players = new ArrayList<>();
        
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
            //gather the players
            for(int i=0;i<numberOfPlayers;i++){
                Player p = new Player(listener.accept(), this);
                players.add(p);
                
                //create a tank
                int x = Rand.randInt(1, boardWidth-1);
                int y = Rand.randInt(1, boardHeight-1);
                Tank tank = new Tank(x, y, p.getUserId());
                
                //give the player the tank
                p.setTank(tank);
            }
            
            //start the players' threads
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
            //wait for every player's input
            while(allPlayersActive() && !allPlayersHaveMessage()){
                synchronized(this){
                    try{
                        this.wait();
                    }
                    catch(InterruptedException e){
                    }
                }
            }
            
            //if we lost a player, then quit
            if(!allPlayersActive()){
                break;
            }
            
            //process the messages - reads and sets the action
            processPlayerMessages();
            
            //execute the messages - applys the action
            executePlayerMessages();
            
            //move the tanks
            moveTanks();
            
            //rotate the tanks
            rotateTanks();
        }
        
        System.out.println("End of Tank Royale!");
    }
    
    public void processPlayerMessages(){
        for(Player p : players){
            Tank tank = p.getTank();
            String message = p.getMessage();
            
            Matcher matchFaster = fasterPattern.matcher(message);
            Matcher matchSlower = slowerPattern.matcher(message);
            Matcher matchCW = cwPattern.matcher(message);
            Matcher matchCCW = ccwPattern.matcher(message);
            
            if(matchFaster.matches()){
                tank.faster();
                tank.setMessage(matchFaster.group("message"));
            }
            else if(matchSlower.matches()){
                tank.slower();
                tank.setMessage(matchSlower.group("message"));
            }
            else if(matchCW.matches()){
                tank.rotateCW();
                tank.setMessage(matchCW.group("message"));
            }
            else if(matchCCW.matches()){
                tank.rotateCCW();
                tank.setMessage(matchCCW.group("message"));
            }
        }
    }
    
    public void executePlayerMessages(){
        for(Player p : players){
            Tank tank = p.getTank();
            
            tank.applyAction();
        }
    }
    
    public void moveTanks(){
        for(int i=0;i<Tank.MAX_SPEED;i++){
            for(Player p : players){
                Tank tank = p.getTank();
                int newX = tank.getxCoordinate();
                int newY = tank.getyCoordinate();
                int o = tank.getOrientation();

                int[] move = Tank.DIRECTIONS[o];
                newX += move[0];
                newY += move[1];
                
                tank.setNewXCoordinate(newX);
                tank.setNewYCoordinate(newY);
                
                //TODO: just set new x and y, need to check collisions
                //and then eventually apply the new x and y
            }
        }
    }
    
    public void rotateTanks(){
        
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
