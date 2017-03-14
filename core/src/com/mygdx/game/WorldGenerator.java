/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameObject;
import Persistence.NoiseGenerator;
import Persistence.Tile;
import Server.MPServer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

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
     * @return
     */
    public static WorldGenerator generateWorld(String path, int sizeX, int sizeY) {
        if (path == null) {
            path = "";
        }
        WorldGenerator worldGenerator = new WorldGenerator(sizeX, sizeY);
        return worldGenerator;
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
    private MPServer logDump;
    private Texture previewImage;

    public WorldGenerator(int w, int h) {
        generationLog = new ArrayList();
        this.h = h;
        this.w = w;
        log("Creating noise generator..");
        noiseGenerator = new NoiseGenerator(null, 1f, w * 32, h * 32);
    }

    public void run() {
        generate(null);
    }

    /**
     *
     * @param logDump can be null
     */
    public void generate(MPServer logDump) {
        this.logDump = logDump;
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
        int iteration = h * 32 - w;
        int variation = 0;
        int yDir = random.nextBoolean() ? 1 : 1;

        this.log("Corruptifies the world");

        while (this.getPercent(4) < 15) {
            generateLine(4, 0+(w*32) / (variation+1), iteration + variation * yDir, w * 32, iteration + variation / 2 * (-yDir), w + variation / 4 * yDir, 2);
            variation += h / 2;
            iteration -= h / 2;
        }
        this.log("Humidifes the world");
        generateLine(0, 0, 0, w * 10 * 32, h * 10 * 32, 4, 2, 3);

        //places 10 percent trees randomly:
        this.log("Plants trees");
        placeByPercentage(0, 5f, 2);
        this.log("Plants some boulders");
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
            boolean o = objectAtPosition(posX * 32, posY * 32);
            if (o == true) {
                i--;
            } else {
                newObject = new WorldObject(null, objectId, x, y);
            }

            //place in list:
            if (newObject != null) {
                WorldObject temp = new WorldObject(null, objectId, posX * 32, posY * 32);
                objectsList.add(temp);
            }
        }
    }

    private void placeByPercentage(int id, float percentage, int... tile) {
        float maxObjects = (((float) (w * 32 * h * 32)) / 100) * percentage;
        log("Placing " + maxObjects + " of the id: " + id + " in the world..");
        int count = 0;
        while (count < maxObjects) {
            int randomX = random.nextInt(w * 32);
            int randomY = random.nextInt(h * 32);
            if (!objectAtPosition(randomX * 32, randomY * 32)) {
                boolean mayPlace = false;
                for (int integer : tile) {
                    if (integer == tiles[randomY][randomX]) {
                        mayPlace = true;
                        break;
                    }
                }
                if (mayPlace) {
                    WorldObject temp = new WorldObject(null, id, randomX * 32, randomY * 32);
                    worldObjects.add(temp);
                    count++;
                }
            }
            if (count % 1000 == 0) {
                log(count + " placed of id: " + id);
            }
        }
    }

    private void generateLine(int id, int x0, int y0, int x1, int y1, int size, int... tilesWhiteList) {
        Point point1 = new Point(x0, y0);
        Point point2 = new Point(x1, y1);
        double distance = diagonalDistance(point1, point2);
        int count = 0;
        for (int step = 0; step <= distance; step++) {
            double t = distance == 0 ? 0.0 : step / distance;
            Point p = lerpPoint(point1, point2, t);
            generateSquare(id, p.getX(), p.getY(), size, size, tilesWhiteList);
            count += size;
        }
        log("Placed aprox: " + count + " of the id: " + id);
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
        return start + t * (end - start);
    }

    private void generateSquare(int id, int x, int y, int sizeX, int sizeY, int... tilesWhiteList) {
        for (int iy = 0; iy < sizeY; iy++) {
            for (int ix = 0; ix < sizeX; ix++) {
                try {
                    boolean allowed = false;
                    for (int i : tilesWhiteList) {
                        if (i == tiles[iy + y][ix + x]) {
                            allowed = true;
                            break;
                        }
                    }
                    if (allowed == true) {
                        tiles[iy + y][ix + x] = id;
                    }
                } catch (IndexOutOfBoundsException e) {

                }
            }
        }
    }

    private boolean objectAtPosition(int x, int y) {
        for (WorldObject object : worldObjects) {
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
                    if (((int) worldObject.x / (32 * 32)) == x
                            && ((int) worldObject.y / (32 * 32) == y)) {
                        worldObject.rectangle = null;
                        worldObject.uId = null;
                        chunk.addObject(worldObject);
                    }
                }
                chunks.add(chunk);
            }
        }
        this.log("Done Generating The World");
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

    private float getPercent(int tileID) {
        return ((float) countTile(tileID) / (float) totalTiles() * 100);
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
        if (this.logDump != null) {
            this.logDump.setLog("Generating World..: " + string);
        }
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

    public void generateImage() {
        Collections.sort(this.worldObjects, new DepthComparator());
        BufferedImage image = new BufferedImage(w * 32 * 32, h * 32 * 32, BufferedImage.TYPE_INT_ARGB);
        Pixmap newPix = new Pixmap(w * 32 * 32, h * 32 * 32, Pixmap.Format.RGBA8888);
        TextureRegion tile = Tile.get(tiles[0][0]).getTileTextureRegion();
        tile.getTexture().getTextureData().prepare();
        Pixmap tileMap = tile.getTexture().getTextureData().consumePixmap();
        for (int y = 0; y < this.h * 32; y++) {
            for (int x = 0; x < this.w * 32; x++) {
                for (int tileY = 0; tileY < 32; tileY++) {
                    for (int tileX = 0; tileX < 32; tileX++) {
                        int c = tileMap.getPixel(tileX + (tiles[y][x] % 16) * 32 + 32, tileY + (tiles[y][x] / 16) * 32);
                        newPix.drawPixel(x * 32 + tileX, (h * 32 * 32) - y * 32 + tileY - 32, c);
                    }
                }
            }
        }

        Texture objectSprite = GameObject.getSprite(0).getTexture();
        objectSprite.getTextureData().prepare();
        Pixmap objMap = objectSprite.getTextureData().consumePixmap();
        for (WorldObject object : this.worldObjects) {
            int objPositionX = (int) object.getX();
            int objPositionY = (int) object.getY();
            for (SpriteRelative rel : GameObject.get(object.id).getSprites()) {
                for (int y = 0; y < 32; y++) {
                    for (int x = 0; x < 32; x++) {
                        int c = objMap.getPixel(x + (rel.getTextureId() % 20) * 32, y + (rel.getTextureId() / 20) * 32);
                        Color color = new Color(c);
                        if (color.a == 0) {
                            continue;
                        }
                        newPix.drawPixel(x + objPositionX + rel.getxRelative(), (h * 32 * 32) - objPositionY + y - rel.getyRelative(), c
                        );
                    }
                }
            }
        }
        FileHandle fileH = Gdx.files.local("worldMap.png");
        PixmapIO.writePNG(fileH, newPix);
        fileH.write(true);
    }

    public void saveWorld(String path) {
        for (Chunk chunk : chunkList) {
            Json json = new Json();
            String str = json.toJson(chunk);

            FileHandle fileHandler = new FileHandle(path + "chunks/tiles" + chunk.getX() + " " + chunk.getY() + ".json");
            fileHandler.writeString(str, false);
        }
    }

    private void generatePreview() {
        Pixmap previewImage = new Pixmap(w * 32, h * 32, Pixmap.Format.RGBA8888);

        //Generate image.
        TextureRegion tile = Tile.get(tiles[0][0]).getTileTextureRegion();
        tile.getTexture().getTextureData().prepare();
        Pixmap tileMap = tile.getTexture().getTextureData().consumePixmap();
        for (int y = 0; y < h * 32; y++) {
            for (int x = 0; x < w * 32; x++) {
                int c = tileMap.getPixel(tiles[y][x] * 32 + 32, 0);
                previewImage.drawPixel(x, (h*32) - y + 1, c);
            }
        }
        this.previewImage = new Texture(previewImage);
    }
    
    public Texture getPreview() {
        if(this.previewImage == null) {
            this.generatePreview();
        }
        return this.previewImage;
    }
}
