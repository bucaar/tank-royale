package utils;

import java.util.Random;

/**
 *
 * @author aaron
 */
public class Rand {
    private static Random rand;
    
    public static int randInt(int a, int b){
        if(a > b){
            throw new IllegalArgumentException(a + " cannot be larger than " + b + ".");
        }
        if(rand == null){
            rand = new Random();
        }
        return rand.nextInt(b - a + 1) + a;
    }
}
