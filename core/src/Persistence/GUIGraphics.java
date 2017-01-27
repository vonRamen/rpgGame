/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.HashMap;

/**
 *
 * @author kristian
 */
public class GUIGraphics {

    private static HashMap<String, TextureRegionDrawable> images;
    private static String path = "gui/";

    public static void load() {
        images = new HashMap();
        FileHandle files = new FileHandle(path);

        for (FileHandle file : files.list()) {
            Texture texture = new Texture(file);
            TextureRegion textRegion = new TextureRegion(texture);
            images.put(file.name(), new TextureRegionDrawable(textRegion));
        }
    }

    public static TextureRegionDrawable get(String name) {
        return images.get(name);
    }
    
    public static Pixmap getPixmap(String name) {
        TextureRegion region = images.get(name).getRegion();
        if(!region.getTexture().getTextureData().isPrepared()) {
            region.getTexture().getTextureData().prepare();
        }
        return region.getTexture().getTextureData().consumePixmap();
    }
}
