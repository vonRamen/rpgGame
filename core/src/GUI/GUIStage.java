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
import com.mygdx.game.Packets;
import com.mygdx.game.Player;
import java.util.ArrayList;
import java.util.Iterator;
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
    private int lastX;
    private int lastY; //makes sure a new movement has been entered.
    private ArrayList<FloatingMessage> messages;

    //Menu you get, when you rightclick on something
    private Table rightClickMenu;

    public GUIStage(GameWorld world, OrthographicCamera camera, Player player, Client client) {
        super();
        this.lastX = 0;
        this.lastY = 0;
        this.client = client;
        this.world = world;
        multiPlexer = new InputMultiplexer();
        multiPlexer.addProcessor(this);
        isDrawing = true;
        skin = new Skin(Gdx.files.internal("gui skins/uiskin.json"));
        this.player = player;
        this.rightClickMenu = new Table();
        this.rightClickMenu.setZIndex(1);
        this.worldClickHandler = new WorldClickHandler(player, camera, world, skin, rightClickMenu);
        this.addActor(rightClickMenu);
        Gdx.input.setInputProcessor(multiPlexer);
        this.camera = camera;
        this.messages = new ArrayList();
        //this.addActor(new MainGUI(skin));
    }

    @Override
    public void act() {
        if (player != null) {

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
            if (!player.isIsDead()) {
                inputHandling();
            }
        } else {
            //See if world has a player to control..
            this.player = this.world.getPlayer();

            //Toggle inventory if a player is found!
            if (this.player != null) {
                this.toggleInventory();
                this.worldClickHandler.setPlayer(player);
            }
        }
        for (FloatingMessage message : messages) {
            message.act(world.getDeltaTime());
        }
        //remove the messages run out of time
        Iterator iterator = messages.iterator();
        while (iterator.hasNext()) {
            FloatingMessage message = (FloatingMessage) iterator.next();
            for (Actor actor : this.getActors()) {
                if (actor == message) {
                    if (((FloatingMessage) actor).alpha <= 0) {
                        actor.remove();
                    }
                }
            }
            if (message.alpha <= 0) {
                iterator.remove();
            }
        }
    }

    public void toggleInventory() {
        if (this.inventory == null) {
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
        //Reset right click menu
        if (button == Input.Buttons.RIGHT) {
            this.rightClickMenu.clear();
        }

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
        if (player != null) {
            String alert = player.getAlert();
            if (alert != null) {
                FloatingMessage message = new FloatingMessage(this, this.skin, screenX + camera.position.x - this.getViewport().getScreenWidth() / 2, (this.getViewport().getScreenHeight() - screenY) + camera.position.y - this.getViewport().getScreenHeight() / 2, alert);
                message.setZIndex(pointer);
                this.addActor(message);
                this.messages.add(message);
            }
            this.rightClickMenu.setPosition(this.rightClickMenu.getX() + camera.position.x - this.getViewport().getScreenWidth() / 2, this.rightClickMenu.getY() + camera.position.y - this.getViewport().getScreenHeight() / 2);
        }
        fixButtonSizes(rightClickMenu);
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

    public void inputHandling() {
        if (player != null) {
            int hAxis = ((Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0) - (Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0));
            int vAxis = ((Gdx.input.isKeyPressed(Input.Keys.W) ? 1 : 0) - (Gdx.input.isKeyPressed(Input.Keys.S) ? 1 : 0));

            player.changeX = hAxis;
            player.changeY = vAxis;

            if ((lastX != hAxis || lastY != vAxis) && (hAxis != 0 || vAxis != 0)) { //send update
                Packets.BeginMovement movementUpdate = new Packets.BeginMovement();
                movementUpdate.entity = new EntitySimpleType();
                movementUpdate.entity.changeX = hAxis;
                movementUpdate.entity.changeY = vAxis;
                movementUpdate.entity.setX(player.getX());
                movementUpdate.entity.setY(player.getY());
                movementUpdate.entity.setUid(player.getUId());
                client.sendTCP(movementUpdate);

            } else if ((hAxis == 0 && vAxis == 0) && (lastX != 0 || lastY != 0)) {
                Packets.EndMovement movementUpdate = new Packets.EndMovement();
                movementUpdate.entity = new EntitySimpleType();
                movementUpdate.entity.setUid(player.getUId());
                movementUpdate.entity.changeX = hAxis;
                movementUpdate.entity.changeY = vAxis;
                movementUpdate.entity.setX(player.getX());
                movementUpdate.entity.setY(player.getY());
                client.sendTCP(movementUpdate);
            }
            if ((hAxis != 0 || vAxis != 0)) { //if the player is moving.
                //Check chunk position
                player.setChunkX((int) player.getX() / (32 * 32));
                player.setChunkY((int) player.getY() / (32 * 32));
                if (player.getChunkX() != player.getChunkXLastOn() || player.getChunkY() != player.getChunkYLastOn()) {
                    //Send package request of next chunks.
                    Packets.requestChunks chunkRequest = new Packets.requestChunks();
                    chunkRequest.entity = new EntitySimpleType();
                    chunkRequest.entity.setChunkX(player.getChunkX());
                    chunkRequest.entity.setChunkY(player.getChunkY());
                    client.sendTCP(chunkRequest);
                    //Remove the chunks from far away
                    world.updateRemoval(player.getChunkX(), player.getChunkY());
                    //update last on spots
                    player.setChunkXLastOn(player.getChunkX());
                    player.setChunkYLastOn(player.getChunkY());
                }
            }

            lastX = hAxis;
            lastY = vAxis;
        }
    }

    private void fixButtonSizes(Table table) {
        if (table.getChildren().size > 1) {
            float size = 0;
            for (Actor actor : table.getChildren()) {
                size = (actor.getWidth() > size) ? actor.getWidth() : size;
            }
            for (Actor actor : table.getChildren()) {
                table.getCell(actor).width(size);
            }
        }
    }

    public void resize() {

    }
}
