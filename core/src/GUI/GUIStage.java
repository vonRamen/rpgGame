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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.Alert;
import com.mygdx.game.EntitySimpleType;
import com.mygdx.game.Game;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Packets;
import com.mygdx.game.Player;
import com.mygdx.game.Town;
import com.mygdx.game.Property;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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
    private GUITown townManagement;
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
    private int screenX, screenY;
    private ArrayList<Message> messages;
    private ClickHistory tileClickHistory;
    private Click clickHold;
    private VerticalGroup screenMessages;

    //Menu you geclickHoldt, when you rightclick on something
    private Table rightClickMenu;

    public GUIStage(GameWorld world, OrthographicCamera camera, Player player, Client client) {
        super();
        this.clickHold = new Click(0, 0);
        this.screenMessages = new VerticalGroup();
        this.screenMessages.columnLeft();
        this.tileClickHistory = new ClickHistory();
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
        this.worldClickHandler.setTileClickHistory(tileClickHistory);
        this.worldClickHandler.setDraggedClick(clickHold);
        this.townManagement = new GUITown(player, skin, rightClickMenu);
        this.townManagement.setWorld(this.world);
        this.townManagement.setTileClickHistory(tileClickHistory);
        this.townManagement.setDraggedClick(clickHold);
        this.addActor(rightClickMenu);
        this.addActor(this.townManagement.getRoot());
        this.addActor(screenMessages);
        Gdx.input.setInputProcessor(multiPlexer);
        this.camera = camera;
        this.messages = new ArrayList();
        //this.addActor(new MainGUI(skin));
    }

    @Override
    public void act() {
        if (player != null) {
            //check alerts:
            checkAlert();

            this.inventory.updatePositioning(camera);
            this.townManagement.updatePositioning(camera);

            this.camera = (OrthographicCamera) this.getViewport().getCamera();
            super.act();
            worldClickHandler.update();
            if (isDrawing) {
            }
            if (player.hasUpdatedInventory()) {
                this.inventory.update();
                player.setInventoryUpdate(false);
            }
            if (this.getKeyboardFocus() == null) {
                if (!player.isIsDead()) {
                    inputHandling();
                }
            }
            //update message positioning:
            if (this.screenMessages.getChildren().size > 0) {
                this.screenMessages.setPosition(camera.position.x - Gdx.graphics.getWidth() / 2, camera.position.y - Gdx.graphics.getHeight() / 2);
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
        for (Message message : messages) {
            message.act(world.getDeltaTime());
        }
        //remove the messages run out of time
        Iterator iterator = messages.iterator();
        while (iterator.hasNext()) {
            Message message = (Message) iterator.next();
            for (Actor actor : this.getActors()) {
                if (actor == message) {
                    if (((Message) actor).alpha <= 0) {
                        actor.remove();
                        break;
                    }
                }
            }
            for (Actor actor : this.screenMessages.getChildren()) {
                if (actor == message) {
                    if (((Message) actor).alpha <= 0) {
                        actor.remove();
                        break;
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
        if (this.getKeyboardFocus() == null) {
            if (keycode == Input.Keys.D || keycode == Input.Keys.W || keycode == Input.Keys.A || keycode == Input.Keys.S) {
                rightClickMenu.clear();
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        if (this.getKeyboardFocus() == null) {
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
        //If you click on something else than a textarea or textbox, nullify
        this.setKeyboardFocus(null);
        super.touchDown(screenX, screenY, pointer, button);
        switch (button) {
            case Input.Buttons.LEFT:
                worldClickHandler.leftClickDown(screenX, screenY);
                break;

            case Input.Buttons.RIGHT:
                break;

            default:
                break;
        }
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
        };
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

            this.rightClickMenu.setPosition(this.rightClickMenu.getX() + camera.position.x - this.getViewport().getScreenWidth() / 2, this.rightClickMenu.getY() + camera.position.y - this.getViewport().getScreenHeight() / 2);
        }
        fixButtonSizes(rightClickMenu);

        return true;
    }

    private void checkAlert() {
        Alert alert = player.getAlert();
        if (alert != null) {
            switch (alert.getType()) {
                case WORLD:
                    FloatingMessage message = new FloatingMessage(this, this.skin, screenX + camera.position.x - this.getViewport().getScreenWidth() / 2, (this.getViewport().getScreenHeight() - screenY) + camera.position.y - this.getViewport().getScreenHeight() / 2, alert.getMessage());
                    message.setZIndex(1);
                    this.addActor(message);
                    this.messages.add(message);
                    break;

                case SCREEN:
                    ScreenMessage screenMessage = new ScreenMessage(this, this.skin, alert.getMessage());
                    System.out.println("Screen Message: " + screenMessage.getY());
                    screenMessage.setZIndex(1);
                    this.screenMessages.addActor(screenMessage);
                    this.messages.add(screenMessage);
                    screenMessage.setAlignment(Align.left);
                    screenMessage.pack();
                    this.screenMessages.setPosition(0, 5);
                    this.screenMessages.left();
                    this.screenMessages.wrap(false);
                    this.screenMessages.pack();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);
        worldClickHandler.touchDragged(screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        super.mouseMoved(screenX, screenY);
        this.screenX = screenX;
        this.screenY = screenY;
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

    /**
     * For drawing rectangles and shapes I.e: town borders
     */
    public void drawShapes() {
        Iterator townIterator = this.world.getTowns().entrySet().iterator();
        while (townIterator.hasNext()) {
            Town town = (Town) ((Map.Entry) townIterator.next()).getValue();
            Game.shapeRenderer.setColor(0.2f, 0.7f, 0.2f, 1f);
            Game.shapeRenderer.rect(town.getBounds().x, town.getBounds().y, town.getBounds().width, town.getBounds().height);
            Game.shapeRenderer.setColor(Color.WHITE);
            for (Property property : town.getProperties()) {
                Game.shapeRenderer.setColor(0.7f, 0f, 0.2f, 1f);
                Game.shapeRenderer.rect(property.getBounds().x, property.getBounds().y, property.getBounds().width, property.getBounds().height);
                Game.shapeRenderer.setColor(Color.WHITE);
            }
        }
        this.townManagement.drawShapes();
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

    /**
     * @return the tileClickHistory
     */
    public ClickHistory getTileClickHistory() {
        return tileClickHistory;
    }
}
