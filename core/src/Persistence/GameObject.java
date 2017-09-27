/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Game;
import com.mygdx.game.KJson;
import com.mygdx.game.Light;
import com.mygdx.game.WorldObject;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author kristian
 */
public class GameObject extends PersistenceFile implements ReportCreatable {

    private static TreeMap<Integer, GameObject> gameObjects = new TreeMap();
    private static TreeMap<Integer, GameObject> ghostObjects = new TreeMap();
    private static String path = "objects/";
    private static final KJson JSONHANDLER = new KJson();

    /**
     * @return the gameObjects
     */
    public static TreeMap<Integer, GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * @return the path
     */
    public static String getPath() {
        return path;
    }
    protected TextureRegion sprite;
    protected TextureRegion ghostSprite;
    protected TextureRegion drawLessSprite; // to hide walls and etc.
    protected String name;
    protected String description;
    protected float respawnTime;
    protected boolean isRespawnObject;
    protected int isRespawningInto;
    protected int id;
    protected Rectangle rectangle;
    protected ArrayList<Action> actions;
    protected String fileName;
    protected int offsetX, offsetY;
    protected ArrayList<Light> lights;
    protected TextureRegionDrawable spriteIcon;

    protected float zIndex = 0;

    //If the object is created through building stuff, then
    //create a ghost object
    protected boolean isBuilt;
    protected boolean hideWhenNear;
    protected boolean isGhostObject;
    protected int requiredConstructionLevel;
    protected ArrayList<DropItem> itemsRequired;
    private ObjectType objectType;

    public GameObject(String name) {
        this.name = name;
        id = GameObject.gameObjects.size();
    }

    public GameObject() {
        this.name = "unnamed";
        id = GameObject.gameObjects.size();
        this.actions = new ArrayList();
        this.setObjectType(ObjectType.UNSPECIFIED);
        uniqueInitialization();
    }

    private void uniqueInitialization() {
        rectangle = new Rectangle(0, 0, 32, 32);
    }

    final protected void makeBuildAbleObject() {
        GameObject ghostObject = new GameObject(this.name);
        ghostObject.itemsRequired = this.itemsRequired;
        ghostObject.sprite = this.sprite;
        ghostObject.zIndex = this.zIndex;
        ghostObject.description = "A 'ghost' structure. It doesn't exist, but the concept is there!";
        ghostObject.isGhostObject = true;
        ghostObject.id = this.id + 1000;
        ghostObject.setObjectType(this.getObjectType());
        ghostObject.spriteIcon = this.spriteIcon;

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
        Game.objectManager.addAdditionalGameObject(ghostObject.id, ghostObject);
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
                object.makeBuildAbleObject();
            }
        }
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
        TextureRegion spriteToUse;

        if (drawLess && drawLessSprite != null) {
            spriteToUse = drawLessSprite;
        } else {
            spriteToUse = this.sprite;
        }

        Game.batch.draw(spriteToUse, x + offsetX, y + offsetY);
    }

    public void drawShadow(int x, int y, boolean drawLess) {
        if (!drawLess) {
            if (zIndex != -1 && !isGhostObject) {
                Game.batch.draw(sprite, x + offsetX, y + 10 - offsetY, 0, 0, sprite.getRegionWidth(), sprite.getRegionHeight(), 1, -1, 0);
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
        for (GameObject gameObject : new Utility.KUtility<GameObject>().getArrayListOfMap(ghostObjects)) {
            returnList.add(gameObject);
        }

        return returnList;
    }

    public TextureRegion getSprite() {
        return sprite;
    }

    public void drawLights(int x, int y) {
        if (this.lights != null) {
            for (Light light : this.lights) {
                light.draw(x, y);
            }
        }
    }

    public void addLight(Light newLight) {
        if (this.lights == null) {
            this.lights = new ArrayList();
        }
        this.lights.add(newLight);
    }

    /**
     * @return the objectType
     */
    public ObjectType getObjectType() {
        if (objectType == null) {
            return ObjectType.UNSPECIFIED;
        }
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public enum ObjectType {
        FLOOR, DOOR, WALL, ROOF, FURNITURE, NATURE, UNSPECIFIED;
    }

    /**
     * @return the spriteIcon
     */
    public TextureRegionDrawable getSpriteIcon() {
        return spriteIcon;
    }
}
