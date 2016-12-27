/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author kristian
 */
public class WorldGenerator {

    private static Random random = new Random();
    private static int w;
    private static int h;

    public static ArrayList<Chunk> generate(int w, int h) {
        WorldGenerator.h = h;
        WorldGenerator.w = w;
        ArrayList<WorldObject> worldObjects = new ArrayList();
        int[][] tiles = new int[32 * h][32 * w];

        //what do you wanna do?
        placeRandomSquare(worldObjects, 0, 0, 32, 32, 0, 100);
        placeRandomSquare(worldObjects, 32, 32, 32, 32, 0, 100);
        placeTileSquare(tiles, 1, 0, 0, 32, 32);

        return finalize(tiles, worldObjects);
    }

    private static void placeTileCircle(int[][] tileMap, int id, int x0, int y0, int r) {
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

    private static void placeRandomSquare(ArrayList<WorldObject> objectsList, int x, int y, int w, int h, int objectId, int total) {
        for (int i = 0; i < total; i++) {
            int randX = random.nextInt(w);
            int randY = random.nextInt(h);
            int posX = ((randX)+x);
            int posY = ((randY)+y);
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

    private static boolean objectAtPosition(ArrayList<WorldObject> objectsList, int x, int y) {
        for (WorldObject object : objectsList) {
            if (object.x == x && object.y == y) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<Chunk> finalize(int[][] tiles, ArrayList<WorldObject> worldObjects) {
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
        return chunks;
    }
}
