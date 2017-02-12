/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.Action;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Drawable;
import com.mygdx.game.Game;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Player;
import com.mygdx.game.WorldObject;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class WorldClickHandler {

    private OrthographicCamera camera;
    private GameWorld world;
    private Player player;
    private int x, y;
    private Skin skin;
    private Table table;
    private ClickHistory tileClickHistory;
    private Click draggedClick;
    private Click mouseLocation;

    public WorldClickHandler(Player player, OrthographicCamera camera, GameWorld world, Skin skin, Table table) {
        this.player = player;
        this.camera = camera;
        this.world = world;
        this.skin = skin;
        this.table = table;
    }

    public void rightClick(int x, int y) {
        if (player == null) {
            return;
        }
        int tileX = getTileX(x);
        int tileY = getTileY(y);

        ArrayList<Drawable> drawablesAtPosition = new ArrayList();

        for (Drawable drawable : world.getDrawable()) {
            int drawableTilePositionX = ((int) drawable.getX()) / 32;
            int drawableTilePositionY = ((int) drawable.getY()) / 32;
            if (drawableTilePositionX == tileX && drawableTilePositionY == tileY) {
                drawablesAtPosition.add(drawable);
            }
        }
        for (Drawable drawable : drawablesAtPosition) {
            if (drawable.getActions() == null) {
                continue;
            }
            for (Action action : drawable.getActions(player.getUId())) {
                TextButton textButton = new TextButton(action.getName() + " " + drawable.toString(), skin);
                textButton.addListener(new ActionClickListener(player, drawable, action));
                table.add(textButton);
                table.row();
            }
        }
        table.setPosition(x, Gdx.graphics.getHeight() - y);
    }

    public void leftClick(int x, int y) {
        if (player == null) {
            return;
        }
        int tileX = getTileX(x);
        int tileY = getTileY(y);
        tileClickHistory.addRelease(tileX, tileY);
        System.out.println("X " + tileX * 32 + " Y " + tileY * 32);
        if (table.getRows() > 0) {
            table.clear();
            return;
        }

        for (Drawable drawable : world.getDrawable()) {
            int drawableTilePositionX = ((int) drawable.getX()) / 32;
            int drawableTilePositionY = ((int) drawable.getY()) / 32;
            if (drawableTilePositionX == tileX && drawableTilePositionY == tileY) {
                if (drawable.getActions() == null || drawable.getActions().size() == 0 || drawable.getActions().get(0) == null) {
                    //if the object doesn't have any actions
                    continue;
                }
                if (drawable.getActions(player.getUId()).size() > 0) {
                    drawable.getActions(player.getUId()).get(0).initializeExecution(player, drawable);
                }
            }
        }
    }

    public void leftClickDown(int x, int y) {
        this.tileClickHistory.addClick(this.getTileX(x), this.getTileY(y));
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update(int x, int y) {
        mouseLocation.set(this.getWorldPositionX(x), this.getWorldPositionY(y));
    }

    public int getTileX(int x) {
        return (int) (getWorldPositionX(x) / 32);
    }

    public int getTileY(int y) {
        return (int) (getWorldPositionY(y) / 32);
    }

    public int getWorldPositionX(int x) {
        return (int) (x + camera.position.x - camera.viewportWidth / 2);
    }

    public int getWorldPositionY(int y) {
        return (int) (Math.abs(y - Gdx.graphics.getHeight()) + camera.position.y - camera.viewportHeight / 2);
    }

    public void draw() {

    }

    /**
     * @param tileClickHistory the tileClickHistory to set
     */
    public void setTileClickHistory(ClickHistory tileClickHistory) {
        this.tileClickHistory = tileClickHistory;
    }

    public void setDraggedClick(Click click) {
        this.draggedClick = click;
    }

    public void touchDragged(int screenX, int screenY) {
        this.draggedClick.set(this.getTileX(screenX), this.getTileY(screenY));
    }

    void setMousePositionObject(Click currentMouseLocation) {
        this.mouseLocation = currentMouseLocation;
    }
}
