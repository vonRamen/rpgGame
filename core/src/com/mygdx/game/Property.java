/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

/**
 *
 * @author kristian
 */
class Property extends Town {

    Town town;

    public Property(GameWorld world, Town town, int tileX, int tileY, int tileW, int tileH) {
        super(world, tileX, tileY, tileW, tileH);
        this.town = town;
        this.setName("Building");
        this.setDescription("This is a building");
    }

    @Override
    public void setWorld(GameWorld world) {
    }

}
