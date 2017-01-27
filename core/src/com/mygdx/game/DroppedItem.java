/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import Persistence.GameItem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryonet.Client;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public class DroppedItem implements Drawable {

    protected int x, y, z;
    protected int yDirection; //1 = up, -1 = down
    protected int id;
    protected int count;
    protected float alpha;
    protected float xFlow, yFlow; //The maplestory flow
    protected float yTop, yBottom;
    protected float ySpeed;
    protected float yTime; //for the interpolation
    protected float despawnTimer;
    protected boolean isRemoving;
    protected boolean toBeRemoved;
    protected Interpolation interpolation; //Used for item bounce
    protected String name;
    protected String description;
    private String uId;
    protected Persistence.GameItem gameItem;
    protected Rectangle bounds;
    protected GameWorld world;

    public DroppedItem(int id, int count, GameWorld world, int x, int y) {
        this.count = count;
        this.id = id;
        this.x = x;
        this.y = y;
        this.world = world;
        uId = UUID.randomUUID().toString();
        alpha = 1;
        yTop = 5;
        yBottom = 16;
        yDirection = 1;
        ySpeed = 0.3f;
        yTime = 0;
        interpolation = Interpolation.bounceIn;
        if (GameItem.get(id) == null) {
            System.out.println("Warning! GameItem doesn't exist!");
            gameItem = GameItem.get(0);
            return;
        }
        gameItem = GameItem.get(id);
        this.name = gameItem.toString();
        bounds = new Rectangle(x + 4, y + 4, 8, 8);
        despawnTimer = 60;
        sendUpdate();
    }

    public DroppedItem() {

    }

    public void initialize() {
        interpolation = Interpolation.bounceIn;
        bounds = new Rectangle(x + 4, y + 4, 8, 8);
        gameItem = GameItem.get(id);
    }

    private double lerp(double point0, double point1, double time) {
        return point0 + time * (point1 - point0);
    }

    public void update(double deltaTime) {
        yTime += ySpeed * deltaTime;
        lerp(yFlow, 16, yTime);
        if (yDirection == 1) {
            yFlow = interpolation.apply(yFlow, yTop, yTime);
        } else {
            yFlow = interpolation.apply(yFlow, 0, yTime);
        }
        if (yFlow > yTop - 0.1) {
            if (yDirection != -1) {
                yTime = 0;
            }
            yDirection = -1;
        } else if (yFlow < 0 + 0.1) {
            if (yDirection != 1) {
                yTime = 0;
            }
            yDirection = 1;
        }
        if (isRemoving) {
            this.alpha -= (float) deltaTime;
            if (this.alpha < 0) {
                toBeRemoved = true;
            }
        }

        //remove item after a certain time.
        despawnTimer -= deltaTime;
        if (despawnTimer < 0) {
            this.setCount(0);
        }
    }

    @Override
    public void draw() {
        //calculate alpha for shadow
        float shadowAlpha = 0.4f - (1 - alpha);
        if (shadowAlpha < 0) {
            shadowAlpha = 0;
        }
        //draw shadow
        Game.batch.setColor(0, 0, 0, shadowAlpha);
        Game.batch.draw(gameItem.getTexture(), x + xFlow, y - 4 - yFlow);
        Game.batch.setColor(Color.WHITE);
        //Draw item
        Game.batch.setColor(1, 1, 1, alpha);
        gameItem.draw(x + (int) xFlow, y + (int) yFlow);
        Game.batch.setColor(1, 1, 1, 1);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getY() {
        return y;
    }

    public void remove() {
        this.isRemoving = true;
    }

    @Override
    public ArrayList<Action> getActions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getX() {
        return x;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
        if (this.count <= 0) {
            this.remove();
            this.sendUpdate();
        }
    }

    public void sendUpdate() {
        //If the item is received from the server, then don't send it!
        
        GameItem gameItemHold = gameItem;
        Interpolation inter = this.interpolation;
        GameWorld worldHold = world;
        Client client = world.getClient();
        this.gameItem = null;
        this.interpolation = null;
        world = null;

        if(client != null)
            client.sendTCP(this);
        this.gameItem = gameItemHold;
        this.interpolation = inter;
        this.world = worldHold;
    }

    /**
     * @return the uId
     */
    public String getuId() {
        return uId;
    }

    @Override
    public boolean isFlaggedForRemoval() {
        return toBeRemoved;
    }

    @Override
    public ArrayList<Action> getActions(String uId) {
        return this.getActions();
    }

    @Override
    public float getZ() {
        return this.z;
    }

}
