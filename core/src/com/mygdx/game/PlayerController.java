/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import GUI.GUIStage;
import Persistence.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author kristian
 */
public class PlayerController { //This class handles all the controls from the player to the game object

    private final Player player;
    private GameWorld world;
    private Client client;
    private GUIStage stage;
    private Scene scene;
    private int lastX;
    private int lastY; //makes sure a new movement has been entered.
    private Skin skin;
    private OrthographicCamera camera;

    public PlayerController(Client client, Player player, OrthographicCamera camera) {
        this.camera = camera;
        this.player = player;
        this.client = client;
        this.lastX = 0;
        this.lastY = 0;
    }

    public void startGUI() {
        Skin skin = new Skin(Gdx.files.internal("gui skins/uiskin.json"));
        stage = new GUIStage(world, camera, player, skin, client);
    }

    public boolean hasGUI() {
        return stage != null;
    }

    public void inputHandling() {
        if (player != null) {
            int hAxis = ((Gdx.input.isKeyPressed(Keys.D) ? 1 : 0) - (Gdx.input.isKeyPressed(Keys.A) ? 1 : 0));
            int vAxis = ((Gdx.input.isKeyPressed(Keys.W) ? 1 : 0) - (Gdx.input.isKeyPressed(Keys.S) ? 1 : 0));
            player.move(hAxis, vAxis);

            if ((lastX != vAxis || lastY != hAxis) && (vAxis != 0 || hAxis != 0)) { //send update
                Packets.BeginMovement movementUpdate = new Packets.BeginMovement();
                movementUpdate.entity = new EntitySimpleType();
                movementUpdate.entity.changeX = hAxis;
                movementUpdate.entity.changeY = vAxis;
                movementUpdate.entity.x = player.x;
                movementUpdate.entity.y = player.y;
                movementUpdate.entity.uId = player.uId;
                client.sendTCP(movementUpdate);

            } else if ((vAxis == 0 && hAxis == 0) && (lastX != 0 || lastY != 0)) {
                Packets.EndMovement movementUpdate = new Packets.EndMovement();
                movementUpdate.entity = new EntitySimpleType();
                movementUpdate.entity.uId = player.uId;
                movementUpdate.entity.changeX = hAxis;
                movementUpdate.entity.changeY = vAxis;
                movementUpdate.entity.x = player.x;
                movementUpdate.entity.y = player.y;
                client.sendTCP(movementUpdate);
            }
            if ((vAxis != 0 || hAxis != 0)) { //if the player is moving.
                //Check chunk position
                player.chunkX = (int) player.x / (32 * 32);
                player.chunkY = (int) player.y / (32 * 32);
                if (player.chunkX != player.chunkXLastOn || player.chunkY != player.chunkYLastOn) {
                    //Send package request of next chunks.
                    Packets.requestChunks chunkRequest = new Packets.requestChunks();
                    chunkRequest.entity = new EntitySimpleType();
                    chunkRequest.entity.chunkX = player.chunkX;
                    chunkRequest.entity.chunkY = player.chunkY;
                    client.sendTCP(chunkRequest);
                    //Remove the chunks from far away
                    world.updateRemoval(player.chunkX, player.chunkY);
                    //update last on spots
                    player.chunkXLastOn = player.chunkX;
                    player.chunkYLastOn = player.chunkY;
                }
            }

            lastX = vAxis;
            lastY = hAxis;
        }
    }

    public void update() {
        if(!player.isDead) {
            inputHandling();
        }
        if (stage != null) {
            stage.act();
        }
    }
    
    public Stage getStage() {
        return this.stage;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    public void draw() {
        //draw gui:
        if (stage != null) {
            stage.draw();
        }
    }

    void dispose() {
        stage.dispose();
    }
}
