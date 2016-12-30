/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.EntitySimpleType;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Player;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class GUIStage extends Stage {

    private Player player;
    private Skin skin;
    private GUIInventory inventory;
    private OrthographicCamera camera;
    private boolean isDrawing;
    private InputMultiplexer multiPlexer;
    private GameWorld world;
    private Client client;
    private Label fpsLabel;
    private Table debugLayout;
    private WorldClickHandler worldClickHandler;

    public GUIStage(GameWorld world, OrthographicCamera camera, Player player, Skin skin, Client client) {
        super();
        this.client = client;
        this.world = world;
        multiPlexer = new InputMultiplexer();
        multiPlexer.addProcessor(this);
        isDrawing = true;
        this.skin = skin;
        this.player = player;
        this.worldClickHandler = new WorldClickHandler(player, camera, world);
        Gdx.input.setInputProcessor(multiPlexer);

        //this.addActor(new MainGUI(skin));
    }

    public void act() {
        super.act();
        if (isDrawing) {
        }
    }

    public void toggleInventory() {
        if (player != null && inventory == null) {
            inventory = new GUIInventory(player, skin);
            this.addActor(inventory.getGroup());
        } else {
            inventory.toggleVisibility();
            if (this.getActors().size < 1) {
                this.addActor(inventory.getGroup());
            }
        }
    }

    public void toggleCharacter() {

    }

    @Override
    public boolean keyDown(int keycode) {
        super.keyDown(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        if (keycode == Input.Keys.I) {
            toggleInventory();
        }
        if (keycode == Input.Keys.D) {
            this.isDrawing = !this.isDrawing;
            System.out.println("Drawing off.");
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        super.keyTyped(character);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);
        if (player != null) {
            player.attack();
            try {
                Player playerCopy = (Player) player.clone();
                playerCopy.removeNonSimpleTypes();
                client.sendTCP(playerCopy);
            } catch (CloneNotSupportedException ex) {
                System.out.println(ex);
            }

        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        worldClickHandler.click(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        super.mouseMoved(screenX, screenY);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        super.scrolled(amount);
        return true;
    }

}
