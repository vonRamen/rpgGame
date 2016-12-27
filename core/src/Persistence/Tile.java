/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.FileHandler;

/**
 *
 * @author kristian
 */
public class Tile {

    private static String path = "tiles/standardTileSet.png";
    private static ArrayList<TextureRegion> sprites = new ArrayList();

    public Tile() {
    }

    public static void load() {
        Texture texture = new Texture(path);
        int xx = (int) (texture.getWidth() / 32);
        int yy = (int) (texture.getHeight() / 32);

        for (int iy = 0; iy < yy; iy++) {
            for (int ix = 0; ix < xx; ix++) {
                TextureRegion textureR = new TextureRegion(texture, ix * 32, iy * 32, 32, 32);
                sprites.add(textureR);
            }
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

    public static TextureRegion get(int id) {
        if (sprites.isEmpty()) {
            load();
        }
        return sprites.get(id);
    }

    public static int getSize() {
        return sprites.size();
    }

    public static String getPath() {
        return path;
    }
}
