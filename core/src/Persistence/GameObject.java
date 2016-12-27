/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Game;
import com.mygdx.game.KJson;
import com.mygdx.game.SpriteRelative;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class GameObject {

    private static ArrayList<GameObject> gameObjects = new ArrayList();
    private static ArrayList<TextureRegion> objectSprites = new ArrayList();
    private static String path = "objects/";
    private static final KJson JSONHANDLER = new KJson();

    /**
     * @return the gameObjects
     */
    public static ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * @return the objectSprites
     */
    public static ArrayList<TextureRegion> getObjectSprites() {
        return objectSprites;
    }

    /**
     * @return the path
     */
    public static String getPath() {
        return path;
    }
    protected ArrayList<SpriteRelative> sprites;
    protected String name;
    protected String description;
    protected int id;
    protected Rectangle rectangle;

    public GameObject(String name) {
        this.name = name;
        sprites = new ArrayList();
        id = GameObject.gameObjects.size();
    }
    
    public GameObject() {
        sprites = new ArrayList();
        id = GameObject.gameObjects.size();
    }

    protected void uniqueInitialization() {
        rectangle = new Rectangle(0, 0, 32, 32);
    }

    public static void load() {
        FileHandle fileHandler = Gdx.files.internal(path+"data/");
        Json json = new Json();
        for(FileHandle file : fileHandler.list()) {
            GameObject object = json.fromJson(GameObject.class, file);
            gameObjects.add(object);
        }
    }
    
    public static void loadObjects() {
        Texture texture = new Texture(getPath()+"standard tileset.png");
        int xx = (int) (texture.getWidth() / 32);
        int yy = (int) (texture.getHeight() / 32);

        for (int iy = 0; iy < yy; iy++) {
            for (int ix = 0; ix < xx; ix++) {
                TextureRegion txt = new TextureRegion(texture, ix * 32, iy * 32, 32, 32);
                objectSprites.add(txt);
            }
        }
        load();
    }
    
    public static File getFileOfObject(int id) {
        TextureRegion textureRegion = objectSprites.get(id);
        Texture texture = textureRegion.getTexture();
        if(!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap pixToDraw = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        System.out.println("Here");
        for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
                int colorInt = pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y);
                pixToDraw.setColor(colorInt);
                pixToDraw.drawPixel(x, y);
                // you could now draw that color at (x, y) of another pixmap of the size (regionWidth, regionHeight)
            }
        }
        FileHandle fileH = Gdx.files.local("temp.png");
        PixmapIO.writePNG(fileH, pixToDraw);
        fileH.write(true);
        return fileH.file();
    }

    public static TextureRegion getSprite(int id) {
        return getObjectSprites().get(id);
    }
    
    public void draw(int x, int y) {
        for(SpriteRelative sprites : sprites) {
            Game.batch.draw(objectSprites.get(sprites.getTextureId()), x+sprites.getxRelative(), y+sprites.getyRelative());
        }
    }

    /**
     * @return the sprites
     */

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    public static GameObject get(int id) {
        if(gameObjects.size()>id) {
            return gameObjects.get(id);
        }
        return null;
    }

    public Rectangle getBounds() {
        return rectangle;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
