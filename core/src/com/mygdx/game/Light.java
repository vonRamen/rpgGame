/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Game;
import java.util.Random;

/**
 *
 * @author Kristian
 */
public class Light {

    /**
     * @return the x
     */
    public int getX() {
        return relativeX;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.relativeX = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return relativeY;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.relativeY = y;
    }
    
    public void setRelativePosition(int x, int y) {
        this.relativeX = x;
        this.relativeY = y;
        this.size = size;
    }

    private TextureRegion sprite;
    private int relativeX, relativeY;
    private float size;
    private float concreteSize;
    private float flickerIntensity;
    private Random flicker;

    public Light(int x, int y, float scale, boolean flicker) {
        this.setRelativePosition(x, y);
        this.flicker = flicker ? new Random() : null;
        this.flickerIntensity = 0.1f;
        this.sprite = Game.textureHandler.generateRegion("light.png", 0, 0, 32, 32);
        this.concreteSize = scale;
    }
    
    public void draw(float x, float y) {
        if(flicker != null) {
            size = concreteSize + ((flicker.nextFloat()*flickerIntensity)-flickerIntensity/2);
        }
        Game.batch.draw(this.sprite, relativeX+x, relativeY+y, this.sprite.getRegionWidth()/2, this.sprite.getRegionHeight()/2, this.sprite.getRegionWidth(), this.sprite.getRegionHeight(), size, size, 0);
    }

    /**
     * @return the size
     */
    public float getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(float size) {
        this.size = size;
    }
}
