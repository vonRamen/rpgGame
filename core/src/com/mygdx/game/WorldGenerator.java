/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameObject;
import Persistence.NoiseGenerator;
import Server.Point;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class WorldGenerator extends Thread {

    /**
     * Generates a new world.
     *
     * @param path can be null
     * @param sizeX size * 32 tiles
     * @param sizeY size * 32 tiles
     */
    public static void generateWorld(String path, int sizeX, int sizeY) {
        if (path == null) {
            path = "";
        }
        WorldGenerator worldGenerator = new WorldGenerator(sizeX, sizeY);
        worldGenerator.start();
        while (!worldGenerator.isFinished) {
            System.out.print(worldGenerator.getLog());
        }
        ArrayList<Chunk> chunksList = worldGenerator.getWorld();
        for (Chunk chunk : chunksList) {
            Json json = new Json();
            String str = json.toJson(chunk);

            FileHandle fileHandler = new FileHandle(path + "worlds/name/chunks/tiles" + chunk.getX() + " " + chunk.getY() + ".json");
            fileHandler.writeString(str, false);
        }
        System.out.println("World saved at path: " + path + "worlds/name");
        System.out.println("World done generating..");
    }

    private int w;
    private int h;
    private float[][] noise;
    private Random random = new Random();
    private NoiseGenerator noiseGenerator;
    private boolean isFinished;
    private ArrayList<Chunk> chunkList;
    private ArrayList<String> generationLog;
    private ArrayList<WorldObject> worldObjects;
    private int[][] tiles;

    public WorldGenerator(int w, int h) {
        generationLog = new ArrayList();
        this.h = h;
        this.w = w;
        log("Creating noise generator..");
        noiseGenerator = new NoiseGenerator(null, 1f, w * 32, h * 32);
    }

    public void run() {
        generate(w, h);
    }

    public void generate(int w, int h) {
        worldObjects = new ArrayList();
        tiles = new int[32 * h][32 * w];
        log("Generating world by the given noise");
        do {
            log("Initializing noise generator..");
            noiseGenerator.initialise();
            log("Getting noise generator..");
            noise = noiseGenerator.get();
            generateByNoise(tiles);
        } while (this.evaluate(2, 70f, 85f) != true);
        log("Placing water straight from corner to corner to test");
        generateLine(0, 0, 0, w*10*32, h*10*32, 4);
        
        //places 10 percent trees randomly:
        placeByPercentage(0, 5f, 2);
        placeByPercentage(2, 2f, 2, 3);
        
        
        //what do you wanna do?
        //placeRandomSquare(worldObjects, 0, 0, 32, 32, 0, 100);
        //placeRandomSquare(worldObjects, 32, 32, 32, 32, 0, 100);
        //placeTileSquare(tiles, 1, 0, 0, 32, 32);
        chunkList = this.finalize(tiles, worldObjects); // finalize by filling the chunk list with stuff
    }

    private void placeTileCircle(int[][] tileMap, int id, int x0, int y0, int r) {
        int x = r;
        int y = 0;
        int err = 0;

        while (r >= y) {
            tileMap[y0 + y][x0 + r] = id;
            tileMap[y0 + r][x0 + y] = id;
            tileMap[y0 + r][x0 - y] = id;
            tileMap[y0 + y][x0 - r] = id;
            tileMap[y0 - y][x0 - r] = id;
            tileMap[y0 - r][x0 - y] = id;
            tileMap[y0 - r][x0 + y] = id;
            tileMap[y0 - y][x0 + r] = id;

            if (err <= 0) {
                y += 1;
                err += 2 * y + 1;
            }
            if (err > 0) {
                r -= 1;
                err -= 2 * r + 1;
            }
        }
    }

    private static void placeTileSquare(int[][] tileMap, int id, int x, int y, int w, int h) {
        for (int yy = y; yy < h + y; yy++) {
            for (int xx = y; xx < w + h; xx++) {
                tileMap[yy][xx] = id;
            }
        }
    }

    private void placeRandomSquare(ArrayList<WorldObject> objectsList, int x, int y, int w, int h, int objectId, int total) {
        for (int i = 0; i < total; i++) {
            int randX = random.nextInt(w);
            int randY = random.nextInt(h);
            int posX = ((randX) + x);
            int posY = ((randY) + y);
            System.out.println(posX);

            WorldObject newObject = null;
            boolean o = objectAtPosition(objectsList, posX * 32, posY * 32);
            if (o == true) {
                i--;
            } else {
                newObject = new WorldObject(objectId, x, y);
            }

            //place in list:
            if (newObject != null) {
                WorldObject temp = new WorldObject(objectId, posX * 32, posY * 32);
                objectsList.add(temp);
            }
        }
    }
    
    private void placeByPercentage(int id, float percentage, int... tile) {
        float maxObjects = (((float) (w*32*h*32))/100)*percentage;
        log("Placing "+maxObjects+" of the id: "+id+" in the world..");
        int count = 0;
        while(count < maxObjects) {
            int randomX = random.nextInt(w*32);
            int randomY = random.nextInt(h*32);
            if(!objectAtPosition(worldObjects, randomX*32, randomY*32)) {
                boolean mayPlace = false;
                for(int integer : tile) {
                    if(integer == tiles[randomY][randomX]) {
                        mayPlace = true;
                        break;
                    }
                }
                if(mayPlace) {
                    WorldObject temp = new WorldObject(id, randomX * 32, randomY * 32);
                    worldObjects.add(temp);
                    count++;
                }
            }
        }
        System.out.println("Trees planted: "+count);
    }
    
    private void generateLine(int id, int x0, int y0, int x1, int y1, int size) {
        Point point1 = new Point(x0, y0);
        Point point2 = new Point(x1, y1);
        double distance = diagonalDistance(point1, point2);
        int count = 0;
        for (int step = 0; step <= distance; step++) {
            double t = distance == 0? 0.0 : step / distance;
            Point p = lerpPoint(point1, point2, t);
            generateSquare(id, p.getX(), p.getY(), size, size);
            count+=size;
        }
        log("Placed aprox: "+count+" of the id: "+id);
    }
    
    private double diagonalDistance(Point p0, Point p1) {
        double dx = p1.getX() - p0.getX(), dy = p1.getY() - p1.getY();
        return Math.max(dx, dy);
    }
    
    private Point lerpPoint(Point p0, Point p1, double t) {
        return new Point(lerp(p0.getX(), p1.getX(), t),
                         lerp(p0.getY(), p1.getY(), t));
    }
    
    private double lerp(double start, double end, double t) {
        return start + t * (end-start);
    }
    
    private void generateSquare(int id, int x, int y, int sizeX, int sizeY) {
        for(int iy = 0; iy < sizeY; iy++) {
            for(int ix = 0; ix < sizeX; ix++) {
                try {
                    tiles[iy+y][ix+x] = id;
                } catch (IndexOutOfBoundsException e) {
                    
                }
            }
        }
    }

    private static boolean objectAtPosition(ArrayList<WorldObject> objectsList, int x, int y) {
        for (WorldObject object : objectsList) {
            if (object.x == x && object.y == y) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Chunk> finalize(int[][] tiles, ArrayList<WorldObject> worldObjects) {
        ArrayList<Chunk> chunks = new ArrayList();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Chunk chunk = new Chunk(x, y, null);
                //int[][] chunkTiles = new int[32][32];
                //iterate through that part of the tiles list
                for (int iy = 0; iy < 32; iy++) {
                    for (int ix = 0; ix < 32; ix++) {
                        chunk.getTiles()[iy][ix] = tiles[iy + y * 32][ix + x * 32];
                    }
                }

                //set objects
                for (WorldObject worldObject : worldObjects) {
                    if (worldObject.x >= x * 32 * 32 && worldObject.x <= (x + 1) * 32 * 32
                            && worldObject.y >= y * 32 * 32 && worldObject.y <= (y + 1) * 32 * 32) {
                        worldObject.rectangle = null;
                        worldObject.uId = null;
                        chunk.addObject(worldObject);
                    }
                }
                chunks.add(chunk);
            }
        }
        this.isFinished = true;
        return chunks;
    }

    private void generateByNoise(int[][] tiles) {
        log("Generating tile placement..");
        for (int iy = 0; iy < h * 32; iy++) {
            for (int ix = 0; ix < w * 32; ix++) {
                if (noise[ix][iy] > 0.1) {
                    tiles[iy][ix] = 2;
                } else if (noise[ix][iy] < 0.1 && noise[ix][iy] > 0.05) {
                    tiles[iy][ix] = 3;
                } else {
                    tiles[iy][ix] = 0;
                }
            }
        }
    }

    private boolean evaluate(int tileID, float minPercentage, float maxPercentage) {
        log("Evaluating size..");
        log("Percentage: " + ((float) countTile(tileID) / (float) totalTiles() * 100));
        return ((float) countTile(tileID) / (float) totalTiles() * 100 > minPercentage) && ((float) countTile(tileID) / (float) totalTiles() * 100 < maxPercentage);
    }

    private int countTile(int tileID) {
        int count = 0;
        for (int[] tile : tiles) {
            for (int ix = 0; ix < tiles.length; ix++) {
                if (tile[ix] == tileID) {
                    count++;
                }
            }
        }
        return count;
    }

    private int totalTiles() {
        return (32 * w) * (32 * h);
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public ArrayList<Chunk> getWorld() {
        return chunkList;
    }

    public void log(String string) {
        generationLog.add(string);
    }

    public String getLog() {
        if (generationLog.size() < 1) {
            return "";
        }
        String returnString = generationLog.get(0);
        generationLog.remove(0);
        return "Generating: " + returnString + "\n";
    }
}
