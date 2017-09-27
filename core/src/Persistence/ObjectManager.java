/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.graphics.g2d.Animation;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles every object for the dynamically loaded objects.
 *
 * @author Kristian
 */
public class ObjectManager {

    private TreeMap<Integer, GameItem> gameItems;
    private TreeMap<String, LoadableEntityAnimation> animationsObjects;
    private TreeMap<Integer, GameObject> gameObjects;

    public ObjectManager() {
        this.gameItems = new TreeMap();
        this.animationsObjects = new TreeMap();
        this.gameObjects = new TreeMap();
    }

    public GameItem getGameItem(int id, boolean newInstance) {
        try {
            return newInstance ? gameItems.get(id).getClass().newInstance() : gameItems.get(id);
        } catch (InstantiationException ex) {
            Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void loadAll() {
        ExtensionLoader<GameItem> ext = new ExtensionLoader();
        this.loadClassesForTypeInt(gameItems, ext, "Persistence/Items");

        ExtensionLoader<GameItem> extObj = new ExtensionLoader();
        this.loadClassesForTypeInt(gameObjects, extObj, "Persistence/Objects");

        ExtensionLoader<LoadableEntityAnimation> extAnim = new ExtensionLoader();
        this.loadClassesForTypeString(animationsObjects, extAnim, "Persistence/AnimationGroups");
    }

    public void addAdditionalGameObject(int id, GameObject object) {
        this.gameObjects.put(id, object);
    }

    private void loadClassesForTypeInt(Map map, ExtensionLoader extensionLoader, String directory) {
        try {
            ArrayList<PersistenceFile> objects = extensionLoader.loadAllClasses(directory);

            for (PersistenceFile object : objects) {
                map.put(object.getId(), object);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadClassesForTypeString(Map map, ExtensionLoader extensionLoader, String directory) {
        try {
            ArrayList<PersistenceFile> objects = extensionLoader.loadAllClasses(directory);

            for (PersistenceFile object : objects) {
                String[] classnameParted = object.getClass().toGenericString().split("\\.");
                map.put(classnameParted[classnameParted.length - 1], object);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the animationsObjects
     */
    public Animation getAnimationsObjects(String classname, String filename, int direction) {
        return animationsObjects.get(classname).getAnimation(filename, direction);
    }

    /**
     * @return the gameObjects
     */
    public GameObject getGameObject(int id) {
        return gameObjects.get(id);
    }
}
