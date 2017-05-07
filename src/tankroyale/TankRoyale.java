package tankroyale;

import gameobjects.Entity;
import gameobjects.Shot;
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
    private final ArrayList<Shot> shots;
    
    private final int numberOfPlayers = 2;
    
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 10;
    public static final int SHOT_DAMAGE = 25;
    public static final int FUEL_COST = 1;
    
    private final Pattern waitPattern = Pattern.compile("WAIT(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    
    private final Pattern forwardPattern = Pattern.compile("FORWARD(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern backwardPattern = Pattern.compile("BACKWARD(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern cwPattern = Pattern.compile("CW(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    private final Pattern ccwPattern = Pattern.compile("CCW(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    
    private final Pattern shootPattern = Pattern.compile("SHOOT\\s+(?<x>\\d{1,2})\\s+(?<y>\\d{1,2})(?:\\s+(?<message>.+))?", Pattern.CASE_INSENSITIVE);
    
    public static int idCounter = 0;
    
    public ServerSocket getListener() {
        return listener;
    }

    public int getBoardWidth() {
        return BOARD_WIDTH;
    }

    public int getBoardHeight() {
        return BOARD_HEIGHT;
    }
    
    public TankRoyale(){
        players = new ArrayList<>();
        shots = new ArrayList<>();
        
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
                int x = Rand.randInt(1, BOARD_WIDTH-1);
                int y = Rand.randInt(1, BOARD_HEIGHT-1);
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
            displayMap();
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
        for(Shot shot : shots){
            go.add(shot);
        }
        //TODO add more to the game output
        //eg. DUST, etc.
        
        //the output
        StringBuilder out = new StringBuilder();
        
        //get the source x and y - used for vision
        int sx = source.getxCoordinate();
        int sy = source.getyCoordinate();
        
        //add to the output - only visible entities
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
            //grab the gank
            Tank tank = p.getTank();
            //reset the action
            tank.nothing();
            
            String message = p.getMessage();
            
            System.out.println("Player " + p.getUserId() + ": " + message);
            
            Matcher matchWait = waitPattern.matcher(message);
            Matcher matchForward = forwardPattern.matcher(message);
            Matcher matchBackward = backwardPattern.matcher(message);
            Matcher matchCW = cwPattern.matcher(message);
            Matcher matchCCW = ccwPattern.matcher(message);
            Matcher matchShoot = shootPattern.matcher(message);
            
            if(matchWait.matches()){
                tank.nothing();
                tank.setMessage(matchWait.group("message"));
            }
            else if(matchForward.matches()){
                tank.forward();
                tank.setMessage(matchForward.group("message"));
            }
            else if(matchBackward.matches()){
                tank.backward();
                tank.setMessage(matchBackward.group("message"));
            }
            else if(matchCW.matches()){
                tank.rotateCW();
                tank.setMessage(matchCW.group("message"));
            }
            else if(matchCCW.matches()){
                tank.rotateCCW();
                tank.setMessage(matchCCW.group("message"));
            }
            else if(matchShoot.matches()){
                int x = Integer.parseInt(matchShoot.group("x"));
                int y = Integer.parseInt(matchShoot.group("y"));
                tank.shoot(x, y);
                tank.setMessage(matchShoot.group("message"));
            }
            else{
                //TODO: Invalid input message
            }
        }
        
        //handle shots fired - before moving/rotating
        shootTanks();
        
        //handle moving
        moveTanks();
        
        //handle rotating
        rotateTanks();
        
        //check for shots hit - after moving/rotating
        checkShots();
    }
    
    public void shootTanks(){
        //TODO: shoot the shot!
        
        //for every tank, if their action is SHOOT
        //then create a Shot at its location towards
        //its shotX, shotY location
        for(Player p : players){
            Tank tank = p.getTank();
            //we need ammo to shoot anyways
            if(tank.getShotsLeft() == 0){
                continue;
            }
            switch(tank.getAction()){
                case SHOOT:
                    int sx = tank.getxCoordinate();
                    int sy = tank.getyCoordinate();
                    int tx = tank.getShotX();
                    int ty = tank.getShotY();
                    
                    int time = 1 + (int)Math.round(distance(sx, sy, tx, ty) / 3.0);
                    Shot shot = new Shot(tank.getShotX(), tank.getShotY(), time, tank.getOwner());
                    shots.add(shot);
                    
                    //tank shot, reduce ammo
                    tank.reduceShots(1);
                    break;
                default:
                    //Nothing necessary
                    break;
            }
        }
    }
    
    public void checkShots(){
        //process all of the shots.
        for(int i=0;i<shots.size();i++){
            Shot shot = shots.get(i);
            //decrease shot's turnsLeft by one
            shot.decreaseTurnsLeft();
            if(shot.getTurnsLeft() == -1){
                shots.remove(i);
                i--;
            }
            else if(shot.getTurnsLeft() == 0){
                for(Player p : players){
                    Tank tank = p.getTank();
                    int tx = tank.getxCoordinate();
                    int ty = tank.getyCoordinate();
                    int sx = shot.getxCoordinate();
                    int sy = shot.getyCoordinate();
                    
                    if(tx == sx && ty == sy){
                        tank.reduceHealth(SHOT_DAMAGE);
                    }
                }
            }
        }
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
                
                //if we are out of fuel, set the speed to 0
                if(tank.getFuel() < FUEL_COST){
                    tank.setSpeed(0);
                }
                
                //if the tanks speed is too low for this loop, skip
                int s = tank.getSpeed();
                if(i > Math.abs(s)){
                    continue;
                }
                
                int[] move = Tank.DIRECTIONS[o];
                //if we are moving forward
                if(s > 0){
                    newX += move[0];
                    newY += move[1];
                }
                //if we are moving backward
                else if(s < 0){
                    newX -= move[0];
                    newY -= move[1];
                }
                
                //if we are still on the map, then update new coords
                if(newX >= 0 && newX < BOARD_WIDTH &&
                        newY >= 0 && newY < BOARD_HEIGHT){
                    tank.setNewXCoordinate(newX);
                    tank.setNewYCoordinate(newY);
                }
                //off map, no new coords!!
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
                    
                    //if the new coords are overlapping, then add the collision
                    if(t1.getNewXCoordinate() == t2.getNewXCoordinate() &&
                            t1.getNewYCoordinate() == t2.getNewYCoordinate()){
                        collisions.add(t1);
                        collisions.add(t2);
                    }
                }
            }
            
            //undo any colliding tanks
            for(Tank t : collisions){
                //undo new coordinate
                t.setNewXCoordinate(t.getxCoordinate());
                t.setNewYCoordinate(t.getyCoordinate());
                //reset the speed after collision
                t.setSpeed(0);
            }
            
            //update tank's positions
            for(Tank t : tanks){
                int ox = t.getxCoordinate();
                int oy = t.getyCoordinate();
                int nx = t.getNewXCoordinate();
                int ny = t.getNewYCoordinate();
                
                //if there has been a change, then reduce the fuel.
                if(ox != nx || oy != ny){
                    t.reduceFuel(FUEL_COST);
                    t.setxCoordinate(t.getNewXCoordinate());
                    t.setyCoordinate(t.getNewYCoordinate());
                }
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
                    //no rotation necessary
                    break;
            }
            
            //ensure orientation stays in [0-3]
            if(o == -1){
                o = 3;
            }
            else if(o == 4){
                o = 0;
            }
            
            tank.setOrientation(o);
        }
    }
    
    public void displayMap(){
        char[][] map = new char[BOARD_WIDTH][BOARD_HEIGHT];
        for(int x=0;x<BOARD_WIDTH;x++){
            for(int y=0;y<BOARD_HEIGHT;y++){
                map[x][y] = '.';
            }
        }
        for(Player p : players){
            Tank t = p.getTank();
            char symbol;
            if(t.getId() == 1){
                symbol = '\u2190';
            }
            else{
                symbol = '\u21E6';
            }
            switch(t.getOrientation()){
                case 3:
                    symbol++;
                case 0:
                    symbol++;
                case 1:
                    symbol++;
                case 2:
                    break;
            }
            map[t.getxCoordinate()][t.getyCoordinate()] = symbol;
        }
        
        for(int y=0;y<BOARD_HEIGHT;y++){
            for(int x=0;x<BOARD_WIDTH;x++){
                System.out.print(map[x][y]);
            }
            System.out.println("");
        }
    }
    
    public boolean allPlayersActive(){
        return players.stream().allMatch((p) -> p.active());
    }
    
    public boolean allPlayersHaveMessage(){
        return players.stream().noneMatch((p) -> (p.messageSize() == 0));
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
        players.stream().filter((p) -> p != null).forEach((p) -> p.deactivate());
    }
    
    public double distance(int x1, int y1, int x2, int y2){
        int x = x2 - x1;
        int y = y2 - y1;
        return Math.sqrt(x*x + y*y);
    }
    
    public static void main(String[] args) throws IOException{
        TankRoyale game = new TankRoyale();
        game.start();
        game.stop();
    }
    
}
