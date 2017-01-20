/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameItem;
import Persistence.GameObject;
import Server.WorldSettings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryonet.Client;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author kristian
 */
public class GameWorld {

    private int fieldOfView;
    private int size;
    private int sizeX;
    private int sizeY;
    private int drawableCount;
    private double deltaTime;
    private String name;
    private ArrayList<Chunk> chunks;
    private ArrayList<Entity> entities;
    private ArrayList<Drawable> drawOrder;
    private ArrayList<DroppedItem> droppedItems;
    private ArrayList<Object> objectsToBeAdded; //This list is going to solve the sync problem..
    private DepthComparator depthComparator;
    private PlayerController playerController;
    private OrthographicCamera camera;
    private Player player;
    private String path;
    private World world;
    private Client client;

    public GameWorld(boolean isServer, String extraPath, OrthographicCamera camera) {
        this.camera = camera;
        entities = new ArrayList();
        chunks = new ArrayList();
        drawOrder = new ArrayList();
        droppedItems = new ArrayList();
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
        for(Chunk chunk : this.chunks) {
            double distanceBetweenX = Math.abs(chunk.getX() - chunkX);
            double distanceBetweenY = Math.abs(chunk.getY() - chunkY);
            System.out.println("Player Chunk X & Y: " + chunkX + " " + chunkY);
            System.out.println("Distance Compared X & Y: " + distanceBetweenX + " " + distanceBetweenY);
            if ((distanceBetweenX > fieldOfView) || (distanceBetweenY > fieldOfView)) {
                ArrayList<WorldObject> worldObjects = chunk.getWorldObjects();
                chunk.flagObjectsForRemoval();
            }
        }
    }

    public void setPlayer(Player player) {
        WorldObject.setClient(this.getClient());
        playerController = new PlayerController(client, player, camera);
        playerController.setWorld(this);
        this.player = player;
        addEntity(player);
    }

    public void spawnItem(int id, int count, int x, int y) {
        DroppedItem newItem = new DroppedItem(id, count, this, x, y);
        getDroppedItems().add(newItem);
    }

    public Entity addEntity(Entity entity) {
        entities.add(entity);
        drawOrder.add(entity);
        entity.world = this;
        return entity;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        drawOrder.remove(entity);
    }

    public void update() {
        //if a gui hasn't been set up - set it up:
        if (playerController != null) {
            if (!playerController.hasGUI()) {
                playerController.startGUI();
            }
            playerController.update();
        }

        deltaTime = Gdx.graphics.getDeltaTime();
        updateEntities();
        try {
            Collections.sort(drawOrder, depthComparator);
        } catch (ConcurrentModificationException e) {

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
            } catch (ConcurrentModificationException exception) {
                System.out.println("Breaking loop...");
                break;
            }
            //update objects in region:
            Iterator objectIterator = chunk.getWorldObjects().iterator();
            int removedObjectCount = 0;
            ArrayList<WorldObject> objectsToBeRemoved = new ArrayList();
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
        while(deleteChunkIterator.hasNext()) {
            Chunk c = (Chunk) deleteChunkIterator.next();
            if(c.isFlaggedForRemoval()) {
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
        for(Entity entity : entities) {
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

    public PlayerController getPlayerController() {
        return playerController;
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
        droppedItems.add(item);
        item.setWorld(this);
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

    public void addReceivedObjects() {
        try {
            for (Object o : objectsToBeAdded) {
                if (o instanceof Chunk) {
                    Chunk chunk = (Chunk) o;
                    chunk.initialize();
                    this.addChunk(chunk);
                    Iterator it = chunk.getWorldObjects().iterator();
                    while (it.hasNext()) {
                        WorldObject worldO = (WorldObject) it.next();
                        getDrawable().add(worldO);
                    }
                }
                if (o instanceof WorldObject) {
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
                    this.entities.add(entity);
                    drawOrder.add(entity);
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
}
