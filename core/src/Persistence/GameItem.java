/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Chunk;
import com.mygdx.game.Drawable;
import com.mygdx.game.Game;
import com.mygdx.game.KJson;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is the general Item class. Like etc in MapleStory.
 *
 * @author kristian
 */
public class GameItem extends PersistenceFile implements ReportCreatable {

    private static ArrayList<GameItem> gameItems;
    protected static ArrayList<TextureRegion> itemTextures;
    protected static String path = "items/";
    protected static KJson JSON;
    protected ArrayList<Action> actions;
    protected int id;
    protected TextureRegion sprite;
    protected String name;
    protected String description;
    protected String fileName;
    private boolean bounce = true;
    protected int basePrice;
    protected int stackSize;

    public GameItem() {
        basePrice = 0;
        stackSize = 30;
        actions = new ArrayList();
    }

    public TextureRegion getTexture() {
        return sprite;
    }

    public void draw(int x, int y) {
        Game.batch.draw(Game.objectManager.getGameItem(this.id, false).getTexture(), x, y);
    }

    /**
     * @return the stackSize
     */
    public int getStackSize() {
        return stackSize;
    }

    @Override
    public String toString() {
        return name;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public ArrayList<? extends ReportCreatable> getAll() {
        return GameItem.gameItems;
    }

    /**
     * @return the bounce
     */
    public boolean doesBounce() {
        return bounce;
    }
}
