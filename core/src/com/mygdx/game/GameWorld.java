/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameObject;
import Persistence.Sound2D;
import Server.WorldSettings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryonet.Client;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kristian
 */
public class GameWorld {

    private int fieldOfView;
    private int size;
    private int sizeX;
    private int sizeY;
    private int lastPlayerX, lastPlayerY;
    private int drawableCount;
    private double deltaTime;
    private String name;
    private ArrayList<Chunk> chunks;
    private ArrayList<Entity> entities;
    private ArrayList<Drawable> drawOrder;
    private ArrayList<DroppedItem> droppedItems;
    private ArrayList<Object> objectsToBeAdded; //This list is going to solve the sync problem..
    private ArrayList<Object> objectsToBeRemoved;
    private HashMap<String, Town> towns;
    private DepthComparator depthComparator;
    private OrthographicCamera camera;
    private Player player;
    private String path;
    private World physicsWorld;
    private Client client;
    private Time time;

    public GameWorld(boolean isServer, String extraPath, OrthographicCamera camera) {
        this.camera = camera;
        this.entities = new ArrayList();
        this.chunks = new ArrayList();
        this.drawOrder = new ArrayList();
        this.droppedItems = new ArrayList();
        this.objectsToBeRemoved = new ArrayList();
        this.towns = new HashMap();
        this.time = new Time(8 * 60);
        size = 5;
        fieldOfView = 1;
        if (isServer) {
//            for (int iy = -size + 1; iy < size; iy++) {
//                for (int ix = -size + 1; ix < size; ix++) {
//                    chunks.add(Chunk.load(ix, iy, drawOrder, extraPath));
//                }
//            }
            FileHandle fileHandler = new FileHandle(extraPath + "chunks/");
            Json json = new Json();
            for (FileHandle file : fileHandler.list()) {
                Chunk chunk = json.fromJson(Chunk.class, file);
                chunk.generateUid();
                chunks.add(chunk);
            }
        } else {
            //Sound2D.playMusic("Deep Forest.ogg");
            this.physicsWorld = new World(new Vector2(0f, 0f), false);
            depthComparator = new DepthComparator();
            deltaTime = Gdx.graphics.getDeltaTime();
            objectsToBeAdded = new ArrayList();
//            player = Player.create(this, 0, 0);
            //addEntity(new Human(this));
        }
        //Chunk.makeSample();
        //Chunk.makeSampleTest();
        //WorldGenerator.generate(10, 10);
    }

    public GameWorld() {

    }

    public void updateRemoval(int chunkX, int chunkY) {
        /*
        for (Chunk chunk : this.chunks) {
            double distanceBetweenX = Math.abs(chunk.getX() - chunkX);
            double distanceBetweenY = Math.abs(chunk.getY() - chunkY);
            if ((distanceBetweenX > fieldOfView) || (distanceBetweenY > fieldOfView)) {
                ArrayList<WorldObject> worldObjects = chunk.getWorldObjects();
                chunk.flagObjectsForRemoval();
            }
        }
        */
        if (this.lastPlayerX != this.player.getChunkX() || this.lastPlayerY != this.player.getChunkY()) {
            this.lastPlayerX = this.player.getChunkX();
            this.lastPlayerY = this.player.getChunkY();

            int rangeX = fieldOfView;
            int rangeY = fieldOfView;

            for (Chunk chunk : this.chunks) {
                if (chunk.getX() < this.lastPlayerX-rangeX || chunk.getX() > this.lastPlayerX+rangeX
                        || chunk.getY() < this.lastPlayerY-rangeY || chunk.getY() > this.lastPlayerY+rangeY) {
                    ArrayList<WorldObject> worldObjects = chunk.getWorldObjects();
                    chunk.flagObjectsForRemoval();
                }
            }
        }
    }

    public void setPlayer(Player player) {
        WorldObject.setClient(this.getClient());
        this.player = player;
        addEntity(player);
    }

