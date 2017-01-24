/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.Sound2D;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
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
    private boolean combatMode;

    //Menu you get, when you rightclick on something
    private Table rightClickMenu;

    public GUIStage(GameWorld world, OrthographicCamera camera, Player player, Skin skin, Client client) {
        super();
        this.client = client;
        this.world = world;
        multiPlexer = new InputMultiplexer();
        multiPlexer.addProcessor(this);
        isDrawing = true;
        this.skin = skin;
        this.player = player;
        this.rightClickMenu = new Table();
        this.rightClickMenu.setZIndex(1);
        this.worldClickHandler = new WorldClickHandler(player, camera, world, skin, rightClickMenu);
        this.addActor(rightClickMenu);
        toggleInventory();
        Gdx.input.setInputProcessor(multiPlexer);
        this.camera = (OrthographicCamera) this.getViewport().getCamera();
        //this.addActor(new MainGUI(skin));
    }

    public void act() {
        this.camera = (OrthographicCamera) this.getViewport().getCamera();
        super.act();
        worldClickHandler.update();
        if (isDrawing) {
        }
        if (player.hasUpdatedInventory()) {
            this.inventory.update();
            player.setInventoryUpdate(false);
        }
        inventory.getGroup().setPosition(camera.position.x, camera.position.y);
    }

    public void toggleInventory() {
        if (player != null && inventory == null) {
            inventory = new GUIInventory(player, skin, rightClickMenu);
            Group invGroup = inventory.getGroup();
            this.addActor(invGroup);
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
        if (keycode == Input.Keys.D || keycode == Input.Keys.W || keycode == Input.Keys.A || keycode == Input.Keys.S) {
            rightClickMenu.clear();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        if (keycode == Input.Keys.I) {
            toggleInventory();
        }
        if (keycode == Input.Keys.Q) {
            combatMode = !combatMode;
        }
        if (keycode == Input.Keys.P) {
            this.isDrawing = !this.isDrawing;
            System.out.println("Drawing off.");
        }
        if (keycode == Input.Keys.Z) {
            if (inventory != null) {
                this.player.pickup();
                this.inventory.update();
            }
        }
        if (keycode == Input.Keys.M) {
            Sound2D.playMusic("What about this.mp3");
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
            if (combatMode) {
                player.attack();
                try {
                    Player playerCopy = (Player) player.clone();
                    playerCopy.removeNonSimpleTypes();
                    client.sendTCP(playerCopy);
                } catch (CloneNotSupportedException ex) {
                    System.out.println(ex);
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        switch (button) {
            case Input.Buttons.LEFT:
                worldClickHandler.leftClick(screenX, screenY);
                break;

            case Input.Buttons.RIGHT:
                worldClickHandler.rightClick(screenX, screenY);
                break;

            default:
                break;
        }
        System.out.println(player.getAlert());
        this.rightClickMenu.setPosition(this.rightClickMenu.getX() + camera.position.x - this.getViewport().getScreenWidth() / 2, this.rightClickMenu.getY() + camera.position.y - this.getViewport().getScreenHeight() / 2);
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

    @Override
    public void draw() {
        super.draw();
        worldClickHandler.draw();
    }

    public Skin getSkin() {
        return skin;
    }
}
