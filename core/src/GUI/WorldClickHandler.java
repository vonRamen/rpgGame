/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.Action;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.Drawable;
import com.mygdx.game.Game;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Player;

/**
 *
 * @author kristian
 */
public class WorldClickHandler {

    private OrthographicCamera camera;
    private GameWorld world;
    private Player player;
    private int x, y;
    private Stage stage;
    private Skin skin;
    private Table table;

    public WorldClickHandler(Player player, OrthographicCamera camera, GameWorld world, Skin skin, Stage stage) {
        this.player = player;
        this.camera = camera;
        this.world = world;
        this.stage = stage;
        this.skin = skin;
        this.table = new Table();
        stage.addActor(table);
    }

    public void rightClick(int x, int y) {
        int tileX = getTileX(x);
        int tileY = getTileY(y);
        System.out.println("X " + tileX * 32 + " Y " + tileY * 32);

        for (Drawable drawable : world.getDrawable()) {
            int drawableTilePositionX = ((int) drawable.getX()) / 32;
            int drawableTilePositionY = ((int) drawable.getY()) / 32;
            if (drawableTilePositionX == tileX && drawableTilePositionY == tileY) {
                table.clear();
                if (drawable.getActions() == null) {
                    return;
                }
                for (Action action : drawable.getActions()) {
                    TextButton textButton = new TextButton(action.getName(), skin);
                    table.add(textButton);
                    table.row();
                }
                table.setPosition(x, Gdx.graphics.getHeight() - y);
            }
        }
    }

    public void leftClick(int x, int y) {
        int tileX = getTileX(x);
        int tileY = getTileY(y);
        System.out.println("X " + tileX * 32 + " Y " + tileY * 32);
        if (table.getRows() > 0) {
            table.clear();
            return;
        }

        for (Drawable drawable : world.getDrawable()) {
            int drawableTilePositionX = ((int) drawable.getX()) / 32;
            int drawableTilePositionY = ((int) drawable.getY()) / 32;
            if (drawableTilePositionX == tileX && drawableTilePositionY == tileY) {
                if(drawable.getActions() == null || drawable.getActions().size() == 0 || drawable.getActions().get(0) == null) {
                    //if the object doesn't have any actions
                    return;
                }
                drawable.getActions().get(0).initializeExecution(player, drawable);
            }
        }
    }

    public void update() {

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
}