    /**
     * Returns true if successful
     *
     * @param name
     * @param description
     * @param tileX
     * @param tileY
     * @param tileW
     * @param tileH
     * @return true or false
     */
    public Town addTown(String name, String description, int tileX, int tileY, int tileW, int tileH) {
        Town town = new Town(this, tileX, tileY, tileW, tileH);
        boolean addTown = true;
        for (Town otherTown : this.getTownsAsList()) {
            if (town.getBounds().overlaps(otherTown.getBounds())) {
                addTown = false;
                this.player.addAlert("The town, that you are trying to add is overlapping another!", AlertType.SCREEN);
            }
        }

        if (tileW < 6 || tileH < 6) {
            this.player.addAlert("A town must at least be 6 x 6 in size!", AlertType.SCREEN);
            addTown = false;
        }

        if (addTown) {
            this.towns.put(town.getuId(), town);
            town.initialize();
            town.addOwner(player.getUId());
            town.setName(name);
            town.setDescription(description);
            town.sendUpdate();
            return town;
        } else {
            return null;
        }
    }

    public void spawnItem(int id, int count, int x, int y) {
        DroppedItem newItem = new DroppedItem(id, count, this, x, y);
        getDroppedItems().add(newItem);
    }

    public void spawnMob(int id, int x, int y) {
        Mob mob = new Mob(id, x, y);
        this.objectsToBeAdded.add(mob);
    }

    public void spawnWorldObject(int id, int x, int y) {
        if (!this.legalToPlaceObject(id, x, y)) {
            return;
        }
        WorldObject worldObject = new WorldObject(this, id, x, y);
        worldObject.initialize();
        worldObject.sendUpdate();
        this.updateWorldObject(worldObject);
    }

