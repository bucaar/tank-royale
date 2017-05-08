package exceptions;

import tankroyale.Player;

/**
 *
 * @author aaron
 */
public class GameOverException extends Exception{
    public static enum Reason{
        SERVER, INVALID_COMMAND, OUT_OF_HEALTH, STALEMATE, NO_MOVES
    }
    
    private Reason reason;
    
    public GameOverException(Reason reason){
        super(reason.name());
        this.reason = reason;
    }
    
    public Reason getReason(){
        return reason;
    }
    
    public void setReason(Reason reason){
        this.reason = reason;
    }
    
}
