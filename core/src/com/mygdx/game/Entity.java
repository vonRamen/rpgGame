/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Server.ExtraCommand;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public abstract class Entity implements Drawable, Cloneable {

    protected boolean toBeRemoved;
    protected boolean isNetworkObject;
    protected boolean isAttacking;
    protected boolean isMoving;
    protected boolean isDead;
    protected float x;
    protected float y;
    protected float z;
    protected float forceDir;
    protected float forceSpeed;
    protected float changeX;
    protected float changeY;
    protected float strength;
    protected float boundsPattingX = 5;
    protected float boundsPattingY = 2;
    protected float animationTimer;
    protected float speed;
    protected double deltaTime;
    protected int chunkX; //The position of the chunk currently on
    protected int chunkY;
    protected int chunkXLastOn;
    protected int chunkYLastOn;
    protected int hp;
    protected String animationName;
    protected int animationDirection;
    protected String name;
    protected TextureRegion currentFrame;
    protected BodyDef bodyDef;
    protected Rectangle bounds;
    protected Animation animation;
    protected GameWorld world;
    protected String uId;
    protected Stack<Task> tasks;
    protected ExtraCommand extraCommand = ExtraCommand.NONE;
    protected Inventory inventory;
    protected EntityState state;

    public Entity(GameWorld world) {
        this.world = world;
        speed = 125;
        strength = 200;
        addBounds();
        uId = UUID.randomUUID().toString();
    }

    public void initialize() {
        this.world = world;
        speed = 125;
        strength = 50;
        addBounds();
        this.inventory.setEntity(this);
        this.state = EntityState.IDLE;
    }

    public Entity() {

    }

    public void attack() {

    }

    protected void addBounds() {
        bounds = new Rectangle(x + boundsPattingX, y + boundsPattingY, 22, 22);
    }

    public void update(double deltaTime) {
        //Apply state
        if (this.getOnTile() == 0) {
            state = EntityState.SWIMMING;
        }
        animationHandler();

        //Reset state
        state = EntityState.IDLE;

        this.animationTimer += deltaTime;
        this.deltaTime = deltaTime;
        if (animation != null) {
            currentFrame = animation.getKeyFrame(this.animationTimer, true);
        }
        if (((changeX != 0 || changeY != 0) && isNetworkObject) || (forceSpeed != 0)) {
            move(changeX, changeY);
        }
    }

    public ArrayList<Drawable> checkDrawableCollision() {
        ArrayList<Drawable> objects = world.getDrawable();
        ArrayList<Drawable> returnObjects = new ArrayList();
        boolean isColliding = false;
        for (Drawable e : objects) {
            if (e instanceof Entity) {
                Entity entity = (Entity) e;
                if (bounds.overlaps(entity.bounds) && !e.equals(bounds) && !e.equals(this)) {
                    returnObjects.add(e);
                }
            }
            if (e instanceof WorldObject) {
                WorldObject worldObject = (WorldObject) e;
                if (worldObject.rectangle != null) {
                    if (bounds.overlaps(worldObject.rectangle)) {
                        returnObjects.add(e);
                    }
                }
            }
        }
        return returnObjects;
    }

    public ArrayList<Entity> checkEntityCollision(Rectangle rect) {
        ArrayList<Entity> returnObjects = new ArrayList();
        ArrayList<Entity> objects = world.getEntities();
        for (Entity entity : objects) {
            if (rect.overlaps(entity.bounds) && !entity.equals(this) && !entity.equals(rect)) {
                returnObjects.add(entity);
            }
        }
        return returnObjects;
    }

    public void move(float change_x, float change_y) {
        if (!isNetworkObject) {
            this.changeX = change_x;
            this.changeY = change_y;
        }
        isMoving = true;
        if ((change_x != 0 || change_y != 0)) {
            this.state = EntityState.WALKING;
            float angle = (float) Math.atan2(change_y, change_x);
            float direction_x = (float) Math.cos(angle);
            float direction_y = (float) Math.sin(angle);
            bounds.x = x + boundsPattingX + change_x * 5;
            bounds.y = y + boundsPattingY + change_y * 5;

            ArrayList<Drawable> objects = checkDrawableCollision();
            if (objects.isEmpty()) {
                //Check tiles in current Chunk
                x += (direction_x * speed) * deltaTime;
                y += (direction_y * speed) * deltaTime;
            } else {
                for (Drawable e : objects) {
                    if (e instanceof Entity) {
                        Entity entity = (Entity) e;
                        float ang = (float) Math.atan2(e.getY() - y, entity.getX() - x);
                        entity.addPush(ang, strength);
                    }
                }
            }
        }
        //If attacked, knockback
        if (forceSpeed != 0) {
            ArrayList<Drawable> objects = checkDrawableCollision();
            if (objects.isEmpty()) {
                float dir_x = (float) Math.cos(forceDir);
                float dir_y = (float) Math.sin(forceDir);
                x += (dir_x * forceSpeed) * deltaTime;
                y += (dir_y * forceSpeed) * deltaTime;
            } else {
                for (Drawable e : objects) {
                    if (e instanceof Entity) {
                        Entity entity = (Entity) e;
                        float ang = (float) Math.atan2(e.getY() - y, entity.getX() - x);
                        entity.addPush(ang, strength);
                    }
                }
            }
        }
        //If hit by a weapon:
        if (forceSpeed != 0) {
            System.out.println((strength + (strength - forceSpeed / 2)));
            forceSpeed -= ((strength + (strength - forceSpeed / 10)) * deltaTime);
            if (forceSpeed <= 0) {
                forceSpeed = 0;
            }
            updateBounds();
        }
    }

    public void damageThis(float direction, int damage, int force) {
        addForce(direction, force);
        hp -= damage;
        if (hp <= 0) {
            isDead = true;
        }
    }

    public void animationHandler() {
        //animate the right animationDirection:
        int verChange = 0;
        int horiChange = 0;
        int value = this.animationDirection;

        if (changeX != 0) {
            value = (changeX > 0) ? Direction.RIGHT.getValue() : Direction.LEFT.getValue();
        }
        if (changeY != 0) {
            value = (changeY > 0) ? Direction.UP.getValue() : Direction.DOWN.getValue();
        }
        setAnimation(animationName, value, state);
    }

    public void addPush(float direction, float power) {
        try {
            float dir_x = (float) Math.cos(direction);
            float dir_y = (float) Math.sin(direction);

            move(dir_x, dir_y);

            //        x += dir_x * power * deltaTime;
            //        y += dir_y * power * deltaTime;
            updateBounds();
        } catch (StackOverflowError stackError) {
            if (!isNetworkObject) {
                x += 32;
                System.out.println("Caught error " + stackError);
            }
        }
    }

    public void addForce(float direction, float power) {
        changeX = 0;
        changeY = 0;
        forceSpeed = power;
        forceDir = direction;
        //        x += dir_x * power * deltaTime;
        //        y += dir_y * power * deltaTime;
        updateBounds();
    }

    protected void updateBounds() {
        bounds.x = x + boundsPattingX;
        bounds.y = y + boundsPattingY;
    }

    public void setAnimation(String animationName, int dir, EntityState state) {
        animationDirection = dir;
        switch (state) {
            case WALKING:
                animation = KAnimation.getAnimation(animationName).getDirectionalAnimation(dir);
                break;

            case SWIMMING:
                animation = KAnimation.getAnimation(animationName).getUnderWaterAnimation(dir);
                break;

            case IDLE:
                animation = KAnimation.getAnimation(animationName).getIdleAnimation(dir);
                break;

            default:

                break;
        }
        this.animationName = animationName;
        currentFrame = animation.getKeyFrame(animationTimer, true);
    }

    public void setIdle(String animationName) {
        animation = KAnimation.getAnimation(animationName).getIdleAnimation(animationDirection);
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    @Override
    public void draw() {
        if (animation != null) {
            Game.batch.setColor(0, 0, 0, 0.4f);
            Game.batch.draw(currentFrame, x, y - 4 + z);
            Game.batch.setColor(Color.WHITE);
            Game.batch.draw(currentFrame, x, y + z);
        }
    }

    /**
     * @return the x
     */
    @Override
    public float getX() {
        return x;
    }

    public String getUId() {
        return uId;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the hp
     */
    public int getHp() {
        return hp;
    }

    /**
     * @param hp the hp to set
     */
    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * @return the bodyDef
     */
    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * @return the body
     */
    /**
     * @return the world
     */
    public GameWorld getWorld() {
        return world;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void removeNonSimpleTypes() {
        currentFrame = null;
        bodyDef = null;
        bounds = null;
        animation = null;
        world = null;
    }

    public void pickup() {
        if (this.inventory == null) {
            return;
        }
        for (DroppedItem item : world.getDroppedItems()) {
            if (bounds.overlaps(item.getBounds())) {
                int countLeft = inventory.addItem(item.getId(), item.getCount());
                item.setCount(countLeft);
            }
        }
    }

    /**
     * Returns the id of the tile, this entity is standing on.
     *
     * @return
     */
    public int getOnTile() {
        Chunk currentChunk = this.world.getChunk(this.chunkX, this.chunkY);
        float relativeX = (this.x - (chunkX * 32 * 32));
        float relativeY = (this.y - (chunkY * 32 * 32));

        int tileX = (int) relativeX / 32;
        int tileY = (int) relativeY / 32;

        if (currentChunk == null || tileX == -1 || tileY == -1 || tileX > 31 || tileY > 31) {
            return -1;
        }

        return currentChunk.getTiles()[tileY][tileX];
    }

    @Override
    public boolean isFlaggedForRemoval() {
        return toBeRemoved;
    }
}
