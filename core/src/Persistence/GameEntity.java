/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.util.HashMap;

/**
 *
 * @author kristian
 */
public class GameEntity {

    private static String path = "entities/";
    private static HashMap<Integer, GameEntity> entities;
    private String name;
    private String animationGroup;
    private String ai;
    private String entityType;
    private int alignment; //-1 = evil, 0 = neutral, 1 = good
    private int hp;
    private int damage;
    private int id;
    private int boundsWidth, boundsHeight;
    private float speed;
    private float startingZ;
    
    public GameEntity() {
        
    }
    
    public static void load() {
        entities = new HashMap();
        FileHandle fileHandler = Gdx.files.internal(path + "data/");
        Json json = new Json();
        for (FileHandle file : fileHandler.list()) {
            GameEntity object = json.fromJson(GameEntity.class, file);
            entities.put(object.id, object);
        }
    }
    
    public static GameEntity get(int id) {
        return entities.get(id);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the animationGroup
     */
    public String getAnimationGroup() {
        return animationGroup;
    }

    /**
     * @return the alignment
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * @return the hp
     */
    public int getHp() {
        return hp;
    }

    /**
     * @return the ai
     */
    public String getAi() {
        return ai;
    }

    /**
     * @return the entityType
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @return the boundsWidth
     */
    public int getBoundsWidth() {
        return boundsWidth;
    }

    /**
     * @return the boundsHeight
     */
    public int getBoundsHeight() {
        return boundsHeight;
    }

    /**
     * @return the startingZ
     */
    public float getStartingZ() {
        return startingZ;
    }
}
