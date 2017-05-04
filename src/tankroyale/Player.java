package tankroyale;

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
    
    private TankRoyale game;
    
    private Socket socket;
    private boolean active;
    
    private int id;
    private static int idCounter = 0;
    private BufferedReader input;
    private PrintWriter output;
    
    private final Queue<String> commands;
    
    public Player(Socket socket, TankRoyale game){
        this.game = game;
        
        this.id = idCounter++;
        this.socket = socket;
        
        commands = new Queue<>();
        
        try {
            active = true;
            
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            
            output.println("WELCOME");
        }
        catch (IOException e) {
            active = false;
            System.out.println("Player died: " + e);
        }
    }
    
    @Override
    public void run(){
        try{
            output.println("START");
            
            while(active){
                String in = input.readLine();
                if(in == null){
                    active = false;
                    synchronized(game){
                        game.notifyAll();
                    }
                    break;
                }
                
                in = in.trim();
                
                if(in.toLowerCase().startsWith("quit")){
                    active = false;
                }
                
                synchronized(commands){
                    commands.enqueue(in);
                }
                
                synchronized(game){
                    game.notify();
                }
            }
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
            }
        }
    }
    
    public int getUserId(){
        return id;
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
        return commands.dequeue();
    }
}
