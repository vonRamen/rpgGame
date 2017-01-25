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

    private Skin skin;
    private OrthographicCamera camera;

    public PlayerController(Client client, Player player, OrthographicCamera camera) {
        this.camera = camera;
        this.player = player;
        this.client = client;
    }

    public void startGUI() {
    }

    public boolean hasGUI() {
        return stage != null;
    }

    public void update() {

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
