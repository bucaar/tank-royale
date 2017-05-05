package tankroyale;

import gameobjects.Entity;
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
    
    private final int boardWidth = 10;
    private final int boardHeight = 10;
    
    private final Pattern fasterPattern = Pattern.compile("FASTER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern slowerPattern = Pattern.compile("SLOWER(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern cwPattern = Pattern.compile("CW(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern ccwPattern = Pattern.compile("CCW(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    
    public static int idCounter = 0;
    
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
                int o = Rand.randInt(0, 3);
                Tank tank = new Tank(x, y, o, p.getUserId());
                
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
            //TODO: output the game turn
            givePlayersGameState();
            
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
            
            //complete the rest of this turn
            doTurn();
        }
        
        System.out.println("End of Tank Royale!");
    }
    
    public String getGameState(Tank source){
        ArrayList<Entity> go = new ArrayList<>();
        //all the tanks
        for(Player player : players){
            Tank tank = player.getTank();
            go.add(tank);
        }
        //TODO add more 
        //eg. DUST, SHOT, etc.
        
        //the output
        StringBuilder out = new StringBuilder();
        
        //add to the output - only visible entities
        int sx = source.getxCoordinate();
        int sy = source.getyCoordinate();
        boolean first = true;
        for(int i=0;i<go.size();i++){
            Entity e = go.get(i);
            
            if(hasObstacle(sx, sy, e.getxCoordinate(), e.getyCoordinate())){
                continue;
            }
            
            String data = e.toPlayerOutput();
            if(!first){
                out.append("\n");
            }
            out.append(data);
            first = false;
        }
        return out.toString();
    }
    
    public boolean hasObstacle(int x1, int y1, int x2, int y2){
        //same point, no obstacle
        if(x1 == x2 && y1 == y2){
            return false;
        }
        
        //TODO: obstacle detection
        return false;
    }
    
    public void givePlayersGameState(){
        for(Player p : players){
            Tank t = p.getTank();
            String gameInfoForPlayer = getGameState(t);
            p.println(gameInfoForPlayer);
        }
    }
    
    public void doTurn(){
        //read in the players' messages and set the action
        for(Player p : players){
            Tank tank = p.getTank();
            String message = p.getMessage();
            
            System.out.println("Player " + p.getUserId() + ": " + message);
            
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
            //TODO: FIRE, REVERSE
        }
        
        //process other commands not handled by moving and turning
        //TODO: commands for FIRE, REVERSE, etc.
        
        //handle moving
        moveTanks();
        
        //handle rotating
        rotateTanks();
        
        //display map to console
        displayMap();
    }
    
    public void moveTanks(){
        for(int i=1;i<=Tank.MAX_SPEED;i++){
            ArrayList<Tank> tanks = new ArrayList<>();
            
            for(Player p : players){
                Tank tank = p.getTank();
                tanks.add(tank);
                
                int newX = tank.getxCoordinate();
                int newY = tank.getyCoordinate();
                int o = tank.getOrientation();
                
                tank.setNewXCoordinate(newX);
                tank.setNewYCoordinate(newY);
                
                if(i > tank.getSpeed()){
                    continue;
                }
                
                int[] move = Tank.DIRECTIONS[o];
                newX += move[0];
                newY += move[1];
                
                if(newX >= 0 && newX < boardWidth &&
                        newY >= 0 && newY < boardHeight){
                    tank.setNewXCoordinate(newX);
                    tank.setNewYCoordinate(newY);
                }
                else{
                    tank.setSpeed(0);
                }
            }
            
            //see if we have any collisions caused by turn
            ArrayList<Tank> collisions = new ArrayList<>();
            
            //check every tank with every other tank for the same new coordinates.
            for(int a=0;a<tanks.size();a++){
                for(int b=a+1;b<tanks.size();b++){
                    Tank t1 = tanks.get(a);
                    Tank t2 = tanks.get(b);
                    
                    if(t1.getNewXCoordinate() == t2.getNewXCoordinate() &&
                            t1.getNewYCoordinate() == t2.getNewYCoordinate()){
                        collisions.add(t1);
                        collisions.add(t2);
                    }
                }
            }
            
            //undo any colliding tanks
            for(Tank t : collisions){
                t.setNewXCoordinate(t.getxCoordinate());
                t.setNewYCoordinate(t.getyCoordinate());
                t.setSpeed(0);
            }
            
            //update tank's positions
            for(Tank t : tanks){
                t.setxCoordinate(t.getNewXCoordinate());
                t.setyCoordinate(t.getNewYCoordinate());
            }
        }
    }
    
    public void rotateTanks(){
        for(Player p : players){
            Tank tank = p.getTank();
            int o = tank.getOrientation();
            
            Tank.Action a = tank.getAction();
            switch(a){
                case CW:
                    o-=1;
                    break;
                case CCW:
                    o+=1;
                    break;
                default:
                    break;
            }
            
            if(o==-1){
                o = 3;
            }
            else if(o == 4){
                o = 0;
            }
            
            tank.setOrientation(o);
        }
    }
    
    public void displayMap(){
        char[][] map = new char[boardWidth][boardHeight];
        for(int x=0;x<boardWidth;x++){
            for(int y=0;y<boardHeight;y++){
                map[x][y] = '.';
            }
        }
        for(Player p : players){
            Tank t = p.getTank();
            map[t.getxCoordinate()][t.getyCoordinate()] = String.valueOf(p.getUserId()).charAt(0);
        }
        
        for(int y=0;y<boardHeight;y++){
            for(int x=0;x<boardWidth;x++){
                System.out.print(map[x][y]);
            }
            System.out.println("");
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
