/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 *
 * @author Kristian
 */
public class Conversion {
    
    
    public static int toTileCoordinate(float numb) {
        return ((int) numb/32);
    }
    
    public static int toChunkCoordinate(float numb) {
        return toTileCoordinate(numb) / 32;
    }
}
