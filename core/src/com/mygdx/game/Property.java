/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class Property extends Town {

    Town town;

    public Property(GameWorld world, Town town, int tileX, int tileY, int tileW, int tileH) {
        super(world, tileX, tileY, tileW, tileH);
        this.town = town;
        this.setName("Building");
        this.setDescription("This is a building");
        this.properties = null;
    }

    @Override
    public Property addProperty(int tileX, int tileY, int tileW, int tileH) {
        return null;
    }
    
    public Property() {
        
    }

    @Override
    public void sendUpdate() {
    }

    void prepareUpdateStart() {
        this.world = null;
        this.bounds = null;
        this.town = null;
    }

    void endUpdate() {
        this.world = town.world;
        this.bounds = new Rectangle(tileX * 32, tileY * 32, tileW * 32, tileH * 32);
    }

    @Override
    public ArrayList<String> getUidsInList(ArrayList<String> uIds, int x, int y) {
        return uIds;
    }
    
    

    @Override
    public void initialize() {
    }

    
}
