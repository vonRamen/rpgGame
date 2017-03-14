/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import Server.ExtraCommand;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
    private boolean isDead;
    protected float x;
    protected float y;
    protected float z;
    protected float forceDir;
    protected float forceSpeed;
    public float changeX;
    public float changeY;
    protected float strength;
    protected float strengthMod;
    protected float lastStrength;
    protected float boundsPattingX = 5;
    protected float boundsPattingY = 2;
    protected float animationTimer;
    protected float speed;
    protected float speedMod;
    protected float lastSpeed;
    protected float alpha;
    protected double deltaTime;
    private int chunkX; //The position of the chunk currently on
    private int chunkY;
    private int chunkXLastOn;
    private int chunkYLastOn;
    protected int hp;
    protected String animationName;
    protected int animationDirection;
    protected String name;
    protected TextureRegion currentFrame;
    protected BodyDef bodyDef;
    protected Rectangle bounds;
    private Rectangle fieldOfView; //field of view for entities and mobs.
    protected Animation animation;
    protected GameWorld world;
    protected String uId;
    protected Stack<Task> tasks;
    protected ExtraCommand extraCommand = ExtraCommand.NONE;
    protected Inventory inventory;
    protected EntityState state;
    protected boolean remove;
    protected Body body;

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
        this.addBounds();
    }

    public void attack() {

    }

    protected void addBounds() {
        bounds = new Rectangle(x + boundsPattingX, y + boundsPattingY, 22, 22);
        this.fieldOfView = new Rectangle(x - 100, y - 100, 200, 200);
    }

    public void update(double deltaTime) {
        if (this.remove) {
            fadeout();
            if (this.alpha <= 0) {
                this.alpha = 0;
                this.remove();
            }
        } else if (this.alpha < 1) {
            this.alpha += deltaTime;
        } else {
            this.alpha = 1;
        }
        animationHandler();

        //Reset state
        state = EntityState.IDLE;

        this.animationTimer += deltaTime;
        this.deltaTime = deltaTime;
        if (animation != null) {
            currentFrame = animation.getKeyFrame(this.animationTimer, true);
        }
        move(changeX, changeY);

        //Apply state
        if (this.getOnTile() == 0) {
            state = EntityState.SWIMMING;
        }
    }

    public ArrayList<Drawable> checkDrawableCollision(Rectangle rect) {
        if (rect == null) {
            return new ArrayList();
        }
        ArrayList<Drawable> objects = world.getDrawable();
        ArrayList<Drawable> returnObjects = new ArrayList();
        boolean isColliding = false;
        for (Drawable e : objects) {
            if (e instanceof Entity) {
                Entity entity = (Entity) e;
                if (entity.bounds != null) {
                    if (rect.overlaps(entity.bounds) && !e.equals(rect) && !e.equals(this)) {
                        returnObjects.add(e);
                    }
                }
            }
            if (e instanceof WorldObject) {
                WorldObject worldObject = (WorldObject) e;
                if (worldObject.rectangle != null) {
                    if (rect.overlaps(worldObject.rectangle)) {
                        returnObjects.add(e);
                    }
                }
            }
        }
        return returnObjects;
    }

    public ArrayList<Entity> checkEntityCollision(Rectangle rect) {
        if (rect == null) {
            return new ArrayList();
        }
        ArrayList<Entity> returnObjects = new ArrayList();
        ArrayList<Entity> objects = world.getEntities();
        for (Entity entity : objects) {
            if (entity.bounds == null) {
                continue;
            }
            if (rect.overlaps(entity.bounds) && !entity.equals(this) && !entity.equals(rect)) {
                returnObjects.add(entity);
            }
        }
        return returnObjects;
    }

    public void move(float change_x, float change_y) {
        if (changeX != 0 || changeY != 0) {
            this.state = EntityState.WALKING;
        }
        if (this.body != null) {
            float angle = (float) Math.atan2(change_y, change_x);
            this.x = this.body.getPosition().x;
            this.y = this.body.getPosition().y;
            this.body.applyLinearImpulse(new Vector2(change_x * speed, change_y * speed), this.body.getWorldCenter(), false);
        }
        /*
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
            if (bounds != null) {
                bounds.x = x + boundsPattingX + change_x * 5;
                bounds.y = y + boundsPattingY + chananimationTimerge_y * 5;
            }
            this.fieldOfView.x = this.x;
            this.fieldOfView.y = this.y;

            ArrayList<Drawable> objects = checkDrawableCollision(bounds);
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
            ArrayList<Drawable> objects = checkDrawableCollision(bounds);
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
            forceSpeed -= ((strength + (strength - forceSpeed / 10)) * deltaTime);
            if (forceSpeed <= 0) {update
                forceSpeed = 0;
            }
            updateBounds();
        }
         */
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
        this.addPush(direction, power);
        //        x += dir_x * power * deltaTime;
        //        y += dir_y * power * deltaTime;
        updateBounds();
    }

    protected void updateBounds() {
        if (bounds != null) {
            bounds.x = x + boundsPattingX;
            bounds.y = y + boundsPattingY;
        }
    }

    public void setAnimation(String animationName, int dir, EntityState state) {
        animationDirection = dir;
        switch (state) {
            case WALKING:
                animation = AnimationGroup.getAnimation(animationName).getDirectionalAnimation(dir);
                break;

            case SWIMMING:
                animation = AnimationGroup.getAnimation(animationName).getUnderWaterAnimation(dir);
                break;

            case IDLE:
                animation = AnimationGroup.getAnimation(animationName).getIdleAnimation(dir);
                break;

            default:
                int index = EntityState.getIndex(state);
                animation = AnimationGroup.getAnimation(animationName).get(index, dir);
                break;
        }
        this.animationName = animationName;
        currentFrame = animation.getKeyFrame(animationTimer, true);
    }

    public void setIdle(String animationName) {
        animation = AnimationGroup.getAnimation(animationName).getIdleAnimation(animationDirection);
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    @Override
    public void draw() {
        if (animation != null) {
            float shadowAlpha = 0.4f - (1 - this.alpha);
            if (shadowAlpha < 0) {
                shadowAlpha = 0;
            }
            Game.batch.setColor(0, 0, 0, shadowAlpha);
            Game.batch.draw(currentFrame, x, y - 4);
            Game.batch.setColor(1, 1, 1, this.alpha);
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

    public boolean solidAtLocation(int x, int y) {
        Rectangle rect = new Rectangle(x, y, 32, 32);
        int chunkX = (int) x / (32 * 32);
        int chunkY = (int) y / (32 * 32);
        int localX = (int) (x % (32 * 32));
        int localY = (int) (y % (32 * 32));
        int tileX = (int) (localX / 32);
        int tileY = (int) (localY / 32);
        Chunk chunk = world.getChunk(chunkX, chunkY);
        return chunk.solidAt(tileX, tileY);
    }

    /**
     * Returns the id of the tile, this entity is standing on.
     *
     * @return
     */
    public int getOnTile() {
        Chunk currentChunk = this.world.getChunk(this.getChunkX(), this.getChunkY());
        float relativeX = (this.x - (getChunkX() * 32 * 32)) + 16;
        float relativeY = (this.y - (getChunkY() * 32 * 32));

        int tileX = (int) relativeX / 32;
        int tileY = (int) relativeY / 32;

        if (currentChunk == null || tileX < 0 || tileY < 0 || tileX > 31 || tileY > 31) {
            return -1;
        }

        return currentChunk.getTiles()[tileY][tileX];
    }

    @Override
    public boolean isFlaggedForRemoval() {
        return toBeRemoved;
    }

    /**
     * @return the isDead
     */
    public boolean isIsDead() {
        return isDead;
    }

    /**
     * @return the chunkX
     */
    public int getChunkX() {
        return chunkX;
    }

    /**
     * @param chunkX the chunkX to set
     */
    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    /**
     * @return the chunkY
     */
    public int getChunkY() {
        return chunkY;
    }

    /**
     * @param chunkY the chunkY to set
     */
    public void setChunkY(int chunkY) {
        this.chunkY = chunkY;
    }

    /**
     * @return the chunkXLastOn
     */
    public int getChunkXLastOn() {
        return chunkXLastOn;
    }

    /**
     * @param chunkXLastOn the chunkXLastOn to set
     */
    public void setChunkXLastOn(int chunkXLastOn) {
        this.chunkXLastOn = chunkXLastOn;
    }

    /**
     * @return the chunkYLastOn
     */
    public int getChunkYLastOn() {
        return chunkYLastOn;
    }

    /**
     * @param chunkYLastOn the chunkYLastOn to set
     */
    public void setChunkYLastOn(int chunkYLastOn) {
        this.chunkYLastOn = chunkYLastOn;
    }

    @Override
    public ArrayList<Action> getActions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Action> getActions(String uId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public float getZ() {
        return this.z;
    }

    /**
     * @return the fieldOfView
     */
    public Rectangle getFieldOfView() {
        return fieldOfView;
    }

    /**
     * @param fieldOfView the fieldOfView to set
     */
    public void setFieldOfView(Rectangle fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    /**
     * This method checks, if another entity has been seen by this entity
     *
     * @return empty list if not found, Entity if found.
     */
    public ArrayList<Entity> checkView() {
        ArrayList<Entity> entities = new ArrayList();
        for (Drawable d : checkDrawableCollision(this.fieldOfView)) {
            if (d instanceof Entity) {
                entities.add((Entity) d);
            }
        }
        return entities;
    }

    public void flagRemoval() {
        this.remove = true;
    }

    private void fadeout() {
        this.alpha -= deltaTime / 5;
    }

    public void remove() {
        this.world.removeObject(this);
    }

    public void sendUpdate() {
        this.world.getClient().sendTCP(this);
    }

    public void createBody() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(x, y));

        Body body = world.getPhysicsWorld().createBody(bdef);
        CircleShape circle = new CircleShape();
        circle.setRadius(5f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = circle;
        fixture.density = 0.1f;
        fixture.friction = 0.4f;
        fixture.restitution = 0.6f;

        body.createFixture(fixture);
        body.setLinearDamping(4f);

        this.body = body;

        circle.dispose();
    }
}
