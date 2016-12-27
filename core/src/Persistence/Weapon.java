/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import static Persistence.GameItem.path;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kristian
 */
public class Weapon {

    private static HashMap<String, Sprite> sprites;
    private static ArrayList<Weapon> weapons;
    private static String path = "items/weapons/";
    
    private String name;
    private String description;
    private String sprite;
    private int requiredCombatLevel;
    private int speed;
    private int damage;
    private int knockback;

    public Weapon() {

    }

    public static void load() {
        sprites = new HashMap();
        weapons = new ArrayList();
        //first sprites:
        FileHandle dirHandle = Gdx.files.internal(path+"sprites/");
        for(FileHandle file : dirHandle.list()) {
            Texture texture = new Texture(file);
            Sprite sprite = new Sprite(texture);
            sprites.put(file.nameWithoutExtension(), sprite);
        }
        //second data:
        FileHandle fileHandler = Gdx.files.internal(path+"data/");
        Json json = new Json();
        for(FileHandle file : fileHandler.list()) {
            Weapon object = json.fromJson(Weapon.class, file);

            weapons.add(object);
        }
    }
    
    public static Weapon get(int id) {
        return weapons.get(id);
    }
    
    public int getLevel() {
        return requiredCombatLevel;
    }
    
    public Sprite getWeaponSprite() {
        return sprites.get(sprite);
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }

    /**
     * @return the knockback
     */
    public int getKnockback() {
        return knockback;
    }
}
