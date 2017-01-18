/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Chunk;
import com.mygdx.game.Drawable;
import com.mygdx.game.Game;
import com.mygdx.game.KJson;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is the general Item class. Like etc in MapleStory.
 *
 * @author kristian
 */
public class GameItem {

    private static ArrayList<GameItem> gameItems;
    protected static ArrayList<TextureRegion> itemTextures;
    protected static String path = "items/";
    protected static KJson JSON;
    protected int id;
    protected int sprite_id;
    protected String name;
    protected String description;
    protected int basePrice;
    private int stackSize;

    public GameItem() {
        basePrice = 0;
        stackSize = 30;
    }
    
    public static void load() {
        KLoader<GameItem> loader = new KLoader();
        gameItems = new ArrayList();
//        GameItem g = new GameItem();
//        g.name = "Spoon";
//        g.description = "It's a spoon";
//        g.id = 0;
//        String str = json.toJson(g);
//        
//        FileHandle fileHandler = new FileHandle(path+"data/dummy.json");
//        fileHandler.writeString(str, false);
        //Load all images first.
        itemTextures = new ArrayList();
 
        Texture texture = new Texture(path+"items.png");
        System.out.println("Loading Items Texture..");
        int xx = (int) (texture.getWidth() / 32);
        int yy = (int) (texture.getHeight() / 32);
        
        for (int iy = 0; iy < yy; iy++) {
            for (int ix = 0; ix < xx; ix++) {
                TextureRegion txt = new TextureRegion(texture, ix * 32, iy * 32, 32, 32);
                itemTextures.add(txt);
            }
        }
        //Load json with data.
        FileHandle dirHandle = Gdx.files.internal(path+"data/");
        System.out.println("Files in data: "+dirHandle.list().length);
        Json json = new Json();
        int count = 0;
        System.out.println("File location: "+dirHandle.path());
        for(FileHandle file : dirHandle.list()) {
            GameItem object = json.fromJson(GameItem.class, file);
            gameItems.add(object);
        }
        System.out.println("Items loaded: "+count);
    }
    
    public TextureRegion getTexture() {
        return itemTextures.get(sprite_id);
    }

    public static GameItem get(int id) {
        for (GameItem gameItem : gameItems) {
            if (gameItem.id == id) {
                return gameItem;
            }
        }
        return null;
    }
    
    public void draw(int x, int y) {
        Game.batch.draw(itemTextures.get(this.id), x, y);
    }

    /**
     * @return the stackSize
     */
    public int getStackSize() {
        return stackSize;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
