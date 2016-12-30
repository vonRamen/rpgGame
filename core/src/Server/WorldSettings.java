/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author kristian
 */
public class WorldSettings {

    private final String name;
    private final int worldSizeX;
    private final int worldSizeY;

    public WorldSettings(String name, int worldSizeX, int worldSizeY) {
        this.name = name;
        this.worldSizeX = worldSizeX;
        this.worldSizeY = worldSizeY;
    }

    public WorldSettings() {
        this.name = "none";
        this.worldSizeX = 0;
        this.worldSizeY = 0;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the worldSizeX
     */
    public int getWorldSizeX() {
        return worldSizeX;
    }

    /**
     * @return the worldSizeY
     */
    public int getWorldSizeY() {
        return worldSizeY;
    }

}
