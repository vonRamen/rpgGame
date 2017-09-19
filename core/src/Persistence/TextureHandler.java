/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.TreeMap;

/**
 *
 * @author Kristian
 */
public class TextureHandler {

    private TreeMap<String, Texture> textures;

    public TextureHandler(String directory) {
        this.textures = new TreeMap();
        for (FileHandle file : new FileLoader(directory).getFiles()) {
            textures.put(file.name(), new Texture(file));
        }
    }

    public TextureRegion generateRegion(String filename, int x, int y, int w, int h) {
        return new TextureRegion(textures.get(filename), x, y, w, h);
    }
    
    public Texture getTexture(String filename) {
        return textures.get(filename);
    }
}
