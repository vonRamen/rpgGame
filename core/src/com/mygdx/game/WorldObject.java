/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameObject;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public class WorldObject implements Drawable {

    protected int x, y;
    protected int id;
    protected String name;
    protected String description;
    protected Rectangle rectangle;
    protected String uId;

    public WorldObject(int id, int x, int y) {
        this.x = x;
        this.y = y;
        this.uId = UUID.randomUUID().toString();
        initialize();
    }
    
    public void initialize() {
        if(GameObject.get(id)!=null) {
            this.rectangle = new Rectangle(GameObject.get(id).getBounds().x+x, //Set's the bounds to be like the one in the GameObject
                    GameObject.get(id).getBounds().y+y,
                    GameObject.get(id).getBounds().width,
                    GameObject.get(id).getBounds().height);
        }
    }
    
    public WorldObject() {
        
    }

    public static WorldObject create(int id, int x, int y) {
        WorldObject temp = new WorldObject(id, x, y);
        return temp;
    }

    @Override
    public void draw() {
        GameObject.get(id).draw(x, y);
    }

    @Override
    public float getY() {
        return y;
    }
}
