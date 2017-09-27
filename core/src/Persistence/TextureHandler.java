/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author Kristian
 */
public class TextureHandler {

    private TreeMap<String, Texture> textures;
    private Random random;

    public TextureHandler(String directory) {
        this.textures = new TreeMap();
        this.random = new Random();
        for (FileHandle file : new FileLoader(directory).getFiles()) {
            textures.put(file.name(), new Texture(file));
        }
    }

    /**
     * Generates and returns 4 animations for each direction stores in an array.
     *
     * @param filename
     * @param xFrames
     * @param speed
     * @return 4 animations for each direction
     */
    public Animation[] generateDirectionalAnimation(String filename, int xFrames, float speed) {
        TextureRegion[][] region = TextureRegion.split(this.getTexture(filename), this.getTexture(filename).getWidth() / xFrames, this.getTexture(filename).getHeight() / 4);

        Animation[] animations = new Animation[]{new Animation(speed, region[0]),
            new Animation(speed, region[1]),
            new Animation(speed, region[2]),
            new Animation(speed, region[3])};

        return animations;
    }

    /**
     * Generates and returns a one-strip animation
     *
     * @param filename
     * @param xFrames
     * @param speed
     * @return one-strip animation
     */
    public Animation generateAnimation(String filename, int xFrames, float speed) {
        TextureRegion[][] region = TextureRegion.split(this.getTexture(filename), this.getTexture(filename).getWidth() / xFrames, this.getTexture(filename).getHeight());
        Animation animation = new Animation(speed, region[0]);
        return animation;
    }

    public TextureRegion generateRandomRegion(String filename, Region... regions) {
        Region region = regions[this.random.nextInt(regions.length)];
        return generateRegion(filename, region);
    }

    public TextureRegion generateRegion(String filename, Region region) {
        return new TextureRegion(textures.get(filename), region.x, region.y, region.w, region.h);
    }
    
    public TextureRegion generateRegion(String filename, int x, int y, int w, int h) {
        return new TextureRegion(textures.get(filename), x, y, w, h);
    }

    public Texture getTexture(String filename) {
        return textures.get(filename);
    }
    
    public TextureRegionDrawable getDrawableTextureRegion(String filename, Region region) {
        return new TextureRegionDrawable(this.generateRegion(filename, region));
    }
    
    public TextureRegionDrawable getCombinedDrawableTextureRegion(String filename, Region region1, Region region2) {
        return null;
    }

    public static class Region {

        int x, y, w, h;

        public Region(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
