/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import static Persistence.GameItem.path;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Game;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.FileHandler;

/**
 *
 * @author kristian
 */
public class Tile implements ReportCreatable {

    private static String path = "tiles/";
    private static ArrayList<TextureRegion> sprites = new ArrayList();
    private static TreeMap<Integer, Tile> tiles;
    private Animation spriteAnimation;
    private ArrayList<Integer> animationIds;
    private float animationSpeed;
    private int id;
    private String fileName;
    private String name;
    private float currentFrame;

    public Tile() {
    }
    
    public void initialize() {
        TextureRegion[] sprites = new TextureRegion[animationIds.size()];
        int count = 0;
        for(Integer integer : animationIds) {
            sprites[count] = Tile.sprites.get(integer);
            count++;
        }
        spriteAnimation = new Animation(animationSpeed, sprites);
        spriteAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public static void load() {
        Texture texture = new Texture(path+"standardTileSet.png");
        tiles = new TreeMap();
        int xx = (int) (texture.getWidth() / 32);
        int yy = (int) (texture.getHeight() / 32);

        for (int iy = 0; iy < yy; iy++) {
            for (int ix = 0; ix < xx; ix++) {
                TextureRegion textureR = new TextureRegion(texture, ix * 32, iy * 32, 32, 32);
                sprites.add(textureR);
            }
        }
        
        //Load data from data folder
        FileHandle dirHandle = Gdx.files.internal(path+"data/");
        System.out.println("Files in data: "+dirHandle.list().length);
        Json json = new Json();
        for(FileHandle file : dirHandle.list()) {
            Tile tile = json.fromJson(Tile.class, file);
            tile.initialize();
            tile.name = file.nameWithoutExtension();
            tile.fileName = file.name();
            tiles.put(tile.id, tile);
        }
    }

    public static File getFileOfTile(int id) {
        TextureRegion textureRegion = sprites.get(id);
        Texture texture = textureRegion.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }

        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap pixToDraw = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        System.out.println("Here");
        for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
                int colorInt = pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y);
                pixToDraw.setColor(colorInt);
                pixToDraw.drawPixel(x, y);
                // you could now draw that color at (x, y) of another pixmap of the size (regionWidth, regionHeight)
            }
        }
        FileHandle fileH = Gdx.files.local("temp.png");
        PixmapIO.writePNG(fileH, pixToDraw);
        fileH.write(true);
        return fileH.file();
    }

    public static TextureRegion getTextureRegionBySpace(int id) {
        if (sprites.isEmpty()) {
            load();
        }
        return sprites.get(id);
    }
    
    public static Tile get(int id) {
        return tiles.get(id);
    }

    public static int getSize() {
        return sprites.size();
    }

    public static String getPath() {
        return path;
    }
    
    public void draw(float deltaTime, int x, int y) {
        Game.batch.draw(spriteAnimation.getKeyFrame(deltaTime), x, y);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        if(this.name == null) {
            return "No name";
        }
        return this.name;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public ArrayList<? extends ReportCreatable> getAll() {
        return new Utility.KUtility<Tile>().getArrayListOfMap(Tile.tiles);
    }
}
