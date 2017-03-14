/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameObject;
import Persistence.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public class Chunk implements Runnable {

    private static String path;

    /**
     * @return the path
     */
    public static String getPath() {
        return path;
    }
    private int x, y; //*32 *32
    private int[][] tiles;
    private int[][] collisionGrid;

    private ArrayList<WorldObject> worldObjects;
    private ArrayList<Drawable> drawable;
    private String uId;
    private String controlledClient;
    private boolean toBeRemoved;
    private double deltaTime;
    private GameWorld world;

    public Chunk(int x, int y, ArrayList<Drawable> drawable) {
        this.drawable = drawable;
        this.x = x;
        this.y = y;
        tiles = new int[32][32];
        worldObjects = new ArrayList();
        uId = UUID.randomUUID().toString();
    }

    public Chunk() {
    }

    public void initialize() {
        for (WorldObject worldObject : worldObjects) {
            worldObject.setWorld(world);
            worldObject.initialize();
        }
    }

    public static Chunk load(int chunk_x, int chunk_y, ArrayList<Drawable> drawable, String extraPath) {
        //Chunk chunk = new Chunk(chunk_x, chunk_y);
        FileHandle fileHandler = new FileHandle(extraPath + "/chunks/" + chunk_x + "," + chunk_y + "/tiles.json");
        Json json = new Json();
        Chunk chunk = json.fromJson(Chunk.class, fileHandler);
        chunk.drawable = drawable;
        return chunk;
    }

    public void update(double deltaTime) {
        this.deltaTime += deltaTime;
    }

    public String getUId() {
        return uId;
    }

    @Override
    public void run() {
    }

    public static void setPath(String path) {
        Chunk.path = path;
    }

    public void draw() {
        Player p = this.world.getPlayer();
        for (int yy = 0; yy < getTiles().length; yy++) {
            for (int xx = 0; xx < getTiles().length; xx++) {
                float distance = Vector2.dst(p.getX(), p.getY(), (getX() * 32 * 32) + xx * 32, (getY() * 32 * 32) + yy * 32);
                if (distance < Game.settings.getViewDistance() && distance > Game.settings.getViewDistance() * 0.9) {
                    float min = distance - Game.settings.getViewDistance() * 0.9f;
                    float max = Game.settings.getViewDistance() - Game.settings.getViewDistance() * 0.9f;
                    float closeness = min / max;
                    Game.batch.setColor(1, 1, 1, 1 - closeness);
                }
                if (distance < Game.settings.getViewDistance()) {
                    if (getTiles()[yy][xx] != -1) {
                        Tile.get(getTiles()[yy][xx]).draw((float) deltaTime, (getX() * 32 * 32) + xx * 32, (getY() * 32 * 32) + yy * 32);
                    }
                }
                Game.batch.setColor(1, 1, 1, 1);
            }
        }
    }

    public void addObject(WorldObject worldObject) {
        worldObjects.add(worldObject);
    }

    public void addObject(GameWorld world, int id, int x, int y) {
        WorldObject w = WorldObject.create(world, id, x, y);
        worldObjects.add(w);
        drawable.add(w);
    }

    public static void makeSample() {
//        for (int iy = 0; iy < 9; iy++) {
//            for (int ix = 0; ix < 9; ix++) {
//                Chunk chunk = new Chunk(ix - 4, iy - 4, null);
//                chunk.fillTile(1);
//                Json json = new Json();
//                String str = json.toJson(chunk);
//
//                FileHandle file = Gdx.files.local("worlds/name/chunks/" + chunk.getX() + "," + chunk.getY());
//                Util.createDirectory("worlds/name/chunks/" + chunk.getX() + "," + chunk.getY());
//                FileHandle fileHandler = new FileHandle("worlds/name/chunks/" + chunk.getX() + "," + chunk.getY() + "/tiles.json");
//                fileHandler.writeString(str, false);
//            }
//        }

    }

    public void fillTile(int tile_id) {
        for (int iy = 0; iy < 32; iy++) {
            for (int ix = 0; ix < 32; ix++) {
                this.tiles[iy][ix] = tile_id;
            }
        }
    }

    public void setClientControlling(String uId) {
        this.controlledClient = uId;
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public String getClientControlling() {
        return controlledClient;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the tiles
     */
    /**
     * @return the worldObjects
     */
    public ArrayList<WorldObject> getWorldObjects() {
        return worldObjects;
    }

    void updateWorldObject(WorldObject worldObject) {
        Iterator iterator = worldObjects.iterator();
        while (iterator.hasNext()) {
            Drawable temp = (Drawable) iterator.next();
            if (temp instanceof WorldObject) {
                WorldObject oldObject = (WorldObject) temp;
                if (oldObject.uId.equals(worldObject.uId)) {
                    iterator.remove();
                    System.out.println("Chunk removed object..");
                    break;
                }
            }
        }
        worldObjects.add(worldObject);
    }

    void generateUid() {
        for (WorldObject worldObject : worldObjects) {
            worldObject.generateUId();
        }
    }

    public void flagObjectsForRemoval() {
        this.toBeRemoved = true;
        for (WorldObject object : worldObjects) {
            object.toBeRemoved = true;
        }
    }

    public boolean isFlaggedForRemoval() {
        return toBeRemoved;
    }

    public int[][] getCollisionGrid() {
        if (collisionGrid == null) {
            collisionGrid = new int[32][32];
            for (WorldObject object : worldObjects) {
                collisionGrid[(int) (object.getX() - this.x * 32 * 32) / 32][(int) (object.getY() - this.y * 32 * 32) / 32] = 1;
            }
        }
        return collisionGrid;
    }

    public boolean solidAt(int x, int y) {
        int[][] grid = getCollisionGrid();
        return grid[y][x] == 1;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }
}
