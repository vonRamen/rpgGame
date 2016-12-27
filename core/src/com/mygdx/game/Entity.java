/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public abstract class Entity implements Drawable, Cloneable {

    protected String name;
    protected float x;
    protected float y;
    protected float forceDir;
    protected float forceSpeed;
    protected int chunkX; //The position of the chunk currently on
    protected int chunkY;
    protected int chunkXLastOn;
    protected int chunkYLastOn;
    protected float changeX;
    protected float changeY;
    protected float strength;
    protected float boundsPattingX = 5;
    protected float boundsPattingY = 2;
    protected float speed;
    protected int hp;
    protected float animationTimer;
    protected double deltaTime;
    protected boolean isMoving;
    protected TextureRegion currentFrame;
    protected BodyDef bodyDef;
    protected Rectangle bounds;
    protected Animation animation;
    protected int animationId;
    protected int animationDirection;
    protected GameWorld world;
    protected String uId;
    protected boolean isNetworkObject;
    protected Stack<Task> tasks;
    protected boolean isAttacking;
    protected boolean isDead;

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
    }

    public Entity() {

    }

    public void attack() {

    }

    protected void addBounds() {
        bounds = new Rectangle(x + boundsPattingX, y + boundsPattingY, 22, 22);
    }

    public void update(double deltaTime) {
        this.animationTimer += deltaTime;
        this.deltaTime = deltaTime;
        if (animation != null) {
            currentFrame = animation.getKeyFrame(this.animationTimer, true);
        }
        animationHandler();
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
            float angle = (float) Math.atan2(change_y, change_x);
            float direction_x = (float) Math.cos(angle);
            float direction_y = (float) Math.sin(angle);
            bounds.x = x + boundsPattingX + change_x * 5;
            bounds.y = y + boundsPattingY + change_y * 5;

            ArrayList<Drawable> objects = checkDrawableCollision();
            if (objects.isEmpty()) {
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
        if (!isAttacking) { //Makes sure you're only able to attack one place at once.
            if (changeX != 0 || changeY != 0) {
                if (changeX != 0) {
                    int horiChange = (changeX > 0) ? KAnimation.Direction.RIGHT.getIndex() : KAnimation.Direction.LEFT.getIndex();
                    setAnimation(animationId, horiChange);
                }
                if (changeY != 0) {
                    int verChange = (changeY > 0) ? KAnimation.Direction.UP.getIndex() : KAnimation.Direction.DOWN.getIndex();
                    setAnimation(animationId, verChange);
                }
            } else {
                setIdle(animationId);
            }
        }
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

    public void setAnimation(int id, int dir) {
        animationDirection = dir;
        animation = KAnimation.getAnimation(id).getDirectionalAnimation(dir);
        animationId = id;
        currentFrame = animation.getKeyFrame(animationTimer, true);
    }

    public void setIdle(int id) {
        animation = KAnimation.getAnimation(id).getIdleAnimation(animationDirection);
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    @Override
    public void draw() {
        if (animation != null) {
            Game.batch.draw(currentFrame, x, y);
        }
    }

    /**
     * @return the x
     */
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
}
