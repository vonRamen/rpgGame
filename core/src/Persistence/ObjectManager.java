/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

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

    public ObjectManager() {
        this.gameItems = new TreeMap();
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
        this.loadClassesForType(gameItems, ext, "Persistence/Items");
    }

    private void loadClassesForType(Map map, ExtensionLoader extensionLoader, String directory) {
        try {
            ArrayList<PersistenceFile> objects = extensionLoader.loadAllClasses(directory);
            
            for(PersistenceFile object : objects) {
                map.put(object.getId(), object);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
