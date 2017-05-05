package tankroyale;

import gameobjects.Tank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import utils.Queue;

/**
 *
 * @author aaron
 */
public class Player extends Thread{
    
    private final TankRoyale game;
    private Tank tank;
    
    private Socket socket;
    private boolean active;
    
    private int id;
    private static int idCounter = 0;
    private BufferedReader input;
    private PrintWriter output;
    
    private final Queue<String> commands;
    
    public Player(Socket socket, TankRoyale game){
        this.game = game;
        this.socket = socket;
        this.id = idCounter++;
        
        commands = new Queue<>();
        active = true;
        
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            
            this.println("HELLO");
        }
        catch (IOException e) {
            active = false;
            System.out.println("Player died: " + e);
        }
    }
    
    @Override
    public void run(){
        try{
            //display to the player the game has started
            this.println("START");
            
            while(active){
                //read from the player
                String in = input.readLine();
                
                //if we didnt get anything, disconnect the player.
                if(in == null){
                    this.deactivate();
                    synchronized(game){
                        //notify the game of a change
                        game.notify();
                    }
                    break;
                }
                
                //trim the input
                in = in.trim();
                
                //if they typed quit, then stop them
                if(in.toLowerCase().startsWith("quit")){
                    this.deactivate();
                }
                
                //otherwise enqueue their message
                synchronized(commands){
                    commands.enqueue(in);
                }
                
                //wake the game for processing
                synchronized(game){
                    game.notify();
                }
            }
            
            this.println("STOP");
        }
        catch(IOException e){
            System.out.println("Player has died: " + e);
        }
        finally{
            try {
                if (socket != null) {
                    socket.close();
                    active = false;
                }
            } catch (IOException e) {
                System.out.println("Could not close the socket?");
            }
        }
    }
    
    public int getUserId(){
        return id;
    }
    
    public Tank getTank(){
        return tank;
    }
    
    public void setTank(Tank t){
        this.tank = t;
    }
    
    public void println(String message){
        output.println(message);
    }
    
    public boolean active(){
        return active;
    }
    
    public void deactivate(){
        active = false;
    }
    
    public int messageSize(){
        synchronized(commands){
            return commands.size();
        }
    }
    
    public String getMessage(){
        synchronized(commands){
            return commands.dequeue();
        }
    }
}
