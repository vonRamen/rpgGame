/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

/**
 * This class makes is possible to have a GameObject consisting of multiple
 * sprites relative to each other.
 *
 * @author kristian
 */
public class SpriteRelative {

    private int textureId;
    private int xRelative;
    private int yRelative;

    public SpriteRelative(int textureId, int xRelative, int yRelative) {
        this.textureId = textureId;
        this.xRelative = xRelative;
        this.yRelative = yRelative;
    }
    
    public SpriteRelative() {
        
    }

    /**
     * @return the textureId
     */
    public int getTextureId() {
        return textureId;
    }

    /**
     * @return the xRelative
     */
    public int getxRelative() {
        return xRelative;
    }

    /**
     * @return the yRelative
     */
    public int getyRelative() {
        return yRelative;
    }
}
