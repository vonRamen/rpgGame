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

/**
 *
 * @author kristian
 */
public class GameWorld {

    private int fieldOfView;
    private int size;
    private int sizeX;
    private int sizeY;
    private double deltaTime;
    private String name;
    private ArrayList<Chunk> chunks;
    private ArrayList<Entity> entities;
    private ArrayList<Drawable> drawOrder;
    private ArrayList<DroppedItem> droppedItems;
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
        fieldOfView = 2;
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
//            player = Player.create(this, 0, 0);
            spawnItem(1, 20, 0, 5);
            spawnItem(1, 64, 0, 64);
            spawnItem(1, 32, 0, 32);
            spawnItem(0, 120, 0, 1);
            //addEntity(new Human(this));
        }
        //Chunk.makeSample();
        //Chunk.makeSampleTest();
        //WorldGenerator.generate(10, 10);
    }

    public GameWorld() {

    }

    public void updateRemoval(int chunkX, int chunkY) {
        Iterator iterator = chunks.iterator();
        while (iterator.hasNext()) {
            try {
                Chunk chunk = (Chunk) iterator.next();
                double distanceBetweenX = Math.pow(Math.sqrt(chunk.getX() - chunkX), 2);
                double distanceBetweenY = Math.pow(Math.sqrt(chunk.getY() - chunkY), 2);
                if ((distanceBetweenX > fieldOfView) || (distanceBetweenY > fieldOfView)) {
                    ArrayList<WorldObject> worldObjects = chunk.getWorldObjects();
                    try {
                        drawOrder.removeAll(worldObjects);
                    } catch(IndexOutOfBoundsException e) {
                        System.out.println("Tried removing objects, but got a"+e);
                    }
                    iterator.remove();
                    System.out.println("Chunk " + chunk.getUId() + " was removed.");
                }
            } catch (ConcurrentModificationException exception) {
                System.out.println("World removal Concurrent Modification Exception, breaking loop.. \n better luck next time");
                break;
            }
        }
    }

    public void setPlayer(Client client, Player player) {
        this.client = client;
        WorldObject.setClient(this.client);
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
        while(itemIterator.hasNext()) {
            DroppedItem item = (DroppedItem) itemIterator.next();
            item.update(deltaTime);
            if(item.toBeRemoved) {
                itemIterator.remove();
            }
        }

        //Update all chunks with access:
        
        
        //Chunk updating privileges
        Iterator iterator = chunks.iterator();
        while(iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();
            if (chunk.getClientControlling().equals(player.uId)) {
                //update objects in region:
                Iterator objectIterator = chunk.getWorldObjects().iterator();
                while (objectIterator.hasNext()) {
                    WorldObject object = (WorldObject) objectIterator.next();
                    object.update(deltaTime);
                }
            }
        }
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
        for (Entity e : entities) {
            e.update(deltaTime);
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

}
