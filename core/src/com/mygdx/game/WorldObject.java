/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import Persistence.GameObject;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryonet.Client;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public class WorldObject implements Drawable {

    protected int x, y;
    protected int id;
    protected boolean toBeRemoved;
    protected String name;
    protected String description;
    protected Rectangle rectangle;
    protected String uId;
    protected static Client client;
    private float updateTimer;
    

    public WorldObject(int id, int x, int y) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.uId = UUID.randomUUID().toString();
        //initialize();
    }
    
    public void initialize() {
        if(GameObject.get(id)!=null) {
            this.rectangle = new Rectangle(GameObject.get(id).getBounds().x+x, //Set's the bounds to be like the one in the GameObject
                    GameObject.get(id).getBounds().y+y,
                    GameObject.get(id).getBounds().width,
                    GameObject.get(id).getBounds().height);
        }
        this.updateTimer = GameObject.get(id).getRespawnTime();
    }
    
    public void update(double deltaTime) {
        GameObject.get(id).update(this, deltaTime);
    }
    
    public void setId(int id) {
        this.id = id;
        WorldObject.client.sendTCP(this);
        initialize();
    }
    
    public WorldObject() {
        this.uId = UUID.randomUUID().toString();
    }

    public static void setClient(Client client) {
        WorldObject.client = client;
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

    @Override
    public ArrayList<Action> getActions() {
        return GameObject.get(id).getActions();
    }

    @Override
    public float getX() {
        return x;
    }
    
    @Override
    public String toString() {
        return GameObject.get(id).getName();
    }
    
    public String getUid() {
        return uId;
    }

    void generateUId() {
        this.uId = UUID.randomUUID().toString();
    }

    /**
     * @return the updateTimer
     */
    public float getUpdateTimer() {
        return updateTimer;
    }

    /**
     * @param updateTimer the updateTimer to set
     */
    public void setUpdateTimer(float updateTimer) {
        this.updateTimer = updateTimer;
    }

    @Override
    public boolean isFlaggedForRemoval() {
        return toBeRemoved;
    }
}
