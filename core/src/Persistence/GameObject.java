/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Game;
import com.mygdx.game.KJson;
import com.mygdx.game.SpriteRelative;
import com.mygdx.game.WorldObject;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import javafx.scene.effect.Blend;

/**
 *
 * @author kristian
 */
public class GameObject implements ReportCreatable {

    private static TreeMap<Integer, GameObject> gameObjects = new TreeMap();
    private static TreeMap<Integer, GameObject> ghostObjects = new TreeMap();
    private static ArrayList<TextureRegion> objectSprites = new ArrayList();
    private static ArrayList<TextureRegion> objectSilhuetes = new ArrayList();
    private static String path = "objects/";
    private static final KJson JSONHANDLER = new KJson();

    /**
     * @return the gameObjects
     */
    public static TreeMap<Integer, GameObject> getGameObjects() {
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
    private ArrayList<SpriteRelative> sprites;
    protected String name;
    protected String description;
    private float respawnTime;
    protected boolean isRespawnObject;
    protected int isRespawningInto;
    protected int id;
    protected Rectangle rectangle;
    protected ArrayList<Action> actions;
    protected String fileName;
    
    private float zIndex = 0;

    //If the object is created through building stuff, then
    //create a ghost object
    private boolean isBuilt;
    private boolean hideWhenNear;
    private boolean isGhostObject;
    private int requiredConstructionLevel;
    protected ArrayList<DropItem> itemsRequired;

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

    private void generateGhostObject() {
        GameObject ghostObject = new GameObject(this.name);
        ghostObject.itemsRequired = this.itemsRequired;
        ghostObject.sprites = this.getSprites();
        ghostObject.zIndex = this.zIndex;
        ghostObject.description = "A 'ghost' structure. It doesn't exist, but the concept is there!";
        ghostObject.id = GameObject.gameObjects.size();
        ghostObject.isGhostObject = true;

        //Generate actions!
        Action build = new Action();
        build.setItemTakes(itemsRequired);
        build.setName("Build");
        build.setPermissionLevel(2); //2 = builder
        build.setTransformIntoId(this.id);
        build.setBaseTime(2);
        build.setRequiredSkill("construction");
        build.setRequiredLevel(requiredConstructionLevel);

        Action remove = new Action();
        remove.setName("Remove");
        remove.setTransformIntoId(-1);
        remove.setPermissionLevel(3); //3 = owner

        ghostObject.actions = new ArrayList();
        ghostObject.actions.add(build);
        ghostObject.actions.add(remove);

        GameObject.ghostObjects.put(ghostObject.id, ghostObject);
        GameObject.gameObjects.put(ghostObject.id, ghostObject);
    }

    public static void load() {
        FileHandle fileHandler = Gdx.files.internal(path + "data/");
        Json json = new Json();
        for (FileHandle file : fileHandler.list()) {
            GameObject object = json.fromJson(GameObject.class, file);
            object.fileName = file.name();
            System.out.println("object id " + object.id);
            gameObjects.put(object.id, object);
        }

        //generate ghost objects:
        for (GameObject object : new Utility.KUtility<GameObject>().getArrayListOfMap(gameObjects)) {
            if (object.isBuilt) {
                object.generateGhostObject();
            }
        }
    }

    public static void loadObjects() {
        Texture texture = new Texture(getPath() + "standard tileset.png");
        int xx = (int) (texture.getWidth() / 32);
        int yy = (int) (texture.getHeight() / 32);

        int count = 0;
        for (int iy = 0; iy < yy; iy++) {
            for (int ix = 0; ix < xx; ix++) {
                TextureRegion txt = new TextureRegion(texture, ix * 32, iy * 32, 32, 32);

                //create silhuetes
                txt.getTexture().getTextureData().prepare();
                Pixmap full = txt.getTexture().getTextureData().consumePixmap();
                Pixmap area = new Pixmap(32, 32, Pixmap.Format.RGB888);
                for (int yPixel = 0; yPixel < 32; yPixel++) {
                    for (int xPixel = 0; xPixel < 32; xPixel++) {
                        int pixel = full.getPixel(xPixel + ix * 32, yPixel + iy * 32);
                        if (pixel != 0) {
                            Color color = new Color(pixel);
                            float colorStrength = (color.r + color.g + color.b) / 3 + 0.7f;
                            if (colorStrength > 1) {
                                colorStrength = 1;
                            }
                            Color newColor = new Color(colorStrength, colorStrength, colorStrength, colorStrength);
                            area.drawPixel(xPixel, yPixel, newColor.toIntBits());
                        }
                    }
                }

                TextureRegion txtWhite = new TextureRegion(new Texture(area));
                objectSprites.add(txt);
                objectSilhuetes.add(txtWhite);
            }
        }
        load();
    }

    public static File getFileOfObject(int id) {
        TextureRegion textureRegion = objectSprites.get(id);
        Texture texture = textureRegion.getTexture();
        if (!texture.getTextureData().isPrepared()) {
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

    /**
     *
     * @param x
     * @param y
     * @param drawLess remove walls and such, to be able to see shit?
     */
    public void draw(int x, int y, boolean drawLess) {
        if (this.hideWhenNear == false) {
            drawLess = false;
        }
        if (!drawLess) {
            for (SpriteRelative spriteShadow : getSprites()) {
                //draw shadows.
                if (zIndex != -1 && !isGhostObject) {
                    Game.batch.setColor(0, 0, 0, 0.4f);
                    Game.batch.draw(objectSprites.get(spriteShadow.getTextureId()), x + spriteShadow.getxRelative(), y - 4 + spriteShadow.getyRelative());
                    Game.batch.setColor(Color.WHITE);
                }
            }
        }
        ArrayList<TextureRegion> listToChoose;
        if (isGhostObject) {
            listToChoose = objectSilhuetes;
        } else {
            listToChoose = objectSprites;
        }
        if (!drawLess) {
            for (SpriteRelative sprites : getSprites()) {
                //draw the actual sprites
                Game.batch.draw(listToChoose.get(sprites.getTextureId()), x + sprites.getxRelative(), y + sprites.getyRelative());
            }
        }
        if (drawLess) {
            if (zIndex == 0) {
                Game.batch.draw(listToChoose.get(getSprites().get(0).getTextureId()), x, y);
            }
            if (zIndex == 5) {

            }
        }
    }

    public void update(WorldObject object, double deltaTime) {
        if (object.getUpdateTimer() > 0) {
            object.setUpdateTimer(object.getUpdateTimer() - (float) deltaTime);
        } else if (isRespawnObject) {
            object.setId(this.isRespawningInto);
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
        return gameObjects.get(id);
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

    public ArrayList<Action> getActions() {
        return actions;
    }

    /**
     * @return the respawnTime
     */
    public float getRespawnTime() {
        return respawnTime;
    }

    /**
     * @return the zIndex
     */
    public float getzIndex() {
        return zIndex;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public ArrayList<? extends ReportCreatable> getAll() {
        return new Utility.KUtility<GameObject>().getArrayListOfMap(gameObjects);
    }

    /**
     * @return the isGhostObject
     */
    public boolean isGhostObject() {
        return isGhostObject;
    }

    public static ArrayList<GameObject> getAllGhostObjects() {
        ArrayList<GameObject> returnList = new ArrayList();
        for (GameObject gameObject : new Utility.KUtility<GameObject>().getArrayListOfMap(gameObjects)) {
            if (gameObject.isGhostObject) {
                returnList.add(gameObject);
            }
        }

        return returnList;
    }

    /**
     * @return the sprites
     */
    public ArrayList<SpriteRelative> getSprites() {
        return sprites;
    }
}
