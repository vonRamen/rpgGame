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
import java.util.Iterator;
import java.util.Map;
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
    private String uIdOfTown;
    protected static Client client;
    private float updateTimer;
    private GameWorld world;

    public WorldObject(GameWorld world, int id, int x, int y) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.uId = UUID.randomUUID().toString();
        this.world = world;
        //initialize();
    }

    public void initialize() {
        if (id != -1) {
            if (GameObject.get(id) != null) {
                this.rectangle = new Rectangle(GameObject.get(id).getBounds().x + x, //Set's the bounds to be like the one in the GameObject
                        GameObject.get(id).getBounds().y + y,
                        GameObject.get(id).getBounds().width,
                        GameObject.get(id).getBounds().height);
            }
            this.updateTimer = GameObject.get(id).getRespawnTime();
        } else {
            this.toBeRemoved = true;
        }
    }

    public void update(double deltaTime) {
        if (id != -1) {
            GameObject.get(id).update(this, deltaTime);
        }
    }

    public void setId(int id) {
        this.id = id;
        GameWorld worldHold = this.world;
        this.world = null;
        WorldObject.client.sendTCP(this);
        this.world = worldHold;
        initialize();
    }

    public WorldObject() {
        this.uId = UUID.randomUUID().toString();
    }

    public static void setClient(Client client) {
        WorldObject.client = client;
    }

    public static WorldObject create(GameWorld world, int id, int x, int y) {
        WorldObject temp = new WorldObject(world, id, x, y);
        return temp;
    }

    @Override
    public void draw() {
        if (id != -1) {
            GameObject.get(id).draw(x, y);
        }
    }

    @Override
    public float getY() {
        return y;
    }
    
    public ArrayList<Action> getActions(String uId) {
        int permissionLevel = this.getPermissionLevel(uId);
        ArrayList<Action> returnActions = new ArrayList();
        for(Action action : GameObject.get(id).getActions()) {
            if(action.getPermissionLevel() <= permissionLevel) {
                returnActions.add(action);
            }
        }
        return returnActions;
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

    public int getPermissionLevel(String uId) {
        if (this.uIdOfTown == null) {
            Iterator townIterator = this.world.getTowns().entrySet().iterator();
            while (townIterator.hasNext()) {
                Town town = (Town) ((Map.Entry) townIterator.next()).getValue();
                if (this.rectangle.overlaps(town.getBounds())) {
                    for(String string : town.getOwnersOfPoint(x, y)) {
                        if(string == uId) {
                            return 3;
                        }
                    }
                    for(String string : town.getBuildersOfPoint(x, y)) {
                        if(string == uId) {
                            return 2;
                        }
                    }
                    for(String string : town.getGuestsOfPoint(x, y)) {
                        if(string == uId) {
                            return 1;
                        }
                    }
                }
            }
        }
        return 0;
    }
    
    public void setWorld(GameWorld world) {
        this.world = world;
    }
}
