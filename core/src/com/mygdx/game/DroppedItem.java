/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameItem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class DroppedItem implements Drawable {

    protected int x, y;
    protected int id;
    protected String name;
    protected String description;
    protected GameItem gameItem;

    public DroppedItem(int id, int x, int y) {
        if (GameItem.get(id) == null) {
            System.out.println("Warning! GameItem doesn't exist!");
            gameItem = GameItem.get(0);
            return;
        }
        gameItem = GameItem.get(id);
    }

    @Override
    public void draw() {
        gameItem.draw(x, y);
    }
    
    public float getY() {
        return y;
    }

}