    public boolean legalToPlaceObject(int id, int x, int y) {
        if (GameObject.get(id) == null) {
            return false;
        }
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);
        int ChunkX = (int) (tileX / 32);
        int ChunkY = (int) (tileX / 32);
        Chunk chunk = getChunk(ChunkX, ChunkY);
        /*
        if (chunk.getTiles()[tileY % 32][tileX % 32] == 0) {
            //return false;
        }*/
        boolean hasSomethingToPlaceOn = true;
        if (GameObject.get(id).getzIndex() > 0) {
            hasSomethingToPlaceOn = false;
        }
        for (WorldObject object : worldObjectsAtLocation(x, y)) {
            if (object.getZ() < GameObject.get(id).getzIndex()) {
                hasSomethingToPlaceOn = true;
            } else {
                return false;
            }
        }
        return hasSomethingToPlaceOn;
    }

    public ArrayList<WorldObject> worldObjectsAtLocation(int x, int y) {
        ArrayList<WorldObject> objects = new ArrayList();

        //binary search:
        for (Drawable drawable : binarySearchY(drawOrder, 0, drawOrder.size() - 1, y)) {
            if (drawable instanceof WorldObject) {
                WorldObject worldObject = (WorldObject) drawable;
                if (worldObject.getX() == x) {
                    objects.add(worldObject);
                }
            }
        }
        return objects;
    }

    /**
     * let position be -1 at initial
     *
     * @param objects
     * @param start
     * @param end
     * @param value
     * @return
     */
    public ArrayList<Drawable> binarySearchY(ArrayList<Drawable> objects, int start, int end, float value) {
        int middle = (int) ((start + end) / 2);

        if (end < start) {
            return new ArrayList();
        }

        float yValue = objects.get(middle).getY();
        if (yValue == value) {
        } else if (value > yValue) {
            return binarySearchY(objects, start, middle - 1, value);
        } else {
            return binarySearchY(objects, middle + 1, end, value);
        }

        //if we found a value around the middle
        while (objects.get(middle - 1).getY() == value) {
            middle--;
        }
        ArrayList<Drawable> returnList = new ArrayList();
        while ((middle < objects.size()) ? objects.get(middle).getY() == value : false) {
            returnList.add(this.drawOrder.get(middle++));
        }
        return returnList;
    }

    public Entity addEntity(Entity entity) {
        entities.add(entity);
        drawOrder.add(entity);
        entity.world = this;
        entity.createBody();
        return entity;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        drawOrder.remove(entity);
    }

    public void update() {
        //update music fadeout and such..
        this.physicsWorld.step(1 / 60f, 6, 2);
        Sound2D.updateMusic(deltaTime);
        deltaTime = Gdx.graphics.getDeltaTime();
        this.time.update(deltaTime);
        updateEntities();
        try {
            Collections.sort(drawOrder, depthComparator);
        } catch (java.lang.IllegalArgumentException e) {

        }

        //Update dropped items:
        Iterator itemIterator = droppedItems.iterator();
        while (itemIterator.hasNext()) {
            DroppedItem item = (DroppedItem) itemIterator.next();
            item.update(deltaTime);
            if (item.toBeRemoved) {
                itemIterator.remove();
                System.out.println("Item removed!");
            }
        }

        //Update all chunks with access:
        //Chunk updating privileges
        Iterator iterator = chunks.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = null;
            try {
                chunk = (Chunk) iterator.next();
                chunk.update(deltaTime);
            } catch (ConcurrentModificationException exception) {
                System.out.println("Breaking loop...");
                break;
            }
            //update objects in region:
            Iterator objectIterator = chunk.getWorldObjects().iterator();
            int removedObjectCount = 0;
            this.objectsToBeRemoved.clear();
            while (objectIterator.hasNext()) {
                WorldObject object = (WorldObject) objectIterator.next();
                if (chunk.getClientControlling().equals(player.uId)) {
                    //update the objects, if the player has the permission.
                    object.update(deltaTime);
                }
                if (object.toBeRemoved) {
                    objectsToBeRemoved.add(object);
                    removedObjectCount++;
                }
            }
            drawOrder.removeAll(objectsToBeRemoved);
            if (drawableCount != drawOrder.size()) {
                System.out.println("Size: " + drawOrder.size() + " Objects removed: " + removedObjectCount);
            }
            drawableCount = drawOrder.size();

            //remove chunk if flagged
        }
        Iterator deleteChunkIterator = chunks.iterator();
        while (deleteChunkIterator.hasNext()) {
            Chunk c = (Chunk) deleteChunkIterator.next();
            if (c.isFlaggedForRemoval()) {
                deleteChunkIterator.remove();
            }
        }
        addReceivedObjects();
    }

    public WorldObject getWorldObject(WorldObject worldObject) {
        for (Drawable drawable : drawOrder) {
            if (drawable instanceof WorldObject) {
                WorldObject w = (WorldObject) drawable;
                if (w.getUid().equals(worldObject.uId)) {
                    return w;
                }
            }

        }
        return null;
    }

    public void updateEntities() {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public ArrayList<Drawable> getDrawable() {
        return drawOrder;
    }

    public ArrayList<Chunk> getChunks() {
        return this.chunks;
    }

    public Chunk getChunk(int index) {
        return chunks.get(index);
    }

    public Chunk getChunk(int x, int y) {
        for (Chunk chunk : chunks) {
            if (chunk.getX() == x && chunk.getY() == y) {
                return chunk;
            }
        }
        return null;
    }

    public boolean hasChunk(Chunk chunk) {
        return getChunk(chunk.getX(), chunk.getY()) != null;
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
    }

    public void draw() {
        try {
            for (Chunk c : chunks) {
                c.draw();
            }
            for (DroppedItem d : getDroppedItems()) {
                d.draw();
            }
            for (Drawable d : drawOrder) {
                d.draw();
            }
        } catch (ConcurrentModificationException exception) {
            System.out.println("Would have generated a: " + exception);
        }
    }

    public void updateWorldObject(WorldObject worldObject) {
        Iterator iterator = drawOrder.iterator();
        while (iterator.hasNext()) {
            Drawable temp = (Drawable) iterator.next();
            if (temp instanceof WorldObject) {
                WorldObject oldObject = (WorldObject) temp;
                if (oldObject.uId == null ? worldObject.uId == null : oldObject.uId.equals(worldObject.uId)) {
                    iterator.remove();
                    drawOrder.add(worldObject);
                    break;
                }
            }
        }
        Chunk chunk = getChunk((int) (worldObject.getX() / 32 / 32), (int) (worldObject.getY() / 32 / 32));
        chunk.updateWorldObject(worldObject);
        drawOrder.add(worldObject);
    }

    void drawDebug() {
        if (Game.isDebug == true) {
            try {
                for (Entity e : entities) {
                    Game.shapeRenderer.rect(e.getBounds().x, e.getBounds().y, e.getBounds().width, e.getBounds().height);
                }
            } catch (ConcurrentModificationException exception) {
                System.out.println("ConcurrentException!" + exception);
            }
        }
    }

    public int getSizeX() {
        if (sizeX == 0) {
            int highestX = 0;
            for (Chunk chunk : getChunks()) {
                if (chunk.getX() > highestX) {
                    highestX = chunk.getX();
                }
            }
            sizeX = highestX * 32 * 32;
        }
        return sizeX;
    }

    public int getSizeY() {
        if (sizeY == 0) {
            int highestY = 0;
            for (Chunk chunk : getChunks()) {
                if (chunk.getX() > highestY) {
                    highestY = chunk.getY();
                }
            }
            sizeY = highestY * 32 * 32;
        }
        return sizeY;
    }

    public Player getPlayer() {
        return player;
    }

    void applySettings(WorldSettings settings) {
        sizeX = settings.getWorldSizeX();
        sizeY = settings.getWorldSizeY();
        name = settings.getName();
    }

    /**
     * @return the droppedItems
     */
    public ArrayList<DroppedItem> getDroppedItems() {
        return droppedItems;
    }

    public void updateDroppedItem(DroppedItem item) {
        Iterator it = droppedItems.iterator();
        while (it.hasNext()) {
            DroppedItem toRemove = (DroppedItem) it.next();
            if (toRemove.getuId().equals(item.getuId())) {
                it.remove();
                break;
            }
        }

        //if the item is not being removed, add it
        if (!item.toBeRemoved) {
            droppedItems.add(item);
            item.setWorld(this);
        }
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    public void addObjectToBeAdded(Object o) {
        this.objectsToBeAdded.add(o);
    }

    public void removeObject(Object o) {
        this.objectsToBeRemoved.add(o);
    }

    public void addReceivedObjects() {
        try {
            for (Object o : objectsToBeAdded) {
                if (o instanceof Chunk) {
                    Chunk chunk = (Chunk) o;
                    chunk.setWorld(this);
                    chunk.initialize();
                    this.addChunk(chunk);
                    Iterator it = chunk.getWorldObjects().iterator();
                    while (it.hasNext()) {
                        WorldObject worldO = (WorldObject) it.next();
                        worldO.setWorld(this);
                        getDrawable().add(worldO);
                    }
                }
                if (o instanceof WorldObject) {
                    ((WorldObject) o).setWorld(this);
                    ((WorldObject) o).initialize();
                    this.updateWorldObject((WorldObject) o);
                }
                if (o instanceof DroppedItem) {
                    DroppedItem item = (DroppedItem) o;
                    item.initialize();
                    updateDroppedItem(item);
                }
                if (o instanceof Entity) {
                    Entity entity = (Entity) o;
                    addEntity(entity);
                    //drawOrder.add(entity);
                }
                if (o instanceof Town) {
                    Town town = (Town) o;
                    this.updateTown(town);
                }
            }
            objectsToBeAdded.clear();
        } catch (ConcurrentModificationException exception) {

        }
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public void updateTown(Town town) {
        towns.put(town.getuId(), town);
        if (this.client != null) {
            town.initialize();
        }
    }

    public HashMap<String, Town> getTowns() {
        return this.towns;
    }

    public ArrayList<Town> getTownsAsList() {
        Iterator townIterator = this.getTowns().entrySet().iterator();
        ArrayList<Town> returnList = new ArrayList();
        while (townIterator.hasNext()) {
            Town town = (Town) ((Map.Entry) townIterator.next()).getValue();
            returnList.add(town);
        }
        return returnList;
    }

    public Town getTownAtPoint(int x, int y) {
        Rectangle point = new Rectangle(x, y, 32, 32);
        for (Town town : this.getTownsAsList()) {
            if (town.getBounds().overlaps(point)) {
                return town;
            }
        }
        return null;
    }

    /**
     * Get the owned towns of the Human with the uId:
     *
     * @param uId
     * @return
     */
    public ArrayList<Town> getOwnedTowns(String uId) {
        ArrayList<Town> currentTowns = getTownsAsList();
        ArrayList<Town> returnList = new ArrayList();

        for (Town town : currentTowns) {
            for (String id : town.getuIdOfOwners()) {
                if (id.equals(uId)) {
                    returnList.add(town);
                }
            }
        }

        return returnList;
    }

    public ArrayList<Property> getOwnedProperties(String uId) {
        ArrayList<Town> currentTowns = getTownsAsList();
        ArrayList<Property> returnList = new ArrayList();

        for (Town town : currentTowns) {
            for (Property property : town.getProperties()) {
                for (String id : property.getuIdOfOwners()) {
                    if (id.equals(uId)) {
                        returnList.add(property);
                    }
                }
            }
        }

        return returnList;
    }

    public boolean canBuildAtPoint(String uId, int x, int y) {
        if (this.getTownAtPoint(x, y) == null) {
            return false;
        }
        for (String id : this.getTownAtPoint(x, y).getOwnersOfPoint(x, y)) {
            if (id.equals(uId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the physicsWorld
     */
    public World getPhysicsWorld() {
        return physicsWorld;
    }
}
