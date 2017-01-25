/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryonet.Client;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public class Town {

    int tileX, tileY, tileW, tileH;
    private Rectangle bounds;
    private String name;
    private String description;
    private GameWorld world;
    private String uId;
    private ArrayList<String> uIdOfOwners;
    private ArrayList<String> uIdOfBuilders;
    private ArrayList<String> uIdOfGuest;
    ArrayList<Property> properties;

    public Town(GameWorld world, int tileX, int tileY, int tileW, int tileH) {
        this.uIdOfOwners = new ArrayList();
        this.properties = new ArrayList();
        this.world = world;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileW = tileW;
        this.tileH = tileH;
        this.bounds = new Rectangle(tileX * 32, tileY * 32, tileW * 32, tileH * 32);
        this.name = "Town";
        this.description = "This is a town";
        this.uId = UUID.randomUUID().toString();
    }

    public Town() {
    }

    public void addProperty(int tileX, int tileY, int tileW, int tileH) {
        this.properties.add(new Property(world, this, tileX, tileY, tileW, tileH));
    }

    /**
     * @return the name
     */
    final public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    final public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    final public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    final public void setDescription(String description) {
        this.description = description;
    }

    public void addOwner(String uId) {
        if (!uIdOfOwners.contains(uId)) {
            this.getuIdOfOwners().add(uId);
        }
    }

    public void removeOwner(String uId) {
        if (getuIdOfOwners().contains(uId)) {
            this.getuIdOfOwners().remove(uId);
        }
    }

    public void setWorld(GameWorld world) {
        this.world = world;
        for (Property property : properties) {
            property.setWorld(world);
        }
    }

    public void sendUpdate() {
        Client client = this.world.getClient();
        Rectangle boundsHold = this.getBounds();
        GameWorld worldHold = this.world;
        this.world = null;
        this.bounds = null;
        client.sendTCP(this);
        this.world = worldHold;
        this.bounds = boundsHold;
    }

    public void initialize() {
        this.bounds = new Rectangle(tileX * 32, tileY * 32, tileW * 32, tileH * 32);
    }

    /**
     * @return the uId
     */
    public String getuId() {
        return uId;
    }

    /**
     * @return the bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }

    public ArrayList<String> getOwnersOfPoint(int x, int y) {
        return this.getUidsInList(this.uIdOfOwners, x, y);
    }
    
    public ArrayList<String> getBuildersOfPoint(int x, int y) {
        return this.getUidsInList(this.uIdOfBuilders, x, y);
    }
    
    public ArrayList<String> getGuestsOfPoint(int x, int y) {
        return this.getUidsInList(this.uIdOfGuest, x, y);
    }
    
    public ArrayList<String> getUidsInList(ArrayList<String> uIds, int x, int y) {
        Rectangle rect = new Rectangle(x, y, 32, 32);
        for (Property property : properties) {
            if (property.getBounds().overlaps(rect)) {
                System.out.println("Does overlap");
                return property.getUidsInList(uIds, x, y);
            }
        }

        return uIds;
    }

    /**
     * @return the uIdOfOwners
     */
    public ArrayList<String> getuIdOfOwners() {
        return uIdOfOwners;
    }
}
